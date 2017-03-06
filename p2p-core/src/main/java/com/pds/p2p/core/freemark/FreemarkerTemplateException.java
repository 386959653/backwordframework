package com.pds.p2p.core.freemark;

/**
 * FreemarkerException等价的异常类，不过继承之RuntimeException
 *
 * @author badqiu
 */
public class FreemarkerTemplateException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 4948172627241679013L;

    public FreemarkerTemplateException() {
        super();
    }

    public FreemarkerTemplateException(String message, Throwable cause) {
        super(message, cause);
    }

    public FreemarkerTemplateException(String message) {
        super(message);
    }

    public FreemarkerTemplateException(Throwable cause) {
        super(cause);
    }

}
