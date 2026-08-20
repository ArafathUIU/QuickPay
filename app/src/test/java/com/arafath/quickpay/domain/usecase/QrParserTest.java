package com.arafath.quickpay.domain.usecase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class QrParserTest {

    @Test
    public void parsesMerchantQr() {
        String raw = "{\"type\":\"QUICKPAY_MERCHANT\",\"merchantId\":\"MER-10001\",\"merchantName\":\"ABC Coffee\",\"version\":\"1\"}";
        QrParser.QrData data = QrParser.parse(raw);
        assertNotNull(data);
        assertTrue(data.isMerchant());
        assertFalse(data.isUser());
        assertEquals("MER-10001", data.id);
        assertEquals("ABC Coffee", data.name);
    }

    @Test
    public void parsesUserQr_withUserIdField() {
        String raw = "{\"type\":\"QUICKPAY_USER\",\"userId\":\"USR-12345\",\"version\":\"1\"}";
        QrParser.QrData data = QrParser.parse(raw);
        assertNotNull(data);
        assertTrue(data.isUser());
        assertFalse(data.isMerchant());
        assertEquals("USR-12345", data.id);
    }

    @Test
    public void parsesUserQr_withIdField() {
        String raw = "{\"type\":\"QUICKPAY_USER\",\"id\":\"USR-42\"}";
        QrParser.QrData data = QrParser.parse(raw);
        assertNotNull(data);
        assertEquals("USR-42", data.id);
    }

    @Test
    public void garbageInput_returnsNull() {
        assertNull(QrParser.parse(null));
        assertNull(QrParser.parse(""));
        assertNull(QrParser.parse("not-json"));
        assertNull(QrParser.parse("{\"type\":\"QUICKPAY_MERCHANT\"}"));
        assertNull(QrParser.parse("{\"merchantId\":\"MER-1\"}"));
    }

    @Test
    public void buildQr_roundTrips() {
        String userQr = QrParser.buildUserQr("USR-12345");
        QrParser.QrData userData = QrParser.parse(userQr);
        assertNotNull(userData);
        assertTrue(userData.isUser());
        assertEquals("USR-12345", userData.id);

        String merchantQr = QrParser.buildMerchantQr("MER-10001", "ABC Coffee");
        QrParser.QrData merchantData = QrParser.parse(merchantQr);
        assertNotNull(merchantData);
        assertTrue(merchantData.isMerchant());
        assertEquals("MER-10001", merchantData.id);
        assertEquals("ABC Coffee", merchantData.name);
    }
}