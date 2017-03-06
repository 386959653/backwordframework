package com.pds.p2p.core.poi.excel.rule.impl;

import java.util.Map;

import com.pds.p2p.core.poi.excel.model.XlMessage;
import com.pds.p2p.core.poi.excel.model.XlMessageType;
import com.pds.p2p.core.poi.excel.model.XlSheet;
import com.pds.p2p.core.poi.excel.rule.XlValidateRuleSurpport;
import com.pds.p2p.core.poi.excel.utils.XlMessageUtils;
import com.pds.p2p.core.utils.UtilType;

public class RequiredRule extends XlValidateRuleSurpport {

    public RequiredRule(XlSheet xlSheet, String columnId) {
        super(xlSheet, columnId);
    }

    public XlMessage perform(Map<String, String> rowdata, int nrow) {
        String val = rowdata.get(this.xlColumn.getId());
        if (!validateTrue(val)) {
            XlMessage message = XlMessageUtils
                    .create(xlSheet.getWorkbook(), xlSheet.getName(), nrow, xlSheet.idxColumn(xlColumn.getName()),
                            this.errType(), "");
            return message;
        }
        return null;
    }

    @Override
    protected boolean validateTrue(String val) {
        return !UtilType.isEmpty(val);
    }

    @Override
    public int errType() {
        return XlMessageType.ERR_REQUIRED;
    }

}
