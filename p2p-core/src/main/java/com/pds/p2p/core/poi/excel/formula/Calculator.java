/**
 *
 */
package com.pds.p2p.core.poi.excel.formula;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import com.alibaba.fastjson.JSON;
import com.pds.p2p.core.poi.excel.model.CellIdx;
import com.pds.p2p.core.poi.excel.utils.ReadUtils;
import com.google.common.collect.Lists;

/****************************************************
 * <pre>
 * 描    述： 提供一些计算方法
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

public class Calculator {
    static ScriptEngineManager sem = new ScriptEngineManager();
    public static ScriptEngine se = sem.getEngineByName("javascript");

    static {
        try {
            se.eval("function IF(a,b,c){if(a){return b;}else{return c;}}");
            se.eval("function ROUND(a,b){var a=Math.round(a*Math.pow(10,b))/Math.pow(10,b);return a;}");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // String s = "SUM(C8:C10,C13,C14:C16,C19,C22,C23:C24)";
        // System.out.println(expendSumExpr(s));
        String str = "C7+C13-C25-C25*C65";
        System.out.println(JSON.toJSONString(StringUtils.splitPreserveAllTokens(str, "+-*")));

        System.out.println(290.44D + 64.42D + 15282.97D + 3977.63D);
        try {
            System.out.println(se.eval("ROUND(1.6365,2)"));
            System.out.println(Calculator.expendSumExpr("SUM(B2:F2,M2:P2)"));
            System.out.println(se.eval("(function(val){return val > 20 && val < 30})(25)"));
        } catch (ScriptException e) {
            e.printStackTrace();
        }

    }

    /***
     * 执行计算
     *
     * @param text :数学表达式
     */
    public static Double eval(String text) throws ScriptException {
        return (Double) se.eval(text);
    }

    /***
     * 执行计算
     *
     * @param text  数学表达式:形如${a}+${b}
     * @param parms 替换数学表达式中的变量
     */
    public static <T> Double eval(String text, Map<String, T> parms) throws ScriptException {
        String rl = StrSubstitutor.replace(text, parms);
        return (Double) se.eval(rl);
    }

    /***
     * 执行计算z
     *
     * @param text  数学表达式:形如${a}+${b}
     * @param parms 替换数学表达式中的变量
     */
    public static <T> Double evalToExpr(String text, Map<String, T> parms) throws ScriptException {
        String rl = StrSubstitutor.replace(text, parms);
        return (Double) se.eval(rl);
    }

    private static Pattern pattern = Pattern.compile("[A-Z]+[0-9]{1,}");

    /***
     * 将字符串中的excel列名给筛选出来
     *
     * @param s 数学表达式:带excel列名的表达式
     *
     * @return 筛选出来的列表
     */
    public static List<String> findCellNms(String s) {
        List<String> result = Lists.newArrayList();
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            String reposter = matcher.group();
            result.add(reposter);
        }
        return result;
    }

    /***
     * 将字符串中的变量转换为${变量}
     *
     * @param text  如：AE+CD
     * @param finds 变量列表
     *
     * @return 字符串：${AE}+${CD}
     */
    public static String toTropExp(String text, List<String> finds) {
        for (String f : finds) {
            text = StringUtils.replace(text, f, "${" + f + "}");
        }
        return text;
    }

    /***
     * 将SUM(C8:C10,C13,C14:C16,C19,C22,C23:C24)
     * 转换为C8+C9+C10+C13+C14+C15+C16+C19+C22+C23+C24
     */
    // =SUM(C8:C10,C13,C14:C16,C19,C22,C23:C24)
    public static String expendSumExpr(String sumExpr) {
        String expr = StringUtils.substringBetween(sumExpr.toUpperCase(), "SUM(", ")");
        String[] exs = StringUtils.split(expr, ',');
        List<String> rns = Lists.newArrayList();
        for (String ex : exs) {
            rns.addAll(rangeNms(ex));
        }
        return StringUtils.join(rns.iterator(), "+");
    }

    public static boolean isSumExpr(String sumExpr) {
        return StringUtils.startsWithIgnoreCase(sumExpr, "SUM(");
    }

    /***
     * C8:C10 转换为C8,C10列表
     * C8:E8->
     */
    public static List<String> rangeNms(String rangeexp) {
        List<String> result = Lists.newArrayList();
        String[] exs = StringUtils.split(rangeexp, ':');
        if (exs.length == 2) {
            CellIdx cellIdx1 = new CellIdx(exs[0]);
            CellIdx cellIdx2 = new CellIdx(exs[1]);
            if (cellIdx1.getColumnKey().equals(cellIdx2.getColumnKey())) {// 同一列相加
                for (int i = cellIdx1.getRowKey(); i <= cellIdx2.getRowKey(); ++i) {
                    result.add(cellIdx1.getColumnKey() + i);
                }
            } else {
                for (int i = ReadUtils.columnToIndex(cellIdx1.getColumnKey());
                     i <= ReadUtils.columnToIndex(cellIdx2.getColumnKey()); ++i) {
                    result.add(ReadUtils.indexToColumn(i) + cellIdx1.getRowKey());
                }
            }
        } else {
            result.add(exs[0]);
        }
        return result;
    }

}
