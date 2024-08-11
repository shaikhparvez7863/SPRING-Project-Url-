package com.CRUD.Util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.DecoderException;

import java.nio.charset.StandardCharsets;

public class AES256Util {

    private static final String AES = "AES";
    private static final String AES_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY = "0123456789abcdef0123456789abcdef"; // Your secret key in hexadecimal format

    private static SecretKeySpec getKey() throws Exception {
        byte[] keyBytes;
        try {
            keyBytes = Hex.decodeHex(SECRET_KEY);
        } catch (DecoderException e) {
            throw new Exception("Invalid secret key format", e);
        }
        return new SecretKeySpec(keyBytes, AES);
    }

    public static String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getKey());
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(encryptedBytes);
    }

    public static String decrypt(String hexData) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getKey());
        byte[] encryptedBytes;
        try {
            encryptedBytes = Hex.decodeHex(hexData);
        } catch (DecoderException e) {
            throw new Exception("Invalid hex data format", e);
        }
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static String objectToJson(Object object) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    public static <T> T jsonToObject(String json, Class<T> clazz) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }
}
