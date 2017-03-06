/**
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.pds.p2p.system.web.session;

import com.pds.p2p.system.redis.JedisUtil;

/**
 * @author v_zoupengfei
 */
public class LoginSessionFlag {
    private static String SESSION_FLAG_PREFFIX = "LOGIN_SESSION_FLAG";

    public static void login(String userName) {
        JedisUtil.hset(SESSION_FLAG_PREFFIX, userName, true);
    }

    public static void logout(String userName) {
        JedisUtil.hset(SESSION_FLAG_PREFFIX, userName, false);
    }

    public static boolean checkLoginFlag(String userName) {
        Object flag = JedisUtil.hget(SESSION_FLAG_PREFFIX, userName);
        if (flag == null) {
            return false;
        } else {
            return (Boolean) flag;
        }
    }

}
