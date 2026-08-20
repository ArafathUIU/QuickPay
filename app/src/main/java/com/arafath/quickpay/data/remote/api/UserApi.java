package com.arafath.quickpay.data.remote.api;

import com.arafath.quickpay.data.remote.dto.UserDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserApi {

    @GET("users/search")
    Call<UserDto> searchByPhone(@Query("phone") String phone);
}