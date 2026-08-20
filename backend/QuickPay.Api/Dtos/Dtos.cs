namespace QuickPay.Api.Dtos;

public record RegisterRequest(string Name, string Phone, string Email, string Password, string Pin);
public record LoginRequest(string Phone, string Password);

public record UserDto(string Id, string Name, string Phone, string Email);
public record WalletDto(string Id, string UserId, decimal Balance, long UpdatedAt);

public record AuthResponse(string Token, UserDto User, WalletDto Wallet);

public record AddMoneyRequest(decimal Amount, string Pin);
public record SendMoneyRequest(string? ReceiverPhone, string? ReceiverId, decimal Amount, string? Note, string Pin);
public record MerchantPaymentRequest(string MerchantId, decimal Amount, string Pin);

public record TransactionDto(
    string Id,
    string Reference,
    string Type,
    decimal Amount,
    string? SenderId,
    string? ReceiverId,
    string? MerchantId,
    string? MerchantName,
    string Status,
    string? FailureReason,
    string? Note,
    long CreatedAt,
    long UpdatedAt);

public record MerchantDto(string Id, string Name, string Category, string Status);