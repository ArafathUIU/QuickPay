package com.arafath.quickpay.data.remote.api;

import com.arafath.quickpay.data.remote.dto.SendMoneyRequest;
import com.arafath.quickpay.data.remote.dto.TransactionDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TransactionApi {

    @POST("transactions/send")
    Call<TransactionDto> sendMoney(@Body SendMoneyRequest request);

    @GET("transactions")
    Call<List<TransactionDto>> getTransactions();

    @GET("transactions/{transactionId}")
    Call<TransactionDto> getTransaction(@Path("transactionId") String transactionId);
}