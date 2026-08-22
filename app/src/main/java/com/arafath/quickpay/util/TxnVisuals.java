package com.arafath.quickpay.util;

import com.arafath.quickpay.R;
import com.arafath.quickpay.domain.model.TransactionType;

public final class TxnVisuals {

    private TxnVisuals() {
    }

    public static int iconRes(TransactionType type) {
        if (type == null) {
            return R.drawable.ic_txn;
        }
        switch (type) {
            case ADD_MONEY:
                return R.drawable.ic_add_money;
            case RECEIVE_MONEY:
                return R.drawable.ic_receive;
            case MERCHANT_PAYMENT:
                return R.drawable.ic_store;
            case SEND_MONEY:
            default:
                return R.drawable.ic_send_money;
        }
    }

    public static int iconTintRes(TransactionType type) {
        if (type == null) {
            return R.color.text_secondary;
        }
        switch (type) {
            case ADD_MONEY:
            case RECEIVE_MONEY:
                return R.color.success;
            case MERCHANT_PAYMENT:
                return R.color.accent_text;
            case SEND_MONEY:
            default:
                return R.color.danger;
        }
    }

    public static int circleBgRes(TransactionType type) {
        if (type == null) {
            return R.drawable.bg_icon_circle;
        }
        switch (type) {
            case ADD_MONEY:
            case RECEIVE_MONEY:
                return R.drawable.bg_icon_circle_green;
            case MERCHANT_PAYMENT:
                return R.drawable.bg_icon_circle_gold;
            case SEND_MONEY:
            default:
                return R.drawable.bg_icon_circle_red;
        }
    }

    public static String titleFor(com.arafath.quickpay.domain.model.Transaction t) {
        if (t.getType() == TransactionType.MERCHANT_PAYMENT) {
            return t.getMerchantName() != null ? t.getMerchantName() : "Merchant Payment";
        }
        if (t.getType() == TransactionType.ADD_MONEY) {
            return "Added Money";
        }
        return t.getNote() != null && !t.getNote().isEmpty()
                ? t.getNote() : "Send Money";
    }

    public static String signedAmount(com.arafath.quickpay.domain.model.Transaction t) {
        boolean inflow = t.getType() == TransactionType.ADD_MONEY
                || t.getType() == TransactionType.RECEIVE_MONEY;
        String prefix = inflow ? "+" : "-";
        if (t.getStatus() == com.arafath.quickpay.domain.model.TransactionStatus.REVERSED) {
            return "0.00";
        }
        return prefix + FormatUtils.amount(t.getAmount());
    }
}