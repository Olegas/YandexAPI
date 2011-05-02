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


public class YandexMoneyAccountInfo {

    private final String account;
    private final double balance;
    private final String currency;

    YandexMoneyAccountInfo(JSONObject response) {
        try {
            account = response.getString("account");
            balance = response.getDouble("balance");
            currency = response.getString("currency");
        } catch (JSONException e) {
            throw new FormatException(e.getMessage());
        }
    }

    public String getAccount() {
        return account;
    }

    public double getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return String.format("Account: %s, Balance: %.2f", account, balance);
    }
}
