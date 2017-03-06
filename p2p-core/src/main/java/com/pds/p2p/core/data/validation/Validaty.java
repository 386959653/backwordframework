package com.pds.p2p.core.data.validation;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.ibatis.reflection.MetaObject;

import com.alibaba.fastjson.JSONArray;

/***
 * <pre>
 * (1)required:true                必输字段
 * (2)remote:"check.php"      使用ajax方法调用check.php验证输入值
 * (3)email:true                    必须输入正确格式的电子邮件
 * (4)url:true                        必须输入正确格式的网址
 * (5)date:true                      必须输入正确格式的日期 日期校验ie6出错，慎用
 * (6)dateISO:true                必须输入正确格式的日期(ISO)，例如：2009-06-23，1998/01/22 只验证格式，不验证有效性
 * (7)number:true                 必须输入合法的数字(负数，小数)
 * (8)digits:true                    必须输入整数
 * (9)creditcard:                   必须输入合法的信用卡号
 * (10)equalTo:"#field"          输入值必须和#field相同
 * (11)accept:                       输入拥有合法后缀名的字符串（上传文件的后缀）
 * (12)maxlength:5               输入长度最多是5的字符串(汉字算一个字符)
 * (13)minlength:10              输入长度最小是10的字符串(汉字算一个字符)
 * (14)rangelength:[5,10]      输入长度必须介于 5 和 10 之间的字符串")(汉字算一个字符)
 * (15)range:[5,10]               输入值必须介于 5 和 10 之间
 * (16)max:5                        输入值不能大于5
 * (17)min:10                       输入值不能小于10
 * </pre>
 *
 * @author Administrator
 */
public class Validaty {
    public static void main(String[] args) {
        System.out.println(Validaty.url(true, "xxx", null));
    }

    private static int check(Object val) {
        if (val != null) {
            return TypeUtils.isArrayType(val.getClass()) ? Array.getLength(val) : val.toString().trim().length();
        }
        return 0;
    }

    public static boolean required(Object parm, Object val, MetaObject soruce) {
        return check(val) > 0;
    }

    public static boolean email(Object parm, Object val, MetaObject soruce) {
        return check(val) == 0 || match(val, "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
    }

    public static boolean url(Object parm, Object val, MetaObject soruce) {
        return check(val) == 0 || isURL((String) val);
    }

    public static boolean date(Object parm, Object val, MetaObject soruce) {
        if (check(val) == 0) {
            return true;
        }
        try {
            DateUtils.parseDate(val.toString(), "yyyy-MM-dd", "yyyy/MM/dd", "yyyyDDmm");
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean dateISO(Object parm, Object val, MetaObject soruce) {
        if (check(val) == 0) {
            return true;
        }
        try {
            DateUtils.parseDate(val.toString(), "yyyy-MM-dd", "yyyy/MM/dd");
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean number(Object parm, Object val, MetaObject soruce) {
        return check(val) == 0 || match(val, "^[+|-]?\\d+$");
    }

    public static boolean digits(Object parm, Object val, MetaObject soruce) {
        return check(val) == 0 || match(val, "^[0-9]*$");
    }

    public static boolean creditcard(Object parm, Object val, MetaObject soruce) {
        return check(val) == 0 || match(val, "^(\\d{6})()?(\\d{4})(\\d{2})(\\d{2})(\\d{3})(\\w)$");
    }

    public static boolean equalTo(Object parm, Object val, MetaObject soruce) {
        if (check(val) == 0) {
            return true;
        }
        String field = (String) parm;
        field = StringUtils.startsWith(field, "#") ? field.substring(1) : field;
        Object sval = soruce.getValue(field);
        return sval.equals(val);
    }

    public static boolean accept(Object parm, Object val, MetaObject soruce) {
        return check(val) == 0 || StringUtils.endsWith((String) val, (String) parm);
    }

    public static boolean maxlength(Object parm, Object val, MetaObject soruce) {
        int len = check(val);
        return len == 0 || Integer.valueOf(parm.toString()) >= len;
    }

    public static boolean minlength(Object parm, Object val, MetaObject soruce) {
        int len = check(val);
        return len == 0 || Integer.valueOf(parm.toString()) <= len;
    }

    public static boolean rangelength(Object parm, Object val, MetaObject soruce) {
        if (check(val) == 0) {
            return true;
        }
        JSONArray rng = (JSONArray) parm;
        Range<Integer> range = Range.between(rng.getIntValue(0), rng.getIntValue(1));
        return range.contains(val.toString().length());
    }

    public static boolean max(Object parm, Object val, MetaObject soruce) {
        return check(val) == 0 || Integer.valueOf(parm.toString()) >= Integer.valueOf(val.toString());
    }

    public static boolean min(Object parm, Object val, MetaObject soruce) {
        int len = check(val);
        return len == 0 || Integer.valueOf(parm.toString()) <= Integer.valueOf(val.toString());
    }

    //////////////////
    public final static boolean chinese(Object parm, Object val, MetaObject soruce) {
        return check(val) == 0 || match(val, "^[\u0391-\uFFE5]+$");
    }

    public final static boolean stringCheck(Object parm, Object val, MetaObject soruce) {
        return check(val) == 0 || match(val, "^[a-zA-Z0-9\u4e00-\u9fa5-_]+$");
    }

    /**
     * 正则表达式匹配
     *
     * @param text 待匹配的文本
     * @param reg  正则表达式
     *
     * @return
     *
     * @author jiqinlin
     */
    private final static boolean match(Object text, String reg) {
        return Pattern.compile(reg).matcher(text.toString()).matches();
    }

    public static boolean isURL(String text) {
        text = text.toLowerCase();
        String regex = "^((https|http|ftp|rtsp|mms)?://)" //
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" // ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,4})?" // 端口- :80
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        return match(text, regex);
    }

    public static boolean isDate(String dateString) {
        Pattern p = Pattern.compile("\\d{4}+[-]\\d{1,2}+[-]\\d{1,2}+");
        Matcher m = p.matcher(dateString);
        if (!m.matches()) {
            return false;
        }
        // 得到年月日
        String[] array = dateString.split("-");
        int year = Integer.valueOf(array[0]);
        int month = Integer.valueOf(array[1]);
        int day = Integer.valueOf(array[2]);

        if (month < 1 || month > 12) {
            return false;
        }
        int[] monthLengths = new int[] {0, 31, -1, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (isLeapYear(year)) {
            monthLengths[2] = 29;
        } else {
            monthLengths[2] = 28;
        }
        int monthLength = monthLengths[month];
        if (day < 1 || day > monthLength) {
            return false;
        }
        return true;
    }

    /**
     * 是否是闰年
     */
    private static boolean isLeapYear(int year) {
        return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0);
    }
}
