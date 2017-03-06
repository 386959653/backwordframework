package com.pds.p2p.core.fastjson;

import java.util.Set;

import com.alibaba.fastjson.serializer.PropertyFilter;
import com.google.common.collect.Sets;

public class IncludePropertyFilter implements PropertyFilter {
    protected Set<String> propIncSet = Sets.newHashSet();

    public void add(String prop) {
        propIncSet.add(prop);
    }

    @Override
    public boolean apply(Object source, String name, Object value) {
        return propIncSet.contains(name);
    }
}