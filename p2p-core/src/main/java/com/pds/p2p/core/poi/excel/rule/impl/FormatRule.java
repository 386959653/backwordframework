package com.pds.p2p.core.poi.excel.rule.impl;

import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.pds.p2p.core.poi.excel.model.XlMessage;
import com.pds.p2p.core.poi.excel.model.XlMessageType;
import com.pds.p2p.core.poi.excel.model.XlSheet;
import com.pds.p2p.core.poi.excel.rule.XlValidateRuleSurpport;

public class FormatRule extends XlValidateRuleSurpport {

    public FormatRule(XlSheet xlSheet, String columnId) {
        super(xlSheet, columnId);
    }

    @Override
    protected boolean validateTrue(String val) {
        String allfmt[] = StringUtils.split(this.xlColumn.getFormat(), ',');
        if (this.xlColumn.isDateType()) {
            if (NumberUtils.isNumber(val)) {
                return true;
            }
            try {
                DateUtils.parseDate(val.toString(), allfmt);
                return true;
            } catch (ParseException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    protected void messageText(XlMessage message, String val) {
        message.setMsg(val);
    }

    @Override
    public int errType() {
        if (this.xlColumn.isDateType()) {
            return XlMessageType.ERR_DATE_FMT;
        }
        return XlMessageType.ERR_FMT;
    }

}
