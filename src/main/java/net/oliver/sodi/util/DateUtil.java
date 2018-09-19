package net.oliver.sodi.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtil {

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm:ss");

	private static String[] smonths = {"01","03","05","07","08","10","12"};
	private static String[] dmonths = {"02","04","06","09","11"};
	
	public static void main(String[] args) {
//		System.out.println(DateUtil.delayDays("10/07/2018 01:16:58", -10));
		System.out.println(DateUtil.getMaxMonthDate("10/07/2018 01:16:58"));
		
		/*String date = "10/07/2018 01:16:58";
//		String s1 = date.substring(0,);
		String month =String.valueOf(Integer.parseInt(date.substring(3,5))+2);
		
		String yyyy = date.substring(6,10);
		String result = "01/"+month+"/"+yyyy+" 00:00:00";
		try {
			Date d = sdf.parse(result);
			System.out.println(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}

	/*
	 * 获取任意时间下个月的最后一天 描述:<描述函数实现的功能>.
	 * 
	 * @param repeatDate
	 * 
	 * @return
	 */
	public static String getMaxMonthDate(String date) {
		StringBuilder sb = new StringBuilder();
		if("".equals(date) || date == null)
		{
			return "";
		}
//		String day = date.substring(0,2);
		
		String month =String.valueOf(Integer.parseInt(date.substring(3,5))+1);
		if(Integer.parseInt(date.substring(3,5))+1<10)
		{
			month ="0"+String.valueOf(Integer.parseInt(date.substring(3,5))+1);
		}
		String ddate = "30/";
		for(String d:smonths )
		{
			if(month.equals(d))
			{
				ddate = "31/";
				break;
			}
		}
		String yyyy = date.substring(6,10);
		
		sb.append(ddate).append(month).append("/").append(yyyy).append(" 18:00:00");
		
		return sb.toString();
		/*Calendar calendar = new GregorianCalendar();
		TimeZone timeZone = TimeZone.getTimeZone("Australia/Melbourne");
		calendar.setTimeZone(timeZone);
		Calendar c = calendar.getInstance(timeZone);
		
		try {
			if (date != null && !"".equals(date)) {
				calendar.setTime(sdf.parse(date));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.add(Calendar.MONTH, 2);
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return sdf.format(calendar.getTime());*/
	}

	/**
	 * 计算日期前后的多少天的日期
	 */
	public static String delayDays(String currDate, int days) {
		Calendar calendar = new GregorianCalendar();
		TimeZone timeZone = TimeZone.getTimeZone("Australia/Melbourne");
		calendar.setTimeZone(timeZone);
		Calendar c = calendar.getInstance(timeZone);

		// String dateString = "10/07/2018 01:16:58";

		try {
			Date date = sdf.parse(currDate);
			c.setTime(date);
			c.add(Calendar.DAY_OF_MONTH, days);
			return sdf.format(c.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return currDate;
	}
}
