package com.pds.p2p.core.j2ee.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.pds.p2p.core.fastjson.JSONStringBuilder;
import com.pds.p2p.core.utils.Exceptions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import com.pds.p2p.core.utils.StringUtils;
import com.pds.p2p.core.utils.UtilType;
import com.google.common.collect.Maps;

public class JsonResult<T> {
    public static void main(String[] args) {
        JsonResult<?> o = new JsonResult<>();
        System.out.println(o.data("xx", 12).toJSONString());

        HashMap<String, Object> data = Maps.newHashMap();
        data.put("list", new ArrayList<>());
        data.put("total", 100);

        System.out.println(JsonResult.okJsonResultWithData(data).toJSONString());

        System.out.println(JsonResult.failJsonResult("错误！").toJSONString());

        System.out.println(JsonResult.needLoginJsonResult().toJSONString());

    }

    final public static String ERROR = "error";
    final public static String OK = "ok";
    final public static String NEED_LOGIN = "needLogin";

    final public static String SESSION_TIMEOUT = "sessionTimeout";
    final public static String DATABAS_EEXCEPTION = "databaseException";

    final public static String JSON_TYPE = "json";
    final public static String TEXT_TYPE = "text";
    final public static String FORWARD_TYPE = "forward";

    private String status;
    private String message;
    private String type;
    private Integer total;
    private Boolean onlyData;
    private String dateFmt;

    private T data;

    public boolean jsonType() {
        return (StringUtils.equalsIgnoreCase(JSON_TYPE, this.getType()));
    }

    public boolean textType() {
        return (StringUtils.equalsIgnoreCase(TEXT_TYPE, this.getType()));
    }

    public boolean forwardType() {
        return (StringUtils.equalsIgnoreCase(FORWARD_TYPE, this.getType()));
    }

    public String getDateFmt() {
        return dateFmt;
    }

    public void setDateFmt(String dateFmt) {
        this.dateFmt = dateFmt;
    }

    public static <T> JsonResult<T> okJsonResultWithData(T data) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setData(data);
        return jsonResult;
    }

    public static JsonResult<?> okJsonResult() {
        return okJsonResultWithMsg("");
    }

    public static JsonResult<?> okJsonResultWithMsg(String message) {
        JsonResult<?> jsonResult = new JsonResult<>();
        jsonResult.setMessage(message);
        return jsonResult;
    }

    public static JsonResult<String> okJsonResultWithContent(String dataType, String content) {
        JsonResult<String> jsonResult = new JsonResult<>();
        jsonResult.setType(dataType);
        jsonResult.setData(content);
        return jsonResult;
    }

    public static JsonResult<String> okJsonResultWithForward(String forwardUrl) {
        JsonResult<String> jsonResult = new JsonResult<String>();
        jsonResult.setForwardUrl(forwardUrl);
        return jsonResult;
    }

    public static JsonResult<?> failJsonResult(String message) {
        JsonResult<?> jsonResult = new JsonResult<>();
        jsonResult.setStatus(ERROR);
        jsonResult.setMessage(message);
        return jsonResult;
    }

    public static <T> JsonResult<T> failJsonResultWithData(T data) {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setStatus(ERROR);
        jsonResult.setData(data);
        return jsonResult;
    }

    public static JsonResult<?> failJsonResult(Throwable wrapped) {
        return failJsonResult(Exceptions.getMsg(wrapped));
    }

    public static JsonResult<?> needLoginJsonResult() {
        JsonResult<?> jsonResult = new JsonResult<>();
        jsonResult.setStatus(NEED_LOGIN);
        jsonResult.setMessage("need login!");
        return jsonResult;
    }

    public JsonResult() {
        this.setStatus(OK);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setData(T data, boolean b) {
        this.onlyData(b).setData(data);
    }

    @SuppressWarnings("unchecked")
    public JsonResult<T> data(Map<String, Object> map) {
        if (this.data == null) {
            this.data = (T) Maps.<String, Object>newLinkedHashMap();
        }
        UtilType.copyProperties(data, map);
        return this;
    }

    @SuppressWarnings("unchecked")
    public JsonResult<T> data(String key, Object value) {
        if (this.data == null) {
            this.data = (T) Maps.<String, Object>newLinkedHashMap();
        }
        MetaObject metaObject = SystemMetaObject.forObject(data);
        metaObject.setValue(key, value);
        return this;
    }

    public Boolean isOnlyData() {
        return onlyData;
    }

    public JsonResult<T> onlyData(Boolean onlyData) {
        this.onlyData = onlyData;
        return this;
    }

    @SuppressWarnings("unchecked")
    public void setForwardUrl(String forwardUrl) {
        this.setType(FORWARD_TYPE);
        this.setData(((T) forwardUrl));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String statusCode) {
        this.status = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String toJSONString() {
        Validate.notBlank(this.getStatus(), "必须提供StatusCode");
        JSONStringBuilder builder = null;
        if (onlyData != null && this.isOnlyData()) {
            builder = new JSONStringBuilder(data);
        } else {
            builder = new JSONStringBuilder(this);
        }
        String[] excludes = {"dateFmt"};
        if (StringUtils.isEmpty(this.message)) {
            excludes = ArrayUtils.add(excludes, "message");
        }
        return builder.dateFmt(dateFmt).exclude(excludes).toString();
    }

}
