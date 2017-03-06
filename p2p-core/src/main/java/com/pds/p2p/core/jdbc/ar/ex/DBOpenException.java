package com.pds.p2p.core.jdbc.ar.ex;

/**
 * 连接数据库时遇到任何问题抛出此异常。
 *
 * @author redraiment
 * @since 1.0
 */
public class DBOpenException extends RuntimeException {
    public DBOpenException(Throwable cause) {
        super(cause);
    }
}
