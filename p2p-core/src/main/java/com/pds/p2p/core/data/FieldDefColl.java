package com.pds.p2p.core.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class FieldDefColl extends LinkedHashMap<String, FieldDef> {
    private static final long serialVersionUID = 8962039642247303200L;

    public FieldDef addFieldDef(FieldDef fieldDef) {
        this.put(fieldDef.id(), fieldDef);
        return fieldDef;
    }

    public FieldDef f(String id) {
        return this.get(id);
    }

    public Map<String, FieldDef> getFieldDefs() {
        return this;
    }

    static public Map<String, FieldDefColl> read(InputStream input) throws IOException {
        Map<String, FieldDefColl> result = new HashMap<String, FieldDefColl>();
        String text = IOUtils.toString(input, "utf-8");
        JSONObject jsonObject = JSONObject.parseObject(text);
        for (Map.Entry<String, Object> ent : jsonObject.entrySet()) {
            String key = ent.getKey();
            JSONArray val = (JSONArray) ent.getValue();
            FieldDefColl coll = new FieldDefColl();
            for (Object obj : val) {
                JSONObject json = (JSONObject) obj;
                FieldDef fieldDef = new FieldDef(json.getString("id"), json.getString("name"));
                if (json.containsKey("empty")) {
                    fieldDef.setRequired(json.getBooleanValue("empty"));
                }
                fieldDef.title(json.getString("title"));
                fieldDef.setHide(json.getBooleanValue("hide"));
                fieldDef.setType(StringUtils.defaultString(json.getString("type"), FieldDef.TEXT));
                fieldDef.setLink(json.getString("link"));
                fieldDef.setDlgType(json.getString("dlgType"));
                fieldDef.setLength(json.getIntValue("length"));
                Object opts = json.get("options");
                if (opts != null) {
                    fieldDef.setType(FieldDef.SELECT);
                    if (opts instanceof JSONArray) {
                        JSONArray optionJson = (JSONArray) opts;
                        for (Object inner : optionJson) {
                            JSONObject jsonObject2 = (JSONObject) inner;
                            Map.Entry<String, Object> inent = jsonObject2.entrySet().iterator().next();
                            fieldDef.li(inent.getKey(), inent.getValue().toString());
                        }
                    } else {
                        JSONObject optionJson = (JSONObject) opts;
                        for (Map.Entry<String, Object> ent2 : optionJson.entrySet()) {
                            fieldDef.li(ent2.getKey(), ent2.getValue().toString());
                        }
                    }
                }
                JSONArray list = json.getJSONArray("list");
                if (list != null) {
                    List<String> newList = new ArrayList<String>(list.size());
                    for (Object o : list) {
                        newList.add(o.toString());
                    }
                    fieldDef.setList(newList);
                }
                coll.addFieldDef(fieldDef);
            }
            result.put(key, coll);
        }
        return result;
    }

}
