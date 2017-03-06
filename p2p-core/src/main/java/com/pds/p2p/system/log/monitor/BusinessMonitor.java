package com.pds.p2p.system.log.monitor;

import com.pds.p2p.core.utils.Exceptions;
import com.pds.p2p.system.log.LogBeanHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.pds.p2p.system.log.bean.BizLogBean;

public class BusinessMonitor {

    public static final Logger logger = LogManager.getLogger(BusinessMonitor.class);

    public static void businessLog() {
        BizLogBean bizLog = LogBeanHolder.getBizLogBean();
        if (StringUtils.equals("1", bizLog.getLevel())) {
            logger.info("<bizlog:meta>" + JSON.toJSONString(bizLog) + "</bizlog:meta>");
        }
    }

    public static void info(String message) {
        logger.info("<bizlog:meta>" + message + "</bizlog:meta>");
    }

    public static void error(String message) {
        logger.error("<bizlog:meta>" + message + "</bizlog:meta>");
    }

    public static void error(Exception e) {
        logger.error("<bizlog:meta>" + Exceptions.getStackTraceAsString(e) + "</bizlog:meta>");
    }
}
