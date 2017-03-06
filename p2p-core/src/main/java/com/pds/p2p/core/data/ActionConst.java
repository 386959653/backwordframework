package com.pds.p2p.core.data;

import org.apache.commons.lang3.StringUtils;

public class ActionConst {
    public static String COPY_NEW = "COPY_NEW"; // 拷贝新建
    public static String NEW = "NEW"; // 新建
    public static String OPEN = "OPEN"; // 打开
    public static String SAVE = "SAVE"; // 保存
    public static String EXCEL_EXPORT = "EXCEL_EXPORT"; // 导出EXCEL
    public static String REVOKE = "REVOKE"; // 撤回
    public static String HISTORY = "HISTORY";// 处理历史
    public static String CANCEL = "CANCEL";// 取消
    public static String SUBMIT = "SUBMIT";// 提交
    public static String QUIT = "QUIT";// 退出,如果有修改，要提示是否退出
    public static String SPLIT = "SPLIT";// 拆分
    public static String DELETE = "DELETE";// 删除
    public static String UPDATE = "UPDATE";// 更新
    public static String RETURN = "RETURN";// 退回
    public static String CONFIRM = "CONFIRM";// 确认
    public static String LIST = "list";// 打开list
    public static String SEND = "SEND";// 下达
    public static String RESET = "RESET";

    public static boolean isList(String action) {
        return StringUtils.equalsIgnoreCase(LIST, action);
    }

    public static boolean isConfirm(String action) {
        return StringUtils.equalsIgnoreCase(CONFIRM, action);
    }

    public static boolean isReturn(String action) {
        return StringUtils.equalsIgnoreCase(RETURN, action);
    }

    public static boolean isDelete(String action) {
        return StringUtils.equalsIgnoreCase(DELETE, action);
    }

    public static boolean isUpdate(String action) {
        return StringUtils.equalsIgnoreCase(UPDATE, action);
    }

    public static boolean isCopyNew(String action) {
        return StringUtils.equalsIgnoreCase(COPY_NEW, action);
    }

    public static boolean isOpen(String action) {
        return StringUtils.equalsIgnoreCase(OPEN, action);
    }

    public static boolean isNew(String action) {
        return StringUtils.equalsIgnoreCase(NEW, action);
    }

    public static boolean isSubmit(String action) {
        return StringUtils.equalsIgnoreCase(SUBMIT, action);
    }

    public static boolean isCancel(String action) {
        return StringUtils.equalsIgnoreCase(CANCEL, action);
    }

    public static boolean isHistory(String action) {
        return StringUtils.equalsIgnoreCase(HISTORY, action);
    }

    public static boolean isRevoke(String action) {
        return StringUtils.equalsIgnoreCase(REVOKE, action);
    }

    public static boolean isQuit(String action) {
        return StringUtils.equalsIgnoreCase(QUIT, action);
    }

    public static boolean isSave(String action) {
        return StringUtils.equalsIgnoreCase(SAVE, action);
    }

    public static boolean isSplit(String action) {
        return StringUtils.equalsIgnoreCase(SPLIT, action);
    }

    static public final ListData ACTION_NAMES = new ListData("ACTION_NAMES")//
            .put(COPY_NEW, "拷贝新建")//
            .put(NEW, "新建")//
            .put(OPEN, "打开")//
            .put(REVOKE, "撤回")//
            .put(HISTORY, "处理历史")//
            .put(CANCEL, "取消")//
            .put(SUBMIT, "提交")//
            .put(QUIT, "退出")//
            .put(SAVE, "保存")//
            .put(EXCEL_EXPORT, "EXCEL导出")//
            .put(RETURN, "退回")//
            .put(CONFIRM, "确认");//

}
