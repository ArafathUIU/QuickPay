package com.arafath.quickpay.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.arafath.quickpay.data.local.entity.UserEntity;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(UserEntity user);

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    UserEntity getById(String userId);

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    UserEntity getByPhone(String phone);

    @Query("DELETE FROM users")
    void clear();
}