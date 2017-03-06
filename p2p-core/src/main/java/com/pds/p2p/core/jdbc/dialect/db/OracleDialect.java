/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.pds.p2p.core.jdbc.dialect.db;

import com.pds.p2p.core.jdbc.dialect.DialectBase;

/**
 * Oracle的方言实现
 *
 * @author wangwen
 * @version 1.0 2010-10-10 下午12:31
 * @since JDK 1.5
 */
public class OracleDialect extends DialectBase {
    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public String getLimitString(String sql, int offset, int limit) {
        return getLimitString(sql, offset, Integer.toString(offset), Integer.toString(limit));
    }

    @Override
    public boolean supportAutoIncrement() {
        return false;
    }

    /**
     * 将sql变成分页sql语句,提供将offset及limit使用占位符号(placeholder)替换.
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
        sql = sql.trim();
        boolean isForUpdate = false;
        if (sql.toLowerCase().endsWith(" for update")) {
            sql = sql.substring(0, sql.length() - 11);
            isForUpdate = true;
        }
        StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);

        if (offset > 0) {
            pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
        } else {
            pagingSelect.append("select * from ( ");
        }
        pagingSelect.append(sql);
        if (offset > 0) {
            String endString = offsetPlaceholder + "+" + limitPlaceholder;
            pagingSelect.append(" ) row_ where rownum <= " + endString + ") where rownum_ > ")
                    .append(offsetPlaceholder);
        } else {
            pagingSelect.append(" ) where rownum <= " + limitPlaceholder);
        }

        if (isForUpdate) {
            pagingSelect.append(" for update");
        }

        return pagingSelect.toString();
    }

    @Override
    public String getSelectInsertedAutoIdClause() {
        return null;
    }

    @Override
    public String getSelectGUIDClause() {
        return "select rawtohex(sys_guid()) from dual";
    }

    @Override
    public boolean supportsSequence() {
        return true;
    }

    @Override
    public String getSelectSequenceClause(String sequenceName) {
        StringBuilder sb = new StringBuilder(32);
        sb.append("select ").append(sequenceName).append(".nextval from dual");
        return sb.toString();
    }

    @Override
    public String getNativeIDGenerator() {
        return "sequence";
    }

}
