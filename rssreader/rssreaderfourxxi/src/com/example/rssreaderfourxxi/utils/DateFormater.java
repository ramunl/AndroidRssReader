package com.example.rssreaderfourxxi.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormater {

	static SimpleDateFormat formater = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

	public static Date getPubdateDate(String aDateStr) {

		Date pubdateDate = null;
		try {
			pubdateDate = formater.parse(aDateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pubdateDate;
	}

}
