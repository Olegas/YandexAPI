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
