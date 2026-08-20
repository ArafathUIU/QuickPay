package com.arafath.quickpay.util;

import com.arafath.quickpay.domain.model.Transaction;

public final class PaymentResultHolder {

    private static Transaction transaction;

    private PaymentResultHolder() {
    }

    public static void set(Transaction transaction) {
        PaymentResultHolder.transaction = transaction;
    }

    public static Transaction get() {
        return transaction;
    }

    public static void clear() {
        transaction = null;
    }
}