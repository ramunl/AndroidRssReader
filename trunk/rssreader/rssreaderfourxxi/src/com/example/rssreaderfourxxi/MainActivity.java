package com.example.rssreaderfourxxi;

import java.util.HashMap;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.View;
import android.view.Window;

import com.example.rssreaderfourxxi.data.UrlItem;
import com.example.rssreaderfourxxi.views.FragmentLeftMenu;
import com.example.rssreaderfourxxi.views.FragmentRssList;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity {

	Fragment mContent;
	HashMap<Integer, Fragment> mFragmentHashMap = new HashMap<Integer, Fragment>();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.responsive_content_frame);

		// check if the content frame contains the menu frame
		if (findViewById(R.id.menu_frame) == null) {
			setBehindContentView(R.layout.menu_frame);
			getSlidingMenu().setSlidingEnabled(true);
			getSlidingMenu()
					.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			// show home as up so we can toggle
			getActionBar().setDisplayHomeAsUpEnabled(true);

		} else {
			// add a dummy view
			View v = new View(this);
			setBehindContentView(v);
			getSlidingMenu().setSlidingEnabled(false);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}

		// set the Above View Fragment
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new FragmentRssList();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();

		// set the Behind View Fragment
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new FragmentLeftMenu()).commit();

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}

	public void switchContent(final UrlItem urlItem) {
		Uri uri = Uri.parse(urlItem.getLink());
		setTitle(uri.getHost());

		if (mFragmentHashMap.containsKey(urlItem.getId())) {
			mContent = mFragmentHashMap.get(urlItem.getId());
		} else {
			mContent = new FragmentRssList(urlItem.getLink(), urlItem.getId());
			mFragmentHashMap.put(urlItem.getId(), mContent);
		}
		this.toggle();
		switchContent(mContent);
	}

	public void switchContent(final Fragment aFragment) {
		if (aFragment != null) {
			Handler h = new Handler();
			h.postDelayed(new Runnable() {
				@Override
				public void run() {
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.content_frame, aFragment).commit();
					// getSlidingMenu().showContent();
				}
			}, 300);
		}
	}

	public void onBirdPressed(int pos) {
		// Intent intent = BirdActivity.newInstance(this, pos);
		// startActivity(intent);
	}

}
