package com.arafath.quickpay.domain.usecase;

import com.arafath.quickpay.util.Validators;

public final class ValidatePaymentUseCase {

    public static final int OK = 0;

    private ValidatePaymentUseCase() {
    }

    public static int validateAmount(double amount) {
        if (amount <= 0) {
            return 1;
        }
        if (amount > com.arafath.quickpay.util.Constants.MAX_TRANSACTION_LIMIT) {
            return 2;
        }
        return OK;
    }

    public static int validatePin(String pin) {
        if (!Validators.isValidPin(pin)) {
            return 3;
        }
        return OK;
    }

    public static int validateBalance(double amount, double balance) {
        if (amount > balance) {
            return 4;
        }
        return OK;
    }

    public static String messageFor(int code) {
        switch (code) {
            case 1:
                return "Amount must be greater than zero.";
            case 2:
                return "Amount exceeds the transaction limit.";
            case 3:
                return "PIN must be 4 or 6 digits.";
            case 4:
                return "Insufficient balance.";
            default:
                return "Invalid request.";
        }
    }
}