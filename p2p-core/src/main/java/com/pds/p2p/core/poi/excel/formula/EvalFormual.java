/**
 *
 */
package com.pds.p2p.core.poi.excel.formula;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Ordering;

/****************************************************
 * <pre>
 * 描    述： 分析行内公式之间的关系,确定计算级别
 *
 * Company:  普德施PDS
 * @author 王文
 * @version 1.0   2015年3月1日
 * @see （选填）
 * 	HISTORY  （必填）
 * 	2015年3月1日  Administrator 创建文件
 *
 * </pre>
 **************************************************/

public class EvalFormual {
    public static void main(String[] args) {
        XlFormlula formlula1 = new XlFormlula();
        formlula1.setExpr("B1+C1");
        XlFormlula formlula2 = new XlFormlula();
        formlula2.setExpr("D1+F1/'1-资产表'!A5");
        XlFormlula formlula3 = new XlFormlula();
        formlula3.setExpr("H1+X1");
        XlFormlula formlula4 = new XlFormlula();
        formlula4.setExpr("K1+AX1");

        XlFormulaRow formulaRow = new XlFormulaRow();
        formulaRow.add("A", formlula1);
        formulaRow.add("B", formlula2);
        formulaRow.add("F", formlula3);
        formulaRow.add("AA", formlula4);

        System.out.println(EvalFormual.eval(formulaRow));

    }

    public static List<XlFormlula> eval(XlFormulaRow formulaRow) {
        Collection<XlFormlula> formlulas = formulaRow.getXlFormlulas().values();
        for (XlFormlula formlula : formlulas) {
            _eval(formulaRow, formlula);
        }
        Ordering<XlFormlula> ordering = new Ordering<XlFormlula>() {
            @Override
            public int compare(XlFormlula arg0, XlFormlula arg1) {
                return arg0.getLevel() - arg1.getLevel();
            }
        };
        List<XlFormlula> result = ordering.sortedCopy(formlulas);
        return result;
    }

    private static void _eval(XlFormulaRow formulaRow, XlFormlula formlula) {
        if (formlula == null) {
            return;
        }
        String col = formlula.getColKey();
        List<String> coltags = formlula.getColtags();
        for (String coltag : coltags) {
            if (coltag.equals(col)) {
                continue;
            }
            XlFormlula fla = formulaRow.getXlFormlulas().get(coltag);
            _eval(formulaRow, fla);
            if (fla == null) {
            } else {
                if (formlula.getLevel() > 0) {
                    return;
                }
                int max = 0;
                for (String coltag1 : coltags) {
                    XlFormlula fla1 = formulaRow.getXlFormlulas().get(coltag1);
                    if (fla1 != null) {
                        max = Math.max(fla1.getLevel(), formlula.getLevel());
                    }
                }
                formlula.incLevel(max);
            }
        }

    }
}
