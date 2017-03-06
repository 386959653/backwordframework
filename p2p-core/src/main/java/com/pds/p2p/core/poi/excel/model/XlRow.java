package com.pds.p2p.core.poi.excel.model;

import java.util.List;

import com.pds.p2p.core.data.FieldDef;
import com.google.common.collect.Lists;

public class XlRow extends FieldDef {
    private List<XlCell> xlCells;

    public List<XlCell> getXlCells() {
        return xlCells;
    }

    public XlRow() {
        this.xlCells = Lists.newArrayList();
    }

    public void add(XlCell xlCell) {
        this.xlCells.add(xlCell);
    }

}
