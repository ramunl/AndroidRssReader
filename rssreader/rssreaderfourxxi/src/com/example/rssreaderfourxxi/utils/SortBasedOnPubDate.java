package com.example.rssreaderfourxxi.utils;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import com.example.rssreaderfourxxi.db.RssDbHandler;

public class SortBasedOnPubDate implements Comparator<HashMap<String, String>> {

	@Override
	public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
		String str = lhs.get(RssDbHandler.KEY_PUB_DATE);
		Date left = DateFormater.getPubdateDate(str);
		str = rhs.get(RssDbHandler.KEY_PUB_DATE);
		Date right = DateFormater.getPubdateDate(str);
		if (left == null || right == null)
			return 0;
		return right.compareTo(left);
	}
}
