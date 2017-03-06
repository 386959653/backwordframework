package com.pds.p2p.core.poi.excel.rule.impl;

import java.util.Set;

import com.pds.p2p.core.poi.excel.model.XlMessageType;
import com.pds.p2p.core.poi.excel.model.XlSheet;
import com.pds.p2p.core.poi.excel.rule.XlValidateRuleSurpport;
import com.google.common.collect.Sets;

/***
 * 判断是否有重复，目前仅判断导入数据内不允许重复，日后可能加入数据表范围内不重复或区域内不重复
 *
 * @author Administrator
 */
public class UniqueRule extends XlValidateRuleSurpport {
    private Set<String> set = Sets.newHashSet();

    public UniqueRule(XlSheet xlSheet, String columnId) {
        super(xlSheet, columnId);
    }

    @Override
    protected boolean validateTrue(String val) {
        String s = val.toString();
        if (set.contains(s)) {
            return false;
        }
        set.add(s);
        return true;
    }

    @Override
    public int errType() {
        return XlMessageType.ERR_UNIQUE;
    }

}
