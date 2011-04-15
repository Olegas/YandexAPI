package ru.elifantiev.yandex_api.oauth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;


public class AuthSequence {

    public static final String OAUTH_SCHEME = "oauth";
    public static final String OAUTH_HOST = "oauth.yandex";
    public static Uri REDIRECT_URI = Uri.parse(OAUTH_SCHEME + "://" + OAUTH_HOST);
    private Uri.Builder serverUriBuilder;
    private final Uri server;

    private AuthSequence(Uri server) {
        this.server = server;
        this.serverUriBuilder = server.buildUpon();
        serverUriBuilder
                .path("/oauth/authorize")
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("redirect_uri", REDIRECT_URI.toString());
    }

    public static AuthSequence atServer(Uri server) {
        return new AuthSequence(server);
    }

    public AuthSequence forScope(String scope) {
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
        if(data != null && data.getScheme().equals(OAUTH_SCHEME) && data.getHost().equals(OAUTH_HOST))
            continuator.execute(server, data);
    }

}
