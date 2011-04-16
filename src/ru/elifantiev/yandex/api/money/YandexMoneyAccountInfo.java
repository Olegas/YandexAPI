package ru.elifantiev.yandex.api.money;

import org.json.JSONException;
import org.json.JSONObject;
import ru.elifantiev.yandex.FormatException;


public class YandexMoneyAccountInfo {

    private String account;
    private double balance;
    private String currency;

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
}
