/**
 *
 */
package com.pds.p2p.core.poi.excel.formula;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.pds.p2p.core.poi.excel.model.CellIdx;
import com.pds.p2p.core.poi.excel.utils.ReadUtils;

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

public class Test {
    public static void main(String[] args) {
        File file = new File("E:/公司工作/普德施/铁塔项目/04.经营分析/09.开发内容/excel需求/最新省分报表20150226（开发版）V1.0.xls");
        try {
            Workbook wb = WorkbookFactory.create(file);
            Sheet sh = wb.getSheet("9、室分项目投资");
            CellIdx cellIdx = new CellIdx("Z7");
            System.out.println(cellIdx.getRowKey());
            System.out.println(cellIdx.getColumnKey());
            Row row = sh.getRow(cellIdx.getRowKey() - 1);
            Cell cell = row.getCell(ReadUtils.columnToIndex(cellIdx.getColumnKey()));
            if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                System.out.println(cell.getCellFormula());
                try {
                    System.out.println(String.valueOf(cell.getNumericCellValue()));
                    System.out.println("ok");
                } catch (IllegalStateException e) {
                    System.out.println(String.valueOf(cell.getRichStringCellValue()));
                }
            }

        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
