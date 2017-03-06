/**
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.pds.p2p.system.log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pds.p2p.core.j2ee.context.Config;
import com.pds.p2p.system.config.ConfigConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pds.p2p.core.utils.WebUtil;
import com.pds.p2p.system.log.bean.AccLogBean;
import com.pds.p2p.system.log.bean.BizLogBean;
import com.pds.p2p.system.log.monitor.AccLogMonitor;
import com.pds.p2p.system.log.monitor.BusinessMonitor;

/**
 * @author wuyunfeng
 */
public class LogTraceManager {
    public static final Logger logger = LogManager.getLogger(LogTraceManager.class);
    private static final String LOG_PID = Config.get(ConfigConstants.APPLICATION_LOG_PID);

    public static void beginTrace(HttpServletRequest request, HttpServletResponse response) {
        String userId = "Anonymous";

        String uri = request.getRequestURI();
        uri = uri.replaceFirst(request.getContextPath(), "");

        AccLogBean accLog = new AccLogBean(LOG_PID, uri, MonitLogType.PV.code, System.currentTimeMillis(),
                WebUtil.getIpAddr(request));
        accLog.setUID(userId);

        BizLogBean bizLog = new BizLogBean(System.currentTimeMillis(), request.getLocalName(), LOG_PID);
        bizLog.setLevel("9");

        LogBeanHolder.setBizLogBean(bizLog);
        LogBeanHolder.setAccLogBean(accLog);
    }

    public static void endTrace(HttpServletRequest request, HttpServletResponse response) {

        AccLogMonitor.monitorLog();
        BusinessMonitor.businessLog();

    }
}
