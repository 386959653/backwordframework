package com.pds.p2p.core.poi.excel.model;

import java.util.List;

import com.google.common.collect.Lists;

public class XlWorkbook {

    private List<XlSheet> sheets = Lists.newArrayList();

    public XlWorkbook(List<XlSheet> sheets) {
        super();
        this.sheets = sheets;
        int n = 0;
        for (XlSheet xlSheet : sheets) {
            xlSheet.setWorkbook(this);
            xlSheet.setIndex(n++);
        }
    }

    public List<XlSheet> getSheets() {
        return sheets;
    }

    public XlSheet findXlSheet(String sheetName) {
        for (XlSheet xlSheet : sheets) {
            if (xlSheet.getName().equalsIgnoreCase(sheetName)) {
                return xlSheet;
            }
        }
        return null;
    }

    public int idxSheet(String sheetName) {
        int k = -1;
        for (int m = 0; m < sheets.size(); ++m) {
            XlSheet xlSheet = sheets.get(m);
            if (xlSheet.getName().equalsIgnoreCase(sheetName)) {
                k = m;
                break;
            }
        }
        return k;
    }

    public boolean containSheetName(String sheetName) {
        return findXlSheet(sheetName) != null;
    }

    public XlSheet findXlSheet(Integer indx) {
        for (XlSheet xlSheet : sheets) {
            if (xlSheet.getIndex() == indx) {
                return xlSheet;
            }
        }
        return null;
    }

    public XlSheet findXlSheetByTable(String table) {
        for (XlSheet xlSheet : sheets) {
            if (xlSheet.getTable().equalsIgnoreCase(table)) {
                return xlSheet;
            }
        }
        return null;
    }
}
