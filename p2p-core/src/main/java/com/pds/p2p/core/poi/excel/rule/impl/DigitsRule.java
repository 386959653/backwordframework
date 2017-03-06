package com.pds.p2p.core.poi.excel.rule.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.lang3.math.NumberUtils;

import com.pds.p2p.core.poi.excel.model.XlMessage;
import com.pds.p2p.core.poi.excel.model.XlMessageType;
import com.pds.p2p.core.poi.excel.model.XlSheet;
import com.pds.p2p.core.poi.excel.rule.XlValidateRuleSurpport;
import com.pds.p2p.core.utils.UtilString;

public class DigitsRule extends XlValidateRuleSurpport {
    public static void main(String[] args) {
        DecimalFormat df = new DecimalFormat("0");
        System.out.println(df.format(123.3333));
    }

    public DigitsRule(XlSheet xlSheet, String columnId) {
        super(xlSheet, columnId);
    }

    @Override
    protected boolean validateTrue(String val) {
        if (this.xlColumn.isNumberType()) {
            BigDecimal bd = new BigDecimal(val.toString());
            val = bd.toPlainString();
        }
        String cell = val.toString();
        cell = UtilString.ereaseBackZeros(cell);
        return NumberUtils.isDigits(cell);
    }

    @Override
    protected void messageText(XlMessage message, String val) {
        message.setMsg(val);
    }

    @Override
    public int errType() {
        return XlMessageType.ERR_NUM_TYPE;
    }

}
