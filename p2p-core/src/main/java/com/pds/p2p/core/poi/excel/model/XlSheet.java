package com.pds.p2p.core.poi.excel.model;

import java.util.List;

import com.pds.p2p.core.poi.excel.rule.XlValidator;
import com.pds.p2p.core.poi.excel.rule.validator.ConfXlValidator;

public class XlSheet {
    private List<XlColumn> columns;

    private String name;
    private String table;
    private Integer index;
    private XlStatics xlStatics;
    private XlStart xlStart;
    private XlKey xlKey;
    private XlWorkbook workbook;
    private String attr;
    private XlValidator validator;

    public int idxColumn(String colnm) {
        int k = -1;
        for (int m = 0; m < columns.size(); ++m) {
            XlColumn xlColumn = columns.get(m);
            if (xlColumn.getName().equalsIgnoreCase(colnm)) {
                k = m;
                break;
            }
        }
        return k;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public XlWorkbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(XlWorkbook workbook) {
        this.workbook = workbook;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public XlKey getXlKey() {
        return xlKey;
    }

    public void setXlKey(XlKey xlKey) {
        this.xlKey = xlKey;
    }

    public XlStatics getXlStatics() {
        return xlStatics;
    }

    public void setXlStatics(XlStatics xlStatics) {
        this.xlStatics = xlStatics;
    }

    public XlStart getXlStart() {
        return xlStart;
    }

    public void setXlStart(XlStart xlStart) {
        this.xlStart = xlStart;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<XlColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<XlColumn> columns) {
        this.columns = columns;
    }

    public XlColumn findXlColumn(String id) {
        for (XlColumn column : columns) {
            if (column.getId().equalsIgnoreCase(id)) {
                return column;
            }
        }
        return null;
    }

    public XlValidator getValidator() {
        if (validator == null) {
            validator = new ConfXlValidator();
            validator.loadRules(this);
        }
        return validator;
    }

    public void setValidator(XlValidator validator) {
        this.validator = validator;
        validator.loadRules(this);
    }

}
