namespace QuickPay.Api.Models;

public class User
{
    public string Id { get; set; } = Guid.NewGuid().ToString();
    public string Name { get; set; } = "";
    public string Phone { get; set; } = "";
    public string Email { get; set; } = "";
    public string PasswordHash { get; set; } = "";
    public string PinHash { get; set; } = "";
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    public Wallet? Wallet { get; set; }
}

public class Wallet
{
    public string Id { get; set; } = Guid.NewGuid().ToString();
    public string UserId { get; set; } = "";
    public decimal Balance { get; set; } = 1000m;
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

    public User? User { get; set; }
}

public class Merchant
{
    public string Id { get; set; } = "";
    public string Name { get; set; } = "";
    public string Category { get; set; } = "";
    public string Status { get; set; } = "ACTIVE";
    public bool SimulateFailure { get; set; }
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}

public class Transaction
{
    public string Id { get; set; } = Guid.NewGuid().ToString();
    public string Reference { get; set; } = "";
    public string Type { get; set; } = "";
    public decimal Amount { get; set; }
    public string? SenderId { get; set; }
    public string? ReceiverId { get; set; }
    public string? MerchantId { get; set; }
    public string? MerchantName { get; set; }
    public string Status { get; set; } = "PENDING";
    public string? FailureReason { get; set; }
    public string? Note { get; set; }
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;
}

public enum TransactionStatus
{
    PENDING,
    PROCESSING,
    SUCCESS,
    FAILED,
    REVERSED
}

public enum TransactionType
{
    ADD_MONEY,
    SEND_MONEY,
    MERCHANT_PAYMENT,
    RECEIVE_MONEY
}