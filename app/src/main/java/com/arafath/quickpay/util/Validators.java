package com.arafath.quickpay.util;

import java.util.regex.Pattern;

public final class Validators {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^01[0-9]{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Validators() {
    }

    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 3;
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidPin(String pin) {
        return pin != null && (pin.length() == 4 || pin.length() == 6) && pin.matches("\\d+");
    }

    public static boolean isValidAmount(double amount) {
        return amount > 0 && amount <= com.arafath.quickpay.util.Constants.MAX_TRANSACTION_LIMIT;
    }
}