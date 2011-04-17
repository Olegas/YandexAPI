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

/**
 * Created by IntelliJ IDEA.
 * User: Olegas
 * Date: 17.04.11
 * Time: 5:11
 * To change this template use File | Settings | File Templates.
 */
abstract public class SerializeableFlagCollection {

    abstract protected String[] getFlagNames();
    abstract protected int getFlagMask();

    @Override
    public String toString() {
        String[] flagNames = getFlagNames();
        int mask = getFlagMask();
        StringBuilder serializedPermissions = new StringBuilder();

        for(int i = 0, l = flagNames.length; i < l; i++) {
            if((mask & (1 << i)) != 0) {
                if(serializedPermissions.length() > 0)
                    serializedPermissions.append(" ");
                serializedPermissions.append(flagNames[i]);
            }
        }

        return serializedPermissions.toString();
    }

}
