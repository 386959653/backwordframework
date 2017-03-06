package com.pds.p2p.system.log;

public enum MonitLogType {

    PV("PVUV,RESPONSE"), UV("PVUV,RESPONSE"), RESPONSE("PVUV,RESPONSE"), AUDIT("AUDIT"), EXCEPTION("EXCEPTION");
    public String code;

    /**
     * @param code
     */
    MonitLogType(String code) {
        this.code = code;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

}
