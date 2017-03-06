package com.pds.p2p.core.jdbc.sql;

/**
 * SQL语句构造器。
 *
 * @author redraiment
 * @since 1.0
 */
public interface SqlBuilder {
    public SqlBuilder insert();

    public SqlBuilder into(String table);

    public SqlBuilder values(String... columns);

    public SqlBuilder update(String table);

    public SqlBuilder set(String... columns);

    public SqlBuilder select(String... columns);

    public SqlBuilder delete();

    public SqlBuilder from(String table);

    public SqlBuilder from(Class<?> clazz);

    public SqlBuilder join(String table);

    public SqlBuilder on(String... conditions);

    public SqlBuilder where(String... conditions);

    public SqlBuilder groupBy(String... columns);

    public SqlBuilder having(String... conditions);

    public SqlBuilder orderBy(String... columns);

    public SqlBuilder limit(int limit);

    public SqlBuilder offset(int offset);

    @Override
    public String toString();
}
