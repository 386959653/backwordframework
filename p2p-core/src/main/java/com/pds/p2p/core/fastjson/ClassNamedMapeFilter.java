package com.pds.p2p.core.fastjson;

import org.apache.commons.lang3.reflect.TypeUtils;

public class ClassNamedMapeFilter<T> extends NamedMapeFilter {
    final protected Class<T> cls;

    public ClassNamedMapeFilter(Class<T> cls) {
        this.cls = cls;
    }

    @Override
    public String process(Object source, String name, Object value) {
        if (TypeUtils.isInstance(source, cls)) {
            return nameMap.get(name);
        }
        return name;
    }
}
