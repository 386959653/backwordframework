package com.pds.p2p.core.poi.excel.rule.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pds.p2p.core.poi.excel.model.XlMessageType;
import com.pds.p2p.core.poi.excel.model.XlSheet;
import com.pds.p2p.core.poi.excel.rule.XlValidateRuleSurpport;

public class NotChineseRule extends XlValidateRuleSurpport {

    public static void main(String[] args) {
        System.out.println(isContainChinese("Yoge Public Relations&Consulting(Beijing) Co., \n\r Ltd"));
    }

    public NotChineseRule(XlSheet xlSheet, String columnId) {
        super(xlSheet, columnId);
    }

    @Override
    protected boolean validateTrue(String val) {
        return !isContainChinese(val.toString());
    }

    public static boolean isContainChinese(String str) {// 检测是否包含中文
        String regEx = "[\\u4E00-\\u9FA5]+";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int errType() {
        return XlMessageType.ERR_CHINESE;
    }
}
