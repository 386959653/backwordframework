package com.pds.p2p.system.dto;

import com.alibaba.fastjson.JSONObject;
import com.pds.p2p.core.utils.StringUtils;

/****
 * 用于MYBATIS 返回对象 <b>Summary: </b> TODO <b>Remarks: </b> TODO
 */

public class DataResult extends JSONObject {

    private static final long serialVersionUID = 7808432631208861145L;

    public DataResult() {
        super();
    }

    public Object put(String key, Object value) {
        if (key instanceof String) {
            String skey = (String) key;
            if (StringUtils.isNotBlank(skey)) {
                if (StringUtils.containsIgnoreCase(skey, "_")) {
                    boolean flag = true;
                    StringBuilder sbd = new StringBuilder();
                    for (String str : StringUtils.split(skey, "_")) {
                        if (flag) {
                            sbd.append(StringUtils.lowerCase(str));
                        } else {
                            sbd.append(StringUtils.capitalize(StringUtils.lowerCase(str)));
                        }
                        flag = false;
                    }
                    return super.put(sbd.toString(), value);
                } else {
                    if (StringUtils.containsUpperAndLowerLetter(key)) {
                        return super.put(key, value);
                    }
                    return super.put(StringUtils.lowerCase(key), value);
                }
            }

        }
        return null;
    }

}
