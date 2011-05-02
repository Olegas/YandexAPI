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
import android.content.SharedPreferences;
import ru.elifantiev.yandex.oauth.AccessToken;
import ru.elifantiev.yandex.oauth.tokenstorage.AccessTokenStorage;

public class SharedPreferencesStorage implements AccessTokenStorage {

    private SharedPreferences prefs;

    public SharedPreferencesStorage(Context ctx) {
        prefs = ctx.getSharedPreferences("tokenStorage", Context.MODE_PRIVATE);
    }

    protected String getTokenValue(String tokenId) {
        return prefs.getString(tokenId, "");
    }

    protected void storeTokenValue(String tokenId, String tokenValue) {
        prefs.edit().putString(tokenId, tokenValue).commit();
    }

    public AccessToken getToken(String tokenId) {
        String sToken = getTokenValue(tokenId);
        return sToken.equals("") ? null : new AccessToken(sToken);
    }

    public void storeToken(AccessToken token, String tokenId) {
        storeTokenValue(tokenId, token.toString());
    }

    public void removeToken(String tokenId) {
        prefs.edit().remove(tokenId).commit();
    }

    public void clearStorage() {
        prefs.edit().clear().commit();
    }
}
