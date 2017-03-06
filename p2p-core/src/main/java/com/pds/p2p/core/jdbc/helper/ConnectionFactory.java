package com.pds.p2p.core.jdbc.helper;

import java.sql.Connection;
import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;
import com.pds.p2p.core.j2ee.context.Config;

public class ConnectionFactory {
    private static interface Singleton {
        final ConnectionFactory INSTANCE = new ConnectionFactory();
    }

    private final DruidDataSource dataSource;

    private ConnectionFactory() {
        this.dataSource = new DruidDataSource();
        try {
            this.dataSource.setDriverClassName(Config.get("jdbc.driver"));
            this.dataSource.setUrl(Config.get("jdbc.url"));
            this.dataSource.setUsername(Config.get("jdbc.username"));
            this.dataSource.setPassword(Config.get("jdbc.password"));
            this.dataSource.init();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static Connection getDatabaseConnection() throws SQLException {
        return Singleton.INSTANCE.dataSource.getConnection();
    }
}
