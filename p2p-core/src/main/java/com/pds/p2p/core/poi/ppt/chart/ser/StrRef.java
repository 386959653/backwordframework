package com.pds.p2p.core.poi.ppt.chart.ser;

import org.w3c.dom.Element;

/***
 * c:strRef代理
 * <p>
 * strRef |--f |--strCache |--ptCount |--pt(s) |--c:v
 *
 * @author wen
 */
public class StrRef extends Ref {
    public StrRef(Element element) {
        super(element, "c:strCache");
    }

}