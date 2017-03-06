package com.pds.p2p.core.j2ee.persistence.entity;

import java.util.Date;

import javax.persistence.Column;

import org.apache.commons.lang3.StringUtils;

/***
 * 提供审计字段
 *
 * @author wen
 */
public class AuditEntity extends IdBaseEntity {

    /**
     * 删除标记（1：正常；100：删除；2：审核；）
     */
    public static final String DEL_FLAG_NORMAL = "1";
    public static final String DEL_FLAG_DELETE = "100";
    public static final String DEL_FLAG_AUDIT = "2";

    /**
     *
     */
    private static final long serialVersionUID = -3616441230949941715L;

    @Column(length = 1000)
    @ColumnComment("备注")
    private String remarks; // 备注

    @Column(length = 20)
    @ColumnComment("创建者")
    private Long createBy; // 创建者

    @ColumnComment("创建日期")
    private Date createDate; // 创建日期

    @Column(length = 20)
    @ColumnComment("更新者")
    private Long updateBy; // 更新者

    @ColumnComment("更新日期")
    private Date updateDate; // 更新日期

    @Column(length = 5)
    @ColumnComment("删除标记（1：正常；100：删除；2：审核）")
    private String delFlag; // 删除标记（1：正常；100：删除；2：审核）

    public boolean isNormal() {
        return StringUtils.equals(delFlag, DEL_FLAG_NORMAL);
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

}
