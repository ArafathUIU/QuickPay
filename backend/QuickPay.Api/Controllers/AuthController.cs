using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QuickPay.Api.Data;
using QuickPay.Api.Dtos;
using QuickPay.Api.Models;
using QuickPay.Api.Services;

namespace QuickPay.Api.Controllers;

[ApiController]
[Route("api/auth")]
public class AuthController : ControllerBase
{
    private readonly AppDbContext _db;
    private readonly JwtService _jwt;

    public AuthController(AppDbContext db, JwtService jwt)
    {
        _db = db;
        _jwt = jwt;
    }

    [HttpPost("register")]
    public async Task<IActionResult> Register(RegisterRequest request)
    {
        if (string.IsNullOrWhiteSpace(request.Name) || request.Name.Trim().Length < 3)
            return BadRequest(new { message = "Name must be at least 3 characters." });
        if (string.IsNullOrWhiteSpace(request.Phone) || !IsValidPhone(request.Phone))
            return BadRequest(new { message = "Enter a valid 11-digit phone number." });
        if (string.IsNullOrWhiteSpace(request.Email) || !request.Email.Contains("@"))
            return BadRequest(new { message = "Enter a valid email address." });
        if (string.IsNullOrWhiteSpace(request.Password) || request.Password.Length < 6)
            return BadRequest(new { message = "Password must be at least 6 characters." });
        if (string.IsNullOrWhiteSpace(request.Pin) || (request.Pin.Length != 4 && request.Pin.Length != 6) || !request.Pin.All(char.IsDigit))
            return BadRequest(new { message = "Transaction PIN must be 4 or 6 digits." });

        if (await _db.Users.AnyAsync(u => u.Phone == request.Phone))
            return Conflict(new { message = "An account with this phone number already exists." });
        if (await _db.Users.AnyAsync(u => u.Email == request.Email))
            return Conflict(new { message = "An account with this email already exists." });

        var user = new User
        {
            Name = request.Name.Trim(),
            Phone = request.Phone.Trim(),
            Email = request.Email.Trim().ToLowerInvariant(),
            PasswordHash = PasswordHasher.Hash(request.Password),
            PinHash = PasswordHasher.Hash(request.Pin)
        };

        var wallet = new Wallet { UserId = user.Id, Balance = 1000m };
        user.Wallet = wallet;

        _db.Users.Add(user);
        await _db.SaveChangesAsync();

        var token = _jwt.GenerateToken(user);
        return Ok(ToAuthResponse(token, user, wallet));
    }

    [HttpPost("login")]
    public async Task<IActionResult> Login(LoginRequest request)
    {
        var user = await _db.Users.Include(u => u.Wallet)
            .FirstOrDefaultAsync(u => u.Phone == request.Phone);

        if (user == null || !PasswordHasher.Verify(request.Password, user.PasswordHash))
            return Unauthorized(new { message = "Invalid phone number or password." });

        var token = _jwt.GenerateToken(user);
        return Ok(ToAuthResponse(token, user, user.Wallet!));
    }

    [Authorize]
    [HttpGet("me")]
    public async Task<IActionResult> Me()
    {
        var userId = User.Identity?.Name ?? User.FindFirst(System.Security.Claims.ClaimTypes.NameIdentifier)?.Value;
        var user = await _db.Users.FindAsync(userId);
        if (user == null)
            return NotFound(new { message = "User not found." });
        return Ok(ToUserDto(user));
    }

    private static AuthResponse ToAuthResponse(string token, User user, Wallet wallet) => new(
        token,
        ToUserDto(user),
        new WalletDto(wallet.Id, wallet.UserId, wallet.Balance, new DateTimeOffset(wallet.UpdatedAt).ToUnixTimeMilliseconds()));

    private static UserDto ToUserDto(User user) => new(user.Id, user.Name, user.Phone, user.Email);

    private static bool IsValidPhone(string phone) =>
        phone.Length == 11 && phone.StartsWith("01") && phone.All(char.IsDigit);
}