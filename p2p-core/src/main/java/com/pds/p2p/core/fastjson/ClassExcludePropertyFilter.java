package com.pds.p2p.core.fastjson;

import org.apache.commons.lang3.reflect.TypeUtils;

public class ClassExcludePropertyFilter<T> extends ClassIncludePropertyFilter<T> {

    public ClassExcludePropertyFilter(Class<T> cls) {
        super(cls);
    }

    /***
     * 不是这个类或不包含这个类的属性返回true，
     * 仅是这个类且包含了这个属性返回false
     */
    @Override
    public boolean apply(Object source, String name, Object value) {
        if (TypeUtils.isInstance(source, cls)) {
            if (!propIncSet.contains(name)) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

}
