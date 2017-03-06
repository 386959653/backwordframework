/**
 *
 */
package com.pds.p2p.core.poi.excel.formula;

import java.util.Map;

import com.google.common.collect.Maps;

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

public class XlFormulaRow {
    public Map<String, XlFormlula> getXlFormlulas() {
        return xlFormlulas;
    }

    private Map<String, XlFormlula> xlFormlulas = Maps.newTreeMap();

    public void add(String colname, XlFormlula xlFormlula) {
        xlFormlula.setColKey(colname);
        xlFormlulas.put(colname, xlFormlula);
    }

    public XlFormlula get(String colname) {
        return xlFormlulas.get(colname);
    }

    @Override
    public String toString() {
        return xlFormlulas.toString();
    }

}
