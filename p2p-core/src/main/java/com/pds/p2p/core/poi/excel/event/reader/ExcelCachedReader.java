package com.pds.p2p.core.poi.excel.event.reader;

import java.util.List;
import java.util.Map;

import com.pds.p2p.core.poi.excel.event.IExcelReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pds.p2p.core.poi.excel.event.Utils;
import com.pds.p2p.core.poi.excel.model.XlColumn;
import com.pds.p2p.core.poi.excel.model.XlMessage;
import com.pds.p2p.core.poi.excel.model.XlSheet;
import com.pds.p2p.core.poi.excel.model.XlWorkbook;
import com.pds.p2p.core.poi.excel.utils.XlMessageUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/***
 * **************************************************
 * <p>
 * <pre>
 * 描    述：将excel的所有数据都放入到内存中处理；
 *
 * 使用方法：
 * File target = new File(realPath, file);
 * UploadParms uploadParms = new 上传参数
 * XlWorkbook xlWorkbook = ReadUtils.readXlWorkbook(BssExcelReader.class, "bss-uplad.xml");
 * ExcelCachedReader excelReader = new ExcelCachedReader(xlWorkbook,1) {
 * 		//要求实现下面的方法
 * 	   void updateAllData(xxx){
 *        }
 * }
 * ExcelReaderUtil.readExcel(excelReader, target);
 *
 * 导入策略：
 * 	1.全量导入；
 *
 * 问题：
 *   如果存在部分的sheet正确可以导入；但不影响业务;
 *
 * Company:  普德施PDS
 * @author 王文
 * @version 1.0   2015年2月8日
 * @see （选填）
 * 	HISTORY  （必填）
 * 	2015年2月8日  Administrator 创建文件,并完成框架最简化的导入
 *
 * </pre>
 * ************************************************
 */
public abstract class ExcelCachedReader implements IExcelReader {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final XlWorkbook xlWorkbook;

    private final List<XlMessage> allMessages = Lists.newArrayList();
    final private long fileId;
    private int total;
    private XlSheet xlSheet;
    private Map<String, List<Map<String, Object>>> cachedMap;

    public ExcelCachedReader(XlWorkbook xlWorkbook, long fileId) {
        this.fileId = fileId;
        this.xlWorkbook = Validate.notNull(xlWorkbook, "ExcelCachedReader需要xlWorkbook");
    }

    /**
     * @return xlWorkbook
     */
    public XlWorkbook getXlWorkbook() {
        return xlWorkbook;
    }

    /**
     * @return allMessages
     */
    public List<XlMessage> getAllMessages() {
        return allMessages;
    }

    /**
     * @return fileId
     */
    public long getFileId() {
        return fileId;
    }

    /**
     * @return xlSheet
     */
    public XlSheet getXlSheet() {
        return xlSheet;
    }

    /***
     * 在读取整个excel前执行
     *
     * @return 如果为false，不读取excel文件的内容
     */
    @Override
    public boolean startRead() {
        logger.info("prepare read the excel, the number of sheet is " + xlWorkbook.getSheets().size());
        this.cachedMap = Maps.newLinkedHashMap();
        List<XlSheet> xlSheets = this.xlWorkbook.getSheets();
        // 创建表
        for (XlSheet xlSheet : xlSheets) {
            String tab = xlSheet.getTable();
            this.cachedMap.put(tab, Lists.<Map<String, Object>>newArrayList());
        }
        return true;
    }

    /***
     * 在读取某一个sheet前执行
     *
     * @param sheetIndex excel内的sheet索引
     * @param sheetName  excel内的sheet标签名称
     *
     * @return 如果返回false，不读取当前sheet
     */
    @Override
    public boolean beforeReadSheet(int sheetIndex, String sheetName) {
        sheetName = StringUtils.deleteWhitespace(sheetName);
        xlSheet = xlWorkbook.findXlSheet(sheetName);
        logger.info("prepare read sheet {}...", sheetName);
        if (xlSheet == null) {
            logger.info("read sheet {} not exist...ignore!", sheetName);
            return false;
        }
        handleSheet(xlSheet, sheetIndex);
        return true;
    }

