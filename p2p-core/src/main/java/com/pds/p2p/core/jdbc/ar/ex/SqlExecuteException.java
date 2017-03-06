package com.pds.p2p.core.jdbc.ar.ex;

/**
 * 执行SQL时遇到任何问题抛出此异常。
 *
 * @author redraiment
 * @since 1.0
 */
public class SqlExecuteException extends RuntimeException {
    public SqlExecuteException(String sql, Throwable cause) {
        super(sql, cause);
    }
}
