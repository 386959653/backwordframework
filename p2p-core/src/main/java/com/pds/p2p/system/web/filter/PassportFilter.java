/**
 * Copyright (C) 2014 Baidu, Inc. All Rights Reserved.
 */
package com.pds.p2p.system.web.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.pds.p2p.core.utils.Exceptions;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasig.cas.client.validation.Assertion;

import com.pds.p2p.core.j2ee.action.JsonResult;
import com.pds.p2p.core.j2ee.context.Config;
import com.pds.p2p.core.utils.WebUtil;
import com.pds.p2p.system.config.ConfigConstants;
import com.pds.p2p.system.i18n.LangEnv;
import com.pds.p2p.system.log.LogTraceManager;
import com.pds.p2p.system.web.session.SessionUtils;

/*********************************
 * * 百度供应商平台 功能模块：不需要登录功能过滤
 * ********************************
 *
 * @author v_lianghua
 * @version 1.0.0
 *          ********************************
 *          修改项 修改人 修改时间
 *          <p>
 *          ********************************
 * @date 2014-12-8 下午01:49:38
 * @copyright baidu.com 2014
 */
public class PassportFilter implements Filter {
    public static final Logger logger = LogManager.getLogger(PassportFilter.class);

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain filterChain) throws IOException,
            ServletException {
        HttpServletRequest request = ((HttpServletRequest) arg0);
        HttpServletResponse response = ((HttpServletResponse) arg1);
        // 获取请求地址
        String requstURL = request.getRequestURI().toString(); // 带项目路径
        String uri = requstURL.replace(request.getContextPath(), ""); // 不带项目路径

        // 日志监控记录开始
        LogTraceManager.beginTrace(request, response);

        //解决chrome4.2内核浏览器（百度浏览器7.x）卡顿Bug.
        response.addHeader("Connection", "close");

        LangEnv.remove();
        SessionUtils.remove();

        // nologin 前缀
        String prefix = Config.get(ConfigConstants.APPLICATION_URL_WHITE_LIST_PREFIX);
        if (StringUtils.isNotEmpty(prefix)) {
            String[] NO_FILTER_PREFIX = prefix.split(";");
            for (String ext : NO_FILTER_PREFIX) {
                if (uri.startsWith(ext)) {
                    noAuthForward(request, response);
                    return;
                }
            }
        }
        // nologin 后缀
        String suffix = Config.get(ConfigConstants.APPLICATION_URL_WHITE_LIST_SUFFIX);
        if (StringUtils.isNotEmpty(suffix)) {
            String[] NO_FILTER_SUFFIX = suffix.split(";");
            for (String ext : NO_FILTER_SUFFIX) {
                if (uri.endsWith(ext)) {
                    noAuthForward(request, response);
                    return;
                }
            }
        }

        try {

            //需要登录的session失效的情况
            HttpSession session = request.getSession(false);
            Assertion assertion =
                    (Assertion) (session == null ? request.getAttribute("_const_cas_assertion_")
                                         : session.getAttribute("_const_cas_assertion_"));
            if (assertion == null) {//登录失效
                switch (WebUtil.getRequestType(request)) {
                    case AJAX_REQUEST:
                        responseJson(response, JsonResult.needLoginJsonResult());
                        return;
                    case UPLOAD_REQUEST:
                        responseJson(response, JsonResult.needLoginJsonResult());
                        return;
                    default:
                        break;
                }

            }

            logger.debug("url:" + uri + ",进入权限认证");
            // 继续下一个过滤
            filterChain.doFilter(request, response);

            LangEnv.remove();
            SessionUtils.remove();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(Exceptions.getStackTraceAsString(e));
            request.getRequestDispatcher(ConfigConstants.ERROR_500).forward(arg0, arg1);
            throw new RuntimeException(e);

        } finally {

            // 日志监控结束，打印日志
            LogTraceManager.endTrace(request, response);

        }
    }

    private void noAuthForward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String echoUrl = request.getServletPath();
        logger.debug("url:" + echoUrl + ",无需权限认证");
        RequestDispatcher dispatcher = request.getRequestDispatcher(echoUrl);
        // 转向到原始页面
        dispatcher.forward(request, response);

        LangEnv.remove();
        SessionUtils.remove();
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

    private void responseJson(HttpServletResponse response, JsonResult<?> result) {
        PrintWriter pw = null;
        try {
            response.setContentType("text/html;charset=UTF-8");
            pw = response.getWriter();
            pw.write(result.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }
}
