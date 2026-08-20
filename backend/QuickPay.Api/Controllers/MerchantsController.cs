using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QuickPay.Api.Data;
using QuickPay.Api.Dtos;

namespace QuickPay.Api.Controllers;

[Authorize]
[ApiController]
[Route("api/merchants")]
public class MerchantsController : ControllerBase
{
    private readonly AppDbContext _db;

    public MerchantsController(AppDbContext db)
    {
        _db = db;
    }

    [HttpGet("{merchantId}")]
    public async Task<IActionResult> GetById(string merchantId)
    {
        var merchant = await _db.Merchants.FirstOrDefaultAsync(m => m.Id == merchantId);
        if (merchant == null)
            return NotFound(new { message = "Merchant not found." });
        if (merchant.Status != "ACTIVE")
            return BadRequest(new { message = "This merchant is not active." });

        return Ok(new MerchantDto(merchant.Id, merchant.Name, merchant.Category, merchant.Status));
    }
}