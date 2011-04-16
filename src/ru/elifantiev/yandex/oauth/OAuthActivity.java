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
import android.content.Intent;
import android.net.Uri;


abstract public class OAuthActivity extends Activity {

    public static final String EXTRA_AUTH_RESULT = "ru.elifantiev.yandex.oauth.AUTH_RESULT_EXTRA";
    public static final String EXTRA_AUTH_RESULT_ERROR = "ru.elifantiev.yandex.oauth.AUTH_RESULT_ERROR_EXTRA";
    public static final String ACTION_AUTH_RESULT = "ru.elifantiev.yandex.oauth.AUTH_RESULT";
    public static final int AUTH_RESULT_OK = 0;
    public static final int AUTH_RESULT_ERROR = 1;

    @Override
    protected void onResume() {
        Uri data = getIntent().getData();
        if (data != null) {
            AuthSequence.atServer(getServer()).forApp(getAppId()).continueSequence(data, getContinuationHandler());
        }
        else
            AuthSequence
                    .atServer(getServer())
                    .forApp(getAppId())
                    .forClient(getClientId())
                    .withScope(getRequiredPermissions())
                    .start(this);
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
    }

    abstract protected PermissionsScope getRequiredPermissions();
    abstract protected String getClientId();
    abstract protected String getAppId();
    abstract protected Uri getServer();
    abstract protected AccessTokenStorage getTokenStorage();

    protected AsyncContinuationHandler getContinuationHandler() {
        return new AsyncContinuationHandler(getClientId(), getDefaultStatusHandler());
    }

    protected AuthStatusHandler getDefaultStatusHandler() {
        return new AuthStatusHandler() {
            public void onResult(AuthResult result) {
                Intent callHome = new Intent(ACTION_AUTH_RESULT);
                if(result.isSuccess()) {
                    getTokenStorage().storeToken(result.getToken(), getAppId());
                    callHome.putExtra(EXTRA_AUTH_RESULT, AUTH_RESULT_OK);
                }
                else  {
                    callHome
                        .putExtra(EXTRA_AUTH_RESULT, AUTH_RESULT_ERROR)
                        .putExtra(EXTRA_AUTH_RESULT_ERROR, result.getError());
                }
                startActivity(callHome);
            }
        };
    }
}
