package com.pds.p2p.core.poi.excel.event;

import java.util.List;
import java.util.Map;

import com.pds.p2p.core.poi.excel.model.XlColumn;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

import com.pds.p2p.core.utils.UtilType;

public class Utils {
    public static boolean valIsEmpty(Map<String, String> vals, List<XlColumn> columns) {
        for (XlColumn column : columns) {
            if (StringUtils.isNotEmpty(vals.get(column.id()))) {
                return false;
            }
        }
        return true;
    }

    public static String dateVal(double val) {
        double v = Double.valueOf(val);
        return DateFormatUtils.format(HSSFDateUtil.getJavaDate(v), "yyyy-MM-dd");
    }

    public static void validateTypeAndInit(Map<String, String> vals, List<XlColumn> columns) {
        for (XlColumn column : columns) {
            String val = vals.get(column.getId());
            if (UtilType.isEmpty(val)) {
                if (column.isRequired()) {
                    vals.put(column.getId(), StringUtils.EMPTY);
                }
            } else if (column.isDateType()) {
                try {
                    DateUtils.parseDate(val, "yyyy-M-d", "yyyy/M/d");
                } catch (java.text.ParseException e) {
                    Double dblVal = NumberUtils.toDouble(val.toString());
                    if (dblVal.compareTo(0D) > 0) {
                        String dtFmt = dateVal(dblVal);
                        vals.put(column.getId(), dtFmt);
                    } else {
                        vals.put(column.getId(), StringUtils.EMPTY);
                    }
                }
            } else if (column.isNumberType()) {
                // 2003读取货币单元格时有前缀
                if (val.startsWith("$")) {
                    val = val.substring(1);
                    // 如果被括号包围表示负数
                    if (val.startsWith("(")) {
                        val = StringUtils.substringBetween(val, "(", ")");
                        val = "-" + val;
                    }
                }
                if (!NumberUtils.isNumber(val)) {
                    boolean isnum = false;
                    if (StringUtils.endsWithIgnoreCase(val, "#DIV/0!")) {
                        isnum = true;
                        vals.put(column.getId(), "0");
                    } else if (StringUtils.endsWith(val, "%")) {
                        val = StringUtils.substring(val, 0, val.length() - 1);
                        if (NumberUtils.isNumber(val)) {
                            isnum = true;
                            double dbval = NumberUtils.toDouble(val) / 100D;
                            vals.put(column.getId(), String.valueOf(dbval));
                        }
                    }
                    if (isnum == false) {
                        vals.put(column.getId(), "0");
                    }
                } else {
                    if (column.getType().equalsIgnoreCase("int")) {
                        if (val.indexOf(".") != -1) {
                            val = val.substring(0, val.indexOf("."));
                        }
                        if (!NumberUtils.isDigits(val)) {
                            vals.put(column.getId(), "0");
                        } else {
                            vals.put(column.getId(), val);
                        }
                    } else {
                        vals.put(column.getId(), val);
                    }
                }
            } else {
                if ("——".equals(val) || "--".equals(val) || "不填".equals(val)) {
                    vals.put(column.getId(), StringUtils.EMPTY);
                }
                if (StringUtils.endsWithIgnoreCase(val, "#DIV/0!")) {
                    vals.put(column.getId(), "0");
                }
            }
        }
    }
}
