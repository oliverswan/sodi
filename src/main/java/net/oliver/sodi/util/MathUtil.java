package net.oliver.sodi.util;

import java.math.BigDecimal;

public class MathUtil {

    public static double trimDouble(double value)
    {
        BigDecimal b   =   new   BigDecimal(value);
        return b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