    /***
     * 设置xlSheet一些属性，如设置验证器
     *
     * @param xlSheet
     */
    abstract protected void handleSheet(XlSheet xlSheet, int sheetIndex);

    /***
     * 在读取sheet的每一行
     *
     * @param sheetIndex excel内的sheet索引
     * @param sheetName  excel内的sheet标签名称
     * @param curRow     sheet内的当前行，以0开始
     * @param rowVals    当前行的数据，{a=xxx,b=xxx}
     */
    @Override
    public void rowRead(int sheetIndex, String sheetName, int curRow, Map<String, String> rowVals) {
        if (curRow < xlSheet.getXlStart().getRow()) {
            return;
        }
        List<XlColumn> columns = xlSheet.getColumns();
        // 如果行为空不读
        if (Utils.valIsEmpty(rowVals, columns)) {
            return;
        }
        if (ignoreRowRead(sheetIndex, sheetName, curRow, rowVals)) {
            return;
        }
        List<XlMessage> rowMessages = xlSheet.getValidator().validate(rowVals, curRow);
        allMessages.addAll(rowMessages);

        // 验证是否存在数据类型错误，如果错误将数据进行初始化，如果是错误，数字类型初始化0，其它类型初始化为空字符串
        // 如果类型不匹配，数据无法保存到数据库中
        Utils.validateTypeAndInit(rowVals, columns);

        Map<String, Object> values = Maps.newHashMap();
        for (XlColumn xlColumn : columns) {
            String val = rowVals.get(xlColumn.getId());
            if (xlColumn.isHide()) {
                continue;
            } else if (xlColumn.isNumberType()) {
                if (StringUtils.isEmpty(val)) {
                    values.put(xlColumn.getField(), 0);
                } else {
                    values.put(xlColumn.getField(), NumberUtils.createNumber(val));
                }
            } else {
                values.put(xlColumn.getField(), val);
            }
        }

        this.cachedMap.get(this.xlSheet.getTable()).add(values);

        ++total;
    }

    /**
     * 用来阻止对当前行进行处理
     *
     * @param sheetIndex
     * @param sheetName
     * @param curRow
     * @param rowVals
     *
     * @return boolean 如果为true就不在处理对当前行做任何处理
     */
    protected boolean ignoreRowRead(int sheetIndex, String sheetName, int curRow, Map<String, String> rowVals) {
        return false;
    }

    /***
     * 在读取某一个sheet后执行
     *
     * @param sheetIndex excel内的sheet索引
     * @param sheetName  excel内的sheet标签名称
     */
    @Override
    public void afterReadSheet(int sheetIndex, String sheetName) {
        logger.info("read sheet {} ok!", sheetName);
    }

    /***
     * 读取完整个excel将调用的方法
     */
    @Override
    public void endRead() {
        logger.info("read all sheets, start update data...");
        for (Map.Entry<String, List<Map<String, Object>>> ent : this.cachedMap.entrySet()) {
            logger.info("sheet {} read data {} records", ent.getKey(), ent.getValue().size());
        }
        updateAllData(this.cachedMap, this.xlWorkbook, this.fileId, this.allMessages);
        logger.info("update data ok!");
    }

    /***
     * 父类完成读取和验证
     * <p>
     * 子类负责数据的存取
     *
     * @param cachedMap
     * @param xlWorkbook
     * @param fieldId
     * @param messages
     */
    abstract protected void updateAllData(Map<String, List<Map<String, Object>>> cachedMap, XlWorkbook xlWorkbook,
                                          long fieldId, List<XlMessage> messages);

    public int countErrRow() {
        return XlMessageUtils.countErrRow(allMessages);
    }

    public String descn() {
        return String.format("成功处理%d条，失败%d条", this.total, countErrRow());
    }

}
