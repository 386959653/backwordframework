package com.pds.p2p.core.poi.excel.rule;

import java.util.List;
import java.util.Map;

import com.pds.p2p.core.poi.excel.model.XlMessage;
import com.pds.p2p.core.poi.excel.model.XlSheet;

public interface XlValidator {
    public void loadRules(XlSheet xlSheet);

    public List<XlMessage> validate(Map<String, String> rowdata, int nrow);
}
