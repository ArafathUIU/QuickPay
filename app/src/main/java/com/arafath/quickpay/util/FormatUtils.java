package com.arafath.quickpay.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class FormatUtils {

    private FormatUtils() {
    }

    public static String currency(double amount) {
        return String.format(Locale.US, "৳ %,.2f", amount);
    }

    public static String amount(double amount) {
        return String.format(Locale.US, "%,.2f", amount);
    }

    public static String dateTime(long millis) {
        return new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date(millis));
    }

    public static String date(long millis) {
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(millis));
    }
}