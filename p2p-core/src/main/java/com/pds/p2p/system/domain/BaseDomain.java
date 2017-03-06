/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.pds.p2p.system.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wuyunfeng on 2016/5/26.
 */
public class BaseDomain implements Serializable {

    private static final long serialVersionUID = 4640993965369456368L;

    /**
     * 创建时间
     * 表字段 : creation_date
     */
    private Date creationDate;

    /**
     * 创建人
     * 表字段 : created_by
     */
    private String createdBy;

    /**
     * 最后更新时间
     * 表字段 : last_update_date
     */
    private Date lastUpdateDate;

    /**
     * 最后更新人
     * 表字段 : last_updated_by
     */
    private String lastUpdatedBy;

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
}
