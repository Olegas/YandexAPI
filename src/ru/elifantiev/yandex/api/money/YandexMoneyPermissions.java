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

import ru.elifantiev.yandex.api.SerializeableFlagCollection;
import ru.elifantiev.yandex.oauth.PermissionsScope;

public class YandexMoneyPermissions extends SerializeableFlagCollection implements PermissionsScope {

    public final static int PERMISSION_ACCOUNT_INFO = 1;
    public final static int PERMISSION_OPERATION_HISTORY = 2;
    public final static int PERMISSION_OPERATION_DETAILS = 4;
    public final static int PERMISSION_PAYMENT_SHOP = 8;

    private final String[] permNames
            = new String[] { "account-info", "operation-history", "operation-details", "payment-shop" };
    public int permissions;

    public YandexMoneyPermissions(int permissions) {
        this.permissions = permissions;
    }

    @Override
    protected String[] getFlagNames() {
        return permNames;
    }

    @Override
    protected int getFlagMask() {
        return permissions;
    }
}
