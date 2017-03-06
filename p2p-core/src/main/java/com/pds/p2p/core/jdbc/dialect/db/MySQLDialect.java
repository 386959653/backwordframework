/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.pds.p2p.core.jdbc.dialect.db;

import com.pds.p2p.core.jdbc.dialect.DialectBase;

/**
 * Mysql方言的实现
 *
 * @author wangwen
 * @version 1.0 2010-10-10 下午12:31
 * @since JDK 1.5
 */
public class MySQLDialect extends DialectBase {

    @Override
    public String getCaseIdentifier(String identifier) {
        return identifier;
    }

    @Override
    public boolean supportAutoIncrement() {
        return true;
    }

    @Override
    public String getLimitString(String sql, int offset, int limit) {
        return getLimitString(sql, offset, Integer.toString(offset), Integer.toString(limit));
    }

    public boolean supportsLimit() {
        return true;
    }

    /**
     * 将sql变成分页sql语句,提供将offset及limit使用占位符号(placeholder)替换.
     * <p>
     * <pre>
     * 如mysql
     * dialect.getLimitString("select * from user", 12, ":offset",0,":limit") 将返回
     * select * from user limit :offset,:limit
     * </pre>
     *
     * @param sql               实际SQL语句
     * @param offset            分页开始纪录条数
     * @param offsetPlaceholder 分页开始纪录条数－占位符号
     * @param limitPlaceholder  分页纪录条数占位符号
     *
     * @return 包含占位符的分页sql
     */
    public String getLimitString(String sql, int offset, String offsetPlaceholder, String limitPlaceholder) {
        StringBuilder stringBuilder = new StringBuilder(sql);
        stringBuilder.append(" limit ");
        if (offset > 0) {
            stringBuilder.append(offsetPlaceholder).append(",").append(limitPlaceholder);
        } else {
            stringBuilder.append(limitPlaceholder);
        }
        return stringBuilder.toString();
    }

    @Override
    public String getSelectInsertedAutoIdClause() {
        return "select last_insert_id()";
    }

    @Override
    public String getSelectGUIDClause() {
        return "select uuid()";
    }

    @Override
    public boolean supportsSequence() {
        return false;
    }

    @Override
    public String getSelectSequenceClause(String sequenceName) {
        throw new UnsupportedOperationException("getSelectSequenceClause not supported");
    }

    @Override
    public String getNativeIDGenerator() {
        return "identity";
    }

}
