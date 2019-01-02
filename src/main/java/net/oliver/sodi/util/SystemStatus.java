package net.oliver.sodi.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SystemStatus {

    public static int getCurrentYM()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);

        int month = cal.get(Calendar.MONTH)+1;

        String x = month<10?"0"+month:""+month;
        return Integer.parseInt(year+""+x);
    }

    public static String getCurrentYear()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        return String.valueOf(year);
    }

    public static int getCurrentYearInt()
    {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
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

    public static List<String> getLastMonthLabel(int month)
    {
        List<String> result = new ArrayList<String>();
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH)+1;

        // 首先解决今年的
        int t =  month - currentMonth;
        if(t<=0)
        {
            for (int x =month;x>0;x--)
            {
                int cm = currentMonth - month;
                String v = cm<10?"0"+cm:""+cm;
                if(v.equals("00"))
                    v = "01";
                result.add(String.valueOf(cal.get(Calendar.YEAR))+v);
            }
        }else{
            int y = t/12;
            int left2 = t - y*12 -1;
            int beginYear =  cal.get(Calendar.YEAR) - 1 - y;
            for (int x =left2;x>=0;x--)
            {
                int cm = 12 - x;
                String v = cm<10?"0"+cm:""+cm;
                result.add(String.valueOf(cal.get(Calendar.YEAR)-1-y)+v);
            }

            for(int i=y;i>0;i--)
            {
                int tempYear = cal.get(Calendar.YEAR)-i;
                for(int k=1;k<=12;k++)
                {
                    String m = k<10?"0"+k:""+k;
                    result.add(String.valueOf(tempYear)+m);
                }
            }
            for (int x =1;x<=currentMonth;x++)
            {
                String v = x<10?"0"+x:""+x;
                result.add(String.valueOf(cal.get(Calendar.YEAR))+v);
            }


        }



        return result;
    }

    public static void main(String[] args) {

        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);
        System.out.println(SystemStatus.getLastMonthLabel(50));
    }

}
