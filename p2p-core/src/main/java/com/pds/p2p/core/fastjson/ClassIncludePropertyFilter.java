package com.pds.p2p.core.fastjson;

import org.apache.commons.lang3.reflect.TypeUtils;

public class ClassIncludePropertyFilter<T> extends IncludePropertyFilter {
    protected Class<T> cls;

    public ClassIncludePropertyFilter(Class<T> cls) {
        this.cls = cls;
    }

    @Override
    public boolean apply(Object source, String name, Object value) {
        if (TypeUtils.isInstance(source, cls)) {
            if (propIncSet.contains(name)) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

}
