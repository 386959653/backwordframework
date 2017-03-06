package com.pds.p2p.core.poi.excel.rule.impl;

import com.pds.p2p.core.poi.excel.model.XlMessageType;
import com.pds.p2p.core.poi.excel.rule.XlValidateRuleSurpport;
import org.apache.commons.lang3.math.NumberUtils;

import com.pds.p2p.core.poi.excel.model.XlSheet;

public class PrecentRule extends XlValidateRuleSurpport {

    public PrecentRule(XlSheet xlSheet, String columnId) {
        super(xlSheet, columnId);
    }

    @Override
    protected boolean validateTrue(String val) {
        if (val instanceof String) {
            String cell = val.toString();
            if (!cell.endsWith("%")) {
                return false;
            } else {
                cell = cell.substring(0, cell.length() - 1);
            }
            if (!NumberUtils.isNumber(cell)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int errType() {
        return XlMessageType.ERR_PRECENT;
    }

}
