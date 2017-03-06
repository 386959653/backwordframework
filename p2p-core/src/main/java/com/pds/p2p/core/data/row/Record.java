package com.pds.p2p.core.data.row;

import java.util.List;

import com.pds.p2p.core.utils.UtilString;

/***
 * 统一驼峰字符key管理
 *
 * @author wen
 */
public class Record extends KeyRow {
    protected StringBuilder buff = new StringBuilder();

    public Record() {

    }

    public Record(String... keys) {
        super(keys);
    }

    public Record(int capacity) {
        super(capacity);
    }

    public Record(List<String> keys) {
        super(keys);
    }

    @Override
    public Object put(String key, Object value) {
        if (!UtilString.isCamelCase(key)) {
            UtilString.camelCaseName((String) key, this.buff);
            return super.put(this.buff.toString(), value);
        } else {
            return super.put(key, value);
        }
    }

}
