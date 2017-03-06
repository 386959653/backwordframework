/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.pds.p2p.core.jdbc.dialect.db;

import com.pds.p2p.core.jdbc.dialect.DialectBase;

/**
 * MSSQLServer 数据库实现分页方言
 *
 * @author poplar.yfyang
 * @version 1.0 2010-10-10 下午12:31
 * @since JDK 1.5
 */
public class SQLServerDialect extends DialectBase {

    public boolean supportsLimit() {
        return true;
    }

    static int getAfterSelectInsertPoint(String sql) {
        int selectIndex = sql.toLowerCase().indexOf("select");
        final int selectDistinctIndex = sql.toLowerCase().indexOf("select distinct");
        return selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
    }

    public String getLimitString(String sql, int offset, int limit) {
        return getLimit(sql, offset, limit);
    }

    /**
     * 将sql变成分页sql语句,提供将offset及limit使用占位符号(placeholder)替换.
     * <pre>
     * 如mysql
     * dialect.getLimitString("select * from user", 12, ":offset",0,":limit") 将返回
     * select * from user limit :offset,:limit
     * </pre>
     *
     * @param sql    实际SQL语句
     * @param offset 分页开始纪录条数
     * @param limit  分页每页显示纪录条数
     *
     * @return 包含占位符的分页sql
     */
    public String getLimit(String sql, int offset, int limit) {
        if (offset > 0) {
            throw new UnsupportedOperationException("sql server has no offset");
        }
        return new StringBuffer(sql.length() + 8)
                .append(sql)
                .insert(getAfterSelectInsertPoint(sql), " top " + limit)
                .toString();
    }

    /* (non-Javadoc)
     * @see com.pds.j2ee.persistence.dialect.Dialect#getSelectInsertedAutoIdClause()
     */
    @Override
    public String getSelectInsertedAutoIdClause() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.pds.j2ee.persistence.dialect.Dialect#getSelectGUIDClause()
     */
    @Override
    public String getSelectGUIDClause() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.pds.j2ee.persistence.dialect.Dialect#supportsSequence()
     */
    @Override
    public boolean supportsSequence() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.pds.j2ee.persistence.dialect.Dialect#getSelectSequenceClause(java.lang.String)
     */
    @Override
    public String getSelectSequenceClause(String sequenceName) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.pds.j2ee.persistence.dialect.Dialect#getNativeIDGenerator()
     */
    @Override
    public String getNativeIDGenerator() {
        // TODO Auto-generated method stub
        return null;
    }

}
