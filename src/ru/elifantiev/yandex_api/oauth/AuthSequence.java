package ru.elifantiev.yandex_api.oauth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;


public class AuthSequence {

    public static final String OAUTH_SCHEME = "oauth";
    private Uri.Builder serverUriBuilder;
    private String appId;
    private final Uri server;

    private AuthSequence(Uri server) {
        this.server = server;
        this.serverUriBuilder = server.buildUpon();
        serverUriBuilder
                .path("/oauth/authorize")
                .appendQueryParameter("response_type", "code");
    }

    public static AuthSequence atServer(Uri server) {
        return new AuthSequence(server);
    }

    public AuthSequence forApp(String appId) {
        this.appId = appId;
        serverUriBuilder.appendQueryParameter("redirect_uri", OAUTH_SCHEME + "://" + appId);
        return this;
    }

    public AuthSequence withScope(String scope) {
        serverUriBuilder.appendQueryParameter("scope", scope);
        return this;
    }

    public AuthSequence forClient(String client) {
        serverUriBuilder.appendQueryParameter("client_id", client);
        return this;
    }

    public AuthSequence start(Context ctx) {
        ctx.startActivity(new Intent(Intent.ACTION_VIEW).setData(serverUriBuilder.build()));
        return this;
    }

    public void continueSequence(Uri data, AsyncContinuationHandler continuator) {
        if(appId != null && server != null)
            if(data != null && data.getScheme().equals(OAUTH_SCHEME) && data.getHost().equals(appId))
                continuator.execute(server, Uri.parse(OAUTH_SCHEME + "://" + appId), data);
    }

}
