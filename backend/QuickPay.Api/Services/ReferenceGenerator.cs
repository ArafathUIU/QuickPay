using QuickPay.Api.Models;

namespace QuickPay.Api.Services;

public static class ReferenceGenerator
{
    public static string Next(TransactionType type)
    {
        string prefix = type switch
        {
            TransactionType.ADD_MONEY => "QP-ADD",
            TransactionType.SEND_MONEY => "QP-SND",
            TransactionType.MERCHANT_PAYMENT => "QP-PAY",
            _ => "QP-TXN"
        };
        return $"{prefix}-{DateTime.UtcNow:yyyyMMdd}-{Random.Shared.Next(1000, 9999)}";
    }
}