/**
 * Copyright (C) 2014 Baidu, Inc. All Rights Reserved.
 */
package com.pds.p2p.system.web.session;



/*********************************
 * * 百度供应商平台 功能模块：session用户对象
 * ********************************
 *
 * @author v_zoupengfei
 * @version 1.0.0
 *          ********************************
 *          修改项 修改人 修改时间
 *          <p>
 *          ********************************
 * @date 2014-11-30 下午11:27:29
 * @copyright baidu.com 2014
 */
public class SessionUser {
    private String sessionId;

    private String username;

//    private UICUserInfoDto uicUser;
    private String uicUser;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

//    public UICUserInfoDto getUicUser() {
//        return uicUser;
//    }

//    public void setUicUser(UICUserInfoDto uicUser) {
//        this.uicUser = uicUser;
//        this.setUsername(uicUser.getUsername());
//    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

//    public static SessionUser newSessionUser(UICUserInfoDto uicUser) {
//        SessionUser sessionUser = new SessionUser();
//        sessionUser.setUicUser(uicUser);
//        sessionUser.setUsername(uicUser.getUsername());
//        return sessionUser;
//    }

}
