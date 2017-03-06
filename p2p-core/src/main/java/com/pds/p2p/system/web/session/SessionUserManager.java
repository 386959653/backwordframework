///**
// * Copyright (C) 2014 Baidu, Inc. All Rights Reserved.
// */
//package com.pds.p2p.system.web.session;
//
//import java.util.Random;
//
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import com.pds.p2p.system.config.ConfigConstants;
//import org.apache.commons.codec.digest.DigestUtils;
//import org.apache.commons.lang3.StringUtils;
//
//import com.pds.p2p.core.j2ee.context.Config;
//import com.pds.p2p.core.utils.DateUtils;
//import com.pds.p2p.core.utils.WebUtil;
//import com.pds.p2p.system.redis.JedisUtil;
//
///*********************************
// * * 百度供应商平台 功能模块：session用户管理
// * ********************************
// *
// * @author v_zoupengfei
// * @version 1.0.0
// *          ********************************
// *          修改项 修改人 修改时间
// *          <p>
// *          ********************************
// * @date 2014-11-30 下午11:27:14
// * @copyright baidu.com 2014
// */
//public class SessionUserManager {
//
//    private static final String COOKIE_KEY = "procurement.portal.session.user";
//
//    private static final Integer SESSION_EXPIRE =
//            Integer.valueOf(Config.get(ConfigConstants.APPLICATION_SESSION_EXPIRE));
//
//    /**
//     * @param request
//     * @param response
//     * @param sessionUser
//     *
//     * @return void
//     *
//     * @function 放入session
//     * @date 2014-12-1 上午12:14:26
//     * @author v_zoupengfei
//     */
//    public static void set(HttpServletRequest request, HttpServletResponse response, SessionUser sessionUser) {
//        String sessionid = createSessionid(request, response, sessionUser);
//        sessionUser.setSessionId(sessionid);
//        JedisUtil.setex(sessionid, SESSION_EXPIRE, sessionUser);
//        SessionUtils.setSessionUser(sessionUser);
//    }
//
//    /**
//     * @param request
//     *
//     * @return SessionUser
//     *
//     * @function 获取session
//     * @date 2014-12-1 上午10:44:09
//     * @author v_zoupengfei
//     */
//    public static SessionUser get(HttpServletRequest request) {
//
//        String sessionId = getSessionid(request);
//
//        if (!StringUtils.isEmpty(sessionId)) {
//            SessionUser sessionUser = JedisUtil.getObject(sessionId, SessionUser.class);
//            if (sessionUser != null) {
//                return (SessionUser) sessionUser;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * @param request
//     *
//     * @return SessionUser
//     *
//     * @function 重设（刷新）session失效日期
//     * @date 2014-12-1 上午10:44:09
//     * @author v_zoupengfei
//     */
//    public static Long resetExpire(HttpServletRequest request) {
//        String sessionId = getSessionid(request);
//        SessionUser user = get(request);
//        if (user != null && !StringUtils.isEmpty(sessionId)) {
//            JedisUtil.expire(sessionId, SESSION_EXPIRE);
//            SessionUtils.setSessionUser(user);
//        }
//        return null;
//    }
//
//    /**
//     * @param request
//     *
//     * @return SessionUser
//     *
//     * @function 重设（刷新）session失效日期
//     * @date 2014-12-1 上午10:44:09
//     * @author v_zoupengfei
//     */
//    public static void del(HttpServletRequest request, HttpServletResponse response) {
//
//        String sessionId = getSessionid(request);
//        if (!StringUtils.isEmpty(sessionId)) {
//            JedisUtil.del(sessionId);
//            SessionUtils.remove();
//        }
//        // 清理Cookie
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (COOKIE_KEY.equals(cookie.getName())) {
//                    Cookie newCookie = new Cookie(COOKIE_KEY, null);
//                    cookie.setPath("/");
//                    cookie.setMaxAge(0);
//                    response.addCookie(newCookie);
//                }
//
//            }
//        }
//    }
//
//    private static String createSessionid(HttpServletRequest request, HttpServletResponse response,
//                                          SessionUser sessionUser) {
//        String ip = WebUtil.getIpAddr(request);
//        String sessionId =
//                DigestUtils.md5Hex(ip + ConfigConstants.UNDER_LINE + sessionUser.getUicUser().getUsername()
//                        + ConfigConstants.UNDER_LINE + DateUtils.getDate(ConfigConstants.DAY_PARTTEN_YYYYMMDDHHMMSS)
//                        + ConfigConstants.UNDER_LINE + getRandomString(6));
//        Cookie cookie = new Cookie(COOKIE_KEY, sessionId);
//        cookie.setMaxAge(SESSION_EXPIRE);
//        cookie.setPath("/");
//        response.addCookie(cookie);
//        sessionUser.setSessionId(sessionId);
//        return sessionId;
//    }
//
//    private static String getSessionid(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        String sessionId = "";
//        if (cookies == null) {
//            return "";
//        }
//        for (Cookie cook : cookies) {
//            if (COOKIE_KEY.equals(cook.getName())) {
//                if (!StringUtils.isEmpty(cook.getValue())) {
//                    sessionId = cook.getValue();
//                }
//
//            }
//        }
//        return sessionId;
//    }
//
//    private static String getRandomString(int length) {
//        Random random = new Random();
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < length; i++) {
//            int number = random.nextInt();
//            sb.append(number);
//        }
//        return sb.toString();
//    }
//
//}
