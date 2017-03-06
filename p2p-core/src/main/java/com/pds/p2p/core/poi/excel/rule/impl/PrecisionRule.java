package com.pds.p2p.core.poi.excel.rule.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.pds.p2p.core.poi.excel.model.XlMessageType;
import com.pds.p2p.core.poi.excel.model.XlSheet;
import com.pds.p2p.core.poi.excel.rule.XlValidateRuleSurpport;

public class PrecisionRule extends XlValidateRuleSurpport {
    final int precision;

    public PrecisionRule(XlSheet xlSheet, String columnId, int precision) {
        super(xlSheet, columnId);
        this.precision = precision;
    }

    @Override
    protected boolean validateTrue(String val) {
        String cell = val.toString();
        if (cell.endsWith("%")) {
            cell = cell.substring(0, cell.length() - 1);
        }
        if (!NumberUtils.isNumber(cell)) {
            return false;
        }
        String[] nums = StringUtils.split(cell, '.');
        if (nums.length != 2) {
            return false;
        }
        return nums[1].length() == this.precision;
    }

    @Override
    public int errType() {
        return XlMessageType.ERR_PRECENT;
    }

}
