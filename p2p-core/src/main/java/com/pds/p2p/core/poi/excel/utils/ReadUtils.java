package com.pds.p2p.core.poi.excel.utils;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class ReadUtils {
    public static String getColumnName(int columnNum) {
        int first;
        int last;
        String result = "";
        if (columnNum > 256) {
            columnNum = 256;
        }
        first = columnNum / 27;
        last = columnNum - (first * 26);
        if (first > 0) {
            result = String.valueOf((char) (first + 64));
        }
        if (last > 0) {
            result = result + String.valueOf((char) (last + 64));
        }
        return result;
    }

    /***
     * @param columnNum 基于0；
     *
     * @return
     */
    public static String indexToColumn(int columnNum) {
        columnNum += 1;
        int first = 0;
        int last;
        String result = "";
        if (columnNum > 256) {
            columnNum = 256;
        }
        first = columnNum / 27;
        last = columnNum - (first * 26);
        if (last > 26) {
            last = last - 26;
            first = first + 1;
        }
        if (first > 0) {
            result = String.valueOf((char) (first + 64));
        }
        if (last > 0) {
            result = result + String.valueOf((char) (last + 64));
        }
        return result;
    }

    /***
     * 返回基于0的索引值
     *
     * @param column
     *
     * @return
     */
    public static int columnToIndex(String column) {
        if (!column.matches("[A-Z]+")) {
            try {
                throw new Exception("Invalid parameter");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int index = 0;
        char[] chars = column.toUpperCase().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            index += ((int) chars[i] - (int) 'A' + 1) * (int) Math.pow(26, chars.length - i - 1);
        }
        return index - 1;
    }

    public static boolean valIsEmpty(Map<String, String> vals, String... cols) {
        for (String str : cols) {
            if (StringUtils.isNotEmpty(vals.get(str))) {
                return false;
            }
        }
        return true;
    }

    public static String removeLastNumber(String tag) {
        int sz = tag.length() - 1;
        int pos = 0;
        for (; sz > 0; sz--) {
            char c = tag.charAt(sz);
            if (Character.isLetter(c)) {
                pos = sz;
                break;
            }
        }
        return tag.substring(0, pos + 1);
    }

}
