package ru.elifantiev.yandex_api.money;

import android.net.Uri;
import ru.elifantiev.yandex_api.YandexAPIService;
import ru.elifantiev.yandex_api.oauth.AccessToken;

import java.util.HashMap;

public class YandexMoneyService extends YandexAPIService {

    public YandexMoneyService(AccessToken token) {
        super(token);
    }

    @Override
    protected Uri getServiceUri() {
        return Uri.parse("https://money.yandex.ru");
    }

    public YandexMoneyAccountInfo getAccountInfo() {
        return new YandexMoneyAccountInfo(callMethod("account-info", new HashMap<String, String>()));
    }
}
