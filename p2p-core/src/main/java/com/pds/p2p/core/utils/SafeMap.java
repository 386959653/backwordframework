package com.pds.p2p.core.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class SafeMap extends HashMap<String, Object> {

    /**
     *
     */
    private static final long serialVersionUID = -7958031299949396923L;

    public SafeMap(int arg0, float arg1) {
        super(arg0, arg1);
    }

    public SafeMap(int arg0) {
        super(arg0);
    }

    public SafeMap(Map<? extends String, ? extends Object> arg0) {
        super(arg0);
    }

    public SafeMap() {
    }

    public Object get(java.lang.Object key) {
        Object o = super.get(key);
        if (o == null) {
            return StringUtils.EMPTY;
        }
        return o;
    }

}
