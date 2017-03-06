package com.pds.p2p.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.ibatis.reflection.ExceptionUtil;

/**
 * 关于异常的工具类.
 */
public class Exceptions {

    /**
     * 将CheckedException转换为UncheckedException.
     */
    public static RuntimeException unchecked(Exception e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else {
            return new RuntimeException(e);
        }
    }

    /**
     * 将ErrorStack转化为String.
     */
    public static String getStackTraceAsString(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    /**
     * 判断异常是否由某些底层的异常引起.
     */
    @SuppressWarnings("unchecked")
    public static boolean isCausedBy(Exception ex, Class<? extends Exception>... causeExceptionClasses) {
        Throwable cause = ex;
        while (cause != null) {
            for (Class<? extends Exception> causeClass : causeExceptionClasses) {
                if (causeClass.isInstance(cause)) {
                    return true;
                }
            }
            cause = cause.getCause();
        }
        return false;
    }

    public static String getMsg(Throwable wrapped) {
        Throwable unthrowable = ExceptionUtil.unwrapThrowable(wrapped);
        String errorMsg = (unthrowable.getMessage());
        if (StringUtils.isEmpty(errorMsg)) {
            StringBuilder sb = new StringBuilder();
            Throwable throwable = unthrowable.getCause();
            while (throwable != null && StringUtils.isEmpty(throwable.getMessage())) {
                sb.append("#cause ").append(throwable.getClass());
                throwable = unthrowable.getCause();
            }
            if (sb.length() == 0) {
                if (throwable != null) {
                    sb.append(":").append(throwable.getMessage());
                } else {
                    sb.append("#cause ").append(unthrowable.getClass());
                    sb.append(":").append("此异常未提供异常信息");
                }
            } else {
                sb.append(":").append(throwable.getMessage());
            }
            return sb.toString();
        }
        return errorMsg;
    }

    public static boolean isSQLException(Throwable wrapped) {
        return isTheThrowable(wrapped, SQLException.class);
    }

    public static boolean isTheThrowable(Throwable wrapped, Class<?> excls) {
        Throwable unthrowable = ExceptionUtil.unwrapThrowable(wrapped);
        if (TypeUtils.isAssignable(unthrowable.getClass(), excls)) {
            return true;
        }
        while (unthrowable.getCause() != null) {
            if (isTheThrowable(unthrowable.getCause(), excls)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T castException(Throwable wrapped, Class<T> excls) {
        Throwable unthrowable = ExceptionUtil.unwrapThrowable(wrapped);
        if (TypeUtils.isAssignable(unthrowable.getClass(), excls)) {
            return (T) unthrowable;
        }
        while (unthrowable.getCause() != null) {
            if (isTheThrowable(unthrowable.getCause(), excls)) {
                return (T) unthrowable.getCause();
            }
        }
        return null;
    }

    /**
     * 在request中获取异常类
     *
     * @param request
     *
     * @return
     */
    public static Throwable getThrowable(HttpServletRequest request) {
        Throwable ex = null;
        if (request.getAttribute("exception") != null) {
            ex = (Throwable) request.getAttribute("exception");
        } else if (request.getAttribute("javax.servlet.error.exception") != null) {
            ex = (Throwable) request.getAttribute("javax.servlet.error.exception");
        }
        return ex;
    }
}
