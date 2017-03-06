package com.pds.p2p.core.data.row;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/***
 * 提供两个固定数组方式，减少内存； 其中keys还可以设定static共享，使用public KeyRow(List<String> keys)
 *
 * @author Administrator
 */
public class KeyRow implements Map<String, Object> {
    private static final int DEFAULT_LEN = 20;
    protected List<Object> values;
    protected List<String> keys;

    private static KeyRow one = new KeyRow();

    public static KeyRow instance() {
        return one;
    }

    public static KeyRow create(String... keys) {
        return new KeyRow(keys);
    }

    public KeyRow() {
        this.keys = Lists.newArrayListWithCapacity(DEFAULT_LEN);
        this.values = Lists.newArrayListWithCapacity(this.keys.size());
    }

    public KeyRow(int capacity) {
        this.keys = Lists.newArrayListWithCapacity(capacity);
        this.values = Lists.newArrayListWithCapacity(capacity);
    }

    public KeyRow(String... keys) {
        this.keys = Lists.newArrayList(keys);
        this.values = Lists.newArrayListWithCapacity(this.keys.size());
    }

    public KeyRow(List<String> keys) {
        this.keys = keys;
        this.values = Lists.newArrayListWithCapacity(this.keys.size());
    }

    @Override
    public int size() {
        return keys.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.keys.contains(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.values.contains(value);
    }

    @Override
    public Object get(Object key) {
        int idx = this.keys.indexOf(key.toString());
        if (idx == ArrayUtils.INDEX_NOT_FOUND) {
            return null;
        }
        return this.values.get(idx);
    }

    @Override
    public Object put(String key, Object value) {
        int idx = this.keys.indexOf(key.toString());
        if (idx == ArrayUtils.INDEX_NOT_FOUND) {
            for (int i = 0; i < this.keys.size(); ++i) {
                String s = this.keys.get(i);
                if (s == null) {
                    Object old = this.values.get(i);
                    this.values.set(i, value);
                    return old;
                }
            }
            this.keys.add(key);
            this.values.add(value);
            return null;
        } else {
            Object old = this.values.get(idx);
            this.values.set(idx, value);
            return old;
        }
    }

    public void putAt(int idx, Object value) {
        this.values.set(idx, value);
    }

    @Override
    public Object remove(Object key) {
        int idx = this.keys.indexOf(key.toString());
        if (idx == ArrayUtils.INDEX_NOT_FOUND) {
            return null;
        }
        Object old = this.values.get(idx);
        this.keys.set(idx, null);
        this.values.set(idx, null);
        return old;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        for (Map.Entry<? extends String, ? extends Object> ent : m.entrySet()) {
            this.put(ent.getKey(), ent.getValue());
        }
    }

    @Override
    public void clear() {
        this.keys.clear();
        this.values.clear();
    }

    @Override
    public Set<String> keySet() {
        Validate.notNull(keys);
        return Sets.newHashSet(keys);
    }

    @Override
    public Collection<Object> values() {
        Validate.notNull(values);
        return Lists.newArrayList(values);
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        Set<java.util.Map.Entry<String, Object>> entries = Sets.newLinkedHashSetWithExpectedSize(keys.size());
        for (int i = 0; i < keys.size(); ++i) {
            org.apache.commons.collections4.keyvalue.DefaultMapEntry<String, Object> keyValue =
                    new org.apache.commons.collections4.keyvalue.DefaultMapEntry<String, Object>(keys.get(i),
                            values.get(i));
            entries.add(keyValue);
        }
        return entries;
    }

    public void fillValues(Object val) {
        Collections.fill(this.values, val);
    }

    public void clearValues() {
        fillValues(null);
    }
}
