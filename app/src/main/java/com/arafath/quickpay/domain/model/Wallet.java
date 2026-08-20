package com.arafath.quickpay.domain.model;

public class Wallet {
    private String walletId;
    private String userId;
    private double balance;
    private long updatedAt;

    public Wallet() {
    }

    public Wallet(String walletId, String userId, double balance, long updatedAt) {
        this.walletId = walletId;
        this.userId = userId;
        this.balance = balance;
        this.updatedAt = updatedAt;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}