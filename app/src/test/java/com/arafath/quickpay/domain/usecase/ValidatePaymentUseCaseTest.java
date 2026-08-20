package com.arafath.quickpay.domain.usecase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ValidatePaymentUseCaseTest {

    @Test
    public void validAmount_isAccepted() {
        assertEquals(ValidatePaymentUseCase.OK, ValidatePaymentUseCase.validateAmount(250.0));
        assertEquals(ValidatePaymentUseCase.OK, ValidatePaymentUseCase.validateAmount(0.01));
    }

    @Test
    public void zeroAndNegativeAmounts_areRejected() {
        assertEquals(1, ValidatePaymentUseCase.validateAmount(0));
        assertEquals(1, ValidatePaymentUseCase.validateAmount(-5));
    }

    @Test
    public void amountOverLimit_isRejected() {
        assertEquals(2, ValidatePaymentUseCase.validateAmount(50001));
    }

    @Test
    public void pin_validation_rules() {
        assertEquals(ValidatePaymentUseCase.OK, ValidatePaymentUseCase.validatePin("1234"));
        assertEquals(ValidatePaymentUseCase.OK, ValidatePaymentUseCase.validatePin("123456"));
        assertEquals(3, ValidatePaymentUseCase.validatePin("123"));
        assertEquals(3, ValidatePaymentUseCase.validatePin("12ab"));
        assertEquals(3, ValidatePaymentUseCase.validatePin(""));
    }

    @Test
    public void insufficientBalance_isRejected() {
        assertEquals(ValidatePaymentUseCase.OK, ValidatePaymentUseCase.validateBalance(100, 200));
        assertEquals(ValidatePaymentUseCase.OK, ValidatePaymentUseCase.validateBalance(200, 200));
        assertEquals(4, ValidatePaymentUseCase.validateBalance(200, 100));
    }
}