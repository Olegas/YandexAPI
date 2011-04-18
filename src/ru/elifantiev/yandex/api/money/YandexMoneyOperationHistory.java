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

package ru.elifantiev.yandex.api.money;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.elifantiev.yandex.FormatException;
import ru.elifantiev.yandex.MethodCallException;

import java.util.ArrayList;

public class YandexMoneyOperationHistory extends ArrayList<YandexMoneyOperation> {

    int nextRecord = 0;

    YandexMoneyOperationHistory(JSONObject response) {

        if(response.has("error")) {
            String error = "";
            try {
                error = response.getString("error");
            } catch (JSONException e) {
                throw new FormatException("Error parsing response", e);
            }
            if(!error.equals(""))
                throw new MethodCallException(error);
        }


        try {
            nextRecord = response.getInt("next_record");
        } catch (JSONException e) {
            // ignore
        }

        try {
            JSONArray operations = response.getJSONArray("operations");
            for(int i = 0, l = operations.length(); i < l; i++) {
                add(new YandexMoneyOperation(operations.getJSONObject(i)));
            }
        } catch (JSONException e) {
            throw new FormatException("Response persing feailed", e);
        }
    }

}
