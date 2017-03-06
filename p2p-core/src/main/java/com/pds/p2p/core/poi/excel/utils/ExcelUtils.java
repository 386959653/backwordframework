package com.pds.p2p.core.poi.excel.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;

public class ExcelUtils {
    public static void setCellVal(Sheet sheet, int nrow, int ncol, String val) {
        Cell cell = getOrCreateCell(sheet, nrow, ncol);
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(val);

    }

    public static void setCellVal(Sheet sheet, int nrow, int ncol, boolean val) {
        Cell cell = getOrCreateCell(sheet, nrow, ncol);
        cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
        cell.setCellValue(val);
    }

    public static void setCellVal(Sheet sheet, int nrow, int ncol, Number val) {
        Cell cell = getOrCreateCell(sheet, nrow, ncol);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellValue(val.doubleValue());
    }

    public static Cell getOrCreateCell(Sheet sheet, int nrow, int ncol) {
        Row row = getOrCreateRow(sheet, nrow);
        Cell cell = row.getCell(ncol);
        if (cell == null) {
            cell = row.createCell(ncol);
        }
        return cell;
    }

    private static Row getOrCreateRow(Sheet sheet, int nrow) {
        Row row = sheet.getRow(nrow);
        if (row == null) {
            row = sheet.createRow(nrow);
        }
        return row;
    }

    static public Object getValue(Sheet sh, String cell) {
        CellReference cellReference = new CellReference(cell);
        Row row = sh.getRow(cellReference.getRow());
        if (row == null) {
            return "";
        }
        Cell _cell = row.getCell(cellReference.getCol());
        return getCellVal(_cell);
    }

    public static Object getCellVal(Cell cell) {
        if (cell == null) {
            return "";
        }
        int cellType = cell.getCellType();
        switch (cellType) {
            case Cell.CELL_TYPE_BLANK:
                return "";
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_ERROR:
                return cell.getErrorCellValue();
            case Cell.CELL_TYPE_FORMULA:
                try {
                    return cell.getNumericCellValue();
                } catch (Exception e) {
                    return 0;
                }
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case Cell.CELL_TYPE_STRING:
                return cell.getRichStringCellValue().toString();
            default:
                return "";
        }
    }

    /**
     * 判断指定的单元格是否是合并单元格
     *
     * @param sheet
     * @param row    行下标
     * @param column 列下标
     *
     * @return
     */
    static public boolean isMergedRegion(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            if (range.isInRange(row, column)) {
                return true;
            }
        }
        return false;
    }

}
