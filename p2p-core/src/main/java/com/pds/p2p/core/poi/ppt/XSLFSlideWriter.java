package com.pds.p2p.core.poi.ppt;

import java.io.IOException;
import java.util.List;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.xslf.usermodel.XSLFChart;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import com.pds.p2p.core.poi.comm.TableData;
import com.pds.p2p.core.poi.ppt.chart.PPtChart;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class XSLFSlideWriter {
    final private List<XSLFTextRun> textRuns = Lists.newArrayList();
    final private List<XSLFTable> tables = Lists.newArrayList();
    final private List<PPtChart> chars = Lists.newArrayList();

    public XSLFSlideWriter(XSLFSlide xslfSlide) {
        this.init(xslfSlide);
    }

    public XSLFSlideWriter replaceText(List<String> vals) {
        int index = 0;
        for (XSLFTextRun r : textRuns) {
            try {
                r.setText(r.getRawText().replace("?", vals.get(index++)));
            } catch (Exception ex) {

            }
        }
        return this;
    }

    public XSLFSlideWriter replaceChartData(int idx, TableData tableData) throws IOException {
        chars.get(idx).write(tableData);
        return this;
    }

    public XSLFSlideWriter replaceTableCells(int index, int startRow, int startCol, List<List<String>> cellVals) {
        XSLFTable table = tables.get(index);
        List<XSLFTableRow> rows = table.getRows();
        for (int trow = startRow; trow < rows.size(); ++trow) {
            XSLFTableRow row = rows.get(trow);
            int vrow = trow - startRow;
            if (vrow >= cellVals.size()) {
                break;
            }
            List<String> cellRowVals = cellVals.get(trow - startRow);
            List<XSLFTableCell> cells = row.getCells();
            for (int tcol = startCol; tcol < cells.size(); tcol++) {
                try {
                    XSLFTableCell cell = cells.get(tcol);
                    String val = cellRowVals.get(tcol - startCol);
                    setXSLFTextShapeVal(cell, val);
                } catch (Exception ex) {

                }
            }
        }
        return this;
    }

    private void setXSLFTextShapeVal(XSLFTextShape txShape, String val) {
        setXSLFTextShapeVal(txShape, ImmutableList.of(val));
    }

    private void setXSLFTextShapeVal(XSLFTextShape txShape, List<String> vals) {
        int index = 0;
        for (XSLFTextParagraph p : txShape) {
            for (XSLFTextRun r : p) {
                if (r.getRawText().contains("?")) {
                    r.setText(r.getRawText().replace("?", vals.get(index++)));
                }
            }
        }
    }

    private void init(XSLFSlide xslfSlide) {
        for (POIXMLDocumentPart part : xslfSlide.getRelations()) {
            if ((part instanceof XSLFChart)) {
                XSLFChart chart = (XSLFChart) part;
                chars.add(new PPtChart(chart));
            }
        }
        for (XSLFShape shape : xslfSlide) {
            if ((shape instanceof XSLFTextShape)) {
                XSLFTextShape txShape = (XSLFTextShape) shape;
                for (XSLFTextParagraph p : txShape) {
                    for (XSLFTextRun r : p) {
                        if (r.getRawText().contains("?")) {
                            textRuns.add(r);
                        }
                    }
                }
            } else if (shape instanceof XSLFTable) {
                XSLFTable table = (XSLFTable) shape;
                tables.add(table);
            } else {

            }
        }
    }
}
