package com.pds.p2p.core.jdbc.lock;

public class JdbcLockException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 884173992141870620L;

    public JdbcLockException() {
        super();
    }

    public JdbcLockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public JdbcLockException(String message, Throwable cause) {
        super(message, cause);
    }

    public JdbcLockException(String message) {
        super(message);
    }

    public JdbcLockException(Throwable cause) {
        super(cause);
    }

}
