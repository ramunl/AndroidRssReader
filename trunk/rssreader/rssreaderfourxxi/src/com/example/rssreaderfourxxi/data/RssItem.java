package com.example.rssreaderfourxxi.data;

import java.util.Date;

import com.example.rssreaderfourxxi.utils.DateFormater;

/**
 * This class handle RSS Item <item> node in rss xml
 * */
public class RssItem {

	Integer mId;
	String mTitle;
	String mLink;
	String mPubdate;
	int mFeedId;

	// constructor
	public RssItem() {

	}

	// constructor with parameters
	public RssItem(String title, String link, String pubdate, int aFeedId) {
		mTitle = title;
		mLink = link;
		mPubdate = pubdate;
		mFeedId = aFeedId;
	}

	public void setId(Integer id) {
		this.mId = id;
	}

	/**
	 * All SET methods
	 * */
	public void setTitle(String title) {
		this.mTitle = title;
	}

	public void setLink(String link) {
		this.mLink = link;
	}

	public void setPubdate(String pubDate) {
		this.mPubdate = pubDate;
	}

	public void setFeedId(int aFeedId) {
		this.mFeedId = aFeedId;
	}

	public int getFeedId() {
		return mFeedId;
	}

	public Integer getId() {
		return this.mId;
	}

	public String getTitle() {
		return this.mTitle;
	}

	public String getLink() {
		return this.mLink;
	}

	Date _pubdateDate = null;

	public Date getPDateDate() {
		if (_pubdateDate == null) {
			_pubdateDate = DateFormater.getPubdateDate(mPubdate);
		}
		return this._pubdateDate;
	}

	public String getPDateStr() {
		return this.mPubdate;
	}

}
