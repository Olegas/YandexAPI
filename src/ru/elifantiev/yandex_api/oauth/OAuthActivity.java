package ru.elifantiev.yandex_api.oauth;

import android.app.Activity;
import android.net.Uri;


abstract public class OAuthActivity extends Activity {

    public static final String ACCESS_TOKEN_EXTRA = "ru.elifantiev.oauth.ACCESS_TOKEN";
    public static final String ACCESS_ERROR_EXTRA = "ru.elifantiev.oauth.ACCESS_ERROR";

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
    abstract protected void onAuthorizationGranted(AccessToken token);
    abstract protected void onAuthorizationFailed(String reason);
    abstract protected AccessTokenStorage getTokenStorage();

    protected AsyncContinuationHandler getContinuationHandler() {
        return new AsyncContinuationHandler(getClientId(), getDefaultStatusHandler());
    }

    protected AuthStatusHandler getDefaultStatusHandler() {
        return new AuthStatusHandler() {
            public void onResult(AuthResult result) {
                if(result.isSuccess()) {
                    getTokenStorage().storeToken(result.getToken(), getAppId());
                    onAuthorizationGranted(result.getToken());
                }
                else
                    onAuthorizationFailed(result.getError());
            }
        };
    }

}
