/**
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.pds.p2p.system.i18n;

import java.util.Locale;

/**
 * @author wuyunfeng
 */
public class LangEnv {
    private static final ThreadLocal<Locale> threadLocal = new ThreadLocal<Locale>();

    public static void remove() {
        threadLocal.remove();
    }

    public static void setLang(Locale locale) {
        threadLocal.set(locale);
    }

    public static Locale getLang() {
        return threadLocal.get();
    }
}
