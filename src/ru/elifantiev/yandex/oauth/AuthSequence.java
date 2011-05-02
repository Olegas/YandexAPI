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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;


final public class AuthSequence {

    public static final String OAUTH_SCHEME = "oauth";
    private Uri.Builder serverUriBuilder;
    private final String appId;
    private final Uri server;

    private AuthSequence(Uri server, String appId) {
        this.server = server;
        this.serverUriBuilder = server.buildUpon();
        this.appId = appId;
        serverUriBuilder
                .path("/oauth/authorize")
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("redirect_uri", OAUTH_SCHEME + "://" + appId);
    }

    public static AuthSequence newInstance(Uri server, String appId) {
        return new AuthSequence(server, appId);
    }

    public AuthSequence start(String clientId, PermissionsScope scope, Context ctx) {
        serverUriBuilder.appendQueryParameter("scope", scope.toString());
        serverUriBuilder.appendQueryParameter("client_id", clientId);
        ctx.startActivity(new Intent(Intent.ACTION_VIEW).setData(serverUriBuilder.build()));
        return this;
    }

    public void continueSequence(Uri data, AsyncContinuationHandler continuator) {
        if(data != null && data.getScheme().equals(OAUTH_SCHEME) && data.getHost().equals(appId))
            continuator.execute(server, Uri.parse(OAUTH_SCHEME + "://" + appId), data);
    }

}
