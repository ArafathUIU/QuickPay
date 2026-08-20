package com.arafath.quickpay.util;

public final class Constants {

    public static final String BASE_URL = "http://10.0.2.2:5000/api/";

    public static final String QR_TYPE_USER = "QUICKPAY_USER";
    public static final String QR_TYPE_MERCHANT = "QUICKPAY_MERCHANT";
    public static final String QR_VERSION = "1";

    public static final String KEY_QR_DATA = "qr_data";
    public static final String KEY_TXN_ID = "transaction_id";
    public static final String KEY_MERCHANT = "merchant";

    public static final double MAX_TRANSACTION_LIMIT = 50000.0;

    private Constants() {
    }
}