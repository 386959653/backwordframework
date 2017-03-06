package com.pds.p2p.core.poi.excel.model;

import com.pds.p2p.core.data.FieldDef;
import com.pds.p2p.core.data.ListData;

public class XlColumn extends FieldDef {
    private XlSheet xlSheet;

    public XlColumn() {
        super();
    }

    public XlColumn(ListData listData) {
        super(listData);
    }

    public XlColumn(String id) {
        super(id);
    }

    public XlColumn(String id, String name) {
        super(id, name);
    }

    public XlColumn(String id, String name, String field) {
        super(id, name, field);
    }

    public XlSheet getXlSheet() {
        return xlSheet;
    }

    public void setXlSheet(XlSheet xlSheet) {
        this.xlSheet = xlSheet;
    }

}
