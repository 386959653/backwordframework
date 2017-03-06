///**
// * Copyright (C) 2014 Baidu, Inc. All Rights Reserved.
// */
//package com.pds.p2p.system.web.filter;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//import com.pds.p2p.core.j2ee.context.Config;
//import com.pds.p2p.core.utils.Exceptions;
//import com.pds.p2p.system.config.ConfigConstants;
//import com.pds.p2p.system.log.LogBeanHolder;
//
//import com.pds.p2p.system.web.session.LoginSessionFlag;
//import com.pds.p2p.system.web.session.SessionUserManager;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.jasig.cas.client.validation.Assertion;
//
//
//import com.pds.p2p.system.web.session.SessionUser;
//
///*********************************
// * * 百度供应商平台 功能模块：用户Session信息统一过滤
// * ********************************
// *
// * @author wuyunfeng
// * @version 1.0.0
// *          ********************************
// *          修改项 修改人 修改时间
// *          <p>
// *          ********************************
// * @date 2014-05-12
// * @copyright baidu.com 2016
// */
//public class SessionUserFilter implements Filter {
//    private static final Logger logger = LogManager.getLogger(SessionUserFilter.class);
//
//    @Override
//    public void destroy() {
//    }
//
//    /**
//     * @function 用户信息及权限设置
//     */
//    @Override
//    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException,
//            ServletException {
//        HttpServletRequest request = (HttpServletRequest) arg0;
//        HttpServletResponse response = (HttpServletResponse) arg1;
//        String url = request.getRequestURI();
//        try {
//            // 获取session
//            HttpSession session = request.getSession(false);
//            Assertion assertion =
//                    (Assertion) (session == null ? request.getAttribute("_const_cas_assertion_") : session
//                            .getAttribute("_const_cas_assertion_"));
//            // 获取登录用户名
//            String userName = null;
//            if (assertion != null) {
//                userName = (String) assertion.getPrincipal().getName();
//                logger.debug("uuap 认证成功，用户名：" + userName);
//            }
//            // 获取redis存储的用户数据
//            SessionUser redisUser = SessionUserManager.get(request);
//            // 如果用户名为空，则表示用户需要重新登录
//            if (StringUtils.isEmpty(userName)) {
//                if (redisUser != null) {
//                    SessionUserManager.del(request, response);
//                }
//
//            } else {
//
//                LogBeanHolder.getAccLogBean().setUID(userName);
//
//                // http监控虚拟用户
//                if ("xn_monitor".equals(userName)) {
//                    PrintWriter pw = null;
//                    try {
//                        pw = response.getWriter();
//                        pw.write("200");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//                        if (pw != null) {
//                            pw.close();
//                        }
//                    }
//
//                    return;
//                }
//
//                // redis存储的用户数据不为空，并且用户名与本次验证的用户名相同
////                if (redisUser != null && redisUser.getUicUser() != null
////                        && userName.equals(redisUser.getUicUser().getUsername())) {
////                    if (redisUser != null && redisUser.getUicUser() != null
//                    if (redisUser != null
//                            ) {
//                    // 设置SessionUser
//                    if (!(StringUtils.equalsIgnoreCase(
//                            Config.get(ConfigConstants.APPLICATION_PROFILE), "debug") || StringUtils
//                            .equalsIgnoreCase(Config.get(ConfigConstants.APPLICATION_PROFILE), "dev"))
//                            && !LoginSessionFlag.checkLoginFlag(userName)) {
//                        request.getRequestDispatcher("/pages/login/logout.jsp").forward(arg0, arg1);
//                        return;
//                    }
//                    SessionUserManager.resetExpire(request);
//                } else {
//
////                    UICUserInfoDto uicUser = UICTransInfoUtils.getInstance().getUserByUserName(userName);
////                    UICUserInfoDto uicUser = UICTransInfoUtils.getInstance().getUserByUserName(userName);
////                    SessionUser sessionUser = SessionUser.newSessionUser(uicUser);
//                    SessionUser sessionUser = SessionUser.newSessionUser(null);
//                    // 设置session
//                    SessionUserManager.set(request, response, sessionUser);
//                    LoginSessionFlag.login(userName);
//
//                }
//            }
//
//            // 继续下一个过滤
//            arg2.doFilter(request, response);
//
//        } catch (Exception e) {
//            logger.error(Exceptions.getStackTraceAsString(e));
//            request.getRequestDispatcher(ConfigConstants.ERROR_500).forward(arg0, arg1);
//        }
//    }
//
//    /**
//     * @param arg0
//     *
//     * @throws ServletException
//     * @function xxx
//     * @date 2014-12-8 上午11:05:43
//     * @author v_zoupegnfei
//     * @overidden @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
//     */
//    @Override
//    public void init(FilterConfig arg0) throws ServletException {
//
//    }
//
//}
