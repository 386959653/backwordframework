/**
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.pds.p2p.system.log;

import com.pds.p2p.system.log.bean.AccLogBean;
import com.pds.p2p.system.log.bean.BizLogBean;

/**
 * @author v_zoupengfei
 */
public class LogBeanHolder {

    private static ThreadLocal<AccLogBean> accLogBeanLocal = new ThreadLocal<AccLogBean>();

    private static ThreadLocal<BizLogBean> bizLogBeanLocal = new ThreadLocal<BizLogBean>();

    public static AccLogBean getAccLogBean() {
        return accLogBeanLocal.get();
    }

    public static BizLogBean getBizLogBean() {
        return bizLogBeanLocal.get();
    }

    public static void setAccLogBean(AccLogBean logBean) {
        accLogBeanLocal.set(logBean);
    }

    public static void setBizLogBean(BizLogBean logBean) {
        bizLogBeanLocal.set(logBean);
    }

}
