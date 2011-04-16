package ru.elifantiev.yandex.oauth;

import android.content.Context;
import ru.elifantiev.util.Base64Coder;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;


public class EncryptedSharedPreferencesStorage extends SharedPreferencesStorage {

    private final String key;
    private final int MIN_KEY_LEN = DESedeKeySpec.DES_EDE_KEY_LEN;

    public EncryptedSharedPreferencesStorage(String key, Context ctx) {
        super(ctx);
        this.key = buildSuitableKey(key);
    }

    private String buildSuitableKey(String key) {
        if(key.length() < MIN_KEY_LEN) {
            StringBuilder hash = new StringBuilder();
            try {
                MessageDigest mDigest = MessageDigest.getInstance("MD5");
                mDigest.update(key.getBytes());
                byte d[] = mDigest.digest();

                for (byte aD : d) {
                    hash.append(Integer.toHexString(0xFF & aD));
                }
                key = hash.toString().substring(0, 24);
            } catch (NoSuchAlgorithmException e) {
                byte[] newKey = new byte[MIN_KEY_LEN];
                byte[] oldKey = key.getBytes();
                System.arraycopy(oldKey, 0, newKey, 0, Math.min(oldKey.length, MIN_KEY_LEN));
                key = newKey.toString();
            }
        }
        return key;
    }

    @Override
    public AccessToken getToken(String tokenId) {
        AccessToken encToken = super.getToken(tokenId);
        if(encToken == null)
            return null;
        try {
            return new AccessToken(tryDecrypt(encToken.toString(), key));
        } catch (Exception e) {
            throw new EncryptedStorageException("Decryption failed", e);
        }
    }

    @Override
    public void storeToken(AccessToken token, String tokenId) {
        try {
            super.storeToken(new AccessToken(tryEncrypt(token.toString(), key)), tokenId);
        } catch (Exception e) {
            throw new EncryptedStorageException("Encryption failed", e);
        }
    }

    private String tryDecrypt(String inString, String key) throws
            UnsupportedEncodingException,
            InvalidKeyException,
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            BadPaddingException {

        byte[] byteKey = key.getBytes("UTF8");
        if (byteKey.length != 24) {
            throw new KeyLengthException("Key is "+byteKey.length+" bytes. Key must be exactly 24 bytes in length.");
        }
        SecretKey sk = SecretKeyFactory.getInstance("DESede").generateSecret(new DESedeKeySpec(byteKey));
        Cipher cph = Cipher.getInstance("DESede");
        cph.init(Cipher.DECRYPT_MODE, sk);
        return new String(cph.doFinal(Base64Coder.decode(inString)));
    }

    private String tryEncrypt(String inString, String key) throws
            UnsupportedEncodingException,
            InvalidKeyException,
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            BadPaddingException {

        byte[] byteKey = key.getBytes("UTF8");
        if (byteKey.length != 24) {
            throw new KeyLengthException("Key is "+byteKey.length+" bytes. Key must be exactly 24 bytes in length.");
        }
        KeySpec ks = new DESedeKeySpec(byteKey);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DESede");
        SecretKey sk = skf.generateSecret(ks);
        Cipher cph = Cipher.getInstance("DESede");
        cph.init(Cipher.ENCRYPT_MODE, sk);
        byte[] r = cph.doFinal(inString.getBytes("UTF8"));
        char[] x = Base64Coder.encode(r);
        return new String(x);
    }
}
