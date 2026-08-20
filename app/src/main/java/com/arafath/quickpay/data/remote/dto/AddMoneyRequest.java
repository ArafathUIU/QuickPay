package com.arafath.quickpay.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class AddMoneyRequest {
    @SerializedName("amount")
    private double amount;

    @SerializedName("pin")
    private String pin;

    public AddMoneyRequest(double amount, String pin) {
        this.amount = amount;
        this.pin = pin;
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