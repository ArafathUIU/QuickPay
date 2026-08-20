package com.arafath.quickpay.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class SendMoneyRequest {
    @SerializedName("receiverPhone")
    private String receiverPhone;

    @SerializedName("receiverId")
    private String receiverId;

    @SerializedName("amount")
    private double amount;

    @SerializedName("note")
    private String note;

    @SerializedName("pin")
    private String pin;

    public SendMoneyRequest(String receiverPhone, String receiverId, double amount, String note, String pin) {
        this.receiverPhone = receiverPhone;
        this.receiverId = receiverId;
        this.amount = amount;
        this.note = note;
        this.pin = pin;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}