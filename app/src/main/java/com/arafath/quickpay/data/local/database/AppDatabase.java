package com.arafath.quickpay.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.arafath.quickpay.data.local.dao.TransactionDao;
import com.arafath.quickpay.data.local.dao.UserDao;
import com.arafath.quickpay.data.local.dao.WalletDao;
import com.arafath.quickpay.data.local.entity.TransactionEntity;
import com.arafath.quickpay.data.local.entity.UserEntity;
import com.arafath.quickpay.data.local.entity.WalletEntity;

@Database(
        entities = {UserEntity.class, WalletEntity.class, TransactionEntity.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract UserDao userDao();

    public abstract WalletDao walletDao();

    public abstract TransactionDao transactionDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "quickpay.db"
                    ).build();
                }
            }
        }
        return instance;
    }
}