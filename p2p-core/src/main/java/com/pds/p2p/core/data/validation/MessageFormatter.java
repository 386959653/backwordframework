package com.pds.p2p.core.data.validation;

import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.parsing.TokenHandler;

public class MessageFormatter {
    @SuppressWarnings("rawtypes")
    public static String formate(String msg, final Object... parms) {
        Object[] theParms = parms;
        if (parms.length == 1 && TypeUtils.isInstance(parms[0], List.class)) {
            theParms = ((List) parms[0]).toArray();
        }
        final Object[] myParms = theParms;
        GenericTokenParser parser = new GenericTokenParser("{", "}", new TokenHandler() {
            public String handleToken(String content) {
                int idx = NumberUtils.toInt(content);
                return myParms[idx].toString();
            }
        });
        return parser.parse(msg);
    }
}
