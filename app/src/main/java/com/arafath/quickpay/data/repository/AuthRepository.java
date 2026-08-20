package com.arafath.quickpay.data.repository;

import com.arafath.quickpay.data.local.dao.UserDao;
import com.arafath.quickpay.data.local.mapper.Mappers;
import com.arafath.quickpay.data.remote.api.AuthApi;
import com.arafath.quickpay.data.remote.dto.LoginRequest;
import com.arafath.quickpay.data.remote.dto.RegisterRequest;
import com.arafath.quickpay.domain.model.ApiState;
import com.arafath.quickpay.domain.model.User;
import com.arafath.quickpay.domain.model.Wallet;
import com.arafath.quickpay.util.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class AuthRepository {

    private final AuthApi authApi;
    private final UserDao userDao;
    private final SessionManager sessionManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AuthRepository(AuthApi authApi, UserDao userDao, SessionManager sessionManager) {
        this.authApi = authApi;
        this.userDao = userDao;
        this.sessionManager = sessionManager;
    }

    public void register(RegisterRequest request, Callback<User> callback) {
        executor.execute(() -> {
            try {
                Response<?> response = authApi.register(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(Mappers.toDomain(((com.arafath.quickpay.data.remote.dto.AuthResponse) response.body()).getUser()));
                } else {
                    callback.onError(com.arafath.quickpay.data.remote.interceptor.ApiErrorParser.parseError(response));
                }
            } catch (Exception e) {
                callback.onError(com.arafath.quickpay.data.remote.interceptor.ApiErrorParser.parseThrowable(e));
            }
        });
    }

    public void login(String phone, String password, Callback<LoginResult> callback) {
        executor.execute(() -> {
            try {
                Call<com.arafath.quickpay.data.remote.dto.AuthResponse> call = authApi.login(new LoginRequest(phone, password));
                Response<com.arafath.quickpay.data.remote.dto.AuthResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {
                    com.arafath.quickpay.data.remote.dto.AuthResponse body = response.body();
                    User user = Mappers.toDomain(body.getUser());
                    Wallet wallet = Mappers.toDomain(body.getWallet());
                    if (user != null) {
                        userDao.upsert(Mappers.toEntity(user));
                    }
                    sessionManager.saveSession(
                            body.getToken(),
                            user != null ? user.getUserId() : null,
                            user != null ? user.getName() : null,
                            user != null ? user.getPhone() : null,
                            user != null ? user.getEmail() : null,
                            wallet != null ? wallet.getWalletId() : null
                    );
                    callback.onSuccess(new LoginResult(user, wallet));
                } else {
                    callback.onError(com.arafath.quickpay.data.remote.interceptor.ApiErrorParser.parseError(response));
                }
            } catch (Exception e) {
                callback.onError(com.arafath.quickpay.data.remote.interceptor.ApiErrorParser.parseThrowable(e));
            }
        });
    }

    public User getCachedUser() {
        return Mappers.toDomain(userDao.getById(sessionManager.getUserId()));
    }

    public void logout() {
        sessionManager.clearSession();
    }

    public interface Callback<T> {
        void onSuccess(T data);

        void onError(String message);
    }

    public static class LoginResult {
        public final User user;
        public final Wallet wallet;

        public LoginResult(User user, Wallet wallet) {
            this.user = user;
            this.wallet = wallet;
        }
    }
}