package com.example.rssreaderfourxxi.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.rssreaderfourxxi.MainActivity;
import com.example.rssreaderfourxxi.R;
import com.example.rssreaderfourxxi.data.RssItem;
import com.example.rssreaderfourxxi.db.RssDbHandler;
import com.example.rssreaderfourxxi.parser.RssParser;
import com.example.rssreaderfourxxi.utils.SortBasedOnPubDate;
import com.example.rssreaderfourxxi.views.PullToRefreshListView.OnRefreshListener;

public class FragmentRssList extends ListFragment {

	ProgressDialog mProgressBar = null;
	RssDbHandler mRssDb;
	RssParser mRssParser = new RssParser();
	ArrayList<HashMap<String, String>> mAdapterList = new ArrayList<HashMap<String, String>>();
	ListView mRssItemsListView;

	HashMap<Integer, String> mUniqueKeyskHashMap = new HashMap<Integer, String>();
	String mCurrentlink;
	int mCurrFeedId;

	public FragmentRssList() {
		// set default url and id
		mCurrentlink = "http://habrahabr.ru/rss/hubs/";
		mCurrFeedId = 0;
	}

	public FragmentRssList(String aLink, int aFeedId) {
		mCurrentlink = aLink;
		mCurrFeedId = aFeedId;
	}

	public boolean putUniqueKey(int aKey, String aVal) {
		if (!mUniqueKeyskHashMap.containsKey(aKey)) {
			mUniqueKeyskHashMap.put(aKey, aVal);
			return true;
		}
		return false;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mRssDb = new RssDbHandler(this.getActivity().getApplicationContext());

		setHasOptionsMenu(true);
		initProgressDialog();

		mRssItemsListView = getListView();
		mRssItemsListView.setEmptyView(this.getActivity().findViewById(
				R.id.empty_list_item));

		// Launching new screen on Selecting Single ListItem
		mRssItemsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent in = new Intent(FragmentRssList.this.getActivity()
						.getApplicationContext(), WebPageActivity.class);

				// getting page url
				String page_url = null;
				try {
					page_url = ((TextView) view.findViewById(R.id.page_url))
							.getText().toString();
				} catch (Exception e) {

				}
				if (page_url != null) {
					in.putExtra("page_url", page_url);
					startActivity(in);
				}
			}
		});

		// Set a listener to be invoked when the list should be refreshed.

		((PullAndLoadListView) getListView()).setOnRefreshListener(

		new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new loadRSSFeedItems().execute(mCurrentlink);
			}
		});
		new loadStoreItems().execute();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Uri uri = Uri.parse(mCurrentlink);
		((MainActivity) this.getActivity()).setTitle(uri.getHost());
		return inflater.inflate(R.layout.rss_item_list, null);
	}

	// Background Async Task to get RSS Feed Items data from URL
	class loadRSSFeedItems extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (mAdapterList.size() == 0) {
				mProgressBar.show();
			}
		}

		// getting all recent articles and showing them in listview
		@Override
		protected String doInBackground(String... args) {
			// rss link url
			String rss_url = args[0];
			// list of rss items
			List<RssItem> rssItems = mRssParser.getRSSFeedItems(rss_url,
					mCurrFeedId);
			if (rssItems == null || rssItems.size() == 0) {
				return null;
			}
			initHashMap(rssItems, true);
			// updating UI from Background Thread

			FragmentRssList.this.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ListAdapter adapter = getListAdapter();
					setListAdapter(adapter);
				}
			});
			return null;
		}

		// After completing background task Dismiss the progress dialog
		@Override
		protected void onPostExecute(String args) {
			// dismiss the dialog after getting all products
			if (mProgressBar.isShowing()) {
				mProgressBar.dismiss();
				((PullAndLoadListView) getListView()).onRefreshComplete();
			}

		}
	}

	class loadStoreItems extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			if (mAdapterList.size() == 0) {
				mProgressBar.show();
			}
		}

		@Override
		protected String doInBackground(String... args) {
			// updating UI from Background Thread
			FragmentRssList.this.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mAdapterList.size() == 0) {
						List<RssItem> rssItems = mRssDb
								.getRSSItemsByFeedId(mCurrFeedId);
						if (rssItems != null && rssItems.size() > 0)
							initHashMap(rssItems, false);
					}
					ListAdapter adapter = getListAdapter();
					// updating listview
					mRssItemsListView.setAdapter(adapter);
					registerForContextMenu(mRssItemsListView);
				}
			});
			return null;
		}

		// After completing background task Dismiss the progress dialog
		@Override
		protected void onPostExecute(String args) {
			// dismiss the dialog after getting all products
			if (mProgressBar.isShowing())
				mProgressBar.dismiss();
			if (mAdapterList.size() == 0) {
				new loadRSSFeedItems().execute(mCurrentlink);
			}
		}

	}

	// the method initializes unique keys list and add items to db if needed
	void initHashMap(List<RssItem> aRssItems, boolean aAddToDb) {
		boolean toSort = false;
		for (RssItem item : aRssItems) {
			if (putUniqueKey(item.getLink().hashCode(), item.getLink())) {
				if (aAddToDb) {
					mRssDb.addRSSItem(item);
				}
				HashMap<String, String> map = new HashMap<String, String>();

				// adding each child node to HashMap key => value
				map.put(RssDbHandler.KEY_TITLE, item.getTitle());
				map.put(RssDbHandler.KEY_LINK, item.getLink());
				map.put(RssDbHandler.KEY_PUB_DATE, item.getPDateStr());

				// adding HashList to ArrayList
				mAdapterList.add(map);
				toSort = true;
			}
		}
		if (toSort) {
			Collections.sort(mAdapterList, new SortBasedOnPubDate());
		}
	}

	@Override
	public ListAdapter getListAdapter() {
		return new SimpleAdapter(this.getActivity(), mAdapterList,
				R.layout.rss_item_list_row, new String[] {
						RssDbHandler.KEY_LINK, RssDbHandler.KEY_TITLE,
						RssDbHandler.KEY_PUB_DATE }, new int[] { R.id.page_url,
						R.id.title, R.id.pub_date });
	}

	void initProgressDialog() {
		mProgressBar = new ProgressDialog(this.getActivity(),
				R.style.DialogTheme);
		mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressBar.setCancelable(true);
		mProgressBar.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
		mProgressBar.setIndeterminate(true);
		mProgressBar.setCanceledOnTouchOutside(false);
		mProgressBar.setCancelable(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			((MainActivity) this.getActivity()).getSlidingMenu().toggle();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
