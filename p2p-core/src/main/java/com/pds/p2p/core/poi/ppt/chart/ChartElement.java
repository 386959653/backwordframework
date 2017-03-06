package com.pds.p2p.core.poi.ppt.chart;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ChartElement {

    public ChartElement() {
        super();
    }

    public ChartElement(Element element) {
        super();
        this.element = element;
    }

    private Element element;

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Element createElement(String tagName) {
        Document doc = this.element.getOwnerDocument();
        Element ele = doc.createElement(tagName);
        return ele;
    }

}
