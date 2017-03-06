package com.pds.p2p.core.jdbc.ar.ex;

/**
 * 列不存在。
 *
 * @author redraiment
 * @since 1.0
 */
public class IllegalFieldNameException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 7329689027664967540L;

    public IllegalFieldNameException(String fieldName) {
        super(String.format("illegal field %s", fieldName));
    }

    public IllegalFieldNameException(String fieldName, Throwable cause) {
        super(String.format("illegal field %s", fieldName), cause);
    }
}
