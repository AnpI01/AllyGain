package com.zenzsol.allygain.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonUtils {
	public static Date getSixMonthsBackDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -6);
		return cal.getTime();
	}
	public static Date getDateDdmmyyyy(String dateSt) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try
		{
			sdf.setLenient(false);
			Date d = sdf.parse(dateSt);
			return d;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	public static String getStringDdmmyyyy(Date dateSt) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try
		{
			sdf.setLenient(false);
			String d = sdf.format(dateSt);
			return d;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	public static Date getDateWithoutTime(Date dateSt) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try
		{
			sdf.setLenient(false);
			return sdf.parse(sdf.format(dateSt));
			
		}
		catch (Exception e)
		{
			return null;
		}
	}

}
