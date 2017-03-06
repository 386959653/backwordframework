package com.pds.p2p.core.poi.ppt.excel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import com.pds.p2p.core.poi.excel.utils.ReadUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.collect.Table;

public class XSSFSheetWriter {
    private InputStream pIs;
    private XSSFWorkbook workbook = null;

    public XSSFSheetWriter(InputStream pIs) {
        this.pIs = pIs;
    }

    public void writeTableData(Table<Integer, String, String> tab, OutputStream out) throws IOException {
        try {
            this.workbook = new XSSFWorkbook(pIs);
            XSSFSheet xssfSheet = workbook.getSheetAt(0);
            Set<Integer> rows = tab.rowKeySet();
            Set<String> cols = tab.columnKeySet();
            for (Integer rownum : rows) {
                Row row = xssfSheet.getRow(rownum - 1);
                for (String colNm : cols) {
                    int colIdx = ReadUtils.columnToIndex(colNm);
                    try {
                        row.getCell(colIdx).setCellValue(Double.valueOf(tab.get(rownum, colNm)));
                    } catch (Exception ex) {
                        row.getCell(colIdx).setCellValue(tab.get(rownum, colNm));
                    }
                }
            }
            //this.workbook.write(out);*/
        } finally {
            IOUtils.closeQuietly(workbook);
            IOUtils.closeQuietly(pIs);
            IOUtils.closeQuietly(out);

        }
    }

}
