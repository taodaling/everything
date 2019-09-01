package com.daltao.oj.tool;

import java.security.Permission;

public class OJSecurityManager extends SecurityManager {
    @Override
    public void checkPermission(Permission perm, Object context) {
        return;
    }

    @Override
    public void checkPermission(Permission perm) {
        return;
    }
}
