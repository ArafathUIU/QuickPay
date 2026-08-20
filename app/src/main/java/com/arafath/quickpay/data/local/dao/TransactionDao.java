package com.arafath.quickpay.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.arafath.quickpay.data.local.entity.TransactionEntity;
import com.arafath.quickpay.domain.model.TransactionStatus;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(TransactionEntity transaction);

    @Update
    void update(TransactionEntity transaction);

    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    LiveData<List<TransactionEntity>> observeAll();

    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    List<TransactionEntity> getAll();

    @Query("SELECT * FROM transactions WHERE transactionId = :transactionId LIMIT 1")
    TransactionEntity getById(String transactionId);

    @Query("SELECT * FROM transactions WHERE status = :status")
    List<TransactionEntity> getByStatus(TransactionStatus status);

    @Query("DELETE FROM transactions")
    void clear();
}