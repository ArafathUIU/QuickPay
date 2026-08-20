package com.arafath.quickpay.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.arafath.quickpay.domain.model.TransactionStatus;
import com.arafath.quickpay.domain.model.TransactionType;

@Entity(tableName = "transactions", indices = {@Index("createdAt")})
public class TransactionEntity {

    @PrimaryKey
    @NonNull
    private String transactionId;

    private String reference;
    private TransactionType type;
    private double amount;
    private String senderId;
    private String receiverId;
    private String merchantId;
    private String merchantName;
    private TransactionStatus status;
    private String failureReason;
    private String note;
    private long createdAt;
    private long updatedAt;

    public TransactionEntity() {
    }

    @androidx.room.Ignore
    public TransactionEntity(@NonNull String transactionId, String reference, TransactionType type,
                             double amount, String senderId, String receiverId, String merchantId,
                             String merchantName, TransactionStatus status, String failureReason,
                             String note, long createdAt, long updatedAt) {
        this.transactionId = transactionId;
        this.reference = reference;
        this.type = type;
        this.amount = amount;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.status = status;
        this.failureReason = failureReason;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @NonNull
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(@NonNull String transactionId) {
        this.transactionId = transactionId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}