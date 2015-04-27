package jp.itnav.derushio.bashomemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import jp.itnav.derushio.bashomemo.adapter.ViewPagerAdapter;
import jp.itnav.derushio.bashomemo.database.MemoDataBaseManager;
import jp.itnav.derushio.bashomemo.fragmentmemoviewer.GoogleMapFragment;
import jp.itnav.derushio.bashomemo.fragmentmemoviewer.MemoFragment;
import jp.itnav.derushio.bashomemo.fragmentmemoviewer.PhotoFragment;

public class MemoViewerActivity extends ActionBarActivity implements TextWatcher {
	/**
	 * メモ部を表示するActivity
	 */

	public static final String INTENT_EXTRA_ID = "INTENT_EXTRA_ID";

	private Toolbar mToolbar;

	private TabWidget tabWidget;
	private View indicator;

	private ViewPager viewPager;
	private FragmentManager fragmentManager;
	private FragmentPagerAdapter fragmentPagerAdapter;

	private MemoDataBaseManager mMemoDataBaseManager;
	private long mId;

	private String mTitle;
	private GoogleMapFragment googleMapFragment;
	private PhotoFragment photoFragment;
	private MemoFragment memoFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memo_viewer);

		Intent intent = getIntent();
		mId = intent.getLongExtra(INTENT_EXTRA_ID, -1);

		mMemoDataBaseManager = new MemoDataBaseManager(this);

		if (mId == -1) {
			mId = mMemoDataBaseManager.addMemoData("新規データ");
		}

		mTitle = mMemoDataBaseManager.findTitleById(mId);
		initializeToolbar();

		fragmentManager = getSupportFragmentManager();

		fragmentPagerAdapter = new ViewPagerAdapter(this, fragmentManager);


		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(fragmentPagerAdapter);

		final TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();

		tabWidget = (TabWidget) findViewById(android.R.id.tabs);
		indicator = findViewById(R.id.indicator);

		for (int i = 0; i < fragmentPagerAdapter.getCount(); i++) {
			TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.tab_textview, null);
			textView.setText(fragmentPagerAdapter.getPageTitle(i));
			tabHost.addTab(tabHost.newTabSpec(String.valueOf(i)).setIndicator(textView).setContent(android.R.id.tabcontent));
		}

		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				viewPager.setCurrentItem(Integer.valueOf(tabId));
			}
		});

		viewPager.setOnPageChangeListener(new PageChangeListener());

		googleMapFragment = (GoogleMapFragment) fragmentPagerAdapter.getItem(0);
		googleMapFragment.lastLatLng = mMemoDataBaseManager.findLatLngById(mId);

		photoFragment = (PhotoFragment) fragmentPagerAdapter.getItem(1);
		photoFragment.setUri(mMemoDataBaseManager.findPictureUriById(mId));

		memoFragment = (MemoFragment) fragmentPagerAdapter.getItem(2);
		memoFragment.memo = mMemoDataBaseManager.findMemoById(mId);


	}

	private void initializeToolbar() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		if (mToolbar == null) {
			throw new IllegalStateException("Layout is required to include a Toolbar with id " +
					"'toolbar'");
		}


		mToolbar.setTitle(mTitle);
		mToolbar.setTitleTextColor(Color.WHITE);
		mToolbar.inflateMenu(R.menu.menu_memo_list);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MemoListActivity.class);
		startActivity(intent);

		googleMapFragment.animationCancel();
		mMemoDataBaseManager.updateMemoData(mId, mTitle, googleMapFragment.getLastLatLng(), photoFragment.getUri(), memoFragment.getMemo());

		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();

		try {
//			mMemoDataBaseManager.updateMemoData(mId, mTitleEdit.getText().toString(), mGoogleMapFragment.getLastLatLng(), mPhotoFragment.getmUri(), mMemoFragment.getMemo());
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_memo_viewer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	public class OnLocationChangedListener {
		public void onChange(double latitude, double longitude) {

		}
	}

	private class PageChangeListener implements ViewPager.OnPageChangeListener {
		private int scrollingState = ViewPager.SCROLL_STATE_IDLE;

		@Override
		public void onPageSelected(int position) {
			if (scrollingState == ViewPager.SCROLL_STATE_IDLE) {
				updateIndicatorPosition(position, 0);
			}
			tabWidget.setCurrentTab(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			scrollingState = state;
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			updateIndicatorPosition(position, positionOffset);
		}

		private void updateIndicatorPosition(int position, float positionOffset) {
			View tabView = tabWidget.getChildTabViewAt(position);

			int indicatorWidth = tabView.getWidth();
			int indicatorLeft = (int) ((position + positionOffset) * indicatorWidth);

			final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) indicator.getLayoutParams();
			layoutParams.width = indicatorWidth;
			layoutParams.setMargins(indicatorLeft, 0, 0, 0);
			indicator.setLayoutParams(layoutParams);
		}
	}
}