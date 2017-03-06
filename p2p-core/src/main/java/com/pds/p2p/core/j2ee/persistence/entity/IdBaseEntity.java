package com.pds.p2p.core.j2ee.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/***
 * 提供基础标识：id、code、name;
 *
 * @author wen
 */
public class IdBaseEntity extends BaseEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 20)
    @ColumnComment("主键")
    private Long id;

    @Column(length = 50)
    @ColumnComment("编码")
    private String code; // 编码

    @Column(length = 50)
    @ColumnComment("名称")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
