package com.pds.p2p.core.jdbc.ar.ex;

/**
 * 遇到未定义的关联时抛出此异常。
 *
 * @author redraiment
 * @since 1.0
 */
public class UndefinedAssociationException extends RuntimeException {
    public UndefinedAssociationException(String name) {
        super(String.format("undefined association name: %s", name));
    }
}
