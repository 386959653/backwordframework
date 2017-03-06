/**
 *
 */
package com.pds.p2p.core.jdbc.dialect;

import java.sql.Connection;

/**
 * @author 王文
 * @date 2015-8-6 下午1:59:24
 */
public abstract class DialectBase implements Dialect {
    public boolean supportAutoIncrement() {
        throw new UnsupportedOperationException("supportAutoIncrement not supported");
    }

    /**
     * 判断给定的数据库连接是否使用当前方言。
     *
     * @param c 数据库连接
     *
     * @return 如果该连接属于当前方言，返回true；否则返回false。
     */
    public boolean accept(Connection c) {
        throw new UnsupportedOperationException("accept not supported");
    }

    /**
     * 返回当前方言定义自增的整数类型主键的方法。
     *
     * @return 返回当前方言定义自增的整数类型主键的方法。
     */
    public String getIdentity() {
        throw new UnsupportedOperationException("getIdentity not supported");
    }

    /**
     * 将给定的标识转换成当前数据库内部的大小写形式。
     *
     * @param identifier 需要转换的标识。
     *
     * @return 转换后的标识。
     */
    public String getCaseIdentifier(String identifier) {
        throw new UnsupportedOperationException("getCaseIdentifier not supported");
    }
}
