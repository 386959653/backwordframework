package com.pds.p2p.system.http;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Created by wuyun on 2016/5/20.
 */
public class HttpClientUtils {

    private static Logger logger = LogManager.getLogger(HttpClientUtils.class);

    /**
     * post请求
     *
     * @param url
     * @param formParams
     *
     * @return
     */
    public static <T> T doPost(String url, Class<T> clazz, Map<String, String> formParams) {
        if (MapUtils.isEmpty(formParams)) {
            return doPost(url, clazz);
        }

        try {
            MultiValueMap<String, String> requestEntity = new LinkedMultiValueMap<>();
            formParams.keySet().stream()
                    .forEach(key -> requestEntity.add(key, MapUtils.getString(formParams, key, "")));
            return RestClient.getClient().postForObject(url, requestEntity, clazz);
        } catch (Exception e) {
            logger.error("POST请求出错：{}", url, e);
        }

        return null;
    }

    public static String doPost(String url, Map<String, String> formParams) {
        return doPost(url, String.class, formParams);
    }

    /**
     * post请求
     *
     * @param url
     *
     * @return
     */
    public static String doPost(String url) {
        try {
            return RestClient.getClient().postForObject(url, HttpEntity.EMPTY, String.class);
        } catch (Exception e) {
            logger.error("POST请求出错：{}", url, e);
        }

        return null;
    }

    /**
     * post请求
     *
     * @param url
     *
     * @return
     */
    public static <T> T doPost(String url, Class<T> clazz) {
        try {
            return RestClient.getClient().postForObject(url, HttpEntity.EMPTY, clazz);
        } catch (Exception e) {
            logger.error("POST请求出错：{}", url, e);
        }

        return null;
    }

    /**
     * get请求
     *
     * @param url
     *
     * @return
     */
    public static String doGet(String url) {
        try {
            return RestClient.getClient().getForObject(url, String.class);
        } catch (Exception e) {
            logger.error("GET请求出错：{}", url, e);
        }

        return null;
    }

    public static <T> T doGet(String url, Class<T> clazz) {
        try {
            return RestClient.getClient().getForObject(url, clazz);
        } catch (Exception e) {
            logger.error("GET请求出错：{}", url, e);
        }

        return null;
    }

    public static void main(String[] args) {
        String s = HttpClientUtils.doPost("http://esb.caigou.baidu.com/uic/getUserByUserName?userName=zhouhan02");
        System.out.println(s);

    }
}
