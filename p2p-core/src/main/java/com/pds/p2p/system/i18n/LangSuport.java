/**
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.pds.p2p.system.i18n;

/**
 * @author v_zoupengfei
 */
public enum LangSuport {
    EN_US("en_US"),
    ZH_CN("zh_CN");

    /**
     * @return the lang
     */
    public String getLang() {
        return lang;
    }

    /**
     * @param lang the lang to set
     */
    public void setLang(String lang) {
        this.lang = lang;
    }

    private String lang;

    /**
     * @param lang
     */
    LangSuport(String lang) {
        this.lang = lang;
    }

}
