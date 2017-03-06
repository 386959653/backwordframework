package com.pds.p2p.core.poi.excel.model;

import com.pds.p2p.core.data.FieldDef;
import com.pds.p2p.core.data.ListData;

public class XlCell extends FieldDef {

    public XlCell(ListData listData) {
        super(listData);
    }

    public XlCell(String id, String name, String field) {
        super(id, name, field);
    }

    public XlCell(String id, String name) {
        super(id, name);
    }

    public XlCell(String id) {
        super(id);
    }

    public XlCell() {
    }

}
