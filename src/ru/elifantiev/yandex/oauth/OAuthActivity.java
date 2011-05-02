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

package ru.elifantiev.yandex.oauth;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import ru.elifantiev.yandex.oauth.tokenstorage.AccessTokenStorage;


abstract public class OAuthActivity extends Activity {

    /**
     * Intent's Extra holding auth result
     */
    public static final String EXTRA_AUTH_RESULT = "ru.elifantiev.yandex.oauth.AUTH_RESULT_EXTRA";

    /**
     * Intent's Extra holding error message (in case of error)
     */

    public static final String EXTRA_AUTH_RESULT_ERROR = "ru.elifantiev.yandex.oauth.AUTH_RESULT_ERROR_EXTRA";

    /**
     * An action, which will be used to start activity, which must handle an authentication result.
     * Must not be the same, as OAuthActivity.
     * Intent's data field contains Uri 'oauth://{appId}' where appId is application Id returned with getAppId method.
     */
    public static final String ACTION_AUTH_RESULT = "ru.elifantiev.yandex.oauth.AUTH_RESULT";
    public static final int AUTH_RESULT_OK = 0;
    public static final int AUTH_RESULT_ERROR = 1;

    @Override
    protected void onResume() {
        authorize();
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected void authorize() {
        Uri data = getIntent().getData();
        if (data != null) {
            AuthSequence
                    .newInstance(getServer(), getAppId())
                    .continueSequence(data, getContinuationHandler());
        }
        else
            AuthSequence
                    .newInstance(getServer(), getAppId())
                    .start(getClientId(), getRequiredPermissions(), this);
    }

    /**
     * This method must return PermissionsScope object declaring permission, required to current application
     * @return PermissionsScope instance
     */
    abstract protected PermissionsScope getRequiredPermissions();

    /**
     * Client ID by OAuth specification
     * @return OAuth client ID
     */
    abstract protected String getClientId();

    /**
     * This method must return an unique string ID of an application
     * It is usually an application's package name
     * This will be used to separate different application OAuth calls and token storage
     * @return Unique ID of calling application
     */
    abstract protected String getAppId();

    /**
     * Method to declare a server, which will handle OAuth calls
     * @return URL of target server
     */
    abstract protected Uri getServer();

    /**
     * Implementation of AccessTokenStorage to use for store application's access token
     * Now implemented:
     *  - SharedPreferencesStorage - uses shared preferences to store token
     *  - EncryptedSharedPreferencesStorage - uses shared preferences but token is encrypted with 3DES and user-supplied key
     * @return Token storage to use
     */
    abstract protected AccessTokenStorage getTokenStorage();

    protected AsyncContinuationHandler getContinuationHandler() {
        return new AsyncContinuationHandler(getClientId(), getDefaultStatusHandler());
    }

    protected AuthStatusHandler getDefaultStatusHandler() {
        return new AuthStatusHandler() {
            public void onResult(AuthResult result) {
                Intent callHome = new Intent(ACTION_AUTH_RESULT);
                callHome.setData(Uri.parse(AuthSequence.OAUTH_SCHEME + "://" + getAppId()));
                if(result.isSuccess()) {
                    getTokenStorage().storeToken(result.getToken(), getAppId());
                    callHome.putExtra(EXTRA_AUTH_RESULT, AUTH_RESULT_OK);
                }
                else  {
                    callHome
                        .putExtra(EXTRA_AUTH_RESULT, AUTH_RESULT_ERROR)
                        .putExtra(EXTRA_AUTH_RESULT_ERROR, result.getError());
                }
                try {
                    startActivity(callHome);
                } catch (ActivityNotFoundException e) {
                    // ignore
                }
                finish();
            }
        };
    }
}
