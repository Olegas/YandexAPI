package ru.elifantiev.yandex_api;

import android.net.Uri;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import ru.elifantiev.yandex_api.oauth.AccessToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// TODO: Add error handling
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

        HttpClient client = SSLHttpClientFactory.getNewHttpClient();
        HttpPost method = new HttpPost(builder.build().toString());

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(params.size());
        for(String param : params.keySet()) {
            nameValuePairs.add(new BasicNameValuePair(param, params.get(param)));
        }
        try {
            method.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

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
