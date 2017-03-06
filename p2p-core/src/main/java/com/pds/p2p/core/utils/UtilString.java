package com.pds.p2p.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class UtilString {

    public static List<String> findEngLinks(String urlText) {
        List<String> result = Lists.newArrayList();
        String regexp = "http://\\w*\\.\\w*/\\w*";
        Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(urlText);
        while (matcher.find()) {
            String url = matcher.group();
            result.add(url);
        }
        return result;
    }

    public static List<String> findLinks(String urlText) {
        List<String> result = Lists.newArrayList();
        // 匹配的条件选项为结束为空格(半角和全角)、换行符、字符串的结尾或者遇到其他格式的文本
        String regexp
                = "(((http|ftp|https|file)://)|((?<!((http|ftp|https|file)://))www\\.))"  // 以http...或www开头
                + ".*?"                                                                   // 中间为任意内容，惰性匹配
                + "(?=(&nbsp;|\\s|　|<br />|$|[<>]))";                                     // 结束条件
        Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(urlText);
        while (matcher.find()) {
            String url = matcher.group().substring(0, 3).equals("www") ? "http://" + matcher.group() : matcher.group();
            result.add(url);
        }
        return result;
    }

    public static String urlToLink(String urlText) {
        // 匹配的条件选项为结束为空格(半角和全角)、换行符、字符串的结尾或者遇到其他格式的文本
        String regexp
                = "(((http|ftp|https|file)://)|((?<!((http|ftp|https|file)://))www\\.))"  // 以http...或www开头
                + ".*?"                                                                   // 中间为任意内容，惰性匹配
                + "(?=(&nbsp;|\\s|　|<br />|$|[<>]))";                                     // 结束条件
        Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(urlText);
        StringBuffer stringbuffer = new StringBuffer();
        while (matcher.find()) {
            String url = matcher.group().substring(0, 3).equals("www") ? "http://" + matcher.group() : matcher.group();
            String tempString = "<a href=\"" + url + "\">" + matcher.group() + "</a>";
            // 这里对tempString中的"\"和"$"进行一次转义，因为下面对它替换的过程中appendReplacement将"\"和"$"作为特殊字符处理
            int tempLength = tempString.length();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < tempLength; ++i) {
                char c = tempString.charAt(i);
                if (c == '\\' || c == '$') {
                    buffer.append("\\").append(c);
                } else {
                    buffer.append(c);
                }
            }
            tempString = buffer.toString();
            matcher.appendReplacement(stringbuffer, tempString);
        }
        matcher.appendTail(stringbuffer);
        return stringbuffer.toString();
    }

    public static String textToLinks(String note) {
        // 转换的思想为把文本中不是链接("/a>"和"<a "之间)的内容逐个进行转换
        // 把字符串中的"\"和"$"加上转义符，避免appendReplacement替换字符串的时候将它们作为特殊字符处理
        int noteLength = note.length();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < noteLength; ++i) {
            char c = note.charAt(i);
            if (c == '\\' || c == '$') {
                buffer.append("\\").append(c);
            } else {
                buffer.append(c);
            }
        }
        String linkNote = "/a>" + buffer.toString() + "<a ";
        String regexp = "(?<=/a>).*?(?=<a )";
        Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(linkNote);
        StringBuffer stringbuffer = new StringBuffer();
        while (matcher.find()) {
            String tempString = urlToLink(matcher.group());
            matcher.appendReplacement(stringbuffer, tempString);
        }
        matcher.appendTail(stringbuffer);
        String result = stringbuffer.toString();
        // 返回的结果去掉加入的"/a>" 和"<a "
        return result.substring(3, result.length() - 3);
    }

    public static void underscoreName(String name, StringBuilder result) {
        result.setLength(0);
        // 第一个字母可能是大写
        result.append(Character.toLowerCase(name.charAt(0)));
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                result.append("_");
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
    }

    public static boolean isCamelCase(String name) {
        if (Character.isUpperCase(name.charAt(0))) {
            return false;
        }
        int upperCaseCnt = 0;
        int lowCaseCnt = 0;
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '_') {
                return false;
            }
            if (Character.isUpperCase(c)) {
                upperCaseCnt++;
            } else {
                lowCaseCnt++;
            }
        }
        return upperCaseCnt > 0 && lowCaseCnt > 0;
    }

    public static void camelCaseName(String name, StringBuilder result) {
        try {
            result.setLength(0);
            for (int i = 0; i < name.length(); ++i) {
                char c = name.charAt(i);
                if (c == '_') {
                    c = name.charAt(++i);
                    result.append(Character.toUpperCase(c));
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        } catch (StringIndexOutOfBoundsException e) {
        }
    }

    public static Map<String, String> toMap(String str, char primarychar, char subchar) {
        Map<String, String> params = Maps.newLinkedHashMap();
        List<String> list1 = UtilString.toList(str, primarychar);
        List<String> buff = new ArrayList<String>(2);
        for (String e : list1) {
            UtilString.toList(e, subchar, buff);
            if (buff.size() > 1) {
                params.put(buff.get(0), buff.get(1));
            }
        }
        return params;
    }

    public static List<String> toList(String str, char sep) {
        if (StringUtils.isEmpty(str)) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<String>();
        toList(str, sep, result);
        return result;
    }

    public static void toList(String str, char sep, List<String> list) {
        StringBuilder sb = new StringBuilder();
        list.clear();
        for (int idx = 0; idx < str.length(); ++idx) {
            char c = str.charAt(idx);
            if (c == sep) {
                list.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        if (sb.length() > 0) {
            list.add(sb.toString().trim());
            sb.setLength(0);
        }
    }

    public static String listToString(List<?> list, char sep) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (Object o : list) {
                if (o instanceof String) {
                    sb.append(sep).append(o);
                } else {
                    sb.append(sep).append(o.toString());
                }
            }
        }
        return sb.toString().length() > 0 ? sb.toString().substring(1) : "";
    }

    public static String arrayToString(Object[] array, char sep) {
        StringBuffer sb = new StringBuffer("");
        if (array != null && array.length > 0) {
            for (Object o : array) {
                if (o instanceof String) {
                    sb.append(sep).append(o);
                } else {
                    sb.append(sep).append(o.toString());
                }
            }
        }
        return sb.toString().length() > 0 ? sb.toString().substring(1) : "";
    }

    /**
     * @param displaysz
     *
     * @return
     */
    public static long displayszToByteCount(String displaysz) {
        Pattern pattern = Pattern.compile("([\\d,]+)([kKmMgG]?).*");
        Matcher matcher = pattern.matcher(displaysz);
        if (!matcher.matches()) {
            return -1;
        } else {
            String digits = matcher.group(1);
            String suffix = matcher.group(2).toLowerCase();
            long number = Long.parseLong(digits);
            if ("k".equals(suffix)) {
                number = number * 1024;
            } else if ("m".equals(suffix)) {
                number = number * 1024 * 1024;
            } else if ("g".equals(suffix)) {
                number = number * 1024 * 1024 * 1024;
            }
            return number;
        }
    }

    /**
     * 将全角转换成半角 男，女
     *
     * @return
     */
    public static String toDBCase(String str) {
        StringBuilder outStr = new StringBuilder(str.length());
        String temp = "";
        byte[] bytes = null;
        boolean isSbc = false;
        for (int i = 0; i < str.length(); i++) {
            try {
                temp = str.substring(i, i + 1);
                bytes = temp.getBytes("unicode");
            } catch (java.io.UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            isSbc = false;
            // utf8
            if (bytes[2] == -1) {
                bytes[3] = (byte) (bytes[3] + 32);
                bytes[2] = 0;
                isSbc = true;
            } else if (bytes[3] == -1) { // gb2312
                bytes[2] = (byte) (bytes[2] + 32);
                bytes[3] = 0;
                isSbc = true;
            }
            if (isSbc) {
                try {
                    outStr.append(new String(bytes, "unicode"));
                } catch (java.io.UnsupportedEncodingException e) {
                }
            } else {
                outStr.append(temp);
            }
        }
        return outStr.toString();
    }

    /**
     * 除去数字字符串的后缀.0,  如2.0变成2
     *
     * @param str
     *
     * @return
     */
    public static final String ereaseBackZeros(String str) {
        if (NumberUtils.isNumber(str)) {
            return str.endsWith(".0") ? str.substring(0, str.length() - 2) : str;
        }
        return str;
    }

    public static boolean isUTF8(byte[] bs) {
        if (Hex.encodeHexString(ArrayUtils.subarray(bs, 0, 3)).equals("efbbbf")) {
            return true;
        }
        int lLen = bs.length;
        for (int i = 0; i < lLen; ) {
            byte b = bs[i++];
            if (b < 0) {
                if (b < -64 || b > -3) {
                    return false;
                }
                int c = b <= -4 ? ((int) (b <= -8 ? ((int) (b <= -16 ? ((int) (b <= -32 ? 1 : 2)) : 3)) : 4)) : 5;
                if (i + c > lLen) {
                    return false;
                }
                for (int j = 0; j < c; ) {
                    if (bs[i] >= -64) {
                        return false;
                    }
                    j++;
                    i++;
                }

            }
        }
        return true;
    }

    static public String filterOffUtf8Mb4(String text) {
        if (StringUtils.isEmpty(text)) {
            return StringUtils.EMPTY;
        }
        try {
            byte[] bytes = text.getBytes("utf-8");
            ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            int i = 0;
            while (i < bytes.length) {
                short b = bytes[i];
                if (b > 0) {
                    buffer.put(bytes[i++]);
                    continue;
                }
                b += 256;
                if ((b ^ 0xC0) >> 4 == 0) {
                    buffer.put(bytes, i, 2);
                    i += 2;
                } else if ((b ^ 0xE0) >> 4 == 0) {
                    buffer.put(bytes, i, 3);
                    i += 3;
                } else if ((b ^ 0xF0) >> 4 == 0) {
                    i += 4;
                } else {
                    i += 2;
                }
            }
            buffer.flip();
            return new String(buffer.array(), "utf-8");
        } catch (UnsupportedEncodingException exception) {
            return StringUtils.EMPTY;
        }
    }

    public static String buildUrlString(String address, Map<String, Object> map) {
        StringBuilder sb = new StringBuilder(address);
        int idx = StringUtils.lastIndexOf(address, '?');
        if (idx == StringUtils.INDEX_NOT_FOUND) {
            sb.append("?");
        } else {
            if (idx != address.length() - 1) {
                sb.append("&");
            }
        }
        for (Map.Entry<String, Object> ent : map.entrySet()) {
            sb.append(ent.getKey()).append("=").append(ent.getValue());
            sb.append("&");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public static Date parseDateForHttp(String dateFmtStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return sdf.parse(dateFmtStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static int[] toIntArray(String list, char c) {
        List<String> ss = toList(list, c);
        int[] result = new int[ss.size()];
        int i = 0;
        for (String s : ss) {
            result[i++] = NumberUtils.toInt(s);
        }
        return result;
    }

    public static JSONArray toJsonArray(String... items) {
        JSONArray result = new JSONArray();
        for (String s : items) {
            if (StringUtils.isNotEmpty(s)) {
                result.add(s);
            }
        }
        return result;
    }

    public static String getHrefText(String hrefString) {
        if (StringUtils.isEmpty(hrefString)) {
            return null;
        }
        Pattern pattern = Pattern.compile(">.*?</a>");
        Matcher matcher = pattern.matcher(hrefString);
        if (matcher.find()) {
            return matcher.group().replaceAll(">|</a>", "");
        }
        return null;
    }

    public static boolean isJsonStyle(String s) {
        return StringUtils.startsWith(s, "{") && StringUtils.endsWith(s, "}");
    }

    public static boolean isJsonJsonArrayStyle(String s) {
        return StringUtils.startsWith(s, "[") && StringUtils.endsWith(s, "]");
    }

    /**
     * @param inputString
     *
     * @return
     *
     * @Description: 文本去除html标签
     */
    public static String html2Text(String inputString) {
        if (inputString == null) {
            return "";
        }
        String htmlStr = inputString;
        String textStr = "";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;

        java.util.regex.Pattern p_html1;
        java.util.regex.Matcher m_html1;

        try {
            String regEx_script =
                    "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; //
            // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
            String regEx_style =
                    "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; //
            // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
            // }
            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
            String regEx_html1 = "<[^>]+";
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签

            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签

            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签

            p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
            m_html1 = p_html1.matcher(htmlStr);
            htmlStr = m_html1.replaceAll(""); // 过滤html标签

            textStr = StringUtils.replace(htmlStr, "&nbsp;", StringUtils.EMPTY);
            textStr = StringUtils.deleteWhitespace(textStr);

        } catch (Exception e) {
            System.err.println("Html2Text: " + e.getMessage());
        }

        return textStr;// 返回文本字符串
    }

    /**
     * 判断是否为手机号
     *
     * @return
     */
    public static boolean isMobileNO(String mobileNo) {
        if (null == mobileNo || "".equals(mobileNo)) {
            return false;
        }
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobileNo);
        return m.matches();
    }

    /**
     * 判断是否为Email
     *
     * @return
     */
    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) {
            return false;
        }
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");// 复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 对参数进行UTF-8编码，并替换特殊字符
     *
     * @param paramDecString 待编码的参数字符串
     *
     * @return 完成编码转换的字符串
     */
    public static String urlEncode(String paramDecString) {
        try {
            return URLEncoder.encode(paramDecString, "UTF-8").replace("+", "%20").replace("*", "%2A")
                    .replace("%7E", "~").replace("#", "%23");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 将 %XX 换为原符号，并进行UTF-8反编码
     *
     * @param paramEncString 待反编码的参数字符串
     *
     * @return 未进行UTF-8编码和字符替换的字符串
     */
    public static String urlDecode(String paramEncString) {
        return urlDecode(paramEncString, "UTF-8");
    }

    public static String urlDecode(String paramEncString, String code) {
        int nCount = 0;
        for (int i = 0; i < paramEncString.length(); i++) {
            if (paramEncString.charAt(i) == '%') {
                i += 2;
            }
            nCount++;
        }
        byte[] sb = new byte[nCount];
        for (int i = 0, index = 0; i < paramEncString.length(); i++) {
            if (paramEncString.charAt(i) != '%') {
                sb[index++] = (byte) paramEncString.charAt(i);
            } else {
                StringBuilder sChar = new StringBuilder();
                sChar.append(paramEncString.charAt(i + 1));
                sChar.append(paramEncString.charAt(i + 2));
                sb[index++] = Integer.valueOf(sChar.toString(), 16).byteValue();
                i += 2;
            }
        }
        String decode = "";
        try {
            decode = new String(sb, code);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decode;
    }

    /**
     * 根据List&#60NameValuePair&#62格式存储的参数队列，生成queryString
     *
     * @return 不包括？的queryString
     */
    public static String getQueryString(Map<String, String> queryParamsList) {
        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> ent : queryParamsList.entrySet()) {
            queryString.append('&');
            queryString.append(ent.getKey());
            queryString.append('=');
            queryString.append(urlEncode(ent.getValue()));
        }
        // 去掉第一个&号
        return queryString.toString().substring(1);
    }

}
