package ru.elifantiev.yandex_api.oauth;

/**
 * Created by IntelliJ IDEA.
 * User: Olegas
 * Date: 16.04.11
 * Time: 19:21
 * To change this template use File | Settings | File Templates.
 */
public class EncryptedStorageException extends RuntimeException {
    public EncryptedStorageException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
