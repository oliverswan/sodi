package net.oliver.sodi.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MathUtil {

    static DecimalFormat df = new DecimalFormat("0.00");

    public static double trimDouble(double value)
    {
        BigDecimal b   =   new   BigDecimal(value);
        return b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String trimDouble2(double value)
    {
        return df.format(value);
    }
}
