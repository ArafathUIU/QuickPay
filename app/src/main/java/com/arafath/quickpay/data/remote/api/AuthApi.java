package com.arafath.quickpay.data.remote.api;

import com.arafath.quickpay.data.remote.dto.AuthResponse;
import com.arafath.quickpay.data.remote.dto.LoginRequest;
import com.arafath.quickpay.data.remote.dto.RegisterRequest;
import com.arafath.quickpay.data.remote.dto.UserDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @GET("auth/me")
    Call<UserDto> me();
}