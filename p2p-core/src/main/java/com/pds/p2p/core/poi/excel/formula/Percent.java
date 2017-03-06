/**
 *
 */
package com.pds.p2p.core.poi.excel.formula;

import java.text.DecimalFormat;

import org.apache.commons.lang3.math.NumberUtils;

/****************************************************
 * <pre>
 * 描    述： （必填）
 *
 * 实施资源: （选填）
 * 调用者  : （选填）
 * 被调用者: （选填）
 * Company:  普德施PDS
 * @author 王文
 * @version 1.0   2015年2月26日
 * @see （选填）
 * 	HISTORY  （必填）
 * 	2015年2月26日  Administrator 创建文件
 *
 * </pre>
 **************************************************/

public class Percent {
    public static String getPercent(double x, double y) {
        String baifenbi = "";// 接受百分比的值
        double baiy = x * 1.0;
        double baiz = y * 1.0;
        double fen = baiy / baiz;
        // NumberFormat nf = NumberFormat.getPercentInstance(); 注释掉的也是一种方法
        // nf.setMinimumFractionDigits( 2 ); 保留到小数点后几位
        DecimalFormat df1 = new DecimalFormat("##.00%"); // ##.00%
        // 百分比格式，后面不足2位的用0补齐
        // baifenbi=nf.format(fen);
        baifenbi = df1.format(fen);

        if (".00%".equals(baifenbi)) {
            baifenbi = "0%";
        }
        if ("100.00%".equals(baifenbi)) {
            baifenbi = "100%";
        }
        if (baifenbi.endsWith(".00%")) {
            baifenbi = baifenbi.substring(0, baifenbi.length() - 4) + "%";
        } // if去0

        return baifenbi;
    }

    public static String getPercent(String val) {
        Double d1 = NumberUtils.createDouble(val);
        DecimalFormat df1 = new DecimalFormat("##.00%"); // ##.00%
        // 百分比格式，后面不足2位的用0补齐
        // baifenbi=nf.format(fen);
        String baifenbi = df1.format(d1);

        if (".00%".equals(baifenbi)) {
            baifenbi = "0%";
        }
        if ("100.00%".equals(baifenbi)) {
            baifenbi = "100%";
        }
        if (baifenbi.endsWith(".00%")) {
            baifenbi = baifenbi.substring(0, baifenbi.length() - 4) + "%";
        } // if去0

        return baifenbi;
    }
}
