package com.pds.p2p.core.jdbc.helper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.sql.DataSource;

import com.pds.p2p.core.j2ee.context.SpringContextHolder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcHelperLogger implements InvocationHandler {
    final private Logger logger = org.slf4j.LoggerFactory.getLogger(JdbcHelperLogger.class);

    private JdbcHelperIF helperIF;

    public JdbcHelperLogger(JdbcHelperIF helperIF) {
        super();
        this.helperIF = helperIF;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        StopWatch stopWatch = null;
        stopWatch = new StopWatch();
        stopWatch.start();
        logger.info(method.getName() + " start...");
        Object result = method.invoke(helperIF, args);
        logger.info(ArrayUtils.toString(args));
        stopWatch.stop();
        logger.info("consum:" + stopWatch.toString());
        logger.info(method.getName() + " end.");
        return result;
    }

    public static JdbcHelperIF newInstance(JdbcHelperIF helperIF) {
        InvocationHandler handler = new JdbcHelperLogger(helperIF);
        ClassLoader cl = JdbcHelperIF.class.getClassLoader();
        return (JdbcHelperIF) Proxy.newProxyInstance(cl, new Class[] {JdbcHelperIF.class}, handler);
    }

    public static JdbcHelperIF newInstance(JdbcTemplate jdbcTemplate) {
        JdbcHelperIF helperIF = new JdbcHelper(jdbcTemplate);
        return newInstance(helperIF);
    }

    public static JdbcHelperIF newInstance(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        JdbcHelperIF helperIF = new JdbcHelper(jdbcTemplate);
        return newInstance(helperIF);
    }

    public static JdbcHelperIF newInstance(String ds) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(SpringContextHolder.getBean(ds, DataSource.class));
        JdbcHelperIF helperIF = new JdbcHelper(jdbcTemplate);
        return newInstance(helperIF);
    }

}
