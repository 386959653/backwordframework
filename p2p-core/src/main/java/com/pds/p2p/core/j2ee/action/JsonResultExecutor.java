package com.pds.p2p.core.j2ee.action;

import com.pds.p2p.core.utils.Exceptions;
import org.apache.commons.collections4.Closure;

public class JsonResultExecutor {
    public static JsonResult doIt(Closure<JsonResult> closure) {
        JsonResult jsonResult = new JsonResult();
        try {
            jsonResult.setStatus(JsonResult.OK);
            closure.execute(jsonResult);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.setStatus(JsonResult.ERROR);
            jsonResult.setMessage(Exceptions.getMsg(e));
        }
        return jsonResult;
    }

}
