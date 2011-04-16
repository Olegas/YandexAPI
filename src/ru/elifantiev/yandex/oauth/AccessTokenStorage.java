package ru.elifantiev.yandex.oauth;

/**
 * Created by IntelliJ IDEA.
 * User: Olegas
 * Date: 16.04.11
 * Time: 18:45
 * To change this template use File | Settings | File Templates.
 */
public interface AccessTokenStorage {

    public AccessToken getToken(String tokenId);
    public void storeToken(AccessToken token, String tokenId);

}
