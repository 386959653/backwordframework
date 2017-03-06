package com.pds.p2p.core.jdbc.ar.ex;

/**
 * 表不存在。
 *
 * @author redraiment
 * @since 1.0
 */
public class IllegalTableNameException extends RuntimeException {
    public IllegalTableNameException(String tableName, Throwable e) {
        super(String.format("illegal table %s", tableName), e);
    }
}
