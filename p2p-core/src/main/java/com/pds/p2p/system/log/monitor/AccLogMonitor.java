package com.pds.p2p.system.log.monitor;

import com.pds.p2p.system.log.LogBeanHolder;
import com.pds.p2p.system.log.bean.AccLogBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;

public class AccLogMonitor {

    public static final Logger logger = LogManager.getLogger(AccLogMonitor.class);

    public static void monitorLog() {

        AccLogBean monitorBean = LogBeanHolder.getAccLogBean();

        long rtime = System.currentTimeMillis() - monitorBean.STIME;

        monitorBean.setRTIME(rtime);

        logger.info("<acclog:meta>" + JSON.toJSONString(monitorBean) + "</acclog:meta>");

    }

}
