package com.arafath.quickpay.util;

import android.os.Handler;
import android.os.Looper;

public final class MainThread {

    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    private MainThread() {
    }

    public static void post(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            MAIN.post(runnable);
        }
    }
}