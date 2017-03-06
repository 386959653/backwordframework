package com.pds.p2p.core.data.row;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class IndexRow implements Map<String, Object>, Iterator<Object> {
    private Object values[] = new Object[25];
    private int objectNum = 0;
    private Iterator<Object> iter;
    private org.apache.commons.collections4.Transformer<Object, Object> transformer;

    public void setTransformer(org.apache.commons.collections4.Transformer<Object, Object> transformer) {
        this.transformer = transformer;
    }

    public void clear() {

    }

    public boolean containsKey(Object key) {
        return false;
    }

    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return null;
    }

    public Object get(Object key) {
        if (key instanceof Integer) {
            Integer idx = (Integer) key;
            return values[idx];
        }
        return null;
    }

    public boolean isEmpty() {
        return false;
    }

    @Override
    public Object put(String key, Object value) {
        values[objectNum++] = value;
        return value;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {

    }

    public int size() {
        return objectNum;
    }

    public Object[] getValues() {
        Object[] result = new Object[this.objectNum];
        for (int k = 0; k < objectNum; ++k) {
            if (transformer != null) {
                result[k] = transformer.transform(values[k]);
            } else {
                result[k] = values[k];
            }
        }
        return result;
    }

    public Object[] rawValues() {
        return values;
    }

    public int getObjectNum() {
        return objectNum;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("val_num:").append(objectNum);
        sb.append("||");
        for (Object val : getValues()) {
            sb.append(val).append(",");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public boolean hasNext() {
        if (iter == null) {
            iter = new org.apache.commons.collections4.iterators.ArrayIterator<Object>(getValues());
        }
        return iter.hasNext();
    }

    public Object next() {
        return iter.next();
    }

    public void remove() {
    }

    public Object remove(Object key) {
        return null;
    }

    public Collection<Object> values() {
        return Arrays.asList(values);
    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    public KeyRow toKeyArrayRow(String... keys) {
        KeyRow keyRow = KeyRow.create(keys);
        int i = 0;
        for (; i < keys.length; ++i) {
            keyRow.putAt(i, this.values[i]);
        }
        return keyRow;
    }

}
