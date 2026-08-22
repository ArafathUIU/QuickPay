package com.arafath.quickpay.data.repository;

import com.arafath.quickpay.data.local.mapper.Mappers;
import com.arafath.quickpay.data.remote.api.UserApi;
import com.arafath.quickpay.data.remote.interceptor.ApiErrorParser;
import com.arafath.quickpay.domain.model.User;
import com.arafath.quickpay.util.MainThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class UserRepository {

    private final UserApi userApi;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UserRepository(UserApi userApi) {
        this.userApi = userApi;
    }

    public void searchByPhone(String phone, Callback<User> rawCallback) {
        Callback<User> callback = onMain(rawCallback);
        executor.execute(() -> {
            try {
                Response<com.arafath.quickpay.data.remote.dto.UserDto> response =
                        userApi.searchByPhone(phone).execute();
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