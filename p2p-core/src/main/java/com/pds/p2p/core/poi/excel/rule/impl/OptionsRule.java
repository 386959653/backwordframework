package com.pds.p2p.core.poi.excel.rule.impl;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.pds.p2p.core.poi.excel.model.XlMessageType;
import com.pds.p2p.core.poi.excel.model.XlSheet;
import com.pds.p2p.core.poi.excel.rule.XlValidateRuleSurpport;
import com.google.common.collect.Sets;

public class OptionsRule extends XlValidateRuleSurpport {
    final private Set<String> options = Sets.newHashSet();

    public OptionsRule(XlSheet xlSheet, String columnId,
                       String optstr) {
        super(xlSheet, columnId);
        String[] opts = StringUtils.split(optstr, ',');
        for (String opt : opts) {
            this.options.add(opt);
        }
    }

    public OptionsRule(XlSheet xlSheet, String columnId) {
        super(xlSheet, columnId);
        Map<String, Object> opts = this.xlColumn.listDataToMap();
        for (Object opt : opts.values()) {
            this.options.add(opt.toString());
        }

    }

    @Override
    protected boolean validateTrue(String val) {
        return options.contains(val.toString());
    }

    @Override
    public int errType() {
        return XlMessageType.ERR_OPTION;
    }

}