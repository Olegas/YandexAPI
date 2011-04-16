package ru.elifantiev.yandex_api.oauth;

import android.app.Activity;
import android.net.Uri;
import android.widget.Toast;


abstract public class OAuthActivity extends Activity {

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

    protected AsyncContinuationHandler getContinuationHandler() {
        return new AsyncContinuationHandler(getClientId(), getDefaultStatusHandler());
    }

    protected AuthStatusHandler getDefaultStatusHandler() {
        return new AuthStatusHandler() {
            public void onResult(AuthResult result) {
                if(result.isSuccess())
                    onAuthorizationGranted(result.getToken());
                else
                    Toast.makeText(OAuthActivity.this, result.getError(), Toast.LENGTH_LONG).show();
            }
        };
    }

}
