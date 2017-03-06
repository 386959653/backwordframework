package com.pds.p2p.core.j2ee.kendo.mvvm.annotation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.pds.p2p.core.utils.UtilType;

public class MvvmBuilder {
    private static String OBSERVABLE_FMT = "kendo.observable(%s)";

    static public String doBuild(Object obj) {
        return String.format(OBSERVABLE_FMT, //
                JSON.toJSONStringWithDateFormat(
                        obj, //
                        UtilType.YYYY_MM_DD_T_HH_MM_SS,//
                        SerializerFeature.WriteMapNullValue,//
                        SerializerFeature.WriteNullListAsEmpty,//
                        SerializerFeature.WriteNullStringAsEmpty,//
                        SerializerFeature.WriteNullBooleanAsFalse
                ));
    }
}
