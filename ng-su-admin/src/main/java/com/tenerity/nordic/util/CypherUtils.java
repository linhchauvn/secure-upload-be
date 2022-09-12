package com.tenerity.nordic.util;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CypherUtils {
    private static final String algo = "AES/CBC/PKCS5Padding";
    private static final String charsetName = "UTF-8";

    public static String rsaEncryptWithIvKey(String value, String key, String initVector) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(charsetName));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(charsetName), "AES");

            Cipher cipher = Cipher.getInstance(algo);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String rsaDecryptWithIvKey(String encrypted, String key, String initVector) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(charsetName));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(charsetName), "AES");

            Cipher cipher = Cipher.getInstance(algo);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
