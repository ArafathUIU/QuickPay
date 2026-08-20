package com.arafath.quickpay.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "wallets")
public class WalletEntity {

    @PrimaryKey
    @NonNull
    private String walletId;

    private String userId;
    private double balance;
    private long updatedAt;

    public WalletEntity() {
    }

    @androidx.room.Ignore
    public WalletEntity(@NonNull String walletId, String userId, double balance, long updatedAt) {
        this.walletId = walletId;
        this.userId = userId;
        this.balance = balance;
        this.updatedAt = updatedAt;
    }

    @NonNull
    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(@NonNull String walletId) {
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