package net.oliver.sodi.util;

import java.util.Calendar;

public class SystemStatus {

    public static int getCurrentMonth()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        return Integer.parseInt(year+""+month);
    }

}
