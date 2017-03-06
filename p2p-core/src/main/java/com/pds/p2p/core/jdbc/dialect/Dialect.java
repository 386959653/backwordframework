/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.pds.p2p.core.jdbc.dialect;

import java.sql.Connection;

/**
 * 类似hibernate的Dialect,但只精简出分页部分
 *
 * @author poplar.yfyang
 * @version 1.0 2011-11-18 下午12:31
 * @since JDK 1.5
 */
public interface Dialect {

    /**
     * 判断给定的数据库连接是否使用当前方言。
     *
     * @param c 数据库连接
     *
     * @return 如果该连接属于当前方言，返回true；否则返回false。
     */
    public boolean accept(Connection c);

    /**
     * 返回当前方言定义自增的整数类型主键的方法。
     *
     * @return 返回当前方言定义自增的整数类型主键的方法。
     */
    public String getIdentity();

    /**
     * 将给定的标识转换成当前数据库内部的大小写形式。
     *
     * @param identifier 需要转换的标识。
     *
     * @return 转换后的标识。
     */
    public String getCaseIdentifier(String identifier);

    /**
     * 数据库本身是否支持分页当前的分页查询方式
     * 如果数据库不支持的话，则不进行数据库分页
     *
     * @return true：支持当前的分页查询方式
     */
    public boolean supportsLimit();

    /**
     * 数据库本身是否支持分页当前的分页查询方式
     * 如果数据库不支持的话，则不进行数据库分页
     *
     * @return true：支持当前的分页查询方式
     */
    public boolean supportAutoIncrement();

    /**
     * 将sql转换为分页SQL，分别调用分页sql
     *
     * @param sql    SQL语句
     * @param offset 开始条数
     * @param limit  每页显示多少纪录条数
     *
     * @return 分页查询的sql
     */
    public String getLimitString(String sql, int offset, int limit);

    /***
     * 获得获得自动插入后的值的语句
     *
     * @return
     */
    public String getSelectInsertedAutoIdClause();

    public String getSelectGUIDClause();

    public boolean supportsSequence();

    public String getSelectSequenceClause(String sequenceName);

    /**
     * 当主键按照native配置时，实际使用哪种generator
     */
    public String getNativeIDGenerator();

}
