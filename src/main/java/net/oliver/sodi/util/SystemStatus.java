package net.oliver.sodi.util;

import java.util.Calendar;

public class SystemStatus {

    public static int getCurrentYM()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        return Integer.parseInt(year+""+month);
    }

    public static String getCurrentYear()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        return String.valueOf(year);
    }


    public static int getCurrentM()
    {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH)+1;
    }


    public static String getCurrentMPrevious(int i)
    {
        Calendar cal = Calendar.getInstance();
        return SystemStatus.getCurrentYear()+(cal.get(Calendar.MONTH)+1-i);
    }

}
