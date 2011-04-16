package ru.elifantiev.yandex_api.oauth;

import android.net.Uri;
import android.os.AsyncTask;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.*;
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

        HttpClient client = new DefaultHttpClient();
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

        KeyStore trustStore  = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        SSLSocketFactory socketFactory = null;
        try {
            socketFactory = new SSLSocketFactory(trustStore);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        Scheme sch = new Scheme("https", socketFactory, 443);
        client.getConnectionManager().getSchemeRegistry().register(sch);


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