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

package ru.elifantiev.yandex.oauth;

/**
 * Created by IntelliJ IDEA.
 * User: Olegas
 * Date: 16.04.11
 * Time: 19:18
 * To change this template use File | Settings | File Templates.
 */
public class KeyLengthException extends RuntimeException {
    public KeyLengthException(String detailMessage) {
        super(detailMessage);
    }
}
