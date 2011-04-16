package ru.elifantiev.yandex.oauth;

/**
 * Created by IntelliJ IDEA.
 * User: Olegas
 * Date: 15.04.11
 * Time: 23:12
 * To change this template use File | Settings | File Templates.
 */
public class OAuthException extends RuntimeException {
    public OAuthException(String message) {
        super(message);
    }
}
