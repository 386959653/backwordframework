package com.pds.p2p.core.jdbc.ar.ex;

/**
 * 在处理事务时遇到任何异常抛出此异常。
 *
 * @author redraiment
 * @since 2.0
 */
public class TransactionException extends RuntimeException {
    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
