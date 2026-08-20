using System.Text;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using QuickPay.Api.Data;
using QuickPay.Api.Models;
using QuickPay.Api.Services;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();

builder.Services.AddDbContext<AppDbContext>(options =>
{
    var provider = builder.Configuration["Database:Provider"] ?? "Sqlite";
    if (provider.Equals("Postgres", StringComparison.OrdinalIgnoreCase))
    {
        options.UseNpgsql(builder.Configuration.GetConnectionString("Postgres"));
    }
    else
    {
        options.UseSqlite(builder.Configuration.GetConnectionString("Sqlite"));
    }
});

var jwtSecret = builder.Configuration["Jwt:Secret"]!;
var jwtIssuer = builder.Configuration["Jwt:Issuer"]!;
var jwtAudience = builder.Configuration["Jwt:Audience"]!;

builder.Services.AddSingleton(new JwtService(builder.Configuration));

builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,
            ValidIssuer = jwtIssuer,
            ValidAudience = jwtAudience,
            IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(jwtSecret))
        };
    });

builder.Services.AddAuthorization();

builder.Services.AddCors(options =>
{
    options.AddDefaultPolicy(policy =>
        policy.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader());
});

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo { Title = "QuickPay API", Version = "v1" });
    c.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        Name = "Authorization",
        Type = SecuritySchemeType.Http,
        Scheme = "bearer",
        BearerFormat = "JWT",
        In = ParameterLocation.Header
    });
    c.AddSecurityRequirement(new OpenApiSecurityRequirement
    {
        {
            new OpenApiSecurityScheme
            {
                Reference = new OpenApiReference { Type = ReferenceType.SecurityScheme, Id = "Bearer" }
            },
            Array.Empty<string>()
        }
    });
});

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    db.Database.EnsureCreated();
    SeedData.Seed(db);
}

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseCors();

app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

app.Run();

public static partial class SeedData
{
    public static void Seed(AppDbContext db)
    {
        if (!db.Merchants.Any())
        {
            db.Merchants.AddRange(
                new Merchant { Id = "MER-10001", Name = "ABC Coffee", Category = "Cafe", Status = "ACTIVE" },
                new Merchant { Id = "MER-10002", Name = "XYZ Store", Category = "Retail", Status = "ACTIVE" },
                new Merchant { Id = "MER-10003", Name = "Rahim's Restaurant", Category = "Food", Status = "ACTIVE" },
                new Merchant { Id = "MER-10004", Name = "Demo Fail Store", Category = "Retail", Status = "ACTIVE", SimulateFailure = true }
            );
        }

        if (!db.Users.Any())
        {
            var akash = new User
            {
                Name = "Akash Rahman",
                Phone = "01712345678",
                Email = "akash@quickpay.demo",
                PasswordHash = PasswordHasher.Hash("123456"),
                PinHash = PasswordHasher.Hash("1234")
            };
            var rahim = new User
            {
                Name = "Rahim Ahmed",
                Phone = "01798765432",
                Email = "rahim@quickpay.demo",
                PasswordHash = PasswordHasher.Hash("123456"),
                PinHash = PasswordHasher.Hash("1234")
            };
            akash.Wallet = new Wallet { Balance = 12500m };
            rahim.Wallet = new Wallet { Balance = 8000m };
            db.Users.AddRange(akash, rahim);
        }

        db.SaveChanges();
    }
}