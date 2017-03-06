package com.pds.p2p.core.poi.excel.event;

public class TerminalException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 2355102668013625419L;

    public TerminalException() {
    }

    public TerminalException(String message) {
        super(message);
    }

    public TerminalException(Throwable cause) {
        super(cause);
    }

    public TerminalException(String message, Throwable cause) {
        super(message, cause);
    }

}
