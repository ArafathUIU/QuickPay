package com.arafath.quickpay.domain.usecase;

import com.arafath.quickpay.domain.model.TransactionStatus;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public final class TransactionStateMachine {

    private static final Map<TransactionStatus, EnumSet<TransactionStatus>> TRANSITIONS = new HashMap<>();

    static {
        TRANSITIONS.put(TransactionStatus.PENDING, EnumSet.of(TransactionStatus.PROCESSING, TransactionStatus.FAILED));
        TRANSITIONS.put(TransactionStatus.PROCESSING, EnumSet.of(TransactionStatus.SUCCESS, TransactionStatus.FAILED, TransactionStatus.REVERSED));
        TRANSITIONS.put(TransactionStatus.SUCCESS, EnumSet.noneOf(TransactionStatus.class));
        TRANSITIONS.put(TransactionStatus.FAILED, EnumSet.noneOf(TransactionStatus.class));
        TRANSITIONS.put(TransactionStatus.REVERSED, EnumSet.noneOf(TransactionStatus.class));
    }

    private TransactionStateMachine() {
    }

    public static boolean canTransition(TransactionStatus from, TransactionStatus to) {
        if (from == null || to == null || from == to) {
            return false;
        }
        EnumSet<TransactionStatus> allowed = TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    public static boolean isTerminal(TransactionStatus status) {
        return status == TransactionStatus.SUCCESS
                || status == TransactionStatus.FAILED
                || status == TransactionStatus.REVERSED;
    }
}