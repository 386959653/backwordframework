package com.pds.p2p.core.fastjson;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.reflect.MethodUtils;

import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;

public class HandleUnknownExtraProcessor implements ExtraProcessor {
    final private String handleUnknown;

    public HandleUnknownExtraProcessor(String handleUnknown) {
        this.handleUnknown = handleUnknown;
    }

    @Override
    public void processExtra(Object obj, String key, Object val) {
        try {
            Method method = MethodUtils.getAccessibleMethod(obj.getClass(), handleUnknown, String.class, Object.class);
            method.invoke(obj, key, val);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
