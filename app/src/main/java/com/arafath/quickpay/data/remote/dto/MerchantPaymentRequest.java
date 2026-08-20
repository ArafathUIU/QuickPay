package com.arafath.quickpay.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class MerchantPaymentRequest {
    @SerializedName("merchantId")
    private String merchantId;

    @SerializedName("amount")
    private double amount;

    @SerializedName("pin")
    private String pin;

    public MerchantPaymentRequest(String merchantId, double amount, String pin) {
        this.merchantId = merchantId;
        this.amount = amount;
        this.pin = pin;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}