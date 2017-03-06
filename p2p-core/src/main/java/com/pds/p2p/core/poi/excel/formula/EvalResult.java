/**
 *
 */
package com.pds.p2p.core.poi.excel.formula;

/****************************************************
 * <pre>
 * 描    述： （必填）
 *
 * 实施资源: （选填）
 * 调用者  : （选填）
 * 被调用者: （选填）
 * Company:  普德施PDS
 * @author 王文
 * @version 1.0   2015年3月1日
 * @see （选填）
 * 	HISTORY  （必填）
 * 	2015年3月1日  Administrator 创建文件
 *
 * </pre>
 **************************************************/

public class EvalResult {
    private boolean eq;
    private double val;

    public boolean isEq() {
        return eq;
    }

    public void setEq(boolean eq) {
        this.eq = eq;
    }

    public double getVal() {
        return val;
    }

    public void setVal(double val) {
        this.val = val;
    }

}
