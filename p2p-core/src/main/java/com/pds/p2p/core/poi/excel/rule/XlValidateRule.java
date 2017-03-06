package com.pds.p2p.core.poi.excel.rule;

import java.util.Map;

import com.pds.p2p.core.poi.excel.model.XlColumn;
import com.pds.p2p.core.poi.excel.model.XlMessage;

/***
 * 验证规则
 * <p>
 * 数据表内保存验证规则
 *
 * @author Administrator
 */
public interface XlValidateRule {
    /***
     * 执行验证规则
     *
     * @param xlColumn column 列配置数据
     * @param rowdata  读入的原始数据
     * @param nrow     所读的当前行
     *
     * @return
     */
    XlMessage perform(Map<String, String> rowdata, int nrow);

    int errType();

    XlColumn getXlColumn();
}