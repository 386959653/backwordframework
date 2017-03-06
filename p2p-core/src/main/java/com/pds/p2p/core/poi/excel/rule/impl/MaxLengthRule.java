package com.pds.p2p.core.poi.excel.rule.impl;

import com.pds.p2p.core.poi.excel.model.XlMessage;
import com.pds.p2p.core.poi.excel.model.XlMessageType;
import com.pds.p2p.core.poi.excel.model.XlSheet;
import com.pds.p2p.core.poi.excel.rule.XlValidateRuleSurpport;

public class MaxLengthRule extends XlValidateRuleSurpport {
    final private int length;

    public MaxLengthRule(XlSheet xlSheet, String columnId, int length) {
        super(xlSheet, columnId);
        this.length = length;
    }

    @Override
    protected boolean validateTrue(String val) {
        return val.length() <= this.length;
    }

    @Override
    public int errType() {
        return XlMessageType.ERR_MAXLEN;
    }

    @Override
    protected void messageText(XlMessage message, String val) {
        message.setMsg(val);
    }

}
