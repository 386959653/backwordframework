package com.pds.p2p.core.poi.excel.rule;

import java.util.Map;
import java.util.regex.Pattern;

import com.pds.p2p.core.poi.excel.model.XlColumn;
import com.pds.p2p.core.poi.excel.model.XlMessage;
import com.pds.p2p.core.poi.excel.model.XlSheet;
import com.pds.p2p.core.poi.excel.utils.XlMessageUtils;
import com.pds.p2p.core.utils.UtilType;

public abstract class XlValidateRuleSurpport implements XlValidateRule {
    public XlColumn getXlColumn() {
        return xlColumn;
    }

    final protected XlSheet xlSheet;
    final protected XlColumn xlColumn;

    public XlValidateRuleSurpport(XlSheet xlSheet, String columnId) {
        this.xlSheet = xlSheet;
        this.xlColumn = xlSheet.findXlColumn(columnId);
    }

    public XlMessage perform(Map<String, String> rowdata, int nrow) {
        String val = rowdata.get(this.xlColumn.getId());
        if (UtilType.isEmpty(val)) {
            return null;
        }
        if (!validateTrue(val)) {
            XlMessage message = XlMessageUtils.create(
                    xlSheet.getWorkbook(), xlSheet.getName(),
                    nrow, xlSheet.idxColumn(xlColumn.getName()), this.errType(), "");
            messageText(message, val);
            return message;
        }
        return null;
    }

    public final static boolean match(Object text, String reg) {
        return Pattern.compile(reg).matcher(text.toString()).matches();
    }

    abstract protected boolean validateTrue(String val);

    protected void messageText(XlMessage message, String val) {
        message.setMsg(val);
    }

}
