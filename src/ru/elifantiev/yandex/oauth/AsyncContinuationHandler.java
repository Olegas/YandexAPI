package ru.elifantiev.yandex.oauth;

import android.net.Uri;
import android.os.AsyncTask;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import ru.elifantiev.yandex.SSLHttpClientFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        }
        else
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
                return new AuthResult(response.getString("error"));

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