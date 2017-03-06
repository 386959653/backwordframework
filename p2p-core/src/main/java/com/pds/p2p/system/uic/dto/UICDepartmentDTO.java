package com.pds.p2p.system.uic.dto;

/**
 * UIC部门信息公共DTO
 *
 * @author v_lianghua
 */
public class UICDepartmentDTO implements java.io.Serializable {

    /***/
    private static final long serialVersionUID = -2540556256539053809L;

    private Long id;
    private String code;
    private String name;
    private String type;
    private String englishName;
    private String abbreviation;
    private Long hrbpId;
    private String hrbpUsername;
    private String hrbpEmail;
    private String hrbpName;
    private String estaffId;
    private String estaffUsername;
    private Long directorId;
    private String directorUsername;
    private String directorEmail;
    private String directorName;
    private String parentName;
    private Long businessGroupId;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Long getHrbpId() {
        return hrbpId;
    }

    public void setHrbpId(Long hrbpId) {
        this.hrbpId = hrbpId;
    }

    public String getHrbpUsername() {
        return hrbpUsername;
    }

    public void setHrbpUsername(String hrbpUsername) {
        this.hrbpUsername = hrbpUsername;
    }

    public String getHrbpEmail() {
        return hrbpEmail;
    }

    public void setHrbpEmail(String hrbpEmail) {
        this.hrbpEmail = hrbpEmail;
    }

    public String getHrbpName() {
        return hrbpName;
    }

    public void setHrbpName(String hrbpName) {
        this.hrbpName = hrbpName;
    }

    public String getEstaffId() {
        return estaffId;
    }

    public void setEstaffId(String estaffId) {
        this.estaffId = estaffId;
    }

    public String getEstaffUsername() {
        return estaffUsername;
    }

    public void setEstaffUsername(String estaffUsername) {
        this.estaffUsername = estaffUsername;
    }

    public Long getDirectorId() {
        return directorId;
    }

    public void setDirectorId(Long directorId) {
        this.directorId = directorId;
    }

    public String getDirectorUsername() {
        return directorUsername;
    }

    public void setDirectorUsername(String directorUsername) {
        this.directorUsername = directorUsername;
    }

    public String getDirectorEmail() {
        return directorEmail;
    }

    public void setDirectorEmail(String directorEmail) {
        this.directorEmail = directorEmail;
    }

    public String getDirectorName() {
        return directorName;
    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Long getBusinessGroupId() {
        return businessGroupId;
    }

    public void setBusinessGroupId(Long businessGroupId) {
        this.businessGroupId = businessGroupId;
    }
}
