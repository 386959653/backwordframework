package com.pds.p2p.core.j2ee.persistence.entity;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.collect.Maps;

/***
 * 实体的基类：通过弹性属性，sqlMap，便于新增特殊变量
 *
 * @author 王文
 */
public abstract class BaseEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 4640993965369456368L;

    /**
     * 自定义SQL（SQL标识，SQL内容）
     */
    @Transient
    protected Map<String, String> sqlMap;

    /**
     * 分表标识比如 _00 _01 _201401 ... 要带上完整的分表后缀 , 用来分表
     */
    @Transient
    private String subTableFlag = "";

    public BaseEntity() {
        super();
    }

    @XmlTransient
    public Map<String, String> getSqlMap() {
        if (sqlMap == null) {
            sqlMap = Maps.newHashMap();
        }
        return sqlMap;
    }

    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    @XmlTransient
    public String getSubTableFlag() {
        return subTableFlag;
    }

    public void setSubTableFlag(String subTableFlag) {
        this.subTableFlag = subTableFlag;
    }

}
