package com.pds.p2p.core.jdbc.ar.ex;

/**
 * 未找到相应的方言。
 *
 * @author redraiment
 * @since 1.0
 */
public class UnsupportedDatabaseException extends RuntimeException {
    public UnsupportedDatabaseException(String product) {
        super(String.format("Unsupported Database: %s", product));
    }
}
