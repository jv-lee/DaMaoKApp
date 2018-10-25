package com.lockstudio.sticklocker.util;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTime {/**
	 * 转换星期缩写方法
	 * 
	 * @author 王雷
	 * @param c 日历
	 * @return 星期缩写
	 */
	public static String getWeekDay_CH() {
		Calendar c = Calendar.getInstance();
		if (c == null) {
			return "一";
		}
		if (Calendar.MONDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "一";
		}
		if (Calendar.TUESDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "二";
		}
		if (Calendar.WEDNESDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "三";
		}
		if (Calendar.THURSDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "四";
		}
		if (Calendar.FRIDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "五";
		}
		if (Calendar.SATURDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "六";
		}
		if (Calendar.SUNDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "日";
		}
		return "一";
	}
	/**
	 * 转换星期缩写方法
	 * 
	 * @author 李玉东
	 * @param c 日历
	 * @return 星期缩写
	 */
	public static String getWeekDay_EN() {
		Calendar c = Calendar.getInstance();
		if (c == null) {
			return "Monday";
		}
		if (Calendar.MONDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "Monday";
		}
		if (Calendar.TUESDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "Tuesday";
		}
		if (Calendar.WEDNESDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "Wednesday";
		}
		if (Calendar.THURSDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "Thursday";
		}
		if (Calendar.FRIDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "Friday";
		}
		if (Calendar.SATURDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "Saturday";
		}
		if (Calendar.SUNDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "Sunday";
		}
		return "Monday";
	}

	/**
	 * 获取当前时间
	 * @author Ray
	 * @return 22:10
	 */
	public static String getTime() {
		long sysTime = System.currentTimeMillis();
		Date date = new Date();
		SimpleDateFormat sdformat = new SimpleDateFormat("HH", Locale.getDefault());
		String LgTime = sdformat.format(date);
		return LgTime + ":" + DateFormat.format("mm", sysTime);
	}

	/**
	 * 获取当前月日周
	 * @author Ray
	 * @return 2月14日周3
	 */
	public static String getWeedendAndDay() {
		Calendar calendar = Calendar.getInstance();
		int d = calendar.get(Calendar.DATE);
		int month = calendar.get(Calendar.MONTH) + 1;
		return month + "月" + d + "日" + "  周"+getWeekDay_CH();
	}
	
	/**
	 * 获取当前月日周
	 * @author Ray
	 * @return 2月14日周3
	 */
	public static String getMonthAndDay() {
		Calendar calendar = Calendar.getInstance();
		int d = calendar.get(Calendar.DATE);
		int month = calendar.get(Calendar.MONTH) + 1;
		return month + "月" + d + "日" ;
	}
	
	/**
	 * 获取当前月日周
	 * @author Ray
	 * @return 2月14日周3
	 */
	public static String getWeedendAndDayEN() {
		Calendar calendar = Calendar.getInstance();
		int d = calendar.get(Calendar.DATE);
		int month = calendar.get(Calendar.MONTH) + 1;
		return getWeekDay_EN() + "/" + d + "/" + month;
	}
	
	//存储电量信息
	public static int batteryLevel;
	//存储字体大小
	public static int textsize;

}
