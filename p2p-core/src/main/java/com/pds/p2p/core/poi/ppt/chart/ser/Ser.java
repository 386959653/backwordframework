package com.pds.p2p.core.poi.ppt.chart.ser;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.pds.p2p.core.j2ee.xml.dom.XmlOper;
import com.pds.p2p.core.poi.comm.TableData;
import com.pds.p2p.core.poi.comm.WorkBookRef;
import com.pds.p2p.core.poi.excel.model.CellIdx;
import com.pds.p2p.core.poi.ppt.chart.ChartElement;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class Ser extends ChartElement {

    private Tx tx;
    private Cat cat;
    private Val val;

    public Ser(Element serElement) {
        super(serElement);
        this.build();
    }

    private void build() {
        Element[] txs = XmlOper.getElementsByMultiName(this.getElement(), "c:tx");
        Element[] cats = XmlOper.getElementsByMultiName(this.getElement(), "c:cat");
        Element[] vals = XmlOper.getElementsByMultiName(this.getElement(), "c:val");
        tx = new Tx(txs[0]);
        cat = new Cat(cats[0]);
        val = new Val(vals[0]);
    }

    public void write(TableData tableData) {

        WorkBookRef bookRef = cat.getStrRef().getBookRef();

        CellIdx startCellIdx = bookRef.getStartCellIdx();
        CellIdx endCellIdx = bookRef.getEndCellIdx();

        String catCol = startCellIdx.getColumnKey();

        int startRow = startCellIdx.getRowKey();
        int endRow = endCellIdx.getRowKey();

        bookRef = val.getNumRef().getBookRef();
        String valCol = bookRef.getEndCellIdx().getColumnKey();

        List<String> catData = Lists.newArrayList();
        List<String> valData = Lists.newArrayList();
        for (int rownum = startRow; rownum <= endRow; ++rownum) {
            catData.add(tableData.getTab().get(rownum, catCol));
            valData.add(tableData.getTab().get(rownum, valCol));
        }
        cat.setData(catData);
        val.setData(valData);

        bookRef = tx.getBookRef();
        startCellIdx = bookRef.getStartCellIdx();
        String txPt = tableData.getTab().get(startCellIdx.getRowKey(), startCellIdx.getColumnKey());
        if (StringUtils.isNotEmpty(txPt)) {
            tx.setData(ImmutableList.of(txPt));
        }
    }

    public void setTxCf(String cfVal) {
        tx.setCf(cfVal);
    }

    public void setTxData(List<String> dat) {
        tx.setData(dat);
    }

    public void setCatCf(String cfVal) {
        cat.setCf(cfVal);
    }

    public void setCatData(List<String> dat) {
        cat.setData(dat);
    }

    public void setValCf(String cfVal) {
        val.setCf(cfVal);
    }

    public void setValData(List<String> dat) {
        val.setData(dat);
    }

    public int getOldPtCount() {
        return val.getOldLength();
    }

    public Tx getTx() {
        return tx;
    }

    public Cat getCat() {
        return cat;
    }

    public Val getVal() {
        return val;
    }

}
