package com.arafath.quickpay.domain.model;

public class Merchant {
    private String merchantId;
    private String name;
    private String category;
    private String status;

    public Merchant() {
    }

    public Merchant(String merchantId, String name, String category, String status) {
        this.merchantId = merchantId;
        this.name = name;
        this.category = category;
        this.status = status;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}