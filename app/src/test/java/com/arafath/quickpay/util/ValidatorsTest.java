package com.arafath.quickpay.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ValidatorsTest {

    @Test
    public void phoneValidation() {
        assertTrue(Validators.isValidPhone("01712345678"));
        assertFalse(Validators.isValidPhone("0171234567"));
        assertFalse(Validators.isValidPhone("017123456789"));
        assertFalse(Validators.isValidPhone("12712345678"));
        assertFalse(Validators.isValidPhone(""));
        assertFalse(Validators.isValidPhone(null));
    }

    @Test
    public void pinValidation() {
        assertTrue(Validators.isValidPin("1234"));
        assertTrue(Validators.isValidPin("123456"));
        assertFalse(Validators.isValidPin("123"));
        assertFalse(Validators.isValidPin("12345"));
        assertFalse(Validators.isValidPin("abcd"));
        assertFalse(Validators.isValidPin(null));
    }

    @Test
    public void amountValidation() {
        assertTrue(Validators.isValidAmount(1.0));
        assertTrue(Validators.isValidAmount(50000.0));
        assertFalse(Validators.isValidAmount(0));
        assertFalse(Validators.isValidAmount(-10));
        assertFalse(Validators.isValidAmount(50000.01));
    }
}