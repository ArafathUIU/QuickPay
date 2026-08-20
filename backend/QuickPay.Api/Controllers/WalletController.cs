using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QuickPay.Api.Data;
using QuickPay.Api.Dtos;
using QuickPay.Api.Models;
using QuickPay.Api.Services;

namespace QuickPay.Api.Controllers;

[Authorize]
[ApiController]
[Route("api")]
public class WalletController : ControllerBase
{
    private readonly AppDbContext _db;

    public WalletController(AppDbContext db)
    {
        _db = db;
    }

    private string CurrentUserId =>
        User.Identity?.Name ?? User.FindFirst(System.Security.Claims.ClaimTypes.NameIdentifier)?.Value ?? "";

    [HttpGet("wallet")]
    public async Task<IActionResult> GetWallet()
    {
        var wallet = await _db.Wallets.FirstOrDefaultAsync(w => w.UserId == CurrentUserId);
        if (wallet == null)
            return NotFound(new { message = "Wallet not found." });
        return Ok(ToDto(wallet));
    }

    [HttpPost("wallet/add-money")]
    public async Task<IActionResult> AddMoney(AddMoneyRequest request)
    {
        if (request.Amount <= 0)
            return BadRequest(new { message = "Amount must be greater than zero." });
        if (request.Amount > 50000)
            return BadRequest(new { message = "Amount exceeds the transaction limit." });
        if (!IsValidPin(request.Pin))
            return BadRequest(new { message = "PIN must be 4 or 6 digits." });

        var user = await _db.Users.Include(u => u.Wallet).FirstOrDefaultAsync(u => u.Id == CurrentUserId);
        if (user?.Wallet == null)
            return NotFound(new { message = "Wallet not found." });
        if (!PasswordHasher.Verify(request.Pin, user.PinHash))
            return BadRequest(new { message = "Incorrect transaction PIN." });

        var wallet = user.Wallet;
        wallet.Balance += request.Amount;
        wallet.UpdatedAt = DateTime.UtcNow;

        var transaction = new Transaction
        {
            Reference = ReferenceGenerator.Next(TransactionType.ADD_MONEY),
            Type = TransactionType.ADD_MONEY.ToString(),
            Amount = request.Amount,
            SenderId = user.Id,
            ReceiverId = user.Id,
            Status = TransactionStatus.SUCCESS.ToString(),
            CreatedAt = DateTime.UtcNow,
            UpdatedAt = DateTime.UtcNow
        };

        _db.Transactions.Add(transaction);
        await _db.SaveChangesAsync();

        return Ok(ToDto(transaction));
    }

    private static WalletDto ToDto(Wallet wallet) =>
        new(wallet.Id, wallet.UserId, wallet.Balance, new DateTimeOffset(wallet.UpdatedAt).ToUnixTimeMilliseconds());

    private static TransactionDto ToDto(Transaction t) =>
        new(t.Id, t.Reference, t.Type, t.Amount, t.SenderId, t.ReceiverId, t.MerchantId, t.MerchantName,
            t.Status, t.FailureReason, t.Note,
            new DateTimeOffset(t.CreatedAt).ToUnixTimeMilliseconds(),
            new DateTimeOffset(t.UpdatedAt).ToUnixTimeMilliseconds());

    private static bool IsValidPin(string pin) =>
        pin is { Length: 4 or 6 } && pin.All(char.IsDigit);
}