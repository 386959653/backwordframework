package com.pds.p2p.core.jdbc.ar;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pds.p2p.core.jdbc.ar.ex.IllegalFieldNameException;
import com.pds.p2p.core.jdbc.helper.JdbcHelper;
import com.pds.p2p.core.jdbc.meta.ColumnMeta;
import com.pds.p2p.core.jdbc.sql.SqlBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pds.p2p.core.jdbc.meta.TableMeta;
import com.pds.p2p.core.jdbc.sql.TSqlBuilder;
import com.pds.p2p.core.utils.StringUtils;
import com.pds.p2p.core.utils.UtilType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 表对象。
 *
 * @author redraiment
 * @since 1.0
 */
public final class Table {
    final DB dbo;
    final String tableName;
    final Map<String, Association> relations;
    final Map<String, Lambda> hooks;
    final String primaryKey;

    final TableMeta tableMeta;
    ColumnMeta pkColumnMeta;

    private String foreignTable;
    private final Map<String, Object> foreignKeys = new HashMap<>();

    private boolean isAutoKey;

    Table(DB dbo, String name, TableMeta tableMeta, Map<String, Association> relations, Map<String, Lambda> hooks) {
        this.dbo = dbo;
        this.tableName = StringUtils.toUnderScoreCase(name);
        this.relations = relations;
        this.hooks = hooks;
        this.pkColumnMeta = tableMeta.getAutoIncrementColumnMeta();
        this.isAutoKey = pkColumnMeta != null;
        if (!isAutoKey) {
            this.pkColumnMeta = tableMeta.getPrimaryKeyolumnMeta();
        }
        Validate.notNull(pkColumnMeta, "%s primaryKey required!", name);
        this.primaryKey = tableName.concat(".").concat(pkColumnMeta.getName());
        this.tableMeta = tableMeta;
    }

    /**
     * 继承给定的JavaBean，扩展Record对象的get和set方法。
     *
     * @param bean 希望被继承的JavaBean
     *
     * @return 返回Table自身
     *
     * @since 2.3
     */
    public Table extend(Object bean) {
        Class<?> type = bean.getClass();
        for (Method method : type.getDeclaredMethods()) {
            Class<?> returnType = method.getReturnType();
            Class<?>[] params = method.getParameterTypes();
            String key = method.getName();
            if (params.length == 2 && key.length() > 3 && (key.startsWith("get") || key.startsWith("set")) && params[0]
                    .isAssignableFrom(Record.class) && params[1].isAssignableFrom(Object.class)
                    && Object.class.isAssignableFrom(returnType)) {
                key = key.replaceAll("(?=[A-Z])", "_").toLowerCase();
                hooks.put(key, new Lambda(bean, method));
            }
        }
        return this;
    }

    /* Association */
    private Association assoc(String name, boolean onlyOne, boolean ancestor) {
        name = DB.parseKeyParameter(name);
        Association assoc = new Association(relations, name, onlyOne, ancestor);
        relations.put(name, assoc);
        return assoc;
    }

    public Association belongsTo(String name) {
        return assoc(name, true, false);
    }

    public Association hasOne(String name) {
        return assoc(name, true, true);
    }

    public Association hasMany(String name) {
        return assoc(name, false, true);
    }

    public Association hasAndBelongsToMany(String name) {
        return assoc(name, false, false);
    }

