/**
 *
 */
package com.pds.p2p.core.poi.excel.model;

/****************************************************
 * <pre>
 * 描    述： （必填）
 *
 * 实施资源:  （选填）
 * 调用者  :  （选填）
 * 被调用者:  （选填）
 * Company: 北京益派市场调查有限公司
 * @author 王文
 * @version 1.0   2015-4-20
 * @see （选填）
 * 	HISTORY  （必填）
 * 	2015-4-20  Administrator 创建文件
 *
 *
 * </pre>
 **************************************************/

public class XlMessageType {
    public static int ERR_REQUIRED = 1; // 违反必填
    public static int ERR_DATE_FMT = 2; // 违反数据格式
    public static int ERR_NUM_TYPE = 3;// 违反必须数字数据类型

    public static int ERR_FMT = 4;// 按格式填写
    public static int ERR_LENGTH = 5;// 按长度填写
    public static int ERR_MAXLEN = 6;// 最大长度
    public static int ERR_MINLEN = 7;// 最小长度
    public static int ERR_CHINESE = 9;// 中文

    public static int ERR_OPTION = 10;// 符合选择列表
    public static int ERR_PRECENT = 11;// 百分比格式

    public static int ERR_URL = 12;// URL格式
    public static int ERR_UNIQUE = 13;// 唯一要求
    public static int ERR_PRECISION = 14;// 唯一要求

    public static int ERR_MOBILE = 50;// 不合法手机

}
