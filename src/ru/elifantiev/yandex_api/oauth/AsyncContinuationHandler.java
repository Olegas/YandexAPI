package ru.elifantiev.yandex_api.oauth;

import android.net.Uri;
import android.os.AsyncTask;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class AsyncContinuationHandler extends AsyncTask<Uri, Void, String> {

    private final String clientId;
    private AuthStatusHandler statusHandler;

    public AsyncContinuationHandler(String clientId, AuthStatusHandler statusHandler) {
        this.clientId = clientId;
        this.statusHandler = statusHandler;
    }

    @Override
    protected String doInBackground(Uri... params) {

        String code = null, error;

        if (params.length == 2) {
            // TODO: Add redir URL checking
            if ((code = params[1].getQueryParameter("code")) == null) {
                if ((error = params[1].getQueryParameter("error")) == null) {
                    statusHandler.onError("Unknown error");
                } else {
                    statusHandler.onError(error);
                }
            }
        }
        else
            statusHandler.onError("Wrong parameters count");

        if (code == null)
            return null;

        HttpClient client = new DefaultHttpClient();
        HttpPost method = new HttpPost(
                params[0].buildUpon()
                        .path("/oauth/token").build().toString());

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("code", code));
        nameValuePairs.add(new BasicNameValuePair("client_id", clientId));
        nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
        nameValuePairs.add(new BasicNameValuePair("redirect_uri", AuthSequence.REDIRECT_URI.toString()));
        try {
            method.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            statusHandler.onError(e.getMessage());
            return null;
        }

        StringBuilder responseBuilder;
        String token = null;
        try {
            responseBuilder = new StringBuilder();
            String line;
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(client.execute(method).getEntity().getContent()), 8192);
            while ((line = reader.readLine()) != null)
                responseBuilder.append(line);
            reader.close();

            String dataRead = responseBuilder.toString();

            JSONObject response = (JSONObject) (new JSONTokener(dataRead).nextValue());

            if (response.has("access_token"))
                token = response.getString("access_token");
            else if (response.has("error"))
                statusHandler.onError(response.getString("error"));

        } catch (IOException e) {
            statusHandler.onError(e.getMessage());
        } catch (JSONException e) {
            statusHandler.onError(e.getMessage());
        }

        return token;
    }

    @Override
    protected void onPostExecute(String accessToken) {
        if (accessToken != null)
            statusHandler.onSuccess(new AccessToken(accessToken));
    }
}