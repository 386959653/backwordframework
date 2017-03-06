package com.pds.p2p.core.j2ee.service;

import javax.sql.DataSource;

import com.pds.p2p.core.jdbc.helper.JdbcHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class JdbcHelperService {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private JdbcHelper jdbcHelper;

    public void setDataSource(DataSource dataSource) {
        this.jdbcHelper = new JdbcHelper(new JdbcTemplate(dataSource));
    }

    public JdbcHelper getJdbcHelper() {
        return jdbcHelper;
    }

}
