package com.pds.p2p.core.poi.ppt.chart.ax;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.w3c.dom.Element;

import com.pds.p2p.core.j2ee.xml.dom.XmlOper;
import com.pds.p2p.core.poi.ppt.chart.ChartElement;

public class ValAx extends ChartElement {

    private Element scalingEl;
    private Element majorUnitEl;

    public ValAx(Element element) {
        super(element);
        scalingEl = XmlOper.getElementsByName(this.getElement(), "c:scaling")[0];
        Element majorUnitEls[] = XmlOper.getElementsByName(this.getElement(), "c:majorUnit");
        if (ArrayUtils.isNotEmpty(majorUnitEls)) {
            majorUnitEl = majorUnitEls[0];
        }
    }

    public void setMajorUnit(String mu) {
        XmlOper.setElementAttr(majorUnitEl, "val", mu);
    }

    public int getMajorUnitForInt() {
        String mu = XmlOper.getElementAttr(majorUnitEl, "val");
        return NumberUtils.toInt(mu);
    }

    public void setMax(String max) {
        Element maxEl = XmlOper.getElementsByName(scalingEl, "c:max")[0];
        XmlOper.setElementAttr(maxEl, "val", max);
    }

}
