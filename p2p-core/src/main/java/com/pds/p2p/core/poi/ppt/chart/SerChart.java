package com.pds.p2p.core.poi.ppt.chart;

import java.util.List;

import org.w3c.dom.Element;

import com.pds.p2p.core.j2ee.xml.dom.XmlOper;
import com.pds.p2p.core.poi.comm.TableData;
import com.pds.p2p.core.poi.ppt.chart.ser.Ser;
import com.google.common.collect.Lists;

public class SerChart extends ChartElement {
    private List<Ser> sers;

    public SerChart(Element barElement) {
        super(barElement);
        Element[] serEls = XmlOper.getElementsByMultiName(barElement, "c:ser");
        sers = Lists.newArrayListWithCapacity(serEls.length);
        for (Element serEl : serEls) {
            sers.add(new Ser(serEl));
        }
    }

    public void write(TableData tableData) {
        for (Ser ser : sers) {
            ser.write(tableData);
            //System.out.println(ser.getVal().getNumRef().getCf());
        }
    }
}
