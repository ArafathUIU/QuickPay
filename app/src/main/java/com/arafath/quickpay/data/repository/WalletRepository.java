package com.arafath.quickpay.data.repository;

import com.arafath.quickpay.data.local.dao.WalletDao;
import com.arafath.quickpay.data.local.mapper.Mappers;
import com.arafath.quickpay.data.remote.api.WalletApi;
import com.arafath.quickpay.data.remote.dto.AddMoneyRequest;
import com.arafath.quickpay.data.remote.interceptor.ApiErrorParser;
import com.arafath.quickpay.domain.model.Transaction;
import com.arafath.quickpay.domain.model.Wallet;
import com.arafath.quickpay.util.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class WalletRepository {

    private final WalletApi walletApi;
    private final WalletDao walletDao;
    private final SessionManager sessionManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public WalletRepository(WalletApi walletApi, WalletDao walletDao, SessionManager sessionManager) {
        this.walletApi = walletApi;
        this.walletDao = walletDao;
        this.sessionManager = sessionManager;
    }

    public Wallet getCachedWallet() {
        return Mappers.toDomain(walletDao.getById(sessionManager.getWalletId()));
    }

    public void fetchWallet(Callback<Wallet> callback) {
        executor.execute(() -> {
            try {
                Response<com.arafath.quickpay.data.remote.dto.WalletDto> response = walletApi.getWallet().execute();
                if (response.isSuccessful() && response.body() != null) {
                    Wallet wallet = Mappers.toDomain(response.body());
                    cache(wallet);
                    callback.onSuccess(wallet);
                } else {
                    callback.onError(ApiErrorParser.parseError(response));
                }
            } catch (Exception e) {
                callback.onError(ApiErrorParser.parseThrowable(e));
            }
        });
    }

    public void addMoney(double amount, String pin, Callback<Transaction> callback) {
        executor.execute(() -> {
            try {
                Response<com.arafath.quickpay.data.remote.dto.TransactionDto> response =
                        walletApi.addMoney(new AddMoneyRequest(amount, pin)).execute();
                if (response.isSuccessful() && response.body() != null) {
                    Transaction transaction = Mappers.toDomain(response.body());
                    refreshCache();
                    callback.onSuccess(transaction);
                } else {
                    callback.onError(ApiErrorParser.parseError(response));
                }
            } catch (Exception e) {
                callback.onError(ApiErrorParser.parseThrowable(e));
            }
        });
    }

    public void updateBalance(double balance) {
        Wallet cached = getCachedWallet();
        if (cached != null) {
            cached.setBalance(balance);
            cached.setUpdatedAt(System.currentTimeMillis());
            cache(cached);
        }
    }

    private void refreshCache() {
        try {
            Response<com.arafath.quickpay.data.remote.dto.WalletDto> response = walletApi.getWallet().execute();
            if (response.isSuccessful() && response.body() != null) {
                cache(Mappers.toDomain(response.body()));
            }
        } catch (Exception ignored) {
        }
    }

    private void cache(Wallet wallet) {
        if (wallet != null) {
            walletDao.upsert(Mappers.toEntity(wallet));
        }
    }

    public interface Callback<T> {
        void onSuccess(T data);

        void onError(String message);
    }
}