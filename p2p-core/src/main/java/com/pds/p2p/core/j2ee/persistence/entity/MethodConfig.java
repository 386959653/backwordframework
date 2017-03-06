package com.pds.p2p.core.j2ee.persistence.entity;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动生成页面方法类别注解
 *
 * @author v_liujunyi
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MethodConfig {
    /**
     * 方法类型枚举
     *
     * @author v_liujunyi
     */
    public enum MethodType {
        LIST, CREATEORUPDATE, DELETE
    }

    ;

    MethodType value();

}
