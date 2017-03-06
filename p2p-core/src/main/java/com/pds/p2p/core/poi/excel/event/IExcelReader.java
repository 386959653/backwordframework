package com.pds.p2p.core.poi.excel.event;

import java.util.Map;

public interface IExcelReader {

    public boolean startRead();

    public void endRead();

    public boolean beforeReadSheet(int sheetIndex, String sheetName);

    public void afterReadSheet(int sheetIndex, String sheetName);

    public void rowRead(int sheetIndex, String sheetName, int curRow, Map<String, String> rowVals);

}
