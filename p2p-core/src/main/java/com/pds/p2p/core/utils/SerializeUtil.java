package com.pds.p2p.core.utils;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class SerializeUtil {

    /**
     * @param object
     *
     * @return byte[]
     *
     * @function 序列化
     * @date 2014-12-19 下午3:00:50
     * @author v_zoupengfei
     */
    public static String serialize(Object object) {
        return JSON.toJSONString(object, SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullBooleanAsFalse);
    }

    /**
     * @param bytes
     *
     * @return Object
     *
     * @function 反序列化对象
     * @date 2014-12-19 下午3:00:38
     * @author v_zoupengfei
     */
    public static Object unserialize(String serializeString) {
        return JSON.parse(serializeString);
    }

    /**
     * @param serializeString
     * @param clazz
     *
     * @return T
     *
     * @function 反序列化单个对象
     * @date 2014-12-19 下午3:27:51
     * @author v_zoupengfei
     */
    public static <T> T unserializeObject(String serializeString, Class<T> clazz) {
        return JSONObject.parseObject(serializeString, clazz);
    }

    /**
     * @param serializeString
     * @param elementType
     *
     * @return List<T>
     *
     * @function 反序列化数组
     * @date 2014-12-19 下午3:46:09
     * @author v_zoupengfei
     */
    public static <T> List<T> unserializeList(String serializeString, Class<T> elementType) {
        return JSONArray.parseArray(serializeString, elementType);
    }

}
