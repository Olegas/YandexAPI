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