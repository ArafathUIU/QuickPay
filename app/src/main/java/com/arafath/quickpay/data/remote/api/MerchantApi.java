package com.arafath.quickpay.data.remote.api;

import com.arafath.quickpay.data.remote.dto.MerchantDto;
import com.arafath.quickpay.data.remote.dto.MerchantPaymentRequest;
import com.arafath.quickpay.data.remote.dto.TransactionDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MerchantApi {

    @GET("merchants/{merchantId}")
    Call<MerchantDto> getMerchant(@Path("merchantId") String merchantId);

    @POST("payments/merchant")
    Call<TransactionDto> merchantPayment(@Body MerchantPaymentRequest request);
}