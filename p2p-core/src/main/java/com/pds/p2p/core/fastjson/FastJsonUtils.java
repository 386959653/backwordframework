package com.pds.p2p.core.fastjson;

import com.alibaba.fastjson.JSON;

public class FastJsonUtils {
    public static <T> T parse(String text, String handleUnknown, Class<T> cls) {
        return JSON.parseObject(text, cls, new HandleUnknownExtraProcessor(handleUnknown));
    }
}
