package com.pds.p2p.system.config;

import java.util.Properties;

import com.pds.p2p.core.j2ee.context.Config;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class PropertyLoad extends PropertyPlaceholderConfigurer {

    /***
     * @param beanFactory
     *
     * @throws BeansException
     * @date 2016-5-12
     * @author 吴运峰
     */
    @Override
    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Properties properties = Config.getPrperties();
        setProperties(properties);

        super.postProcessBeanFactory(beanFactory);
    }

}
