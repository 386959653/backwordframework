package com.pds.p2p.core.jdbc.mapper;

import org.springframework.jdbc.core.ColumnMapRowMapper;

import com.pds.p2p.core.utils.UtilString;

public class CamelCaseNameColumnMapRowMapper extends ColumnMapRowMapper {
    private StringBuilder sb = new StringBuilder();

    @Override
    protected String getColumnKey(String columnName) {
        columnName = super.getColumnKey(columnName);
        UtilString.camelCaseName(columnName, sb);
        return sb.toString();
    }
}