package ru.elifantiev.yandex_api.oauth;

public interface AuthStatusHandler {
    public void onSuccess(AccessToken token);

    public void onError(String message);
}