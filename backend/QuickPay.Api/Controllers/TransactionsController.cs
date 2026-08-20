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
public class TransactionsController : ControllerBase
{
    private readonly AppDbContext _db;

    public TransactionsController(AppDbContext db)
    {
        _db = db;
    }

    private string CurrentUserId =>
        User.Identity?.Name ?? User.FindFirst(System.Security.Claims.ClaimTypes.NameIdentifier)?.Value ?? "";

    [HttpPost("transactions/send")]
    public async Task<IActionResult> Send(SendMoneyRequest request)
    {
        if (request.Amount <= 0)
            return BadRequest(new { message = "Amount must be greater than zero." });
        if (request.Amount > 50000)
            return BadRequest(new { message = "Amount exceeds the transaction limit." });
        if (!IsValidPin(request.Pin))
            return BadRequest(new { message = "PIN must be 4 or 6 digits." });

        var sender = await _db.Users.Include(u => u.Wallet).FirstOrDefaultAsync(u => u.Id == CurrentUserId);
        if (sender?.Wallet == null)
            return NotFound(new { message = "Wallet not found." });

        if (!PasswordHasher.Verify(request.Pin, sender.PinHash))
            return BadRequest(new { message = "Incorrect transaction PIN." });

        var receiver = await _db.Users.Include(u => u.Wallet)
            .FirstOrDefaultAsync(u => u.Phone == request.ReceiverPhone || u.Id == request.ReceiverId);

        if (receiver == null)
            return NotFound(new { message = "Recipient not found." });
        if (receiver.Id == sender.Id)
            return BadRequest(new { message = "You cannot send money to yourself." });
        if (sender.Wallet.Balance < request.Amount)
            return BadRequest(new { message = "Insufficient balance." });

        sender.Wallet.Balance -= request.Amount;
        sender.Wallet.UpdatedAt = DateTime.UtcNow;
        receiver.Wallet!.Balance += request.Amount;
        receiver.Wallet.UpdatedAt = DateTime.UtcNow;

        var transaction = new Transaction
        {
            Reference = ReferenceGenerator.Next(TransactionType.SEND_MONEY),
            Type = TransactionType.SEND_MONEY.ToString(),
            Amount = request.Amount,
            SenderId = sender.Id,
            ReceiverId = receiver.Id,
            Status = TransactionStatus.SUCCESS.ToString(),
            Note = string.IsNullOrWhiteSpace(request.Note) ? null : request.Note.Trim(),
            CreatedAt = DateTime.UtcNow,
            UpdatedAt = DateTime.UtcNow
        };

        _db.Transactions.Add(transaction);
        await _db.SaveChangesAsync();

        return Ok(ToDto(transaction));
    }

    [HttpGet("transactions")]
    public async Task<IActionResult> GetAll()
    {
        var transactions = await _db.Transactions
            .Where(t => t.SenderId == CurrentUserId || t.ReceiverId == CurrentUserId)
            .OrderByDescending(t => t.CreatedAt)
            .ToListAsync();

        return Ok(transactions.Select(ToDto));
    }

    [HttpGet("transactions/{transactionId}")]
    public async Task<IActionResult> GetById(string transactionId)
    {
        var transaction = await _db.Transactions.FindAsync(transactionId);
        if (transaction == null)
            return NotFound(new { message = "Transaction not found." });
        return Ok(ToDto(transaction));
    }

    private static TransactionDto ToDto(Transaction t) =>
        new(t.Id, t.Reference, t.Type, t.Amount, t.SenderId, t.ReceiverId, t.MerchantId, t.MerchantName,
            t.Status, t.FailureReason, t.Note,
            new DateTimeOffset(t.CreatedAt).ToUnixTimeMilliseconds(),
            new DateTimeOffset(t.UpdatedAt).ToUnixTimeMilliseconds());

    private static bool IsValidPin(string pin) =>
        pin is { Length: 4 or 6 } && pin.All(char.IsDigit);
}