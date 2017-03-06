package com.pds.p2p.core.poi.ppt.chart.ser;

import java.util.List;

import com.pds.p2p.core.j2ee.xml.dom.XmlOper;
import com.pds.p2p.core.poi.comm.WorkBookRef;
import com.pds.p2p.core.poi.ppt.chart.ChartElement;
import org.w3c.dom.Element;

public class WithStrRef extends ChartElement {
    public StrRef getStrRef() {
        return strRef;
    }

    private StrRef strRef;

    public WithStrRef(Element element) {
        super(element);
        Element strRef = XmlOper.getElementsByName(this.getElement(), "c:strRef")[0];
        this.strRef = new StrRef(strRef);
    }

    public void setCf(String cfVal) {
        strRef.setCf(cfVal);
    }

    public void setData(List<String> dat) {
        strRef.setData(dat);
    }

    public int getOldLength() {
        return strRef.getOldLength();
    }

    public WorkBookRef getBookRef() {
        return strRef.getBookRef();
    }

}
