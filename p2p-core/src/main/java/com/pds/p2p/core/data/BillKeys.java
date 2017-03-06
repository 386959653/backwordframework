package com.pds.p2p.core.data;

public abstract class BillKeys {
    private String userName;
    private String companyCd;
    private String ruleCode;
    private long currentBillId;
    private String currentBillNo;

    public String getRuleCode() {
        return ruleCode;
    }

    public BillKeys(String companyCd, String userName, String ruleCode) {
        super();
        this.userName = userName;
        this.companyCd = companyCd;
        this.ruleCode = ruleCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public void setCurrentBillId(long currentBillId) {
        this.currentBillId = currentBillId;
    }

    public void setCurrentBillNo(String currentBillNo) {
        this.currentBillNo = currentBillNo;
    }

    public long getCurrentBillId() {
        return currentBillId;
    }

    public String getCurrentBillNo() {
        return currentBillNo;
    }

    public void initCurrentKeys() {
        currentBillId = this.nextBillId();
        currentBillNo = this.nextBillNo();
    }

    public abstract long nextBillId();

    public abstract String nextBillNo();
}
