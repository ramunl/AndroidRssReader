package com.example.rssreaderfourxxi.data;

public class UrlItem {

	int mId;
	String mLink;

	// constructor
	public UrlItem() {

	}

	// constructor with parameters
	public UrlItem(String aLink) {
		if (!aLink.startsWith("http://") && !aLink.startsWith("https://")) {
			mLink = "http://" + aLink;
		} else {
			mLink = aLink;
		}
	}

	public void setId(int id) {
		mId = id;
	}

	public void setLink(String link) {
		mLink = link;
	}

	public String getLink() {
		return mLink;
	}

	public Integer getId() {
		return mId;
	}
}
