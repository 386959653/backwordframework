package com.pds.p2p.core.jdbc.mysql;

import java.util.regex.Pattern;

/**
 * 过滤SQL参数,防止特殊字符引发的异常和SQL注入 只适用于MySQL数据库
 *
 * @author sodarfish
 * @update 2008.1.24
 * @since 2008.1.21
 */
public class MySqlStringFilter {

    public MySqlStringFilter() {
    }

    public static final int NUMBER_FOR_QUERY = 0;
    public static final int MYSQL_CHAR_FOR_UPDATE = 1;
    public static final int MYSQL_CHAR_FOR_NONLIKE_QUERY = 2;
    public static final int MYSQL_CHAR_FOR_LIKE_QUERY = 3;
    public static final int MYSQL_P_CHAR_FOR_LIKE_QUERY = 4;

    /**
     * 针对MySQL使用Statement和PreparedStatement进行插入和查询时的参数 进行合法化。 由于MS SQLServer
     * 默认没有用\做转义符,在处理普通操作时,只需要注意'转为''就可以了. 如果用PreparedStatement，那么这一步也可以免掉.
     * 如果要查询%符号,那么在MSSQLSERVER中必须使用escape 进行转义,如果使用了escape '\'
     * 可以使用该类的MYSQL_P_CHAR_FOR_LIKE_QUERY来指定过滤类型.
     *
     * @param sql
     * @param paramType
     *
     * @return
     */
    public static String filtrateSQL(String sql, int paramType) {
        if (sql == null) {
            // throw new IllegalArgumentException("参数为空!");
            return null;
        }
        // 识别参数是否是数字
        if (paramType == MySqlStringFilter.NUMBER_FOR_QUERY) {
            Pattern pattern = Pattern.compile("(0|[1-9])[0-9]*(\\.[0-9]*)?");
            if (pattern.matcher(sql).matches()) {
                return sql;
            } else
            // throw new IllegalArgumentException("非法参数!");
            {
                return null;
            }
        }
        // MySQL处理插入操作.将特殊字符加上转义
        if (paramType == MySqlStringFilter.MYSQL_CHAR_FOR_UPDATE) {
            String newKey = sql.replaceAll("\\\\", "\\\\\\\\");// 处理\插入
            newKey = newKey.replaceAll("'", "\\\\'"); // 处理'插入防止SQL注入
            newKey = newKey.replaceAll("\"", "\\\\\"");
            return newKey;
        }
        // MySQL处理非LIKE匹配查询操作.将特殊字符加上转义
        if (paramType == MySqlStringFilter.MYSQL_CHAR_FOR_NONLIKE_QUERY) {
            String newKey = sql.replaceAll("\\\\", "\\\\\\\\");// 处理\插入
            newKey = newKey.replaceAll("'", "\\\\'"); // 处理'插入防止SQL注入
            newKey = newKey.replaceAll("\"", "\\\\\"");
            return newKey;
        }
        /**
         * 对于MySQL 使用Statement对象进行LIKE模糊匹配的特殊处理.
         *
         */
        if (paramType == MySqlStringFilter.MYSQL_CHAR_FOR_LIKE_QUERY) {
            // 处理查询特殊字符首先处理\,因为它有转义作用,在MySQL LIKE匹配中,
            // 会出现两次转义,因此实际查询一个\符号,在SQL语句中的输入应当是\\\\，
            // 这里由于加上方法中对正则表达式的转义要求和JAVA本身的转义要求,输入16个\
            // 恰好表示SQL中4个\
            // 然后处理其他特殊字符,他们在SQL中表示为\% \'等.
            String newKey = sql;
            newKey = newKey.replaceAll("\\\\", "\\\\\\\\\\\\\\\\");
            newKey = newKey.replaceAll("'", "\\\\'");
            newKey = newKey.replaceAll("_", "\\\\_");
            newKey = newKey.replaceAll("%", "\\\\%");
            newKey = newKey.replaceAll("\"", "\\\\\"");
            return newKey;
        }
        /**
         * 对于MySQL 使用PreparedStatement对象进行LIKE模糊匹配的特殊处理.
         * 对MySQL来说,使用PreparedStatement时,只需要特殊处理LIKE匹配问题.
         *
         * 此方法还适用于MSSQL Server2000中,Like 匹配使用了 escape '\'的语句.
         */
        if (paramType == MySqlStringFilter.MYSQL_P_CHAR_FOR_LIKE_QUERY) {
            // 由于MySQL中\为默认转义字符
            // 处理参数中的特殊字符,\用\\转义成普通的\字符.
            String newKey = sql;
            newKey = newKey.replaceAll("\\\\", "\\\\\\\\");
            newKey = newKey.replaceAll("'", "\\\\'");
            newKey = newKey.replaceAll("_", "\\\\_");
            newKey = newKey.replaceAll("%", "\\\\%");
            newKey = newKey.replaceAll("\"", "\\\\\"");
            return newKey;
        }
        return null;
    }
}
