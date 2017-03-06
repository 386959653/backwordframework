package com.pds.p2p.system.uic.dto;

/**
 * UIC用户信息公共DTO
 *
 * @author v_lianghua
 */
public class UICUserInfoDto implements java.io.Serializable {

    /** */
    private static final long serialVersionUID = 4469053413191532983L;

    /**
     * 用户唯一标识
     */
    private java.lang.Long id;

    /**
     * 用户AD登录名
     */
    private java.lang.String username;

    /**
     * 用户姓名
     */
    private java.lang.String name;

    /**
     * 用户ERPID
     */
    private java.lang.Long employeeId;

    /**
     * 员工编号
     */
    private java.lang.String employeeNumber;

    /**
     * 员工类型
     */
    private java.lang.String employeeType;

    /**
     * 电子邮箱
     */
    private java.lang.String email;

    /**
     * 入职时间，格式：yyyy-MM-dd
     */
    private java.lang.String serviceStartDate;

    /**
     * 离职时间，格式：yyyy-MM-dd
     */
    private java.lang.String serviceEndDate;

    /**
     * 开始工作日期.最早参加工作日期（不一定是在百度工作）格式：yyyy-MM-dd
     */
    private java.lang.String workStartDate;

    /**
     * 正式入职日期.即实习生毕业后正式入职百度的日期格式：yyyy-MM-dd
     */
    private java.lang.String regularStartDate;

    /**
     * 部门ID
     */
    private java.lang.Long departmentId;

    /**
     * 部门编码
     */
    private java.lang.String departmentCode;

    /**
     * 部门名称
     */
    private java.lang.String departmentName;

    /**
     * 公司ID
     */
    private java.lang.Long companyId;

    /**
     * 公司编码
     */
    private java.lang.String companyCode;

    /**
     * 公司名称
     */
    private java.lang.String companyName;

    /**
     * 英文名
     */
    private java.lang.String englishName;

    /**
     * 分机号码
     */
    private java.lang.String phoneNumber;

    /**
     * Hi账号
     */
    private java.lang.String hiNumber;

    /**
     * 职位标识
     */
    private java.lang.Long positionId;

    /**
     * 职位名称
     */
    private java.lang.String positionName;

    /**
     * 职务级别，数字1-25
     */
    private java.lang.Integer grade;

    /**
     * 职务级别名称
     */
    private java.lang.String gradeName;

    /**
     * 职位类型全称，例如："管理类.财务.主管.1"
     */
    private java.lang.String jobDescription;

    /**
     * 入职地点的大厦信息
     */
    private java.lang.String entryLocation;

    /**
     * 是否总部
     */
    private java.lang.String inHq;

    /**
     * 对外职位信息
     */
    private java.lang.String externalPosition;

    /**
     * 上级ID
     */
    private java.lang.Long superiorId;

    /**
     * 上级用户名
     */
    private java.lang.String superiorUsername;

    /**
     * 上级电子邮箱
     */
    private java.lang.String superiorEmail;

    /**
     * 上级姓名
     */
    private java.lang.String superiorName;

    /**
     * 部门类型
     */
    private java.lang.String departmentType;

    /**
     * 部门简称
     */
    private java.lang.String departmentAbbreviation;

    /**
     * 工作小组
     */
    private java.lang.String workGroup;

    /**
     * 身份证号
     */
    private java.lang.String idCardNumber;

    /**
     * 国家号，如86
     */
    private java.lang.String nationalCode;

    /**
     * 区号，如10
     */
    private java.lang.String dialingCode;

    /**
     * 完整电话号码,如59924099
     */
    private java.lang.String completeTelNumber;

    /**
     * 工位号，如SC-F3-C737
     */
    private java.lang.String stationNumber;

    /**
     * 教育程度，如本科
     */
    private java.lang.String education;

    /**
     * 年龄
     */
    private java.lang.Integer age;

    /**
     * 公司所属组，如总部、上分
     */
    private java.lang.String companyGroup;

    /**
     * 黑名单成员
     */
    private java.lang.String employeeBlack;

    /**
     * 职位编号，如1110T040
     */
    private java.lang.String positionCode;

    /**
     * 职位大序列，如研发
     */
    private java.lang.String positionMajorSeq;

    /**
     * 职位小序列，如开发
     */
    private java.lang.String positionMinorSeq;

    /**
     * 职位子序列，如用户、联盟、大客户
     */
    private java.lang.String positionMicroSeq;

    /**
     * 职位大级别，如Band4
     */
    private java.lang.String positionMajorLevel;

    /**
     * 职位小级别,如：T4
     */
    private java.lang.String positionMinorLevel;

    /**
     * 入职单号
     */
    private java.lang.String entrySheetNumber;

    /**
     * 工卡卡号
     */
    private java.lang.String cardNumber;

