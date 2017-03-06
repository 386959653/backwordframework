package com.pds.p2p.core.poi.excel.model;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.pds.p2p.core.data.FieldDef;
import com.pds.p2p.core.data.ListData;
import com.pds.p2p.core.poi.excel.utils.ExcelUtils;
import com.pds.p2p.core.poi.excel.utils.ReadUtils;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class XlRange extends FieldDef {

    private String rows;
    private String cols;
    private String numclos;

    private ArrayTable<Integer, String, Object> rawTab;//放置原始数据
    private ArrayTable<Integer, String, Double> calcTab; // 放置计算z
    private List<XlRow> xlRows = Lists.newArrayList();

    private Predicate<String> predicateIgnore;

    public Predicate<String> getPredicateIgnore() {
        return predicateIgnore;
    }

    public XlRange setPredicateIgnore(Predicate<String> predicateIgnore) {
        this.predicateIgnore = predicateIgnore;
        return this;
    }

    public void add(XlRow xlRow) {
        xlRows.add(xlRow);
    }

    public ArrayTable<Integer, String, Object> getRawTab() {
        return rawTab;
    }

    public ArrayTable<Integer, String, Double> getCalcTab() {
        return calcTab;
    }

    public List<XlRow> getXlRows() {
        return xlRows;
    }

    public String getRows() {
        return rows;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }

    public String getCols() {
        return cols;
    }

    public void setCols(String cols) {
        this.cols = cols;
    }

    public String getNumclos() {
        return numclos;
    }

    public void build() {
        String[] rowrg = StringUtils.split(this.rows, '-');
        String[] colrg = StringUtils.split(this.cols, '-');
        String[] ncolrg = StringUtils.split(this.numclos, '-');
        List<Integer> rowKeys = Lists.newArrayList();
        List<String> columnKeys = Lists.newArrayList();
        List<String> ncolumnKeys = Lists.newArrayList();
        for (int i = NumberUtils.toInt(rowrg[0]); i <= NumberUtils.toInt(rowrg[1]); ++i) {
            rowKeys.add(i);
        }
        for (int i = colrg[0].charAt(0); i <= colrg[1].charAt(0); ++i) {
            columnKeys.add((char) i + "");
        }
        for (int i = ncolrg[0].charAt(0); i <= ncolrg[1].charAt(0); ++i) {
            ncolumnKeys.add((char) i + "");
        }
        rawTab = ArrayTable.create(rowKeys, columnKeys);
        calcTab = ArrayTable.create(rowKeys, ncolumnKeys);
    }

    public void readSheet(Sheet sheet) {
        List<Integer> rowKeys = rawTab.rowKeyList();
        List<String> columnKeys = rawTab.columnKeyList();
        for (Integer rowKey : rowKeys) {
            Row row = sheet.getRow(rowKey - 1);
            for (String columnKey : columnKeys) {
                int idx = ReadUtils.columnToIndex(columnKey);
                Cell cell = row.getCell(idx);
                Object val = ExcelUtils.getCellVal(cell);
                rawTab.put(rowKey, columnKey, val);
                try {
                    calcTab.put(rowKey, columnKey, NumberUtils.toDouble(val.toString(), 0d));
                } catch (Exception e) {

                }
            }
        }
    }

    public void putValToTabcp(String rowId, String colId, Double val) {
        calcTab.put(NumberUtils.toInt(rowId), colId, val);
    }

    /***
     * 取行数据
     *
     * @param rowId
     *
     * @return
     */
    public Map<String, Double> rowValMap(String rowId) {
        return calcTab.row(NumberUtils.toInt(rowId));
    }

    /**
     * 取单元格数据
     *
     * @param finds
     *
     * @return
     */
    public Map<String, Double> cellValMap(List<String> finds) {
        Map<String, Double> result = Maps.newHashMap();
        for (String find : finds) {
            CellIdx cellIdx = new CellIdx(find);
            Double val = calcTab.get(cellIdx.getRowKey(), cellIdx.getColumnKey());
            result.put(find, val);
        }
        return result;
    }

    public void setNumclos(String numclos) {
        this.numclos = numclos;
    }

    public XlRange(ListData listData) {
        super(listData);
    }

    public XlRange(String id, String name, String field) {
        super(id, name, field);
    }

    public XlRange(String id, String name) {
        super(id, name);
    }

    public XlRange(String id) {
        super(id);
    }

    public XlRange() {
    }

}
