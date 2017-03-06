package com.pds.p2p.system.log.bean;

public class BizLogBean {
    private String level;// level string 可选 全匹配 日志级别
    private String name;// name string 可选 全匹配 日志名称
    private String content;// content string 必要 分词 日志内容
    private long time;// time long 必要 时间段 日志生成时间
    private String maching;// machine string 可选 全匹配 机器名
    private String thread;// thread string 可选 全匹配 线程名
    private String tenant;// tenant string 必要 全匹配 租户ID
    private String ext1;// ext1 string 可选 全匹配 扩展字段，自定义用途
    private String ext2;// ext2 string 可选 全匹配 扩展字段，自定义用途
    private String ext3;// ext3 string 可选 全匹配 扩展字段，自定义用途
    private String ext4;// ext4 string 可选 全匹配 扩展字段，自定义用途
    private String ext5;// ext5 string 可选 全匹配 扩展字段，自定义用途

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMaching() {
        return maching;
    }

    public void setMaching(String maching) {
        this.maching = maching;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getExt1() {
        return ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getExt3() {
        return ext3;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }

    public String getExt4() {
        return ext4;
    }

    public void setExt4(String ext4) {
        this.ext4 = ext4;
    }

    public String getExt5() {
        return ext5;
    }

    public void setExt5(String ext5) {
        this.ext5 = ext5;
    }

    public BizLogBean(String level, String name, String content, long time, String maching, String thread,
                      String tenant) {
        super();
        this.level = level;
        this.name = name;
        this.content = content;
        this.time = time;
        this.maching = maching;
        this.thread = thread;
        this.tenant = tenant;
    }

    public BizLogBean() {
        super();
    }

    public BizLogBean(long time, String tenant) {
        super();
        this.time = time;
        this.tenant = tenant;
    }

    public BizLogBean(long time, String maching, String tenant) {
        super();
        this.time = time;
        this.tenant = tenant;
        this.maching = maching;
        this.thread = Thread.currentThread().getName();
        this.name = tenant;
    }

}
