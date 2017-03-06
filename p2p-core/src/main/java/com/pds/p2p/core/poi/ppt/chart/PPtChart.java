package com.pds.p2p.core.poi.ppt.chart;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.xslf.usermodel.XSLFChart;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.w3c.dom.Element;

import com.pds.p2p.core.j2ee.xml.dom.XmlOper;
import com.pds.p2p.core.poi.comm.TableData;
import com.pds.p2p.core.poi.excel.utils.ReadUtils;
import com.pds.p2p.core.poi.ppt.chart.ax.ValAx;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Lists;

public class PPtChart {
    private XSLFChart xslfChart;
    private ValAx valAx;
    private List<SerChart> serCharts;
    private POIXMLDocumentPart xlsPart;

    public PPtChart(XSLFChart xslfChart) {
        this.xslfChart = xslfChart;
        this.parse();
    }

    ;

    private void parse() {
        CTChart ctChart = this.xslfChart.getCTChart();
        CTPlotArea plotArea = ctChart.getPlotArea();

        System.out.println(ctChart);

        Element root = (Element) plotArea.getDomNode();

        Element[] barChartEls = XmlOper.getElementsByName(root, "c:barChart");
        Element[] lineChartEls = XmlOper.getElementsByName(root, "c:lineChart");
        Element[] valAxEl = XmlOper.getElementsByName(root, "c:valAx");

        xlsPart = (POIXMLDocumentPart) xslfChart.getRelations().get(0);
        valAx = new ValAx(valAxEl[0]);
        serCharts = Lists.newArrayListWithCapacity(lineChartEls.length);
        for (Element lineChartEl : lineChartEls) {
            serCharts.add(new SerChart(lineChartEl));
        }

        serCharts = Lists.newArrayListWithCapacity(lineChartEls.length + barChartEls.length);
        for (Element lineChartEl : lineChartEls) {
            serCharts.add(new SerChart(lineChartEl));
        }
        for (Element barChartEl : barChartEls) {
            serCharts.add(new SerChart(barChartEl));
        }
    }

    public void write(TableData tableData) throws IOException {
        for (SerChart chart : serCharts) {
            chart.write(tableData);
        }
        InputStream pIs = xlsPart.getPackagePart().getInputStream();
        XSSFWorkbook wb = new XSSFWorkbook(pIs);
        XSSFSheet xssfSheet = wb.getSheetAt(0);
        pIs.close();
        ArrayTable<Integer, String, String> tab = tableData.getTab();
        List<Integer> rows = tab.rowKeyList();
        List<String> cols = tab.columnKeyList();
        for (Integer rownum : rows) {
            if (rownum == 1) {
                // 目前不能改标签，改过标签后文档这被破坏
                continue;
            }
            XSSFRow xssfRow = xssfSheet.getRow(rownum - 1);
            for (String colNm : cols) {
                int colIdx = ReadUtils.columnToIndex(colNm);
                String val = tab.get(rownum, colNm);
                if (StringUtils.isEmpty(val)) {
                    continue;
                }
                try {
                    xssfRow.getCell(colIdx).setCellValue(Double.valueOf(tab.get(rownum, colNm)));
                } catch (Exception ex) {
                    xssfRow.getCell(colIdx).setCellValue(tab.get(rownum, colNm));
                }
            }
        }
        OutputStream xlsOut = xlsPart.getPackagePart().getOutputStream();
        try {
            wb.write(xlsOut);
        } finally {
            xlsOut.close();
            wb.close();
        }
    }

    public void setValAxMax(String max) {
        valAx.setMax(max);
    }

    public void setValAxMajorUnit(String majorUnit) {
        valAx.setMajorUnit(majorUnit);
    }

}
