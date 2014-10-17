package com.example.rssreaderfourxxi.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.rssreaderfourxxi.R;

public class WebPageActivity extends Activity {

	WebView webview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);

		Intent in = getIntent();
		String page_url = in.getStringExtra("page_url");

		webview = (WebView) findViewById(R.id.webpage);
		webview.loadUrl(page_url);

		webview.setWebViewClient(new disPlayWebPageActivityClient());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
			webview.goBack();
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class disPlayWebPageActivityClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

}
