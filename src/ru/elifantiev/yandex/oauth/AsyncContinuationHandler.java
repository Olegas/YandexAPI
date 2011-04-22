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

import android.net.Uri;
import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import ru.elifantiev.yandex.SSLHttpClientFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class AsyncContinuationHandler extends AsyncTask<Uri, Void, AuthResult> {

    private final String clientId;
    private AuthStatusHandler statusHandler;

    public AsyncContinuationHandler(String clientId, AuthStatusHandler statusHandler) {
        this.clientId = clientId;
        this.statusHandler = statusHandler;
    }

    @Override
    protected AuthResult doInBackground(Uri... params) {

        String code, error;

        if (params.length == 3) {
            // TODO: Add redir URL checking
            if ((code = params[2].getQueryParameter("code")) == null) {
                if ((error = params[2].getQueryParameter("error")) == null) {
                    return new AuthResult("Unknown error");
                } else {
                    return new AuthResult(error);
                }
            }
        } else
            return new AuthResult("Wrong parameters count");

        HttpClient client = SSLHttpClientFactory.getNewHttpClient();
        HttpPost method = new HttpPost(
                params[0].buildUpon()
                        .path("/oauth/token").build().toString());

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("code", code));
        nameValuePairs.add(new BasicNameValuePair("client_id", clientId));
        nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
        nameValuePairs.add(new BasicNameValuePair("redirect_uri", params[1].toString()));
        try {
            method.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            return new AuthResult(e.getMessage());
        }

        String token = null;
        try {
            HttpResponse httpResponse = client.execute(method);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200 || statusCode == 400) {
                JSONObject response = (JSONObject)
                        (new JSONTokener(EntityUtils.toString(httpResponse.getEntity())).nextValue());
                if (response.has("access_token"))
                    token = response.getString("access_token");
                else if (response.has("error"))
                    return new AuthResult(response.getString("error"));
            }
            else
                return new AuthResult("Call failed. Returned HTTP response code " + String.valueOf(statusCode));
        } catch (IOException e) {
            return new AuthResult(e.getMessage());
        } catch (JSONException e) {
            return new AuthResult(e.getMessage());
        }

        return new AuthResult(new AccessToken(token));
    }

    @Override
    protected void onPostExecute(AuthResult authResult) {
        statusHandler.onResult(authResult);
    }
}