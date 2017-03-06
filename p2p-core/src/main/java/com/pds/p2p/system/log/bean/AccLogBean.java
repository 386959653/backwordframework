package com.pds.p2p.system.log.bean;

public class AccLogBean {
    public String PID; // 必填 项目ID（在平台做项目配置时生成）
    public String MITEM;// 必填 监控项，url或接口
    public String MTYPE; // 必填 监控类型，PV，UV，RESPONSE，AUDIT，需要哪些统计类别，就填哪些，用逗号分隔
    public long STIME;// 必填 日志生成时间戳
    public String DESC;// 监控项描述
    public String IP;// IP地址，用于计算地理位置，用户ID等，不传则无法统计城市等信息
    public String UID;// UUAP_NAME:邮箱前缀(@以前)
    public Long SEQ;// 拉取方式必要 拉取数据时读取数据的序列数，用于判断上次拉取数据的位置

    public Long RTIME;

    public void setPID(String pID) {
        PID = pID;
    }

    public void setMITEM(String mITEM) {
        MITEM = mITEM;
    }

    public void setMTYPE(String mTYPE) {
        MTYPE = mTYPE;
    }

    public void setSTIME(long sTIME) {
        STIME = sTIME;
    }

    public void setDESC(String dESC) {
        DESC = dESC;
    }

    public void setIP(String iP) {
        IP = iP;
    }

    public void setUID(String uID) {
        UID = uID;
    }

    public void setSEQ(Long sEQ) {
        SEQ = sEQ;
    }

    public AccLogBean(String pID, String mITEM, String mTYPE, Long sTIME, String dESC, String iP, String uID,
                      Long sEQ) {
        super();
        PID = pID;
        MITEM = mITEM;
        MTYPE = mTYPE;
        STIME = sTIME;
        DESC = dESC;
        IP = iP;
        UID = uID;
        SEQ = sEQ;
    }

    public AccLogBean(String pID, String mITEM, String mTYPE, long sTIME, String iP) {
        super();
        PID = pID;
        MITEM = mITEM;
        MTYPE = mTYPE;
        STIME = sTIME;
        IP = iP;
    }

    public AccLogBean() {
        super();
    }

    public void setRTIME(Long rTIME) {
        RTIME = rTIME;
    }
}
