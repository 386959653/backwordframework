package com.pds.p2p.core.poi.excel.rule.validator;

import java.util.List;
import java.util.Map;

import com.pds.p2p.core.poi.excel.model.XlColumn;
import com.pds.p2p.core.poi.excel.rule.XlValidateRule;
import org.apache.commons.lang3.StringUtils;

import com.pds.p2p.core.poi.excel.model.XlMessage;
import com.pds.p2p.core.poi.excel.model.XlSheet;
import com.pds.p2p.core.poi.excel.rule.XlValidator;
import com.pds.p2p.core.poi.excel.rule.impl.DigitsRule;
import com.pds.p2p.core.poi.excel.rule.impl.FormatRule;
import com.pds.p2p.core.poi.excel.rule.impl.LengthRule;
import com.pds.p2p.core.poi.excel.rule.impl.MaxLengthRule;
import com.pds.p2p.core.poi.excel.rule.impl.NotChineseRule;
import com.pds.p2p.core.poi.excel.rule.impl.NumberRule;
import com.pds.p2p.core.poi.excel.rule.impl.OptionsRule;
import com.pds.p2p.core.poi.excel.rule.impl.PrecentRule;
import com.pds.p2p.core.poi.excel.rule.impl.RequiredRule;
import com.pds.p2p.core.poi.excel.rule.impl.UniqueRule;
import com.pds.p2p.core.poi.excel.rule.impl.UrlRule;
import com.google.common.collect.Lists;

public class ConfXlValidator implements XlValidator {
    List<XlValidateRule> rules = Lists.newArrayList();

    @Override
    public void loadRules(XlSheet xlSheet) {
        List<XlColumn> columns = xlSheet.getColumns();
        for (XlColumn column : columns) {
            if (column.isRequired()) {
                rules.add(new RequiredRule(xlSheet, column.getId()));
            }
            if (column.isUnique()) {
                rules.add(new UniqueRule(xlSheet, column.getId()));
            }
            if (column.isDigits()) {
                rules.add(new DigitsRule(xlSheet, column.getId()));
            }
            if (column.isNumberType()) {
                rules.add(new NumberRule(xlSheet, column.getId()));
            }
            if (column.getLength() > 0) {
                rules.add(new LengthRule(xlSheet, column.getId(), column.getLength()));
            }
            if (column.getMaxlength() > 0) {
                rules.add(new MaxLengthRule(xlSheet, column.getId(), column.getMaxlength()));
            }
            if (StringUtils.isNotEmpty(column.getFormat())) {
                if (column.isDateType()) {
                    rules.add(new FormatRule(xlSheet, column.getId()));
                }
            }
            if (column.isEnumType()) {
                rules.add(new OptionsRule(xlSheet, column.getId()));
            }
            String valids[] = StringUtils.split(column.getValidation());
            if (valids != null) {
                for (String valid : valids) {
                    if (StringUtils.startsWithIgnoreCase(valid, "NotChinese")) {
                        rules.add(new NotChineseRule(xlSheet, column.getId()));
                    }
                    if (StringUtils.startsWithIgnoreCase(valid, "url")) {
                        rules.add(new UrlRule(xlSheet, column.getId()));
                    }
                    if (StringUtils.startsWithIgnoreCase(valid, "Precent")) {
                        rules.add(new PrecentRule(xlSheet, column.getId()));
                    }
                }
            }
        }
    }

    @Override
    public List<XlMessage> validate(Map<String, String> rowdata, int nrow) {
        List<XlMessage> result = Lists.newArrayList();
        for (XlValidateRule rule : rules) {
            XlMessage message = rule.perform(rowdata, nrow);
            if (message != null) {
                result.add(message);
            }
        }
        return result;
    }

}
