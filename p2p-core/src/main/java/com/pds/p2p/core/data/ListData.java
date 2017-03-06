package com.pds.p2p.core.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.ImmutableMap;

@SuppressWarnings("unchecked")
public class ListData {
    final private JSONArray data;
    private String name;

    public ListData(String name) {
        this.name = name;
        this.data = new JSONArray(5);
    }

    public ListData put(String id, String text) {
        this.data.add(ImmutableMap.of("id", id, "text", text));
        return this;
    }

    public String toJSONString() {
        return data.toJSONString();
    }

    public List<Map<String, Object>> toList() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(this.data.size());
        Iterator<Object> iterator = this.data.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> map = (Map<String, Object>) iterator.next();
            result.add(map);
        }
        return result;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        Iterator<Object> iterator = this.data.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> map = (Map<String, Object>) iterator.next();
            result.put(map.get("id").toString(), map.get("text").toString());
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public ListData name(String name) {
        this.name = name;
        return this;
    }

    public ListData clone() {
        ListData result = new ListData(this.name);
        Iterator<Object> iterator = this.data.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> map = (Map<String, Object>) iterator.next();
            result.data.add(ImmutableMap.of("id", map.get("id"), "text", map.get("text")));
        }
        return result;
    }

    public ListData cloneBy(String... ids) {
        ListData result = new ListData(this.name);
        Iterator<Object> iterator = this.data.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> map = (Map<String, Object>) iterator.next();
            String id = (String) map.get("id");
            if (ArrayUtils.contains(ids, id)) {
                result.data.add(ImmutableMap.of("id", map.get("id"), "text", map.get("text")));
            }

        }
        return result;
    }

    public void remove(String id) {
        Iterator<Object> iterator = this.data.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> map = (Map<String, Object>) iterator.next();
            String val = (String) map.get("id");
            if (StringUtils.equals(id, val)) {
                iterator.remove();
                break;
            }
        }
    }

    public String get(String id) {
        Iterator<Object> iterator = this.data.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> map = (Map<String, Object>) iterator.next();
            if (map.get("id").equals(id)) {
                return map.get("text").toString();
            }
        }
        return StringUtils.EMPTY;
    }

}
