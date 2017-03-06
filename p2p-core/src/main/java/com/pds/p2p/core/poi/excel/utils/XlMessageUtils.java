/**
 *
 */
package com.pds.p2p.core.poi.excel.utils;

import java.util.List;
import java.util.Set;

import com.pds.p2p.core.poi.excel.model.XlMessage;
import com.pds.p2p.core.poi.excel.model.XlWorkbook;
import com.google.common.collect.Sets;

/****************************************************
 * <pre>
 * 描    述： （必填）
 *
 * 实施资源:  （选填）
 * 调用者  :  （选填）
 * 被调用者:  （选填）
 * Company: 北京益派市场调查有限公司
 * @author 王文
 * @version 1.0   2015-4-21
 * @see （选填）
 * 	HISTORY  （必填）
 * 	2015-4-21  Administrator 创建文件
 *
 *
 * </pre>
 **************************************************/

public class XlMessageUtils {

    /**
     *
     */
    public XlMessageUtils() {
    }

    public static int countErrRow(List<XlMessage> allMessages) {
        Set<Integer> rowSet = Sets.newHashSet();
        for (XlMessage message : allMessages) {
            rowSet.add(message.getRow());
        }
        int errorNum = rowSet.size();
        return errorNum;
    }

    public static XlMessage create(XlWorkbook xlWorkbook, String sheetName, int row, int colIdx, int type, String msg) {
        XlMessage message = new XlMessage();
        message.setRow(row);
        message.setCol(colIdx);
        message.setSheet(xlWorkbook.idxSheet(sheetName));
        message.setMsg(msg);
        message.setType(type);
        return message;
    }

}
