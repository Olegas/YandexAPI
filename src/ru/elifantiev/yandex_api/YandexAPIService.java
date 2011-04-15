package ru.elifantiev.yandex_api;

import android.net.Uri;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import ru.elifantiev.yandex_api.oauth.AccessToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.*;
import java.util.Map;

abstract public class YandexAPIService {

    private final AccessToken token;

    public YandexAPIService(AccessToken token) {

        this.token = token;
    }

    abstract protected Uri getServiceUri();

    protected JSONObject callMethod(String methodName, Map<String, String> params) {

        Uri.Builder builder = getServiceUri().buildUpon();
        builder
                .scheme("https")
                .appendPath("api")
                .appendPath(methodName).build();
        for(String param : params.keySet()) {
            builder.appendQueryParameter(param,  params.get(param));
        }

        HttpClient client = new DefaultHttpClient();
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

        HttpPost method = new HttpPost(builder.build().toString());

        method.setHeader(new BasicHeader("Authorization", "Bearer " + token.toString()));

        try {
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(client.execute(method).getEntity().getContent()), 8192);
            while ((line = reader.readLine()) != null)
                responseBuilder.append(line);
            reader.close();

            return (JSONObject)new JSONTokener(responseBuilder.toString()).nextValue();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


}
