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

import android.net.Uri;
import ru.elifantiev.yandex.YandexAPIService;
import ru.elifantiev.yandex.oauth.AccessToken;

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
