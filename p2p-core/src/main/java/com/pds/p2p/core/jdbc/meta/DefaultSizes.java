package com.pds.p2p.core.jdbc.meta;

import java.sql.Types;
import java.util.HashMap;

import com.google.common.collect.Maps;

public class DefaultSizes {
    private static HashMap<Integer, String> _defaultSizes = Maps.newHashMap();

    static {
        _defaultSizes.put(new Integer(Types.CHAR), "254");
        _defaultSizes.put(new Integer(Types.VARCHAR), "254");
        _defaultSizes.put(new Integer(Types.LONGVARCHAR), "254");
        _defaultSizes.put(new Integer(Types.BINARY), "254");
        _defaultSizes.put(new Integer(Types.VARBINARY), "254");
        _defaultSizes.put(new Integer(Types.LONGVARBINARY), "254");
        _defaultSizes.put(new Integer(Types.INTEGER), "32");
        _defaultSizes.put(new Integer(Types.BIGINT), "64");
        _defaultSizes.put(new Integer(Types.REAL), "7,0");
        _defaultSizes.put(new Integer(Types.FLOAT), "15,0");
        _defaultSizes.put(new Integer(Types.DOUBLE), "15,0");
        _defaultSizes.put(new Integer(Types.DECIMAL), "15,15");
        _defaultSizes.put(new Integer(Types.NUMERIC), "15,15");
    }

    static public String get(int key) {
        return _defaultSizes.get(key);
    }
}
