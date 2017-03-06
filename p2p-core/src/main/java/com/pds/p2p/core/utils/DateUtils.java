/**
 */
package com.pds.p2p.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.google.common.base.Throwables;
import com.google.common.collect.Range;
import com.google.common.util.concurrent.Uninterruptibles;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 *
 * @author ThinkGem
 * @version 2014-4-15
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd）
     */
    public static String getDate() {
        return getDate("yyyy-MM-dd");
    }

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String getDate(String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
        }
        return formatDate;
    }

    /**
     * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前时间字符串 格式（HH:mm:ss）
     */
    public static String getTime() {
        return formatDate(new Date(), "HH:mm:ss");
    }

    /**
     * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String getDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前年份字符串 格式（yyyy）
     */
    public static String getYear() {
        return formatDate(new Date(), "yyyy");
    }

    /**
     * 得到当前月份字符串 格式（MM）
     */
    public static String getMonth() {
        return formatDate(new Date(), "MM");
    }

    /**
     * 得到当天字符串 格式（dd）
     */
    public static String getDay() {
        return formatDate(new Date(), "dd");
    }

    /**
     * 得到当前星期字符串 格式（E）星期几
     */
    public static String getWeek() {
        return formatDate(new Date(), "E");
    }

    /**
     * 日期型字符串转化为日期 格式
     * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
     * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
     * "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取过去的天数
     *
     * @param date
     *
     * @return
     */
    public static long pastDays(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (24 * 60 * 60 * 1000);
    }

    /**
     * 获取过去的小时
     *
     * @param date
     *
     * @return
     */
    public static long pastHour(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (60 * 60 * 1000);
    }

    /**
     * 获取过去的分钟
     *
     * @param date
     *
     * @return
     */
    public static long pastMinutes(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (60 * 1000);
    }

    /**
     * 转换为时间（天,时:分:秒.毫秒）
     *
     * @param timeMillis
     *
     * @return
     */
    public static String formatDateTime(long timeMillis) {
        long day = timeMillis / (24 * 60 * 60 * 1000);
        long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
        long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
        return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
    }

    /**
     * 获取两个日期之间的天数
     *
     * @param before
     * @param after
     *
     * @return
     */
    public static double getDistanceOfTwoDate(Date before, Date after) {
        long beforeTime = before.getTime();
        long afterTime = after.getTime();
        return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
    }

    public static long tryNextDelay(long retries, long scale) {
        long delay = (long) Math.pow(2.0D, retries) * scale;
        return delay;
    }

    public static void delay(long retries, long scale) {
        long delay = tryNextDelay(retries, scale);
        System.out.println("retries:" + retries + "," + " wait..." + delay);
        Uninterruptibles.sleepUninterruptibly(delay, TimeUnit.MILLISECONDS);
    }

    public static void delay(long retries, long scale, TimeUnit timeUnit) {
        long delay = tryNextDelay(retries, scale);
        System.out.println("retries:" + retries + "," + " wait..." + delay);
        Uninterruptibles.sleepUninterruptibly(delay, timeUnit);
    }

    public static int dateToInt(Date date) {
        return Long.valueOf(date.getTime() / 1000).intValue();
    }

    public static int nowToInt() {
        return dateToInt(new Date());
    }

    public static Date intToDate(int ival) {
        return new Date(Long.valueOf(ival).longValue() * 1000L);
    }

    public static String intToDateFmt(int ival, String fmt) {
        return DateFormatUtils.format(intToDate(ival), fmt);
    }

    public static int dateFmtToInt(String fmt) throws ParseException {
        Date date = DateUtils.parseDate(fmt);
        return dateToInt(date);
    }

    public static boolean laterThan(String httpDateFmt, int dateInt) {
        int createdAt = dateToInt(UtilString.parseDateForHttp(httpDateFmt));
        return createdAt > dateInt;
    }

    public static boolean containsOpen(Date value, Date start, Date end) {
        Range<Date> range = Range.open(start, end);
        return range.contains(value);
    }

    public static boolean containsOpen(Date start, Date end) {
        Range<Date> range = Range.open(start, end);
        return range.contains(new Date());
    }

    public static boolean containsClosed(Date value, Date start, Date end) {
        Range<Date> range = Range.closed(start, end);
        return range.contains(value);
    }

    public static int minusDays(int createdAt, int days) {
        Date date = DateUtils.addDays(intToDate(createdAt), -1 * days);
        return dateToInt(date);
    }

    public static Date yesterday() {
        return DateUtils.addDays(new Date(), -1);
    }

    public static Date getNextHourStart(Date date, int aNumHours) {
        date = DateUtils.addHours(date, aNumHours);
        date = DateUtils.truncate(date, Calendar.HOUR_OF_DAY);
        return date;
    }

    public static Date getNextMinuteStart(Date date, int aNumMinutes) {
        date = DateUtils.addMinutes(date, aNumMinutes);
        date = DateUtils.truncate(date, Calendar.MINUTE);
        return date;
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     *
     * @return 相差天数
     *
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            smdate = sdf.parse(sdf.format(smdate));
            bdate = sdf.parse(sdf.format(bdate));
            Calendar cal = Calendar.getInstance();
            cal.setTime(smdate);
            long time1 = cal.getTimeInMillis();
            cal.setTime(bdate);
            long time2 = cal.getTimeInMillis();
            long between_days = (time2 - time1) / (1000 * 3600 * 24);
            return Integer.parseInt(String.valueOf(between_days));
        } catch (ParseException e) {
            throw Throwables.propagate(e);
        }
    }

    public static String getTimeState(int timestamp) {
        return getTimeState(String.valueOf(timestamp));
    }

    /**
     * 根据 timestamp 生成各类时间状态串
     *
     * @param timestamp 距1970 00:00:00 GMT的秒数
     *
     * @return 时间状态串(如：刚刚5分钟前)
     */
    public static String getTimeState(String timestamp) {
        if (timestamp == null || "".equals(timestamp)) {
            return "";
        }
        try {
            long _timestamp = Long.parseLong(timestamp) * 1000;
            if (System.currentTimeMillis() - _timestamp < 1 * 60 * 1000) {
                return "刚刚";
            } else if (System.currentTimeMillis() - _timestamp < 30 * 60 * 1000) {
                return ((System.currentTimeMillis() - _timestamp) / 1000 / 60) + "分钟前";
            } else {
                Calendar now = Calendar.getInstance();
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(_timestamp);
                if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR) && c.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                        && c.get(Calendar.DATE) == now.get(Calendar.DATE)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("今天 HH:mm");
                    return sdf.format(c.getTime());
                }
                if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR) && c.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                        && c.get(Calendar.DATE) == now.get(Calendar.DATE) - 1) {
                    SimpleDateFormat sdf = new SimpleDateFormat("昨天 HH:mm");
                    return sdf.format(c.getTime());
                } else if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("M月d日 HH:mm:ss");
                    return sdf.format(c.getTime());
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日 HH:mm:ss");
                    return sdf.format(c.getTime());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Date parseTimeState(String timestate) {
        Date today = new Date();
        if (StringUtils.contains(timestate, "刚刚")) {
            return today;
        }
        int idx = StringUtils.indexOf(timestate, "分钟前");
        if (idx != StringUtils.INDEX_NOT_FOUND) {
            String secendAmount = StringUtils.substring(timestate, 0, idx).trim();
            return DateUtils.addMinutes(new Date(), -NumberUtils.toInt(secendAmount));
        } else if (StringUtils.contains(timestate, "今天")) {
            String hours = StringUtils.substring(timestate, "今天".length()).trim();
            String ss[] = StringUtils.split(hours, ':');
            Date result = DateUtils.setHours(today, NumberUtils.toInt(ss[0].trim()));
            result = DateUtils.setMinutes(result, NumberUtils.toInt(ss[1].trim()));
            return result;
        } else if (StringUtils.contains(timestate, "昨天")) {
            String hours = StringUtils.substring(timestate, "昨天".length()).trim();
            String ss[] = StringUtils.split(hours, ':');
            Date result = DateUtils.addDays(today, -1);
            result = DateUtils.setHours(result, NumberUtils.toInt(ss[0].trim()));
            result = DateUtils.setMinutes(result, NumberUtils.toInt(ss[1].trim()));
            return result;
        } else {
            Date result = null;
            timestate = StringUtils.replaceOnce(timestate, "年", "$");
            timestate = StringUtils.replaceOnce(timestate, "月", "$");
            timestate = StringUtils.replaceOnce(timestate, "日", "$");
            timestate = StringUtils.replaceOnce(timestate, ":", "$");
            timestate = StringUtils.replaceOnce(timestate, " ", "$");
            timestate = StringUtils.replace(timestate, "-", "$");
            String date[] = StringUtils.split(timestate, '$');
            System.out.println(ArrayUtils.toString(date));
            if (date.length == 5) {
                result = DateUtils.setYears(today, NumberUtils.toInt(date[0].trim()));
                result = DateUtils.setMonths(result, NumberUtils.toInt(date[1].trim()) - 1);
                result = DateUtils.setDays(result, NumberUtils.toInt(date[2].trim()));
                result = DateUtils.setHours(result, NumberUtils.toInt(date[3].trim()));
                result = DateUtils.setMinutes(result, NumberUtils.toInt(date[4].trim()));
            } else {
                result = DateUtils.setMonths(today, NumberUtils.toInt(date[0].trim()) - 1);
                result = DateUtils.setDays(result, NumberUtils.toInt(date[1].trim()));
                result = DateUtils.setHours(result, NumberUtils.toInt(date[2].trim()));
                result = DateUtils.setMinutes(result, NumberUtils.toInt(date[3].trim()));
            }
            return result;
        }
    }

    /**
     * 获取当前月份的第一天
     */
    public static Calendar getCurMonthFirstDay(int year, int month) throws ParseException {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.YEAR, year);
        startCalendar.set(Calendar.MONTH, month);
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        return startCalendar;
    }

    /**
     * 获取当前月份的最后一天
     */
    public static Calendar getCurMonthLastDay(int year, int month) throws ParseException {
        Calendar thisMonth = DateUtils.getCurMonthFirstDay(year, month);
        thisMonth.set(Calendar.YEAR, year);
        thisMonth.set(Calendar.MONTH, month);
        thisMonth.setFirstDayOfWeek(Calendar.SUNDAY);
        thisMonth.set(Calendar.DAY_OF_MONTH, 1);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.YEAR, year);
        endCalendar.set(Calendar.MONTH, month);
        endCalendar.set(Calendar.DAY_OF_MONTH, thisMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        return endCalendar;
    }

    /**
     * 将 Calendar 转为字符串
     *
     * @param curCalendar
     * @param string
     *
     * @return
     */
    public static String formatDate(Calendar curCalendar, String string) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(curCalendar.getTime());
        return dateStr;
    }

    /**
     * @param args
     *
     * @throws ParseException
     */
    public static void main(String[] args) throws ParseException {
        //		System.out.println(formatDate(parseDate("2010/3/6")));
        //		System.out.println(getDate("yyyy年MM月dd日 E"));
        //		long time = new Date().getTime()-parseDate("2012-11-19").getTime();
        //		System.out.println(time/(24*60*60*1000));
    }

}
