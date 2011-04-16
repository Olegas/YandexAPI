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

package ru.elifantiev.yandex;

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
import ru.elifantiev.yandex.oauth.AccessToken;

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
