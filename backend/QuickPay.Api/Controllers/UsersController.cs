using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QuickPay.Api.Data;
using QuickPay.Api.Dtos;

namespace QuickPay.Api.Controllers;

[Authorize]
[ApiController]
[Route("api/users")]
public class UsersController : ControllerBase
{
    private readonly AppDbContext _db;

    public UsersController(AppDbContext db)
    {
        _db = db;
    }

    [HttpGet("search")]
    public async Task<IActionResult> Search([FromQuery] string phone)
    {
        if (string.IsNullOrWhiteSpace(phone))
            return BadRequest(new { message = "Phone number is required." });

        var user = await _db.Users.FirstOrDefaultAsync(u => u.Phone == phone.Trim());
        if (user == null)
            return NotFound(new { message = "Recipient not found." });

        return Ok(new UserDto(user.Id, user.Name, user.Phone, user.Email));
    }
}