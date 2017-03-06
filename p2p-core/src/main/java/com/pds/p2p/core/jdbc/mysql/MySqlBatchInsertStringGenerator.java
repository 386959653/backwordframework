package com.pds.p2p.core.jdbc.mysql;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import com.pds.p2p.core.utils.UtilString;
import com.pds.p2p.core.utils.UtilType;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class MySqlBatchInsertStringGenerator {
    /***
     * @param tableName
     * @param rows
     * @param fields
     *
     * @return
     */

    public static String build(String tableName, Collection<?> rows, String... fields) {
        return build(tableName, rows, false, ImmutableSet.<String>of(), fields);
    }

    public static String build(boolean underscoreFieldName, String tableName, List<?> rows, String... fields) {
        return build(underscoreFieldName, tableName, rows, false, ImmutableSet.<String>of(), fields);
    }

    /***
     * <pre>
     * 生成insert into （）on duplicate key update 语句
     * 主要针对批量数据
     *
     * </pre>
     *
     * @param tableName       表名
     * @param rows            数据列表，是一个list，数据项可以是pojo或map
     * @param duplicateUpdate 是否生成duplicate key update内容，如果true生成，false不生成
     * @param keys            如果duplicateUpdate为true表示依据keys做更新
     * @param fields          指定需要处理的字段，如果不设置此参数，表示插入或更新所有字段
     *
     * @return
     */
    public static String build(String tableName, Collection<?> rows, boolean duplicateUpdate, Set<String> keys,
                               String... fields) {
        return build(true, tableName, rows, duplicateUpdate, keys, fields);
    }

    public static String build(boolean underscoreFieldName, String tableName, Collection<?> rows,
                               boolean duplicateUpdate, Set<String> keys, String... fields) {
        Set<String> lowKeys = Sets.newHashSet();
        for (String key : keys) {
            lowKeys.add(key.toLowerCase());
        }
        StringBuilder result = new StringBuilder();
        StringBuilder buff = new StringBuilder();
        if (underscoreFieldName) {
            UtilString.underscoreName(tableName, buff);
            tableName = buff.toString();
        }
        result.append("insert ignore into ").append(tableName).append(" (");
        if (fields == null || fields.length == 0) {
            MetaObject metaObject = SystemMetaObject.forObject(rows.iterator().next());
            fields = metaObject.getGetterNames();
        }
        if (!duplicateUpdate && !CollectionUtils.isEmpty(keys)) {
            List<String> tflds = Lists.newArrayListWithCapacity(fields.length);
            for (String fld : fields) {
                if (!lowKeys.contains(fld.toLowerCase())) {
                    tflds.add(fld);
                }
            }
            fields = tflds.toArray(new String[0]);
        }
        for (String field : fields) {
            if (underscoreFieldName) {
                UtilString.underscoreName(field, buff);
                field = buff.toString();
            } else {
                field = field.toLowerCase();
            }
            result.append(mysqlqu(field)).append(",");
        }
        result.setCharAt(result.length() - 1, ')');
        result.append(" values ");
        for (Object row : rows) {
            result.append("(");
            MetaObject metaObject = SystemMetaObject.forObject(row);
            for (String field : fields) {
                Object val = metaObject.getValue(field);
                if (val == null) {
                    result.append("null");
                } else {
                    Class<?> intClazz = val.getClass();
                    if (TypeUtils.isAssignable(intClazz, Number.class)) {
                        result.append(val);
                    } else if (UtilType.isJavaStringType(intClazz)) {
                        if (UtilType.isEmpty(val)) {
                            result.append("''");
                        } else {
                            result.append("'"). //
                                    append(MySqlStringFilter
                                    .filtrateSQL(val.toString(), MySqlStringFilter.MYSQL_CHAR_FOR_UPDATE))//
                                    .append("'");
                        }
                    } else if (UtilType.isJavaDateType(intClazz)) {
                        Date dt = (Date) UtilType.convert(val, Date.class);
                        result.append("'").append(DateFormatUtils.format(dt, UtilType.YYYY_MM_DD_HH_MM_SS)).append("'");
                    } else {
                        String s = val.toString();
                        if (StringUtils.startsWith(s, "'") && StringUtils.endsWith(s, "'")) {
                            s = StringUtils.substringBetween(s, "'");
                        }
                        result.append("'"). //
                                append(MySqlStringFilter.filtrateSQL(s, MySqlStringFilter.MYSQL_CHAR_FOR_UPDATE))//
                                .append("'");
                    }
                }
                result.append(",");
            }
            result.setCharAt(result.length() - 1, ')');
            result.append(",");
        }
        result.setCharAt(result.length() - 1, ' ');
        if (duplicateUpdate) {
            result.append("  on duplicate key update ");
            for (String field : fields) {
                if (!lowKeys.contains(field.toLowerCase())) {
                    String nm = null;
                    if (underscoreFieldName) {
                        UtilString.underscoreName(field, buff);
                        nm = mysqlqu(buff.toString());
                    } else {
                        nm = mysqlqu(field.toLowerCase());
                    }
                    result.append(nm).append("=values(").append(nm).append("),");
                }
            }
            result.setCharAt(result.length() - 1, ' ');
        }
        return result.toString();
    }

    /***
     * 复制表数据
     *
     * @param targetableName
     * @param sourceTableName
     * @param duplicateUpdate
     * @param keys
     * @param fields
     *
     * @return
     */
    public static String build(String targetableName, String sourceTableName, boolean duplicateUpdate, Set<String> keys,
                               String[] fields) {
        Set<String> upperKeys = Sets.newHashSet();
        for (String key : keys) {
            upperKeys.add(key.toUpperCase());
        }
        StringBuilder result = new StringBuilder();
        StringBuilder fieldBuff = new StringBuilder();
        if (!duplicateUpdate && !CollectionUtils.isEmpty(keys)) {
            List<String> tflds = Lists.newArrayListWithCapacity(fields.length);
            for (String fld : fields) {
                if (!upperKeys.contains(fld.toUpperCase())) {
                    tflds.add(fld);
                }
            }
            fields = tflds.toArray(new String[0]);
        }
        for (String field : fields) {
            fieldBuff.append(mysqlqu(field)).append(",");
        }
        fieldBuff.setCharAt(result.length() - 1, ' ');

        result.append("insert into ").append(targetableName).append(" (");
        result.append(fieldBuff).append(")");
        result.append(" select ").append(fieldBuff).append(" from ").append(sourceTableName);
        if (duplicateUpdate) {
            result.append("  on duplicate key update ");
            for (String field : fields) {
                if (!upperKeys.contains(field.toUpperCase())) {
                    String nm = mysqlqu(field);
                    result.append(mysqlqu(field)).append("=values(").append(nm).append("),");
                }
            }
            result.setCharAt(result.length() - 1, ' ');
        }
        return result.toString();
    }

    private static String mysqlqu(String field) {
        if (field.charAt(0) != '`') {
            return '`' + field + '`';
        }
        return field;
    }

}
