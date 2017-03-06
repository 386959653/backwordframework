package com.pds.p2p.core.j2ee.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.pds.p2p.core.utils.UtilType;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UrlPathHelper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

/*
 * HttpServletRequest帮助类
 */
public class Requests {

    /***
     * 读取request的内容，转换成指定的bean；
     *
     * @param clazz
     * @param request
     *
     * @return
     */
    public static <T> T cast(Class<T> clazz, HttpServletRequest request) {
        JSON json = toJSON(request);
        return JSON.toJavaObject(json, clazz);
    }

    /***
     * 读取request的内容为一个map
     *
     * @param request
     *
     * @return
     */
    public static Map<String, String> getParameterStringMap(HttpServletRequest request) {
        return getParameterStringMap(request, StringUtils.EMPTY);
    }

    /***
     * 读取request的内容（以prefix为前缀的）为一个map
     *
     * @param request
     *
     * @return
     */
    public static Map<String, String> getParameterStringMap(HttpServletRequest request, String prefix) {
        boolean nameWithPrefix = StringUtils.isNotEmpty(prefix);
        Map<String, String[]> parms = getParameterMap(request);
        Map<String, String> map = new HashMap<String, String>();
        String name, key, value;
        for (Map.Entry<String, String[]> ent : parms.entrySet()) {
            name = ent.getKey();
            key = name.substring(prefix.length());
            value = StringUtils.join(request.getParameterValues(name), ',');
            if (nameWithPrefix && StringUtils.startsWithIgnoreCase(name, prefix)) {
                map.put(key, value);
            } else {
                map.put(key, value);
            }
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String[]> getParameterMap(HttpServletRequest request) {
        String encoding = StringUtils.defaultString(request.getCharacterEncoding(), CharEncoding.UTF_8);
        String method = request.getMethod();
        Map<String, String[]> parms = null;
        if (StringUtils.equalsIgnoreCase("GET", method)) {
            String queryString = request.getQueryString();
            if (StringUtils.isNotEmpty(queryString)) {
                try {
                    queryString = URLDecoder.decode(queryString, encoding);
                    parms = parseQueryString(queryString);
                } catch (UnsupportedEncodingException e) {
                }
            }
        } else {
            parms = (Map<String, String[]>) request.getParameterMap();
        }
        return MapUtils.isEmpty(parms) ? Maps.<String, String[]>newLinkedHashMap() : parms;
    }

    /****
     * 读取request的内容，转换为JSON对象
     *
     * @param request
     *
     * @return
     */
    public static JSON toJSON(HttpServletRequest request) {
        JSON result = null;
        Map<String, Object> reqMap = getParameters(request);
        if (MapUtils.isNotEmpty(reqMap)) {
            result = UtilType.mapToJSON(reqMap);
        } else {
            InputStream input = null;
            try {
                String encoding = StringUtils.defaultString(request.getCharacterEncoding(), CharEncoding.UTF_8);
                input = request.getInputStream();
                String queryString = IOUtils.toString(input, encoding);
                if (StringUtils.isEmpty(queryString)) {
                    result = UtilType.strToJSON(queryString);
                    if (result == null) {
                        Map<String, String[]> toObjectMap = parseQueryString(queryString);
                        if (MapUtils.isEmpty(toObjectMap)) {
                            result = new JSONArray(ImmutableList.<Object>of(queryString));
                        } else {
                            result = UtilType.mapToJSON(toObjectMap);
                        }
                    }
                }
            } catch (IOException e) {
            } finally {
                IOUtils.closeQuietly(input);
            }
        }
        return result;
    }

    public static String getParameter(HttpServletRequest request, String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        String val = request.getParameter(name);
        if (request.getMethod().equalsIgnoreCase("GET")) {
            String encoding = StringUtils.defaultString(request.getCharacterEncoding(), CharEncoding.UTF_8);
            try {
                val = URLDecoder.decode(val, encoding);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return val;
    }

    public static Map<String, Object> getParameters(HttpServletRequest request) {
        Map<String, String[]> map = getParameterMap(request);
        return toObjectMap(map);
    }

    private static Map<String, Object> toObjectMap(Map<String, String[]> map) {
        Map<String, Object> params = new HashMap<String, Object>();
        int len;
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            len = entry.getValue().length;
            if (len == 1) {
                params.put(entry.getKey(), entry.getValue()[0]);
            } else if (len > 1) {
                params.put(entry.getKey(), entry.getValue());
            }
        }
        return params;
    }

    /**
     * Parses a query string passed from the client to the server and builds a
     * <code>HashTable</code> object with key-value pairs. The query string
     * should be in the form of a string packaged by the GET or POST method,
     * that is, it should have key-value pairs in the form <i>key=value</i>,
     * with each pair separated from the next by a &amp; character.
     * <p>
     * <p>
     * A key can appear more than once in the query string with different
     * values. However, the key appears only once in the hashtable, with its
     * value being an array of strings containing the multiple values sent by
     * the query string.
     * <p>
     * <p>
     * The keys and values in the hashtable are stored in their decoded form, so
     * any + characters are converted to spaces, and characters sent in
     * hexadecimal notation (like <i>%xx</i>) are converted to ASCII characters.
     *
     * @param s a string containing the query to be parsed
     *
     * @return a <code>HashTable</code> object built from the parsed key-value
     * pairs
     *
     * @throws IllegalArgumentException if the query string is invalid
     */
    private static Map<String, String[]> parseQueryString(String s) {
        String valArray[] = null;
        if (s == null) {
            throw new IllegalArgumentException();
        }
        Map<String, String[]> ht = new HashMap<String, String[]>();
        StringTokenizer st = new StringTokenizer(s, "&");
        while (st.hasMoreTokens()) {
            String pair = (String) st.nextToken();
            int pos = pair.indexOf('=');
            if (pos == -1) {
                continue;
            }
            String key = pair.substring(0, pos);
            String val = pair.substring(pos + 1, pair.length());
            if (ht.containsKey(key)) {
                String oldVals[] = (String[]) ht.get(key);
                valArray = new String[oldVals.length + 1];
                for (int i = 0; i < oldVals.length; i++) {
                    valArray[i] = oldVals[i];
                }
                valArray[oldVals.length] = val;
            } else {
                valArray = new String[1];
                valArray[0] = val;
            }
            ht.put(key, valArray);
        }
        return ht;
    }

    /**
     * 获取访问者IP
     * <p>
     * 在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
     * <p>
     * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)，
     * 如果还不存在则调用Request .getRemoteAddr()。
     *
     * @param request
     *
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多次反向代理后会有多个IP值，第一个为真实IP。
        int index = ip.indexOf(',');
        if (index != -1) {
            return ip.substring(0, index);
        } else {
            return ip;
        }
    }

    /**
     * 获得当的访问路径
     * <p>
     * HttpServletRequest.getRequestURL+"?"+HttpServletRequest.getQueryString
     *
     * @param request
     *
     * @return
     */
    public static String getLocation(HttpServletRequest request) {
        UrlPathHelper helper = new UrlPathHelper();
        StringBuffer buff = request.getRequestURL();
        String uri = request.getRequestURI();
        String origUri = helper.getOriginatingRequestUri(request);
        buff.replace(buff.length() - uri.length(), buff.length(), origUri);
        String queryString = helper.getOriginatingQueryString(request);
        if (queryString != null) {
            buff.append("?").append(queryString);
        }
        return buff.toString();
    }

    /**
     * 获得请求的session id，但是HttpServletRequest#getRequestedSessionId()方法有一些问题。
     * 当存在部署路径的时候，会获取到根路径下的jsessionid。
     *
     * @param request
     *
     * @return
     *
     * @see HttpServletRequest#getRequestedSessionId()
     */
    public static String getRequestedSessionId(HttpServletRequest request) {
        String sid = request.getRequestedSessionId();
        String ctx = request.getContextPath();
        // 如果session id是从url中获取，或者部署路径为空，那么是在正确的。
        if (request.isRequestedSessionIdFromURL() || StringUtils.isBlank(ctx)) {
            return sid;
        } else {
            // 手动从cookie获取
            Cookie cookie = CookieUtils.getCookie(request, Constants.JSESSION_COOKIE);
            if (cookie != null) {
                return cookie.getValue();
            } else {
                return request.getSession().getId();
            }
        }

    }

}
