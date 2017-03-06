package com.pds.p2p.core.j2ee.action;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pds.p2p.core.j2ee.context.SpringContextHolder;
import com.pds.p2p.core.j2ee.web.Responses;
import com.pds.p2p.core.j2ee.web.Servlets;
import com.pds.p2p.core.utils.StringUtils;
import com.pds.p2p.core.utils.UtilType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pds.p2p.core.j2ee.web.Requests;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;

public class ActionHandle {
    public static void main(String[] args) {

    }

    public static final Logger log = LoggerFactory.getLogger(Responses.class);

    private static final String DEFAULT_SERVICE = "service";
    public static final String _MOD = "_mod"; // class 方法
    public static final String _CLS = "_cls"; // class name

    public void execute(HttpServletRequest request, HttpServletResponse response) {
        JsonResult jsonResult = null;
        try {
            request.setCharacterEncoding(CharEncoding.UTF_8);
            response.setCharacterEncoding(CharEncoding.UTF_8);
            response.reset();
            JSON json = Requests.toJSON(request);
            if (log.isDebugEnabled()) {
                log.debug("request is {}.", json);
            }
            jsonResult = this.doIt(request, response, json);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult = JsonResult.failJsonResult(e);
        }
        boolean isAjax = Servlets.isAjaxRequest(request);
        if (isAjax) {
            Responses.renderJson(response, jsonResult.toJSONString());
        } else {
            if (jsonResult.forwardType()) {
                String forward = jsonResult.getData().toString();
                if (!forward.startsWith("/")) {
                    forward = "/" + forward;
                }
                try {
                    request.getRequestDispatcher(forward).forward(request, response);
                } catch (ServletException | IOException e) {
                    throw Throwables.propagate(e);
                }
            } else if (jsonResult.jsonType()) {
                Responses.renderText(response, jsonResult.toJSONString());
            } else {
                Responses.renderText(response, jsonResult.getData().toString());
            }
        }
    }

    /***
     * <pre>
     * 可调用的方法签名，最多支持三个参数，如下:
     * 一、单参数
     * 1）xx(Map<String,Object> p);
     * 2）xx(HttpServletRequest p);
     * 3）xx(Object p);
     * 二、双参数
     * 1）xx(HttpServletRequest p1, Map<String,Object> p2 );
     * 2）xx(HttpServletRequest p1, HttpServletResponse p2 );
     * 3）xx(HttpServletRequest p1, Object p2 );
     * 三、三个参数
     * 2）xx(HttpServletRequest p1, HttpServletResponse p2, Map<String,Object> p3);
     * 2）xx(HttpServletRequest p1, HttpServletResponse p2, Object p3);
     *
     * </pre>
     *
     * @param request
     * @param response
     * @param parms
     *
     * @return
     */
    public JsonResult doIt(HttpServletRequest request, HttpServletResponse response, JSON parms) {
        JsonResult jsonResult = null;
        String cls = null;
        String method = null;
        boolean isObject = TypeUtils.isAssignable(parms.getClass(), JSONObject.class);
        boolean isArray = TypeUtils.isAssignable(parms.getClass(), JSONArray.class);
        if (isObject) {
            JSONObject jsonObject = ((JSONObject) parms);
            cls = jsonObject.getString(_CLS);
            method = jsonObject.getString(_MOD);
        }
        method = StringUtils.defaultString(method, DEFAULT_SERVICE);// 要执行的方法，默认为service
        boolean self = (cls == null);
        Class<?> workCls = null;
        Object obj = null;
        try {
            Method methodexe = null;
            workCls = self ? this.getClass() : (Class<?>) ClassUtils.getClass(cls);
            obj = self ? this : ConstructorUtils.invokeConstructor(workCls);
            Object result = null;
            methodexe = UtilType.getMethodForName(workCls, method);
            Validate.notNull(methodexe, "%s such [%s] method", workCls.getName(), method);
            Class<?>[] classes = methodexe.getParameterTypes();
            Object parmObjs[] = new Object[classes.length];
            for (int i = 0; i < parmObjs.length; ++i) {
                if (TypeUtils.isAssignable(classes[i], HttpServletRequest.class)) {
                    parmObjs[i] = request;
                } else if (TypeUtils.isAssignable(classes[i], HttpServletResponse.class)) {
                    parmObjs[i] = response;
                } else {
                    if (UtilType.isJavaStringType(classes[i])) {
                        if (isArray) {
                            JSONArray a = (JSONArray) parms;
                            if (CollectionUtils.isNotEmpty(a)) {
                                parmObjs[i] = a.get(0).toString();
                            } else {
                                parmObjs[i] = StringUtils.EMPTY;
                            }
                        } else {
                            parmObjs[i] = parms.toString();
                        }
                    } else if (TypeUtils.isAssignable(classes[i], Collection.class)) {
                        if (isArray) {
                            parmObjs[i] = parms;
                        } else {
                            parmObjs[i] = ImmutableList.of(parms);
                        }
                    } else if (!TypeUtils.isAssignable(classes[i], Map.class)) {
                        parmObjs[i] = JSON.toJavaObject(parms, classes[i]);
                    } else {
                        parmObjs[i] = parms;
                    }
                }
            }
            List<Field> fields = UtilType.getAllFields(workCls);
            for (Field field : fields) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                if (autowired != null) {
                    Object fval = null;
                    Qualifier qualifier = field.getAnnotation(Qualifier.class);
                    if (qualifier != null) {
                        fval = SpringContextHolder.getBean(qualifier.value(), field.getType());
                    } else {
                        fval = SpringContextHolder.getBean(field.getType());
                    }
                    FieldUtils.writeField(field, obj, fval, true);
                }
            }
            result = methodexe.invoke(obj, parmObjs);
            result = result == null ? ImmutableList.of() : result;
            if (TypeUtils.isAssignable(result.getClass(), JsonResult.class)) {
                jsonResult = (JsonResult) result;
            } else {
                jsonResult = JsonResult.okJsonResultWithData(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult = JsonResult.failJsonResult(e);
        }
        // String dateFmt = StringUtils.defaultString((String)
        // parms.get("_dateFmt"), UtilType.YYYY_MM_DD_HH_MM_SS);
        // jsonResult.setDateFmt(dateFmt);
        afterHandle(jsonResult);
        return jsonResult;
    }

    protected void afterHandle(JsonResult jsonResult) {

    }
}
