package com.pds.p2p.core.jdbc.sql;

import java.util.Arrays;

import com.pds.p2p.core.utils.Seq;
import com.pds.p2p.core.utils.StringUtils;

/**
 * TSQL构造器。
 *
 * @author redraiment
 * @since 1.0
 */
public class TSqlBuilder extends AbstractSqlBuilder {
    @Override
    public SqlBuilder insert() {
        return start(Mode.Insert);
    }

    @Override
    public SqlBuilder into(String table) {
        return setTables(table);
    }

    @Override
    public SqlBuilder values(String... columns) {
        return setFields(columns);
    }

    @Override
    public SqlBuilder update(String table) {
        start(Mode.Update);
        return setTables(table);
    }

    @Override
    public SqlBuilder set(String... columns) {
        return setFields(columns);
    }

    @Override
    public SqlBuilder select(String... columns) {
        start(Mode.Select);
        if (columns != null && columns.length > 0) {
            return setFields(columns);
        } else {
            return setFields("*");
        }
    }

    @Override
    public SqlBuilder delete() {
        return start(Mode.Delete);
    }

    @Override
    public SqlBuilder from(String table) {
        return setTables(table);
    }

    @Override
    public SqlBuilder from(Class<?> clazz) {
        return this.from(tableName(clazz));
    }

    protected String tableName(Class<?> clazz) {
        javax.persistence.Table table = clazz.getAnnotation(javax.persistence.Table.class);
        if (table != null && StringUtils.isNotEmpty(table.name())) {
            return table.name();
        }
        String tableNm = clazz.getSimpleName();
        return StringUtils.toUnderScoreCase(tableNm);
    }

    @Override
    public SqlBuilder join(String table) {
        return addTable(table);
    }

    @Override
    public SqlBuilder on(String... conditions) {
        String table = tables.removeLast();
        return addTable(table.concat(" on ").concat(Seq.join(Arrays.asList(conditions), "and")));
    }

    @Override
    public SqlBuilder where(String... conditions) {
        return setConditions(conditions);
    }

    @Override
    public SqlBuilder groupBy(String... columns) {
        return setGroups(columns);
    }

    @Override
    public SqlBuilder having(String... conditions) {
        return setHaving(conditions);
    }

    @Override
    public SqlBuilder orderBy(String... columns) {
        return setOrders(columns);
    }

    @Override
    public SqlBuilder limit(int limit) {
        return setLimit(limit);
    }

    @Override
    public SqlBuilder offset(int offset) {
        return setOffset(offset);
    }
}
