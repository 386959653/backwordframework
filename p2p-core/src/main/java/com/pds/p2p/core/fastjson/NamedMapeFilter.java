package com.pds.p2p.core.fastjson;

import java.util.Map;

import com.alibaba.fastjson.serializer.NameFilter;
import com.google.common.collect.Maps;

public class NamedMapeFilter implements NameFilter {
    protected Map<String, String> nameMap = Maps.newHashMap();

    public String put(String key, String value) {
        return nameMap.put(key, value);
    }

    @Override
    public String process(Object source, String name, Object value) {
        return nameMap.get(name);
    }
}