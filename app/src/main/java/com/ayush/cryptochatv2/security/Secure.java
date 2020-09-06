package com.ayush.cryptochatv2.security;


import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Secure {

    private static AlgorithmParameterSpec spec;
    private static Cipher cipher;
    private static SecretKeySpec key;

    private static void initialize(String sharedKey) throws NoSuchAlgorithmException, NoSuchPaddingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(sharedKey.getBytes(StandardCharsets.UTF_8));
        byte[] keyBytes = new byte[32];
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
        cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING");
        key = new SecretKeySpec(keyBytes, "AES");
        spec = getIV();
    }

    private static AlgorithmParameterSpec getIV() {
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, };
        return new IvParameterSpec(iv);
    }

    public static String encrypt(String message, String sharedKey) throws InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
        initialize(sharedKey);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encrypted = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.encode(encrypted, Base64.DEFAULT), StandardCharsets.UTF_8);
    }

    public static String decrypt(String message, String sharedKey) throws InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
        initialize(sharedKey);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] bytes = Base64.decode(message, Base64.DEFAULT);
        byte[] decrypted = cipher.doFinal(bytes);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
