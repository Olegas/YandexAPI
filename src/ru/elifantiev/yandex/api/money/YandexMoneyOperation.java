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

import org.json.JSONException;
import org.json.JSONObject;
import ru.elifantiev.yandex.FormatException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class YandexMoneyOperation {

    public static final int DIRECTION_UNKNOWN = 0;
    public static final int DIRECTION_IN = 1;
    public static final int DIRECTION_OUT = 2;

    private final int id;
    private int pattern_id = 0;
    private final Date date;
    private final String title;
    private int direction = DIRECTION_UNKNOWN;
    private double amount = 0.0;

    YandexMoneyOperation(JSONObject operation) {
        try {
            id = operation.getInt("operation_id");
            title = operation.getString("title");
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(operation.getString("datetime"));
        } catch (JSONException e) {
            throw new FormatException("Parsing failed", e);
        } catch (ParseException e) {
            throw new FormatException("Parsing failed", e);
        }

        try {
            pattern_id = Integer.parseInt(operation.getString("pattern_id"));
        } catch (JSONException e) {
            // ignore
        }

        try {
            amount = operation.getDouble("amount");
        } catch (JSONException e) {
            // ignore
        }

        try {
            direction = operation.getString("direction").equals("in") ? DIRECTION_IN : DIRECTION_OUT;
        } catch (JSONException e) {
            // ignore
        }
    }

    public int getId() {
        return id;
    }

    public int getPattern_id() {
        return pattern_id;
    }

    public Date getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public int getDirection() {
        return direction;
    }

    public double getAmount() {
        return amount;
    }
}
