package com.pds.p2p.core.jdbc.ar;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import com.pds.p2p.core.jdbc.ar.ex.DBOpenException;
import com.pds.p2p.core.jdbc.ar.ex.IllegalTableNameException;
import com.pds.p2p.core.jdbc.ar.pool.SingletonDataSource;
import com.pds.p2p.core.jdbc.dialect.Dialect;
import com.pds.p2p.core.jdbc.dialect.DialectFactory;
import com.pds.p2p.core.jdbc.helper.DatabaseType;
import com.pds.p2p.core.jdbc.helper.EnMap;
import com.pds.p2p.core.jdbc.meta.ColumnMeta;
import com.pds.p2p.core.jdbc.meta.DefaultSizes;
import com.pds.p2p.core.jdbc.pk.SequenceFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.pds.p2p.core.jdbc.meta.TableMeta;
import com.pds.p2p.core.utils.StringUtils;

/**
 * 数据库对象。
 *
 * @author redraiment
 * @since 1.0
 */
public final class DB {

    public static DB open(String url) {
        return open(url, new Properties());
    }

    public static DB open(String url, String username, String password) {
        Properties info = new Properties();
        info.put("user", username);
        info.put("password", password);
        return open(url, info);
    }

    public static DB open(String url, Properties info) {
        try {
            return open(new SingletonDataSource(url, info));
        } catch (SQLException e) {
            throw new DBOpenException(e);
        }
    }

    public static DB open(DataSource pool) {
        return new DB(pool);
    }

    private final DataSource dataSource;
    private final Map<String, TableMeta> tableMetas;
    private final Map<String, Map<String, Association>> relations;
    private final Map<String, Map<String, Lambda>> hooks;
    private final JdbcTemplate jdbcTemplate;
    private Dialect dialect;
    private DataFieldMaxValueIncrementer dataFieldMaxValueIncrementer;
    private SequenceFactory sequenceFactory;

    public DB(DataSource pool) {
        DatabaseType databaseType;
        try {
            databaseType = DatabaseType.fromMetaData(pool);
            this.dialect = DialectFactory.get(databaseType);
            this.dataSource = pool;
            this.tableMetas = new EnMap<>();
            this.relations = new EnMap<>();
            this.hooks = new EnMap<>();
            this.jdbcTemplate = new JdbcTemplate(pool);
        } catch (MetaDataAccessException e) {
            throw new DBOpenException(e);
        }
    }

    public DB(DataSource pool, DataFieldMaxValueIncrementer dataFieldMaxValueIncrementer) {
        this(pool);
        this.dataFieldMaxValueIncrementer = dataFieldMaxValueIncrementer;
    }

    public DB(DataSource pool, SequenceFactory sequenceFactory) {
        this(pool);
        this.sequenceFactory = sequenceFactory;
    }

    public SequenceFactory getSequenceFactory() {
        return sequenceFactory;
    }

    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public Set<String> getTableNames() {
        Set<String> tables = new HashSet<String>();
        try (Connection c = dataSource.getConnection()) {
            DatabaseMetaData db = c.getMetaData();
            try (ResultSet rs = db.getTables(null, null, "%", new String[] {"TABLE"})) {
                while (rs.next()) {
                    tables.add(rs.getString("table_name"));
                }
            }
        } catch (SQLException e) {
            throw new DBOpenException(e);
        }
        return tables;
    }

    public Set<Table> getTables() {
        Set<Table> tables = new HashSet<Table>();
        for (String name : getTableNames()) {
            tables.add(active(name));
        }
        return tables;
    }

