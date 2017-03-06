package com.pds.p2p.core.jdbc.meta;

import com.pds.p2p.core.jdbc.helper.EnMap;

/**
 * 列map的key值，不缺分大小写以及下划线，所以要求创建表时，不允许相同的字母的字段（下滑线不一个位置）
 *
 * @author wen
 */
public class TableMeta {
    private EnMap<String, ColumnMeta> columns = new EnMap<String, ColumnMeta>();

    /**
     * Returns the number of columns in this table.
     *
     * @return The number of columns
     */
    public int getColumnCount() {
        return columns.size();
    }

    /**
     * Returns the columns in this table.
     *
     * @return The columns
     */
    public ColumnMeta[] getColumns() {
        return columns.values().toArray(new ColumnMeta[columns.size()]);
    }

    /**
     * Adds the given column.
     *
     * @param column The column
     */
    public void addColumnMeta(ColumnMeta column) {
        if (column != null) {
            columns.put(column.getName(), column);
        }
    }

    /**
     * Finds the column with the specified name, using case insensitive
     * matching. Note that this method is not called getColumn(String) to avoid
     * introspection problems.
     *
     * @param name The name of the column
     *
     * @return The column or <code>null</code> if there is no such column
     */
    public ColumnMeta findColumn(String column) {
        return columns.get(column);
    }

    public boolean containsColumn(String column) {
        return columns.containsKey(column);
    }

    public ColumnMeta getAutoIncrementColumnMeta() {
        for (ColumnMeta columnMeta : columns.values()) {
            if (columnMeta.isAutoIncrement()) {
                return columnMeta;
            }
        }
        return null;
    }

    public ColumnMeta getPrimaryKeyolumnMeta() {
        for (ColumnMeta columnMeta : columns.values()) {
            if (columnMeta.isPrimaryKey()) {
                return columnMeta;
            }
        }
        return null;
    }

}
