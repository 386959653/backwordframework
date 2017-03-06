package com.pds.p2p.system.web.session;

import com.pds.p2p.core.j2ee.service.ServiceException;

public class SessionUtils {

    private static final ThreadLocal<SessionUser> threadLocal = new ThreadLocal<SessionUser>();

    public static void setSessionUser(SessionUser sessionUser) {

        threadLocal.set(sessionUser);

    }

    public static void remove() {

        threadLocal.remove();

    }

    public static SessionUser getSessionUser() {

        if (threadLocal != null) {
            return threadLocal.get();
        } else {
            throw new ServiceException("SESSION 为空");
        }
    }

}
