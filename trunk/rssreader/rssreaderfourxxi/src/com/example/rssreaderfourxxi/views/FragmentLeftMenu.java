package com.example.rssreaderfourxxi.views;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.rssreaderfourxxi.MainActivity;
import com.example.rssreaderfourxxi.R;
import com.example.rssreaderfourxxi.data.UrlItem;
import com.example.rssreaderfourxxi.db.RssDbHandler;

public class FragmentLeftMenu extends ListFragment {

	static public final List<String> DEFAULT_URL_LIST = new ArrayList<String>() {
		{
			add("http://habrahabr.ru/rss/hubs/");
			add("http://ria.ru/export/rss2/politics/index.xml");
			add("http://www.vesti.ru/vesti.rss");
		}
	};

	RssDbHandler mRssDb;
	List<UrlItem> mUrlItems;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_settings:
			ShowEditDialog();
			((MainActivity) this.getActivity()).getSlidingMenu().toggle();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	void ShowEditDialog() {
		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(this.getActivity());
		View promptView = layoutInflater.inflate(R.layout.prompt_dialog, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this.getActivity());
		alertDialogBuilder.setView(promptView);
		final EditText input = (EditText) promptView
				.findViewById(R.id.userInput);

		alertDialogBuilder.setCancelable(false);

		alertDialogBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {
						UrlItem urlItem = new UrlItem(input.getText()
								.toString());
						mRssDb.addUrlItem(urlItem);
						mUrlItems = mRssDb.getUrlItems();
						SetAdapter();
					}

				}).setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		// create an alert dialog
		AlertDialog alertD = alertDialogBuilder.create();
		alertD.show();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.left_menu_list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		mRssDb = new RssDbHandler(this.getActivity().getApplicationContext());
		new loadStoreItems().execute();
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {

		UrlItem urlItem = mUrlItems.get(position);
		if (getActivity() == null)
			return;
		if (getActivity() instanceof MainActivity) {
			MainActivity readerMainActivity = (MainActivity) getActivity();
			readerMainActivity.switchContent(urlItem);
		}
	}

	public void SetAdapter() {
		final int arraySize = mUrlItems.size();
		String[] strArray = new String[arraySize];
		for (int i = 0; i < arraySize; i++) {
			strArray[i] = mUrlItems.get(i).getLink();
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, android.R.id.text1,
				strArray);
		setListAdapter(adapter);
	}

	class loadStoreItems extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... args) {
			// updating UI from Background Thread
			FragmentLeftMenu.this.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mUrlItems = mRssDb.getUrlItems();

					if (mUrlItems == null || mUrlItems.size() == 0) {

						for (String url : DEFAULT_URL_LIST) {
							UrlItem urlItem = new UrlItem(url);
							mRssDb.addUrlItem(urlItem);
						}
						// to init url id's we need to recover url items form db
						mUrlItems = mRssDb.getUrlItems();
					}
					SetAdapter();
					// registerForContextMenu(mRssItemsListView);
				}
			});
			return null;
		}

		@Override
		protected void onPostExecute(String args) {
		}

	}
}
