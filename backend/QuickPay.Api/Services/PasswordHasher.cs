namespace QuickPay.Api.Services;

public static class PasswordHasher
{
    public static string Hash(string value) => BCrypt.Net.BCrypt.HashPassword(value, BCrypt.Net.BCrypt.GenerateSalt(12));

    public static bool Verify(string value, string hash) => BCrypt.Net.BCrypt.Verify(value, hash);
}