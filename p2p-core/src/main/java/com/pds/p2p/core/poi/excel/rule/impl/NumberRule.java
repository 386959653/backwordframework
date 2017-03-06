package com.pds.p2p.core.poi.excel.rule.impl;

import org.apache.commons.lang3.math.NumberUtils;

import com.pds.p2p.core.poi.excel.model.XlMessageType;
import com.pds.p2p.core.poi.excel.model.XlSheet;
import com.pds.p2p.core.poi.excel.rule.XlValidateRuleSurpport;

public class NumberRule extends XlValidateRuleSurpport {

    public NumberRule(XlSheet xlSheet, String columnId) {
        super(xlSheet, columnId);
    }

    @Override
    protected boolean validateTrue(String val) {
        return NumberUtils.isNumber(val.toString());
    }

    @Override
    public int errType() {
        return XlMessageType.ERR_NUM_TYPE;
    }
}
