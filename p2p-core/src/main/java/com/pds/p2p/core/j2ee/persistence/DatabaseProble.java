package com.pds.p2p.core.j2ee.persistence;

import javax.sql.DataSource;

import com.pds.p2p.core.jdbc.dialect.DialectFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;

import com.pds.p2p.core.jdbc.dialect.Dialect;
import com.pds.p2p.core.jdbc.helper.DatabaseType;
import com.pds.p2p.core.jdbc.helper.JdbcHelper;
import com.pds.p2p.core.jdbc.helper.JdbcHelperIF;

public class DatabaseProble implements InitializingBean {
    private DataSource defaultDatasource;
    private DatabaseType defaultDatabaseType;
    private Dialect dialect;
    private JdbcHelperIF jdbcHelperIF;

    public DataSource getDefaultDatasource() {
        return defaultDatasource;
    }

    public void setDefaultDatasource(DataSource defaultDatasource) {
        this.defaultDatasource = defaultDatasource;
    }

    public DatabaseType getDefaultDatabaseType() {
        return defaultDatabaseType;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public JdbcHelperIF getJdbcHelperIF() {
        return jdbcHelperIF;
    }

    /***
     * <pre>
     * CREATE TABLE `sys_sequence` (
     * 	`seq_val` bigint(20) NOT NULL AUTO_INCREMENT,
     *   PRIMARY KEY (`seq_val`)
     * ) ENGINE=MyISAM DEFAULT
     * CHARSET=utf8
     * insert into sys_sequence values(0)
     * </pre>
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.defaultDatabaseType = DatabaseType.fromMetaData(defaultDatasource);
        this.dialect = DialectFactory.get(this.defaultDatabaseType);
        this.jdbcHelperIF = new JdbcHelper(new JdbcTemplate(defaultDatasource));
    }

}
