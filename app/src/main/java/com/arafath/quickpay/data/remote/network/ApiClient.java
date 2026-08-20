package com.arafath.quickpay.data.remote.network;

import com.arafath.quickpay.data.remote.api.AuthApi;
import com.arafath.quickpay.data.remote.api.MerchantApi;
import com.arafath.quickpay.data.remote.api.TransactionApi;
import com.arafath.quickpay.data.remote.api.UserApi;
import com.arafath.quickpay.data.remote.api.WalletApi;
import com.arafath.quickpay.data.remote.interceptor.AuthInterceptor;
import com.arafath.quickpay.util.Constants;
import com.arafath.quickpay.util.SessionManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private final AuthApi authApi;
    private final WalletApi walletApi;
    private final UserApi userApi;
    private final TransactionApi transactionApi;
    private final MerchantApi merchantApi;

    public ApiClient(SessionManager sessionManager) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(sessionManager))
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        authApi = retrofit.create(AuthApi.class);
        walletApi = retrofit.create(WalletApi.class);
        userApi = retrofit.create(UserApi.class);
        transactionApi = retrofit.create(TransactionApi.class);
        merchantApi = retrofit.create(MerchantApi.class);
    }

    public AuthApi getAuthApi() {
        return authApi;
    }

    public WalletApi getWalletApi() {
        return walletApi;
    }

    public UserApi getUserApi() {
        return userApi;
    }

    public TransactionApi getTransactionApi() {
        return transactionApi;
    }

    public MerchantApi getMerchantApi() {
        return merchantApi;
    }
}