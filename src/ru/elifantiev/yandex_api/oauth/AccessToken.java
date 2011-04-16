package ru.elifantiev.yandex_api.oauth;


public class AccessToken {

    private final String token;

    public AccessToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return token;
    }
}
