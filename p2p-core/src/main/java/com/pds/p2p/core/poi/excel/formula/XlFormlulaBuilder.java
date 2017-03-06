/**
 *
 */
package com.pds.p2p.core.poi.excel.formula;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.pds.p2p.core.poi.excel.model.XlColumn;
import com.pds.p2p.core.poi.excel.model.XlSheet;
import com.pds.p2p.core.poi.excel.model.XlStart;
import com.pds.p2p.core.poi.excel.model.XlWorkbook;
import com.pds.p2p.core.poi.excel.utils.ReadUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.ImmutableList;
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

public class XlFormlulaBuilder {

    private Map<String, XlFormulaRow> maptol = Maps.newLinkedHashMap();
    private Map<String, XlFormulaRow> mapdal = Maps.newLinkedHashMap();

    public XlFormlulaBuilder get(XlWorkbook xlWorkbook, InputStream input) throws InvalidFormatException, IOException {
        Workbook wb = WorkbookFactory.create(input);
        int ns = wb.getNumberOfSheets();
        for (int i = 0; i < ns; ++i) {
            Sheet sh = wb.getSheetAt(i);
            XlSheet xlSheet = xlWorkbook.findXlSheet(sh.getSheetName());
            if (xlSheet != null) {
                XlStart xlStart = xlSheet.getXlStart();
                int nstart = xlStart.getRow();
                int ndetail = nstart + 1;
                Row tolrow = sh.getRow(nstart); // 起始行是总计行
                Row dalrow = sh.getRow(ndetail);// 总计行下是第一个细节行
                for (XlColumn xlColumn : xlSheet.getColumns()) {
                    String id = xlColumn.getId();
                    int colnmIdx = ReadUtils.columnToIndex(id);
                    Cell celltol = tolrow.getCell(colnmIdx);
                    Cell celldal = dalrow.getCell(colnmIdx);
                    if (celltol.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        XlFormlula xlFormlula = new XlFormlula(celltol.getCellFormula());
                        getXlFormulaRow(maptol, sh.getSheetName()).add(id, xlFormlula);
                    }
                    if (celldal.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        XlFormlula xlFormlula = new XlFormlula(celldal.getCellFormula());
                        getXlFormulaRow(mapdal, sh.getSheetName()).add(id, xlFormlula);
                    }
                }
            }
        }

        return this;
    }

    /***
     * 从特定的sheet读出特定列的计算公式:针对总计行
     *
     * @param shnm
     * @param colnm
     *
     * @return XlFormlula
     */
    public XlFormlula getTotlFormula(String shnm, String colnm) {
        return maptol.get(shnm).get(colnm);
    }

    /***
     * 从特定的sheet读出特定列的计算公式:针对明细行
     *
     * @param shnm
     * @param colnm
     *
     * @return XlFormlula
     */
    public XlFormlula getDetailFormula(String shnm, String colnm) {
        return mapdal.get(shnm).get(colnm);
    }

    private XlFormulaRow getXlFormulaRow(Map<String, XlFormulaRow> map, String shnm) {
        XlFormulaRow formulaRow = map.get(shnm);
        if (formulaRow == null) {
            formulaRow = new XlFormulaRow();
            map.put(shnm, formulaRow);
        }
        return formulaRow;
    }

    private List<XlSheetFormlula> xlSheetFormlulas;

    public void pare(String text) {
        xlSheetFormlulas = JSONArray.parseArray(text, XlSheetFormlula.class);
        if (xlSheetFormlulas == null) {
            xlSheetFormlulas = ImmutableList.of();
        }
    }

    public List<XlFormlula> getFormlulasOfSheet(String sheetName) {
        for (XlSheetFormlula xlSheetFormlula : xlSheetFormlulas) {
            if (xlSheetFormlula.getSheetName().equalsIgnoreCase(sheetName)) {
                return xlSheetFormlula.getFormlulas();
            }
        }
        return ImmutableList.of();
    }
}
