/*
 * Copyright 2011 Oleg Elifantiev
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.elifantiev.yandex.oauth.tokenstorage.impl;

import android.content.Context;
import ru.elifantiev.util.Base64Coder;
import ru.elifantiev.yandex.oauth.AccessToken;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;


public class EncryptedSharedPreferencesStorage extends SharedPreferencesStorage {

    private final String key;
    private final static int MIN_KEY_LEN = DESedeKeySpec.DES_EDE_KEY_LEN;

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
                key = Arrays.toString(newKey);
            }
        }
        return key;
    }

    @Override
    public AccessToken getToken(String tokenId) {
        String tokenValue = getTokenValue(tokenId);
        if(tokenValue.equals(""))
            return null;
        try {
            return new AccessToken(tryDecrypt(tokenValue, key));
        } catch (Exception e) {
            throw new EncryptedStorageException("Decryption failed", e);
        }
    }

    @Override
    public void storeToken(AccessToken token, String tokenId) {
        try {
            storeTokenValue(tokenId, tryEncrypt(token.toString(), key));
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
            throw new IllegalArgumentException("Key is "+byteKey.length+" bytes. Key must be exactly 24 bytes in length.");
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
            throw new IllegalArgumentException("Key is "+byteKey.length+" bytes. Key must be exactly 24 bytes in length.");
        }
        KeySpec ks = new DESedeKeySpec(byteKey);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DESede");
        SecretKey sk = skf.generateSecret(ks);
        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.ENCRYPT_MODE, sk);
        return new String(
                Base64Coder.encode(
                        cipher.doFinal(
                                inString.getBytes("UTF8"))));
    }
}
