package com.pds.p2p.core.poi.comm;

import java.util.List;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;

public class TableData {
    public static void main(String[] args) {
        List<Integer> rowKeys = ImmutableList.of(1, 2, 3, 23, 31, 6);
        List<String> columnKeys = ImmutableList.of("A", "B", "C");
        Table<Integer, String, String> tab = ArrayTable.create(rowKeys, columnKeys);
        System.out.println(tab.rowKeySet());

    }

    private ArrayTable<Integer, String, String> tab;

	/*public static Table<Integer, String, String> createTable(WorkBookRef... refs) {
        Set<String> columnKeys = Sets.newTreeSet();
		Set<Integer> rowKeys = Sets.newTreeSet();
		for (WorkBookRef ref : refs) {
			CellIdx startCellIdx = ref.getStartCellIdx();
			CellIdx endCellIdx = ref.getEndCellIdx();
			columnKeys.add(startCellIdx.getColumnKey());
			columnKeys.add(endCellIdx.getColumnKey());
			rowKeys.add(startCellIdx.getRowKey());
			rowKeys.add(endCellIdx.getRowKey());
		}
		List<Integer> list = ImmutableList.copyOf(rowKeys);
		Integer start = list.get(0);
		Integer end = list.get(1);
		rowKeys.clear();
		for (Integer n = start; n <= end; ++n) {
			rowKeys.add(n);
		}
		return ArrayTable.create(rowKeys, columnKeys);
	}*/

    public TableData(Iterable<Integer> rowKeys, Iterable<String> columnKeys) {
        this.tab = ArrayTable.create(rowKeys, columnKeys);
    }

    public void put(int rowKey, String columnKey, String value) {
        this.tab.put(rowKey, columnKey, value);
    }

    public ArrayTable<Integer, String, String> getTab() {
        return tab;
    }

}
