package com.pds.p2p.core.poi.ppt.chart.ser;

import org.w3c.dom.Element;

import com.pds.p2p.core.j2ee.xml.dom.XmlCreater;
import com.pds.p2p.core.poi.ppt.chart.ChartElement;

public class Order extends ChartElement {

    public Order() {
        super();
    }

    public Order(String val) {
        super();
        this.val = val;
    }

    public Order(Element element) {
        super(element);
    }

    private String val;

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public Element newElement() {
        XmlCreater xmlCreater = new XmlCreater("");
        Element element = xmlCreater.createRootElement("c:order");
        element.setAttribute("val", this.getVal());
        return element;
    }

}
