package com.pds.p2p.core.fastjson;

public class ExcludePropertyFilter extends IncludePropertyFilter {
    @Override
    public boolean apply(Object source, String name, Object value) {
        return !super.apply(source, name, value);
    }
}