    /**
     * 工卡十六进制号码
     */
    private java.lang.String cardHex;

    /**
     * 工作地点信息
     */
    private java.lang.String location;

    /**
     * 业务组ID
     */
    private java.lang.Long businessGroupId;

    /**
     * 业务组名称
     */
    private java.lang.String businessGroupName;

    /**
     * 用户性别
     */
    private java.lang.String sex;

    /**
     * 生日，格式：yyyy-MM-dd
     */
    private java.lang.String birthday;

    /**
     * 手机号码
     */
    private java.lang.String mobileNumber;

    /**
     * 工资单名称
     */
    private java.lang.String payrollName;

    public java.lang.Long getId() {
        return id;
    }

    public void setId(java.lang.Long id) {
        this.id = id;
    }

    public java.lang.String getUsername() {
        return username;
    }

    public void setUsername(java.lang.String username) {
        this.username = username;
    }

    public java.lang.String getName() {
        return name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }

    public java.lang.Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(java.lang.Long employeeId) {
        this.employeeId = employeeId;
    }

    public java.lang.String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(java.lang.String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public java.lang.String getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(java.lang.String employeeType) {
        this.employeeType = employeeType;
    }

    public java.lang.String getEmail() {
        return email;
    }

    public void setEmail(java.lang.String email) {
        this.email = email;
    }

    public java.lang.String getServiceStartDate() {
        return serviceStartDate;
    }

    public void setServiceStartDate(java.lang.String serviceStartDate) {
        this.serviceStartDate = serviceStartDate;
    }

    public java.lang.String getServiceEndDate() {
        return serviceEndDate;
    }

    public void setServiceEndDate(java.lang.String serviceEndDate) {
        this.serviceEndDate = serviceEndDate;
    }

    public java.lang.String getWorkStartDate() {
        return workStartDate;
    }

    public void setWorkStartDate(java.lang.String workStartDate) {
        this.workStartDate = workStartDate;
    }

    public java.lang.String getRegularStartDate() {
        return regularStartDate;
    }

    public void setRegularStartDate(java.lang.String regularStartDate) {
        this.regularStartDate = regularStartDate;
    }

    public java.lang.Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(java.lang.Long departmentId) {
        this.departmentId = departmentId;
    }

    public java.lang.String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(java.lang.String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public java.lang.String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(java.lang.String departmentName) {
        this.departmentName = departmentName;
    }

    public java.lang.Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(java.lang.Long companyId) {
        this.companyId = companyId;
    }

    public java.lang.String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(java.lang.String companyCode) {
        this.companyCode = companyCode;
    }

    public java.lang.String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(java.lang.String companyName) {
        this.companyName = companyName;
    }

    public java.lang.String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(java.lang.String englishName) {
        this.englishName = englishName;
    }

    public java.lang.String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(java.lang.String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public java.lang.String getHiNumber() {
        return hiNumber;
    }

    public void setHiNumber(java.lang.String hiNumber) {
        this.hiNumber = hiNumber;
    }

    public java.lang.Long getPositionId() {
        return positionId;
    }

    public void setPositionId(java.lang.Long positionId) {
        this.positionId = positionId;
    }

    public java.lang.String getPositionName() {
        return positionName;
    }

    public void setPositionName(java.lang.String positionName) {
        this.positionName = positionName;
    }

    public java.lang.Integer getGrade() {
        return grade;
    }

    public void setGrade(java.lang.Integer grade) {
        this.grade = grade;
    }

    public java.lang.String getGradeName() {
        return gradeName;
    }

    public void setGradeName(java.lang.String gradeName) {
        this.gradeName = gradeName;
    }

    public java.lang.String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(java.lang.String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public java.lang.String getEntryLocation() {
        return entryLocation;
    }

    public void setEntryLocation(java.lang.String entryLocation) {
        this.entryLocation = entryLocation;
    }

    public java.lang.String getInHq() {
        return inHq;
    }

    public void setInHq(java.lang.String inHq) {
        this.inHq = inHq;
    }

    public java.lang.String getExternalPosition() {
        return externalPosition;
    }

    public void setExternalPosition(java.lang.String externalPosition) {
        this.externalPosition = externalPosition;
    }

    public java.lang.Long getSuperiorId() {
        return superiorId;
    }

    public void setSuperiorId(java.lang.Long superiorId) {
        this.superiorId = superiorId;
    }

    public java.lang.String getSuperiorUsername() {
        return superiorUsername;
    }

    public void setSuperiorUsername(java.lang.String superiorUsername) {
        this.superiorUsername = superiorUsername;
    }

    public java.lang.String getSuperiorEmail() {
        return superiorEmail;
    }

    public void setSuperiorEmail(java.lang.String superiorEmail) {
        this.superiorEmail = superiorEmail;
    }

    public java.lang.String getSuperiorName() {
        return superiorName;
    }

    public void setSuperiorName(java.lang.String superiorName) {
        this.superiorName = superiorName;
    }

    public java.lang.String getDepartmentType() {
        return departmentType;
    }

    public void setDepartmentType(java.lang.String departmentType) {
        this.departmentType = departmentType;
    }

    public java.lang.String getDepartmentAbbreviation() {
        return departmentAbbreviation;
    }

    public void setDepartmentAbbreviation(java.lang.String departmentAbbreviation) {
        this.departmentAbbreviation = departmentAbbreviation;
    }

    public java.lang.String getWorkGroup() {
        return workGroup;
    }

    public void setWorkGroup(java.lang.String workGroup) {
        this.workGroup = workGroup;
    }

    public java.lang.String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(java.lang.String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public java.lang.String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(java.lang.String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public java.lang.String getDialingCode() {
        return dialingCode;
    }

    public void setDialingCode(java.lang.String dialingCode) {
        this.dialingCode = dialingCode;
    }

    public java.lang.String getCompleteTelNumber() {
        return completeTelNumber;
    }

    public void setCompleteTelNumber(java.lang.String completeTelNumber) {
        this.completeTelNumber = completeTelNumber;
    }

    public java.lang.String getStationNumber() {
        return stationNumber;
    }

    public void setStationNumber(java.lang.String stationNumber) {
        this.stationNumber = stationNumber;
    }

    public java.lang.String getEducation() {
        return education;
    }

    public void setEducation(java.lang.String education) {
        this.education = education;
    }

    public java.lang.Integer getAge() {
        return age;
    }

    public void setAge(java.lang.Integer age) {
        this.age = age;
    }

    public java.lang.String getCompanyGroup() {
        return companyGroup;
    }

    public void setCompanyGroup(java.lang.String companyGroup) {
        this.companyGroup = companyGroup;
    }

    public java.lang.String getEmployeeBlack() {
        return employeeBlack;
    }

    public void setEmployeeBlack(java.lang.String employeeBlack) {
        this.employeeBlack = employeeBlack;
    }

    public java.lang.String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(java.lang.String positionCode) {
        this.positionCode = positionCode;
    }

    public java.lang.String getPositionMajorSeq() {
        return positionMajorSeq;
    }

    public void setPositionMajorSeq(java.lang.String positionMajorSeq) {
        this.positionMajorSeq = positionMajorSeq;
    }

    public java.lang.String getPositionMinorSeq() {
        return positionMinorSeq;
    }

    public void setPositionMinorSeq(java.lang.String positionMinorSeq) {
        this.positionMinorSeq = positionMinorSeq;
    }

    public java.lang.String getPositionMicroSeq() {
        return positionMicroSeq;
    }

    public void setPositionMicroSeq(java.lang.String positionMicroSeq) {
        this.positionMicroSeq = positionMicroSeq;
    }

    public java.lang.String getPositionMajorLevel() {
        return positionMajorLevel;
    }

    public void setPositionMajorLevel(java.lang.String positionMajorLevel) {
        this.positionMajorLevel = positionMajorLevel;
    }

    public java.lang.String getPositionMinorLevel() {
        return positionMinorLevel;
    }

    public void setPositionMinorLevel(java.lang.String positionMinorLevel) {
        this.positionMinorLevel = positionMinorLevel;
    }

    public java.lang.String getEntrySheetNumber() {
        return entrySheetNumber;
    }

    public void setEntrySheetNumber(java.lang.String entrySheetNumber) {
        this.entrySheetNumber = entrySheetNumber;
    }

    public java.lang.String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(java.lang.String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public java.lang.String getCardHex() {
        return cardHex;
    }

    public void setCardHex(java.lang.String cardHex) {
        this.cardHex = cardHex;
    }

    public java.lang.String getLocation() {
        return location;
    }

    public void setLocation(java.lang.String location) {
        this.location = location;
    }

    public java.lang.Long getBusinessGroupId() {
        return businessGroupId;
    }

    public void setBusinessGroupId(java.lang.Long businessGroupId) {
        this.businessGroupId = businessGroupId;
    }

    public java.lang.String getBusinessGroupName() {
        return businessGroupName;
    }

    public void setBusinessGroupName(java.lang.String businessGroupName) {
        this.businessGroupName = businessGroupName;
    }

    public java.lang.String getSex() {
        return sex;
    }

    public void setSex(java.lang.String sex) {
        this.sex = sex;
    }

    public java.lang.String getBirthday() {
        return birthday;
    }

    public void setBirthday(java.lang.String birthday) {
        this.birthday = birthday;
    }

    public java.lang.String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(java.lang.String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public java.lang.String getPayrollName() {
        return payrollName;
    }

    public void setPayrollName(java.lang.String payrollName) {
        this.payrollName = payrollName;
    }

}
