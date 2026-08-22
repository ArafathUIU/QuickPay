package com.arafath.quickpay.data.repository;

import com.arafath.quickpay.data.local.mapper.Mappers;
import com.arafath.quickpay.data.remote.api.MerchantApi;
import com.arafath.quickpay.data.remote.interceptor.ApiErrorParser;
import com.arafath.quickpay.domain.model.Merchant;
import com.arafath.quickpay.domain.model.Transaction;
import com.arafath.quickpay.util.MainThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class MerchantRepository {

    private final MerchantApi merchantApi;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public MerchantRepository(MerchantApi merchantApi) {
        this.merchantApi = merchantApi;
    }

    public void validateMerchant(String merchantId, Callback<Merchant> rawCallback) {
        Callback<Merchant> callback = onMain(rawCallback);
        executor.execute(() -> {
            try {
                Response<com.arafath.quickpay.data.remote.dto.MerchantDto> response =
                        merchantApi.getMerchant(merchantId).execute();
                if (response.isSuccessful() && response.body() != null) {
                    Merchant merchant = Mappers.toDomain(response.body());
                    if (merchant != null && "ACTIVE".equalsIgnoreCase(merchant.getStatus())) {
                        callback.onSuccess(merchant);
                    } else {
                        callback.onError("This merchant is not active.");
                    }
                } else {
                    callback.onError(ApiErrorParser.parseError(response));
                }
            } catch (Exception e) {
                callback.onError(ApiErrorParser.parseThrowable(e));
            }
        });
    }

    public void pay(double amount, String merchantId, String pin, Callback<Transaction> rawCallback) {
        Callback<Transaction> callback = onMain(rawCallback);
        executor.execute(() -> {
            try {
                Response<com.arafath.quickpay.data.remote.dto.TransactionDto> response =
                        merchantApi.merchantPayment(
                                new com.arafath.quickpay.data.remote.dto.MerchantPaymentRequest(merchantId, amount, pin)
                        ).execute();
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(Mappers.toDomain(response.body()));
                } else {
                    callback.onError(ApiErrorParser.parseError(response));
                }
            } catch (Exception e) {
                callback.onError(ApiErrorParser.parseThrowable(e));
            }
        });
    }

    public interface Callback<T> {
        void onSuccess(T data);

        void onError(String message);
    }

    private <T> Callback<T> onMain(Callback<T> cb) {
        return new Callback<T>() {
            @Override
            public void onSuccess(T data) {
                MainThread.post(() -> cb.onSuccess(data));
            }

            @Override
            public void onError(String message) {
                MainThread.post(() -> cb.onError(message));
            }
        };
    }
}