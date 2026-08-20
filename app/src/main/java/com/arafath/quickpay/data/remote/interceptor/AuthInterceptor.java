package com.arafath.quickpay.data.remote.interceptor;

import com.arafath.quickpay.util.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final SessionManager sessionManager;

    public AuthInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = sessionManager.getToken();
        if (token == null) {
            return chain.proceed(original);
        }
        Request.Builder builder = original.newBuilder()
                .header("Authorization", "Bearer " + token);
        return chain.proceed(builder.build());
    }
}