package ru.elifantiev.yandex.api.money;

import ru.elifantiev.yandex.oauth.PermissionsScope;

public class YandexMoneyPermissions extends PermissionsScope {

    public final static int PERMISSION_ACCOUNT_INFO = 1;
    public final static int PERMISSION_OPERATION_HISTORY = 2;
    public final static int PERMISSION_OPERATION_DETAILS = 4;
    public final static int PERMISSION_PAYMENT_SHOP = 8;

    private final String[] permNames
            = new String[] { "account-info", "operation-history", "operation-details", "payment-shop" };
    public int permissions;

    public YandexMoneyPermissions(int permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        StringBuilder serializedPermissions = new StringBuilder();

        for(int i = 0, l = permNames.length; i < l; i++) {
            if((permissions & (1 << i)) != 0) {
                if(serializedPermissions.length() > 0)
                    serializedPermissions.append(" ");
                serializedPermissions.append(permNames[i]);
            }
        }

        return serializedPermissions.toString();
    }
}
