package com.pds.p2p.core.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class CamelCaseKeyMap<V> extends LinkedHashMap<String, V> {
    public static void main(String[] args) {
        System.out.println(Number.class.isAssignableFrom(Long.class));
    }

    /**
     *
     */
    private static final long serialVersionUID = -4380802260759119767L;

    private StringBuilder camelCaseNameKeyBuff;

    private boolean isCamelCaseNameKey;

    public CamelCaseKeyMap() {
        this(true);
    }

    public CamelCaseKeyMap(boolean isCamelCaseNameKey) {
        this.isCamelCaseNameKey = isCamelCaseNameKey;
        if (isCamelCaseNameKey) {
            camelCaseNameKeyBuff = new StringBuilder(15);
        }
    }

    public CamelCaseKeyMap(Map<? extends String, ? extends V> m) {
        for (Map.Entry<? extends String, ? extends V> ent : m.entrySet()) {
            this.put(ent.getKey(), ent.getValue());
        }
        this.isCamelCaseNameKey = true;
    }

    @Override
    public V put(String key, V value) {
        if (isCamelCaseNameKey) {
            if (!StringUtils.isCamelCase(key)) {
                StringUtils.camelCaseName((String) key, camelCaseNameKeyBuff);
                return super.put(camelCaseNameKeyBuff.toString(), value);
            } else {
                return super.put(key, value);
            }
        } else {
            return super.put(key, value);
        }
    }

    /***
     * 如果没有获得，看key是否可以转换为驼峰，然后再调用父get方法
     */
    @Override
    public V get(Object key) {
        V ret = super.get(key);
        if (ret == null && !StringUtils.isCamelCase((String) key)) {
            StringUtils.camelCaseName((String) key, camelCaseNameKeyBuff);
            key = camelCaseNameKeyBuff.toString();
            return super.get(key);
        } else {
            return ret;
        }
    }

    @Override
    public boolean containsKey(Object key) {
        boolean ret = super.containsKey(key);
        if (!ret && !StringUtils.isCamelCase((String) key)) {
            StringUtils.camelCaseName((String) key, camelCaseNameKeyBuff);
            return super.containsKey(camelCaseNameKeyBuff.toString());
        }
        return ret;
    }

    public Object putNoRule(String key, V value) {
        return super.put(key, value);
    }

    public void setCamelCaseNameKey(boolean isCamelCaseNameKey) {
        this.isCamelCaseNameKey = isCamelCaseNameKey;
    }

}
