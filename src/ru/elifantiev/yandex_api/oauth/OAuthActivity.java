package ru.elifantiev.yandex_api.oauth;

import android.app.Activity;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;


abstract public class OAuthActivity extends Activity {

    @Override
    protected void onResume() {
        Uri data = getIntent().getData();
        if (data != null) {
            AuthSequence.atServer(getServer()).continueSequence(data, getContinuationHandler());
        }
        else
            AuthSequence
                    .atServer(getServer())
                    .forClient(getClientId())
                    .forScope("account-info")
                    .start(this);
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
    }

    abstract protected String getClientId();
    abstract protected Uri getServer();
    abstract protected void onAuthorizationGranted(AccessToken token);

    protected AsyncContinuationHandler getContinuationHandler() {
        return new AsyncContinuationHandler(getClientId(), getDefaultStatusHandler());
    }

    protected AuthStatusHandler getDefaultStatusHandler() {
        final Handler handler = new Handler();
        return new AuthStatusHandler() {
            public void onSuccess(final AccessToken token) {
                handler.post(new Runnable(){
                    public void run() {
                        onAuthorizationGranted(token);
                    }
                });
            }

            public void onError(final String message) {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(OAuthActivity.this, message, Toast.LENGTH_LONG);
                    }
                });
            }
        };
    }

}
