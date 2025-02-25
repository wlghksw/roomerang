package com.roomerang.util;

import java.util.Random;

public class StringHelper {
    public static String maskUsername(String username) {
        if (username.length() <= 4) {
            return "****";
        }
        // 예: username = sirusiru
        String start = username.substring(0, 2);  // "si"
        String end = username.substring(username.length() - 2); // "ru"
        return start + "****" + end;  // "si****ru"
    }

    public static String generateTempPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
