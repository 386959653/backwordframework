package com.pds.p2p.core.poi.excel.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class XlKey {
    private final XlSheet xlSheet;
    private final List<XlColumn> columns;
    private String columnIds;

    public XlKey(XlSheet xlSheet) {
        this.xlSheet = xlSheet;
        this.columns = Lists.newArrayList();
    }

    public void setColumnIds(String columnIdxs) {
        String ids[] = StringUtils.split(columnIdxs, ",");
        for (String id : ids) {
            this.columns.add(xlSheet.findXlColumn(id));
        }
        this.columnIds = columnIdxs;
    }

    public String getColumnIds() {
        return columnIds;
    }

    public XlSheet getXlSheet() {
        return xlSheet;
    }

    public List<XlColumn> getColumns() {
        return columns;
    }

}
