using Microsoft.EntityFrameworkCore;
using QuickPay.Api.Models;

namespace QuickPay.Api.Data;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options)
    {
    }

    public DbSet<User> Users => Set<User>();
    public DbSet<Wallet> Wallets => Set<Wallet>();
    public DbSet<Merchant> Merchants => Set<Merchant>();
    public DbSet<Transaction> Transactions => Set<Transaction>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<User>()
            .HasIndex(u => u.Phone).IsUnique();
        modelBuilder.Entity<User>()
            .HasIndex(u => u.Email).IsUnique();

        modelBuilder.Entity<Wallet>()
            .HasOne(w => w.User)
            .WithOne(u => u.Wallet)
            .HasForeignKey<Wallet>(w => w.UserId);

        modelBuilder.Entity<Merchant>()
            .HasIndex(m => m.Id).IsUnique();

        modelBuilder.Entity<Transaction>()
            .HasIndex(t => t.Reference).IsUnique();
        modelBuilder.Entity<Transaction>()
            .HasIndex(t => t.CreatedAt);
    }
}