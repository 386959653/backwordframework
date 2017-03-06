package com.pds.p2p.system.config;

import java.net.InetAddress;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.pds.p2p.core.j2ee.context.Config;

public class InitConfigListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {

            String logDir = Config.get(ConfigConstants.APPLICATION_LOG_DIR);

            System.setProperty(ConfigConstants.APPLICATION_LOG_DIR, logDir);

            InetAddress host = InetAddress.getLocalHost();
            System.setProperty(ConfigConstants.P2P_HOST_NAME, host.getHostName());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}
