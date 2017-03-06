package com.pds.p2p.core.utils;

import java.math.BigDecimal;

public class MathUtils {
    /**
     * 除法向上取整
     *
     * @param a
     * @param b
     *
     * @return
     */
    public static BigDecimal getDivCeilVal(BigDecimal a, BigDecimal b) {
        BigDecimal c = a.divide(b, 2, BigDecimal.ROUND_HALF_UP);
        return new BigDecimal(Math.ceil(c.doubleValue()));
    }

    /**
     * 乘法向上取整
     *
     * @param a
     * @param b
     *
     * @return
     */
    public static BigDecimal getMulCeilVal(BigDecimal a, BigDecimal b) {
        BigDecimal c = a.multiply(b);
        return new BigDecimal(Math.ceil(c.doubleValue()));
    }

}
