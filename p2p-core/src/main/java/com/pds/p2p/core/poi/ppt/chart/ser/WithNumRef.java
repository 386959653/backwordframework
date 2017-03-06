package com.pds.p2p.core.poi.ppt.chart.ser;

import java.util.List;

import org.w3c.dom.Element;

import com.pds.p2p.core.j2ee.xml.dom.XmlOper;
import com.pds.p2p.core.poi.comm.WorkBookRef;
import com.pds.p2p.core.poi.ppt.chart.ChartElement;

public class WithNumRef extends ChartElement {
    private NumRef numRef;

    public NumRef getNumRef() {
        return numRef;
    }

    public WithNumRef(Element element) {
        super(element);
        Element numRef = XmlOper.getElementsByName(this.getElement(), "c:numRef")[0];
        this.numRef = new NumRef(numRef);
    }

    public void setCf(String cfVal) {
        numRef.setCf(cfVal);
    }

    public void setData(List<String> dat) {
        numRef.setData(dat);
    }

    public int getOldLength() {
        return numRef.getOldLength();
    }

    public WorkBookRef getBookRef() {
        return numRef.getBookRef();
    }

}
