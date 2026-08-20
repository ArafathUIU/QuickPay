package com.arafath.quickpay.data.remote.api;

import com.arafath.quickpay.data.remote.dto.AddMoneyRequest;
import com.arafath.quickpay.data.remote.dto.TransactionDto;
import com.arafath.quickpay.data.remote.dto.WalletDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface WalletApi {

    @GET("wallet")
    Call<WalletDto> getWallet();

    @POST("wallet/add-money")
    Call<TransactionDto> addMoney(@Body AddMoneyRequest request);
}