package com.pds.p2p.system.redis;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RedisUtil {

    private static Logger logger = LogManager.getLogger(RedisUtil.class);

    public static String GROUP_LOCALE_KEY = "_LOCALE_";

    public static Locale getLocale(String userName) {
        if (StringUtils.isEmpty(userName)) {
            return null;
        }
        String localeString = JedisUtil.hgetToObject(GROUP_LOCALE_KEY, userName, String.class);
        if (StringUtils.isEmpty(localeString)) {
            return null;
        }

        Locale locale = org.springframework.util.StringUtils.parseLocaleString(localeString);

        return locale;
    }

    public static void setLocale(String userName, Locale locale) {

        if (StringUtils.isNotEmpty(userName)) {
            JedisUtil.hset(GROUP_LOCALE_KEY, userName, locale.toString());
        }
    }
}