    public TableMeta getTableMeta(final String name) throws SQLException {
        if (!tableMetas.containsKey(name)) {
            synchronized(tableMetas) {
                if (!tableMetas.containsKey(name)) {
                    try {
                        JdbcUtils.extractDatabaseMetaData(dataSource, new DatabaseMetaDataCallback() {
                            @Override
                            public Object processMetaData(DatabaseMetaData dbmd)
                                    throws SQLException, MetaDataAccessException {
                                String catalog, schema, table;
                                String tnm = StringUtils.toUnderScoreCase(name);
                                String[] patterns = tnm.split("\\.");
                                if (patterns.length == 1) {
                                    catalog = null;
                                    schema = null;
                                    table = patterns[0];
                                } else if (patterns.length == 2) {
                                    catalog = null;
                                    schema = patterns[0];
                                    table = patterns[1];
                                } else if (patterns.length == 3) {
                                    catalog = patterns[0];
                                    schema = patterns[1];
                                    table = patterns[2];
                                } else {
                                    throw new IllegalArgumentException(String.format("Illegal table name: %s", name));
                                }
                                TableMeta tableMeta = buildTableMeta(dbmd, catalog, schema, table);
                                tableMetas.put(table, tableMeta);
                                return dbmd;
                            }
                        });
                    } catch (MetaDataAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return tableMetas.get(name);
    }

    private TableMeta buildTableMeta(DatabaseMetaData dbmd, String catalog, String schema, String table)
            throws SQLException {
        TableMeta tableMeta = new TableMeta();
        ResultSet rs = dbmd.getColumns(catalog, schema, table, null);
        while (rs.next()) {
            ColumnMeta column = new ColumnMeta();
            column.setName(rs.getString("COLUMN_NAME"));
            column.setDefaultValue(rs.getString("COLUMN_DEF"));
            column.setTypeCode(rs.getInt("DATA_TYPE"));
            column.setPrecisionRadix(rs.getInt("NUM_PREC_RADIX"));
            String size = rs.getString("COLUMN_SIZE");
            int scale = rs.getInt("DECIMAL_DIGITS");
            if (size == null) {
                size = DefaultSizes.get(column.getTypeCode());
            }
            // we're setting the size after the precision and radix in case
            // the database prefers to return them in the size value
            column.setSize(size);
            if (scale != 0) {
                // if there is a scale value, set it after the size (which
                // probably did not contain
                // a scale specification)
                column.setScale(scale);
            }
            column.setRequired("NO".equalsIgnoreCase(rs.getString("IS_NULLABLE").trim()));
            column.setDescription(rs.getString("REMARKS"));
            tableMeta.addColumnMeta(column);
        }
        ResultSet pkRs = dbmd.getPrimaryKeys(catalog, schema, table);
        while (pkRs.next()) {
            String columnName = pkRs.getString("COLUMN_NAME");
            ColumnMeta columnMeta = tableMeta.findColumn(columnName);
            columnMeta.setPrimaryKey(true);
        }
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeResultSet(pkRs);
        if (this.dialect.supportAutoIncrement()) {
            this.determineAutoIncrementFromResultSetMetaData(table, tableMeta.getColumns());
        }
        return tableMeta;
    }

    public DataFieldMaxValueIncrementer getDataFieldMaxValueIncrementer() {
        return dataFieldMaxValueIncrementer;
    }

    public Table active(String name) {
        name = dialect.getCaseIdentifier(name);
        if (!relations.containsKey(name)) {
            synchronized(relations) {
                if (!relations.containsKey(name)) {
                    relations.put(name, new EnMap<String, Association>());
                }
            }
        }
        if (!hooks.containsKey(name)) {
            synchronized(hooks) {
                if (!hooks.containsKey(name)) {
                    hooks.put(name, new EnMap<String, Lambda>());
                }
            }
        }
        try {
            return new Table(this, name, this.getTableMeta(name), relations.get(name), hooks.get(name));
        } catch (SQLException e) {
            throw new IllegalTableNameException(name, e);
        }
    }

    /* Utility */
    public static Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    static String parseKeyParameter(String name) {
        // name = name.toLowerCase();
        if (name.startsWith(":")) {
            name = name.substring(1, name.length());
        }
        return name;
    }

    public static String removeUnderScore(String name) {
        return StringUtils.remove(name, '_');
    }

    /*
     *
     * Helper method that determines the auto increment status for the given
     * columns via the {@link ResultSetMetaData#isAutoIncrement(int)} method.
     *
     * @param table The table
     *
     * @param columnsToCheck The columns to check (e.g. the primary key columns)
     */
    protected void determineAutoIncrementFromResultSetMetaData(String table, final ColumnMeta[] columnsToCheck)
            throws SQLException {
        if (columnsToCheck == null || columnsToCheck.length == 0) {
            return;
        }
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        for (int idx = 0; idx < columnsToCheck.length; idx++) {
            if (idx > 0) {
                query.append(",");
            }
            query.append(columnsToCheck[idx].getName());
        }
        query.append(" FROM ");
        query.append(table);
        query.append(" WHERE 1 = 0");

        this.jdbcTemplate.query(query.toString(), new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                ResultSetMetaData rsMetaData = rs.getMetaData();
                for (int idx = 0; idx < columnsToCheck.length; idx++) {
                    if (rsMetaData.isAutoIncrement(idx + 1)) {
                        columnsToCheck[idx].setAutoIncrement(true);
                    }
                }
                return null;
            }
        });
    }
}