    private String[] getForeignKeys() {
        List<String> conditions = new ArrayList<>(foreignKeys.size());
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> e : foreignKeys.entrySet()) {
            sb.setLength(0);
            sb.append(tableName).append(".").append(e.getKey()).append("=");
            Object val = e.getValue();
            if (UtilType.isJavaStringType(val.getClass())) {
                sb.append("'").append(val).append("'");
            } else {
                sb.append(val);
            }
            conditions.add(sb.toString());
        }
        return conditions.toArray(new String[0]);
    }

    public Table constrain(String foreignKey, Object val) {
        foreignKeys.put(DB.parseKeyParameter(foreignKey), val);
        return this;
    }

    public Table join(String table) {
        this.foreignTable = table;
        return this;
    }

    public ColumnMeta getPkColumnMeta() {
        return pkColumnMeta;
    }

    public TableMeta getTableMeta() {
        return tableMeta;
    }

    public Record makeRecord() {
        return new Record(this);
    }

    /* CRUD */
    public Record create(Object... args) {
        Map<String, Object> data = Maps.newLinkedHashMap();
        data.putAll(foreignKeys);
        for (int i = 0; i < args.length; i += 2) {
            String key = DB.parseKeyParameter(args[i].toString());
            if (!this.tableMeta.containsColumn(key)) {
                throw new IllegalFieldNameException(key);
            }
            Object value = args[i + 1];
            data.put(key, value);
        }

        final String[] fields = new String[data.size()];
        final int[] types = new int[data.size()];
        final Object[] values = new Object[data.size()];

        int index = 0;
        for (Map.Entry<String, Object> e : data.entrySet()) {
            ColumnMeta columnMeta = this.tableMeta.findColumn(e.getKey());
            fields[index] = columnMeta.getName();
            types[index] = columnMeta.getTypeCode();
            values[index] = e.getValue();
            boolean istext = columnMeta.isOfTextType();
            boolean required = columnMeta.isRequired();
            if (required && values[index] == null) {
                Validate.notNull(values[index], "[%s].%s required,but is null!", this.tableName, columnMeta.getName());
            }
            if (istext && values[index] != null) {
                Validate.isTrue(values[index].toString().length() <= columnMeta.getSizeAsInt(),
                        "%s.%s size must be %d-%s", this.tableName, columnMeta.getName(), columnMeta.getSizeAsInt(),
                        values[index]);
            }
            index++;
        }

        final SqlBuilder sql = new TSqlBuilder();
        Record record = new Record(this, fields, values);
        long newId = -1L;
        if (this.isAutoKey) {
            sql.insert().into(tableName).values(fields);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            dbo.getJdbcTemplate().update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                    PreparedStatement ps = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
                    for (int i = 0; i < values.length; i++) {
                        StatementCreatorUtils.setParameterValue(ps, i + 1, types[i], values[i]);
                    }
                    return ps;
                }
            }, keyHolder);
            newId = keyHolder.getKey().intValue();
        } else if (dbo.getDataFieldMaxValueIncrementer() != null) {
            newId = dbo.getDataFieldMaxValueIncrementer().nextLongValue();
        } else if (dbo.getSequenceFactory() != null) {
            newId = dbo.getSequenceFactory().name(this.tableName).nextUniqueID();
        } else if (this.dbo.getDialect().supportsSequence()) {
            String seqClauseSql = this.dbo.getDialect().getSelectSequenceClause("SEQ_" + tableName.toUpperCase());
            newId = dbo.getJdbcTemplate().queryForObject(seqClauseSql, Long.class);
        } else {

        }
        if (!this.isAutoKey) {
            String[] nfields = ArrayUtils.add(fields, this.pkColumnMeta.getName());
            int[] ntypes = ArrayUtils.add(types, this.pkColumnMeta.getTypeCode());
            Object[] nvalues = ArrayUtils.add(values, newId);
            sql.insert().into(tableName).values(nfields);
            dbo.getJdbcTemplate().update(sql.toString(), nvalues, ntypes);
        }
        record.set(this.pkColumnMeta.getName(), newId);
        return record;
    }

    /**
     * <pre>
     * 根据现有的Record创建新的Record.
     * 为跨数据库之间导数据提供便捷接口；
     * 同时也方便根据模板创建多条相似的纪录。
     * </pre>
     *
     * @param o Record对象
     *
     * @return 根据参数创建的新的Record对象
     */
    public Record create(Record o) {
        List<Object> params = Lists.newArrayList();
        ColumnMeta[] columnMetas = this.tableMeta.getColumns();
        for (ColumnMeta columnMeta : columnMetas) {
            String simplenm = columnMeta.getSimpleName();
            if (!foreignKeys.containsKey(columnMeta.getSimpleName())) {
                Object val = o.get(simplenm);
                if (val != null) {
                    params.add(simplenm);
                    params.add(o.get(simplenm));
                }
            }
        }
        return create(params.toArray());
    }

    /***
     * 更新记录
     *
     * @param record    记录对象
     * @param selective
     */
    public void updateByPrimaryKey(Record record, boolean selective) {
        Object id = record.get(this.pkColumnMeta.getName());
        Validate.notNull(id, "update require pk field", this.pkColumnMeta.getName());

        List<String> fields = Lists.newArrayList();
        List<Object> values = Lists.newArrayList();
        List<Integer> types = Lists.newArrayList();

        ColumnMeta[] columns = this.tableMeta.getColumns();
        for (ColumnMeta column : columns) {
            if (column.isAutoIncrement() || column.isPrimaryKey()) {
                continue;
            }
            Object val = record.get(column.getName());
            if (!selective || val != null) {
                fields.add(column.getName());
                types.add(column.getTypeCode());
                values.add(val);
            }
        }

        SqlBuilder sql = new TSqlBuilder();
        sql.update(tableName).set(fields.toArray(new String[0])).where(String.format("%s=?", primaryKey));

        fields.add(this.primaryKey);
        types.add(pkColumnMeta.getTypeCode());
        values.add(record.get(this.pkColumnMeta.getName()));

        Object[] args = values.toArray();
        int[] argTypes = ArrayUtils.toPrimitive(types.toArray(new Integer[0]));

        dbo.getJdbcTemplate().update(sql.toString(), args, argTypes);
    }

    public void updateByPrimaryKey(Record record) {
        this.updateByPrimaryKey(record, true);
    }

    public void deleteByPrimaryKey(Record record) {
        Object id = record.get(this.pkColumnMeta.getName());
        Validate.notNull(id, "delete require pk field", this.pkColumnMeta.getName());
        SqlBuilder sql = new TSqlBuilder();
        sql.delete().from(tableName).where(String.format("%s = ?", primaryKey));
        dbo.getJdbcTemplate().update(sql.toString(), id);
    }

    public void purge() {
        // TODO: need enhancement
        for (Record record : all()) {
            deleteByPrimaryKey(record);
        }
    }

    List<Record> query(SqlBuilder sql, Object... args) {
        List<Record> records = Lists.newArrayList();
        JdbcHelper helper = new JdbcHelper(this.dbo.getJdbcTemplate());
        List<Map<String, Object>> maps = helper.directQueryForMapList(sql.toString(), args);
        for (Map<String, Object> values : maps) {
            records.add(new Record(this, values));
        }
        return records;
    }

    /**
     * query
     **/
    public Query select(String... columns) {
        Query sql = new Query(this);
        if (columns == null || columns.length == 0) {
            sql.select(String.format("%s.*", tableName));
        } else {
            StringBuilder sb = new StringBuilder();
            for (String column : columns) {
                ColumnMeta columnMeta = this.tableMeta.findColumn(column);
                Validate.notNull(columnMeta);
                sb.append(columnMeta.getName()).append(",");
            }
            sb.setLength(sb.length() - 1);
            sql.select(sb.toString());
        }
        sql.from(tableName);
        if (foreignTable != null && !foreignTable.isEmpty()) {
            sql.join(foreignTable);
        }
        if (!foreignKeys.isEmpty()) {
            for (String condition : getForeignKeys()) {
                sql.where(condition);
            }
        }
        return sql.orderBy(primaryKey);
    }

    public Record first() {
        return select().limit(1).one();
    }

    public Record first(String condition, Object... args) {
        return select().where(condition).limit(1).one(args);
    }

    public Record last() {
        return select().orderBy(primaryKey.concat(" desc")).limit(1).one();
    }

    public Record last(String condition, Object... args) {
        return select().where(condition).orderBy(primaryKey.concat(" desc")).limit(1).one(args);
    }

    public Record find(long id) {
        return first(primaryKey.concat(" = ?"), id);
    }

    /**
     * 根据指定列，返回符合条件的第一条记录.
     *
     * @param key   要匹配的列名
     * @param value 要匹配的值
     *
     * @return 返回符合条件的第一条记录
     */
    public Record findOne(String key, Object value) {
        key = _getKey(key);
        if (value != null) {
            return first(key.concat(" = ?"), value);
        } else {
            return first(key.concat(" is null"));
        }
    }

    public List<Record> findBy(String key, Object value) {
        key = _getKey(key);
        if (value != null) {
            return where(key.concat(" = ?"), value);
        } else {
            return where(key.concat(" is null"));
        }
    }

    /**
     * @param key
     *
     * @return
     */
    private String _getKey(String key) {
        key = DB.parseKeyParameter(key);
        ColumnMeta columnMeta = this.tableMeta.findColumn(key);
        key = columnMeta.getName();
        return key;
    }

    public List<Record> all() {
        return select().all();
    }

    public List<Record> where(String condition, Object... args) {
        return select().where(condition).all(args);
    }

    public DB getDb() {
        return dbo;
    }

    public List<Record> paging(int page, int size) {
        return select().limit(size).offset(page * size).all();
    }

}
