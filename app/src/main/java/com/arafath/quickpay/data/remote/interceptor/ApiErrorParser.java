package com.arafath.quickpay.data.remote.interceptor;

import com.arafath.quickpay.data.remote.dto.ApiErrorBody;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

public final class ApiErrorParser {

    private static final Gson GSON = new Gson();

    private ApiErrorParser() {
    }

    public static String parseError(Response<?> response) {
        if (response == null) {
            return "Something went wrong. Please try again.";
        }
        ResponseBody body = response.errorBody();
        if (body == null) {
            return "Request failed (" + response.code() + ").";
        }
        try {
            ApiErrorBody error = GSON.fromJson(body.string(), ApiErrorBody.class);
            if (error != null && error.getMessage() != null) {
                return error.getMessage();
            }
        } catch (IOException ignored) {
        }
        return "Request failed (" + response.code() + ").";
    }

    public static String parseThrowable(Throwable t) {
        if (t instanceof IOException) {
            return "Network error. Please check your connection.";
        }
        return t != null && t.getMessage() != null ? t.getMessage() : "Unexpected error.";
    }
}