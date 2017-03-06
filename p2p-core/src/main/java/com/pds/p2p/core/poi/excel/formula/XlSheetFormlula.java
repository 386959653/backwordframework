/**
 *
 */
package com.pds.p2p.core.poi.excel.formula;

import java.util.List;

/****************************************************
 * <pre>
 * 描    述： 一个sheet对应一个公式
 *
 * 实施资源: （选填）
 * 调用者  : （选填）
 * 被调用者: （选填）
 * Company:  普德施PDS
 * @author 王文
 * @version 1.0   2015年3月1日
 * @see （选填）
 * 	HISTORY  （必填）
 * 	2015年3月1日  Administrator 创建文件
 *
 * </pre>
 **************************************************/

public class XlSheetFormlula {
    private String sheetName;
    private List<XlFormlula> formlulas;

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<XlFormlula> getFormlulas() {
        return formlulas;
    }

    public void setFormlulas(List<XlFormlula> formlulas) {
        this.formlulas = formlulas;
    }

}
