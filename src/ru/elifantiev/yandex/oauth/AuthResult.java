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

public class AuthResult {


    private final AccessToken token;
    private final String error;

    AuthResult(AccessToken token) {
        this.token = token;
        this.error = null;
    }

    AuthResult(String error) {
        this.token = null;
        this.error = error;
    }

    public boolean isSuccess() {
        return token != null;
    }

    public AccessToken getToken() {
        return token;
    }

    public String getError() {
        return error;
    }

}