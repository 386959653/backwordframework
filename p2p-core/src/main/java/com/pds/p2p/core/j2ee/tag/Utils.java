package com.pds.p2p.core.j2ee.tag;

public class Utils {
    public static String BLOCK = "__override__";

    public static String getOverrideVariableName(String name) {
        return BLOCK + name;
    }
}
