package com.book.gpt.JWT;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class JwtUtils {
    public static SecretKey generateSecretKey(String secret) {
        byte[] encodedSecretKey = secret.getBytes();
        return new SecretKeySpec(encodedSecretKey, 0, encodedSecretKey.length, "HmacSHA256");
    }
}
