package net.oliver.sodi.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MathUtil {

    public static DecimalFormat df = new DecimalFormat("0.00");

    public static double trimDouble(double value)
    {
        BigDecimal b   =   new   BigDecimal(String.valueOf(value));
        return b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();//BigDecimal.ROUND_HALF_UP)
//        return Math.round(value*100)/100;
    }


    public static double trimDouble2(double value)
    {
        return  Math.round(value*100)/100;
//        return Math.round(value*100)/100;
    }



    public static String trimDoubleString(double value)
    {
        return df.format(value);
    }// 四舍五入
    public static double trimDouble3(double value)
    {
        return Math.round(value*100)/100;
    }

    public static void main(String[] args) {
//        System.out.println(MathUtil.trimDouble3(8.00));
        double a =(double)8/14;
        System.out.println(a);


        System.out.println(MathUtil.trimDouble(a));
    }
}
