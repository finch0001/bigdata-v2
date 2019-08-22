package com.yee.bigdata.common.util;

import org.apache.hadoop.hbase.util.Pair;

public class NumberUtil {

    /**
     * Calculates the precision and scale for BigDecimal arithmetic operation results. It uses the algorithm mentioned
     * <a href="http://db.apache.org/derby/docs/10.0/manuals/reference/sqlj124.html#HDRSII-SQLJ-36146">here</a>
     * @param lp precision of the left operand
     * @param ls scale of the left operand
     * @param rp precision of the right operand
     * @param rs scale of the right operand
     * @param op The operation type
     * @return {@link Pair} comprising of the precision and scale.
     */
    public static Pair<Integer, Integer> getResultPrecisionScale(int lp, int ls, int rp, int rs, Operation op) {
        int resultPrec = 0, resultScale = 0;
        switch (op) {
            case MULTIPLY:
                resultPrec = lp + rp;
                resultScale = ls + rs;
                break;
            case DIVIDE:
                resultPrec = lp - ls + rp + Math.max(ls + rp - rs + 1, 4);
                resultScale = 31 - lp + ls - rs;
                break;
            case ADD:
                resultPrec = 2 * (lp - ls) + ls; // Is this correct? The page says addition -> 2 * (p - s) + s.
                resultScale = Math.max(ls, rs);
                break;
            case AVG:
                resultPrec = Math.max(lp - ls, rp - rs) + 1 + Math.max(ls, rs);
                resultScale = Math.max(Math.max(ls, rs), 4);
                break;
            case OTHERS:
                resultPrec = Math.max(lp - ls, rp - rs) + 1 + Math.max(ls, rs);
                resultScale = Math.max(ls, rs);
        }
        return new Pair<Integer, Integer>(resultPrec, resultScale);
    }

    public static enum Operation {
        MULTIPLY, DIVIDE, ADD, AVG, OTHERS;
    }
}
