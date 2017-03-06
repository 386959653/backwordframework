package com.pds.p2p.core.jdbc.pk;

import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.InitializingBean;

public class SequenceFactory implements InitializingBean {

    private DataSource dataSource;
    private String incrementerName;
    private int increment;

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getIncrementerName() {
        return incrementerName;
    }

    public void setIncrementerName(String incrementerName) {
        this.incrementerName = incrementerName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(this.dataSource);
        Validate.notNull(this.incrementerName);
    }

    static final Map<String, SequenceGenerator> tblnameMap = new CaseInsensitiveMap<String, SequenceGenerator>();

    synchronized public SequenceGenerator name(String name) {
        SequenceGenerator result = tblnameMap.get(name);
        if (result == null) {
            result = new SequenceGenerator(dataSource, incrementerName, name, increment);
            tblnameMap.put(name, result);
        }
        return result;
    }

    public SequenceGenerator name(Class<?> clazz) {
        return name(clazz.getSimpleName());
    }

}
