package jp.itnav.derushio.bashomemo;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import jp.itnav.derushio.bashomemo.adapter.ViewPagerAdapter;
import jp.itnav.derushio.bashomemo.database.MemoDataBaseManager;
import jp.itnav.derushio.bashomemo.fragmentmemoviewer.GoogleMapFragment;
import jp.itnav.derushio.bashomemo.fragmentmemoviewer.MemoFragment;
import jp.itnav.derushio.bashomemo.fragmentmemoviewer.PhotoFragment;

public class MemoViewerActivity extends AppCompatActivity implements TextWatcher {
	/**
	 * メモ部を表示するActivity
	 */

	// Statics
	public static final String INTENT_EXTRA_ID = "INTENT_EXTRA_ID";
	// Statics

	// View
	private Toolbar mToolbar;
	private TabWidget mTabWidget;
	private View mIndicator;
	private ViewPager mViewPager;
	// View

	// ViewPagerClass
	private FragmentManager mFragmentManager;
	private FragmentPagerAdapter mFragmentPagerAdapter;
	// ViewPagerClass

	// Fragments
	private GoogleMapFragment mGoogleMapFragment;
	private PhotoFragment mPhotoFragment;
	private MemoFragment mMemoFragment;
	// Fragments

	// Vars
	private MemoDataBaseManager mMemoDataBaseManager;
	private long mId;
	// Vars

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

		initializeToolbar();

		mFragmentManager = getSupportFragmentManager();

		mFragmentPagerAdapter = new ViewPagerAdapter(this, mFragmentManager);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mFragmentPagerAdapter);

		final TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();

		mTabWidget = (TabWidget) findViewById(android.R.id.tabs);
		mIndicator = findViewById(R.id.indicator);

		for (int i = 0; i < mFragmentPagerAdapter.getCount(); i++) {
			TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.tab_textview, null);
			textView.setText(mFragmentPagerAdapter.getPageTitle(i));
			tabHost.addTab(tabHost.newTabSpec(String.valueOf(i)).setIndicator(textView).setContent(android.R.id.tabcontent));
		}

		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				mViewPager.setCurrentItem(Integer.valueOf(tabId));
			}
		});

		mViewPager.setOnPageChangeListener(new PageChangeListener());

		mGoogleMapFragment = (GoogleMapFragment) mFragmentPagerAdapter.getItem(0);
		Bundle googleMapArgment = new Bundle();
		final LatLng latLng = mMemoDataBaseManager.findLatLngById(mId);
		double[] latLngDouble;
		if (latLng != null) {
			latLngDouble = new double[]{latLng.latitude, latLng.longitude};
		} else {
			latLngDouble = new double[]{GoogleMapFragment.ARGMENT_NULL, GoogleMapFragment.ARGMENT_NULL};
		}
		googleMapArgment.putDoubleArray(GoogleMapFragment.LAT_LNG_ARGMENT, latLngDouble);
		mGoogleMapFragment.setArguments(googleMapArgment);

		mPhotoFragment = (PhotoFragment) mFragmentPagerAdapter.getItem(1);
		Bundle photoFragmentArgment = new Bundle();
		Uri uri = mMemoDataBaseManager.findPictureUriById(mId);
		String path;
		if (uri != null) {
			path = uri.getPath();
		} else {
			path = PhotoFragment.ARGMENT_NULL;
		}
		photoFragmentArgment.putString(PhotoFragment.PHTO_URI_ARGMENT, path);
		mPhotoFragment.setArguments(photoFragmentArgment);

		mMemoFragment = (MemoFragment) mFragmentPagerAdapter.getItem(2);
		Bundle memoFragmentArgment = new Bundle();
		String memo = mMemoDataBaseManager.findMemoById(mId);
		if (memo == null) {
			memo = MemoFragment.ARGMENT_NULL;
		}
		memoFragmentArgment.putString(MemoFragment.MEMO_ARGMENT, memo);
		mMemoFragment.setArguments(memoFragmentArgment);
	}

	private void initializeToolbar() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		if (mToolbar == null) {
			throw new IllegalStateException("Layout is required to include a Toolbar with id " +
					"'toolbar'");
		}

		mToolbar.setTitle(mMemoDataBaseManager.findTitleById(mId));
		mToolbar.setTitleTextColor(Color.WHITE);
		mToolbar.inflateMenu(R.menu.menu_memo_viewer);
		setSupportActionBar(mToolbar);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MemoListActivity.class);
		startActivity(intent);

		mGoogleMapFragment.animationCancel();
		mMemoDataBaseManager.updateMemoData(mId, mToolbar.getTitle().toString(), mGoogleMapFragment.getLastLatLng(), mPhotoFragment.getUri(), mMemoFragment.getMemo());

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
		// automatically handle clicks on the Home/Up button, so png
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		switch (id) {
			case (R.id.action_change_name):
				final AppCompatDialog appCompatDialog = new AppCompatDialog(this);
				View view = LayoutInflater.from(this).inflate(R.layout.dialog_change_name, null, false);
				appCompatDialog.setContentView(view);
				appCompatDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

				final EditText editName = (EditText) view.findViewById(R.id.edit_name);
				Button buttonOk = (Button) view.findViewById(R.id.button_ok);
				Button buttonCancel = (Button) view.findViewById(R.id.button_cancel);

				editName.setText(mToolbar.getTitle().toString());

				buttonOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mToolbar.setTitle(editName.getText().toString());
						appCompatDialog.dismiss();
					}
				});

				buttonCancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						appCompatDialog.dismiss();
					}
				});

				appCompatDialog.show();
				break;
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
			mTabWidget.setCurrentTab(position);
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
			View tabView = mTabWidget.getChildTabViewAt(position);

			int indicatorWidth = tabView.getWidth();
			int indicatorLeft = (int) ((position + positionOffset) * indicatorWidth);

			final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mIndicator.getLayoutParams();
			layoutParams.width = indicatorWidth;
			layoutParams.setMargins(indicatorLeft, 0, 0, 0);
			mIndicator.setLayoutParams(layoutParams);
		}
	}
}