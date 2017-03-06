package com.pds.p2p.core.poi.comm;

import com.pds.p2p.core.poi.excel.model.CellIdx;
import org.apache.commons.lang3.StringUtils;

public class WorkBookRef {
    private String sheet;
    private String startCell;
    private String endCell;

    public static WorkBookRef newBookRef(String txt) {
        WorkBookRef bookRef = new WorkBookRef();
        String[] ss = StringUtils.split(txt, "!");
        bookRef.sheet = ss[0];
        ss = StringUtils.split(ss[1], ":");
        bookRef.startCell = StringUtils.remove(ss[0], '$');
        if (ss.length > 1) {
            bookRef.endCell = StringUtils.remove(ss[1], '$');
        } else {
            bookRef.endCell = bookRef.startCell;
        }
        return bookRef;
    }

    public String getSheet() {
        return sheet;
    }

    public String getStartCell() {
        return startCell;
    }

    public String getEndCell() {
        return endCell;
    }

    public CellIdx getStartCellIdx() {
        return new CellIdx(this.getStartCell());
    }

    public CellIdx getEndCellIdx() {
        return new CellIdx(this.getEndCell());
    }

}
