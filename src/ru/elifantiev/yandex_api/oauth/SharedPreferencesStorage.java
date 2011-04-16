package ru.elifantiev.yandex_api.oauth;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesStorage implements AccessTokenStorage {

    private SharedPreferences prefs;

    public SharedPreferencesStorage(Context ctx) {
        prefs = ctx.getSharedPreferences("tokenStorage", Context.MODE_PRIVATE);
    }

    public AccessToken getToken(String tokenId) {
        String sToken = prefs.getString(tokenId, "");
        if(sToken.equals(""))
            return null;
        else
            return new AccessToken(sToken);
    }

    public void storeToken(AccessToken token, String tokenId) {
        prefs.edit().putString(tokenId, token.toString()).commit();
    }
}
