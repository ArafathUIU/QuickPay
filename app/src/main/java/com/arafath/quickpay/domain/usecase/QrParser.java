package com.arafath.quickpay.domain.usecase;

import com.arafath.quickpay.util.Constants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class QrParser {

    public static class QrData {
        public final String type;
        public final String id;
        public final String name;

        public QrData(String type, String id, String name) {
            this.type = type;
            this.id = id;
            this.name = name;
        }

        public boolean isMerchant() {
            return Constants.QR_TYPE_MERCHANT.equals(type);
        }

        public boolean isUser() {
            return Constants.QR_TYPE_USER.equals(type);
        }
    }

    private QrParser() {
    }

    public static QrData parse(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            JsonObject json = JsonParser.parseString(raw).getAsJsonObject();
            String type = json.has("type") ? json.get("type").getAsString() : null;
            String id = null;
            if (json.has("id")) {
                id = json.get("id").getAsString();
            } else if (json.has("userId")) {
                id = json.get("userId").getAsString();
            } else if (json.has("merchantId")) {
                id = json.get("merchantId").getAsString();
            }
            String name = json.has("name") ? json.get("name").getAsString() : null;
            if (name == null && json.has("merchantName")) {
                name = json.get("merchantName").getAsString();
            }
            if (type == null || id == null || id.isEmpty()) {
                return null;
            }
            return new QrData(type, id, name);
        } catch (Exception e) {
            return null;
        }
    }

    public static String buildUserQr(String userId) {
        JsonObject json = new JsonObject();
        json.addProperty("type", Constants.QR_TYPE_USER);
        json.addProperty("userId", userId);
        json.addProperty("version", Constants.QR_VERSION);
        return json.toString();
    }

    public static String buildMerchantQr(String merchantId, String name) {
        JsonObject json = new JsonObject();
        json.addProperty("type", Constants.QR_TYPE_MERCHANT);
        json.addProperty("merchantId", merchantId);
        json.addProperty("merchantName", name);
        json.addProperty("version", Constants.QR_VERSION);
        return json.toString();
    }
}