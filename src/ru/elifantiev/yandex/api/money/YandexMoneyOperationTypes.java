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

public class YandexMoneyOperationTypes extends SerializeableFlagCollection {

    public static final int OPERATION_PAYMENT = 1;
    public static final int OPERATION_DEPOSITION = 2;

    public int operation_types = 0;

    public YandexMoneyOperationTypes(int operation_types) {
        this.operation_types = operation_types;
    }

    @Override
    protected String[] getFlagNames() {
        return new String[] { "payment", "deposition" };
    }

    @Override
    protected int getFlagMask() {
        return operation_types;
    }
}
