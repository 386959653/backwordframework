/**
 *
 */
package com.pds.p2p.core.poi.excel.formula;

import java.util.List;
import java.util.Set;

import com.pds.p2p.core.poi.excel.utils.ReadUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/****************************************************
 * <pre>
 * 描    述： （必填）
 *
 * 实施资源: （选填）
 * 调用者  : （选填）
 * 被调用者: （选填）
 * Company:  普德施PDS
 * @author 王文
 * @version 1.0   2015年2月28日
 * @see （选填）
 * 	HISTORY  （必填）
 * 	2015年2月28日  Administrator 创建文件
 *
 * </pre>
 **************************************************/

public class XlFormlula {
    private static final String AC12_DB23_S12_A23_100 = "F1+A12+AC12+DB23*S12+(AC23)*100/'1、塔类全流程'!K7";

    public static void main(String[] args) {
        XlFormlula formlula = new XlFormlula();
        System.out.println(AC12_DB23_S12_A23_100);
        formlula.setExpr("sum(A1:X1)");
        System.out.println(formlula);
        System.out.println(formlula.getExpr());
    }

    @Override
    public String toString() {
        return "XlFormlula [" + colKey + "=" + expr + ", msg=" + msg + ", exprType=" + exprType + ", colKey=" + colKey
                + ", sheetTags=" + sheetTags + ", rawExpr=" + rawExpr + ", tipExpr=" + tipExpr + ", sumType=" + sumType
                + ", coltags=" + coltags + ", level=" + level + "]";
    }

    public XlFormlula() {
        super();
    }

    public XlFormlula(String expr) {
        super();
        this.setExpr(expr);
    }

    static public int TYPE_SIMPLE = 0;
    static public int TYPE_REF = 1;
    static public int TYPE_SUM = 2;

    static public int SUM_COL = 0;
    static public int SUM_ROW = 1;

    private String expr; // 公式表达式
    private String msg; // 描述信息
    private int exprType = TYPE_SIMPLE; // 0-simple,1-sheet之间,2-sum
    private String colKey;
    private List<String> sheetTags; // 如果公式中含有
    private String rawExpr;
    private String tipExpr;
    private int sumType = SUM_COL; // 0-汇总列，1-汇总行
    private java.util.List<String> coltags;
    private int level;

    public void incLevel(int step) {
        this.level += (step + 1);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getExpr() {
        return expr;
    }

    public String getRawExpr() {
        return rawExpr;
    }

    public void setExpr(String expr) {
        this.rawExpr = expr;
        this.expr = normalExpr(expr);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getExprType() {
        return exprType;
    }

    public void setExprType(int exprType) {
        this.exprType = exprType;
    }

    public String getColKey() {
        return colKey;
    }

    public void setColKey(String colKey) {
        this.colKey = colKey;
    }

    private String normalExpr(String expr) {
        if (expr.contains("!")) {
            exprType = TYPE_REF;
        } else if (StringUtils.startsWithIgnoreCase(expr, "sum")) {
            exprType = TYPE_SUM;
        }
        char[] chars = expr.toCharArray();
        boolean findSheetTag = false;
        sheetTags = Lists.newArrayList();
        StringBuilder sb = new StringBuilder();
        int pos = -1;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (findSheetTag) {
                sb.append(c);
            }
            if (c == '\'') {
                if (!findSheetTag) {
                    findSheetTag = true;
                    pos = i;
                }
                if (i > 0 && chars[i - 1] == '(') {
                    sb.append("('");
                }
            }
            if (findSheetTag && c == ')') {
                findSheetTag = false;
                pos = -1;
                sheetTags.add(sb.toString());
            }
        }
        if (pos != -1) {
            String str = expr.substring(pos);
            sheetTags.add(str);
        }
        if (!sheetTags.isEmpty()) {
            List<String> tmp = Lists.newArrayListWithCapacity(sheetTags.size());
            for (String sheetTag : sheetTags) {
                sheetTag = StringUtils.replaceEach(sheetTag, new String[] {"(", ")"}, new String[] {"", ""});
                String s = ReadUtils.removeLastNumber(sheetTag);
                this.tipExpr = StringUtils.replace(expr, sheetTag, s, 1);
                expr = StringUtils.replace(expr, sheetTag, "${" + s + "}", 1);
                tmp.add(s);
            }
            sheetTags.clear();
            sheetTags.addAll(tmp);
            tmp.clear();
            tmp = null;
        }
        if (exprType == TYPE_SUM) {
            this.tipExpr = expr.replace("[\\d、]", "");
            //expr = Calculator.expendSumExpr(expr);
        }
        List<String> rawcoltags = null;//Calculator.findCellNms(expr);
        coltags = Lists.newArrayListWithCapacity(rawcoltags.size());
        String s = expr;
        this.tipExpr = s;
        for (String tag : rawcoltags) {
            String coltag = ReadUtils.removeLastNumber(tag);
            s = StringUtils.replace(s, tag, "${" + coltag + "}", 1);
            this.tipExpr = StringUtils.replace(this.tipExpr, tag, coltag, 1);
            coltags.add(coltag);
        }
        String source = s;
        source = StringUtils.replace(source, "100%", "1");
        if (exprType == 2) {
            String ss[] = StringUtils.split(source, "+");
            Set<String> set = Sets.newHashSet(ss);
            if (set.size() == 1) {
                this.sumType = SUM_ROW;
            } else {
                this.sumType = SUM_COL;
            }
        }
        return source;
    }

    public java.util.List<String> getColtags() {
        return coltags;
    }

    public String getTipExpr() {
        /*
         * if(StringUtils.isNotEmpty(this.rawExpr)){ return
		 * this.rawExpr.replace("[\\d、]", ""); }
		 */
        return this.tipExpr;
    }

    public int getSumType() {
        return sumType;
    }

    public List<String> getSheetTags() {
        return sheetTags;
    }

}
