package com.arafath.quickpay.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class ApiErrorBody {
    @SerializedName("message")
    private String message;

    @SerializedName("error")
    private String error;

    public String getMessage() {
        if (message != null) {
            return message;
        }
        return error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}