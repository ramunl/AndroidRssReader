package com.example.rssreaderfourxxi.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rssreaderfourxxi.data.RssItem;
import com.example.rssreaderfourxxi.data.UrlItem;

public class RssDbHandler extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;

	public static final String DATABASE_NAME = "rssReader";
	public static final String TABLE_RSS = "RSSItems";
	public static final String TABLE_URL = "UrlItems";

	public static final String KEY_ID = "id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_LINK = "link";
	public static final String KEY_PUB_DATE = "pub_date";
	public final static String KEY_FEED_ID = "feed_id";

	public RssDbHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_RSS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
				+ KEY_LINK + " TEXT," + KEY_PUB_DATE + " TEXT, " + KEY_FEED_ID
				+ " TEXT" + ")";
		db.execSQL(createTable);

		createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_URL + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY," + KEY_LINK + " TEXT" + ")";

		db.execSQL(createTable);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RSS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_URL);
		// Create tables again
		onCreate(db);
	}

	/**
	 * Adding a new RSSItem in RSSItems table Function will check if a rssItem
	 * already existed in database. If existed will update the old one else
	 * creates a new row
	 * */
	public void addUrlItem(UrlItem aUrlItem) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_LINK, aUrlItem.getLink()); // rssItem url
		// rssItem not existed, create a new row
		db.insert(TABLE_URL, null, values);
		db.close();
	}

	public void addRSSItem(RssItem rssItem) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TITLE, rssItem.getTitle()); // rssItem title
		values.put(KEY_LINK, rssItem.getLink()); // rssItem url
		values.put(KEY_PUB_DATE, rssItem.getPDateStr()); // rssItem description
		values.put(KEY_FEED_ID, rssItem.getFeedId()); // rssItem description

		// rssItem not existed, create a new row
		db.insert(TABLE_RSS, null, values);
		db.close();

	}

	/**
	 * Reading all rows from database
	 * */
	public List<UrlItem> getUrlItems() {
		List<UrlItem> urlItemList = new ArrayList<UrlItem>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_URL + " ORDER BY id ASC";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				UrlItem urlItem = new UrlItem();
				urlItem.setId(Integer.parseInt(cursor.getString(0)));
				urlItem.setLink(cursor.getString(1));
				urlItemList.add(urlItem);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();

		// return contact list
		return urlItemList;
	}

	public List<RssItem> getRSSItemsByFeedId(int aFeedId) {
		List<RssItem> rssItemList = new ArrayList<RssItem>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_RSS + " WHERE "
				+ KEY_FEED_ID + "=" + aFeedId + " ORDER BY id DESC";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				RssItem rssItem = new RssItem();
				rssItem.setId(Integer.parseInt(cursor.getString(0)));
				rssItem.setTitle(cursor.getString(1));
				rssItem.setLink(cursor.getString(2));
				rssItem.setPubdate(cursor.getString(3));
				rssItem.setFeedId(cursor.getInt(4));
				// Adding contact to list
				rssItemList.add(rssItem);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();

		// return contact list
		return rssItemList;
	}
}