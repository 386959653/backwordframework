package com.pds.p2p.core.jdbc.helper;

import java.util.Map;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;

public class EnMap<K, V> extends CaseInsensitiveMap<K, V> {

    /**
     *
     */
    private static final long serialVersionUID = 8859374155461153552L;

    public EnMap() {
        super();
    }

    public EnMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public EnMap(int initialCapacity) {
        super(initialCapacity);
    }

    public EnMap(Map<? extends K, ? extends V> map) {
        super(map);
    }

    @Override
    protected Object convertKey(Object key) {
        String str = super.convertKey(key).toString();
        return StringUtils.remove(str, '_');
    }

}
