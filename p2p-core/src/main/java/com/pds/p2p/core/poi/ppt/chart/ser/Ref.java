package com.pds.p2p.core.poi.ppt.chart.ser;

import java.util.List;

import com.pds.p2p.core.j2ee.xml.dom.XmlOper;
import org.w3c.dom.Element;

import com.pds.p2p.core.poi.comm.WorkBookRef;
import com.pds.p2p.core.poi.ppt.chart.ChartElement;

/***
 * c:strRef代理
 * <p>
 * strRef |--f |--strCache |--ptCount |--pt(s) |--c:v
 *
 * @author wen
 */
public class Ref extends ChartElement {
    private Element cf;
    private Element cache;
    private Element ptCount;
    private int oldLength;

    public Ref(Element element, String tagCacheName) {
        super(element);
        cf = XmlOper.getElementsByName(this.getElement(), "c:f")[0];
        cache = XmlOper.getElementsByName(this.getElement(), tagCacheName)[0];
        ptCount = XmlOper.getElementsByName(cache, "c:ptCount")[0];
        this.oldLength = this.getPtCount();
    }

    public Element getCache() {
        return cache;
    }

    public void setCache(Element cache) {
        this.cache = cache;
    }

    public String getCf() {
        return XmlOper.getElementValue(cf);
    }

    public int getOldLength() {
        return oldLength;
    }

    public void setCf(String cfVal) {
        XmlOper.setElementValue(cf, cfVal);
    }

    public WorkBookRef getBookRef() {
        return WorkBookRef.newBookRef(XmlOper.getElementValue(cf));
    }

    public void setData(List<String> dat) {
        int n = 0;
        XmlOper.setElementAttr(ptCount, "val", String.valueOf(dat.size()));
        Element[] elements = XmlOper.getElementsByName(cache, "c:pt");
        for (; n < dat.size(); ++n) {
            XmlOper.setElementValue((Element) elements[n].getChildNodes().item(0), dat.get(n));
        }
        /*
         * for (int k = n; k < elements.length; ++k) {
		 * cache.removeChild(elements[k]); }
		 */
    }

    public int getPtCount() {
        return Integer.valueOf(XmlOper.getElementAttr(ptCount, "val"));
    }

}
