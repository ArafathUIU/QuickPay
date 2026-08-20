package com.arafath.quickpay.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.arafath.quickpay.data.local.entity.WalletEntity;

@Dao
public interface WalletDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(WalletEntity wallet);

    @Query("SELECT * FROM wallets WHERE walletId = :walletId LIMIT 1")
    WalletEntity getById(String walletId);

    @Query("SELECT * FROM wallets WHERE userId = :userId LIMIT 1")
    WalletEntity getByUserId(String userId);

    @Query("UPDATE wallets SET balance = :balance, updatedAt = :updatedAt WHERE walletId = :walletId")
    void updateBalance(String walletId, double balance, long updatedAt);
}