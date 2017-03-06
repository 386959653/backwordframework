package com.pds.p2p.core.poi.excel.model;

import com.pds.p2p.core.poi.excel.utils.ReadUtils;

public class CellIdx {

    final private int rowKey;
    final private String columnKey;

    public CellIdx(String cellnm) {
        StringBuilder colsb = new StringBuilder();
        StringBuilder rowsb = new StringBuilder();
        for (int i = 0; i < cellnm.length(); ++i) {
            char c = cellnm.charAt(i);
            if (Character.isLetter(c)) {
                colsb.append(c);
            } else {
                rowsb.append(c);
            }
        }
        rowKey = Integer.valueOf(rowsb.toString());
        columnKey = colsb.toString();
    }

    public int colForInt() {
        return ReadUtils.columnToIndex(getColumnKey());
    }

    public int rowForInt() {
        return getRowKey() - 1;
    }

    public int getRowKey() {
        return rowKey;
    }

    public String getColumnKey() {
        return columnKey;
    }

}