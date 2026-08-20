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
public class PaymentsController : ControllerBase
{
    private readonly AppDbContext _db;

    public PaymentsController(AppDbContext db)
    {
        _db = db;
    }

    private string CurrentUserId =>
        User.Identity?.Name ?? User.FindFirst(System.Security.Claims.ClaimTypes.NameIdentifier)?.Value ?? "";

    [HttpPost("payments/merchant")]
    public async Task<IActionResult> MerchantPayment(MerchantPaymentRequest request)
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

        var merchant = await _db.Merchants.FirstOrDefaultAsync(m => m.Id == request.MerchantId);
        if (merchant == null)
            return NotFound(new { message = "Merchant not found." });
        if (merchant.Status != "ACTIVE")
            return BadRequest(new { message = "This merchant is not active." });

        var wallet = sender.Wallet;

        if (wallet.Balance < request.Amount)
        {
            var failed = NewTransaction(request, merchant, wallet, TransactionStatus.FAILED,
                "Insufficient balance");
            _db.Transactions.Add(failed);
            await _db.SaveChangesAsync();
            return Ok(ToDto(failed));
        }

        if (merchant.SimulateFailure)
        {
            wallet.Balance -= request.Amount;
            wallet.UpdatedAt = DateTime.UtcNow;

            var processing = NewTransaction(request, merchant, wallet, TransactionStatus.PROCESSING, null);
            _db.Transactions.Add(processing);
            await _db.SaveChangesAsync();

            wallet.Balance += request.Amount;
            wallet.UpdatedAt = DateTime.UtcNow;
            processing.Status = TransactionStatus.REVERSED.ToString();
            processing.FailureReason = "Merchant failed to confirm the payment. The amount has been refunded.";
            processing.UpdatedAt = DateTime.UtcNow;
            await _db.SaveChangesAsync();

            return Ok(ToDto(processing));
        }

        wallet.Balance -= request.Amount;
        wallet.UpdatedAt = DateTime.UtcNow;

        var success = NewTransaction(request, merchant, wallet, TransactionStatus.SUCCESS, null);
        _db.Transactions.Add(success);
        await _db.SaveChangesAsync();

        return Ok(ToDto(success));
    }

    private static Transaction NewTransaction(MerchantPaymentRequest request, Merchant merchant,
        Wallet wallet, TransactionStatus status, string? failureReason)
    {
        return new Transaction
        {
            Reference = ReferenceGenerator.Next(TransactionType.MERCHANT_PAYMENT),
            Type = TransactionType.MERCHANT_PAYMENT.ToString(),
            Amount = request.Amount,
            SenderId = wallet.UserId,
            MerchantId = merchant.Id,
            MerchantName = merchant.Name,
            Status = status.ToString(),
            FailureReason = failureReason,
            CreatedAt = DateTime.UtcNow,
            UpdatedAt = DateTime.UtcNow
        };
    }

    private static TransactionDto ToDto(Transaction t) =>
        new(t.Id, t.Reference, t.Type, t.Amount, t.SenderId, t.ReceiverId, t.MerchantId, t.MerchantName,
            t.Status, t.FailureReason, t.Note,
            new DateTimeOffset(t.CreatedAt).ToUnixTimeMilliseconds(),
            new DateTimeOffset(t.UpdatedAt).ToUnixTimeMilliseconds());

    private static bool IsValidPin(string pin) =>
        pin is { Length: 4 or 6 } && pin.All(char.IsDigit);
}