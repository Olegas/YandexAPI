package ru.elifantiev.yandex_api.oauth;

import android.net.Uri;

public class TokenExtractor {

    public static String extractTemporaryToken(Uri data) {
        String token;
        token = data.getQueryParameter("code");
        if(token == null) {
            token = data.getQueryParameter("error");
            throw new OAuthException(token == null ? "Unknown error" : token);
        }
        return token;
    }


}
