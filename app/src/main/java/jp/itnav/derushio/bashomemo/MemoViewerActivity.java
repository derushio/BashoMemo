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
	private Toolbar toolbar;
	private TabWidget tabWidget;
	private View indicator;
	private ViewPager viewPager;
	// View

	// ViewPagerClass
	private FragmentManager fragmentManager;
	private FragmentPagerAdapter fragmentPagerAdapter;
	// ViewPagerClass

	// Fragments
	private GoogleMapFragment googleMapFragment;
	private PhotoFragment photoFragment;
	private MemoFragment memoFragment;
	// Fragments

	// Vars
	private MemoDataBaseManager memoDataBaseManager;
	private long id;
	// Vars

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memo_viewer);

		Intent intent = getIntent();
		id = intent.getLongExtra(INTENT_EXTRA_ID, -1);

		memoDataBaseManager = new MemoDataBaseManager(this);

		if (id == -1) {
			id = memoDataBaseManager.addMemoData("新規データ");
		}

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
		Bundle googleMapArgment = new Bundle();
		final LatLng latLng = memoDataBaseManager.findLatLngById(id);
		double[] latLngDouble;
		if (latLng != null) {
			latLngDouble = new double[]{latLng.latitude, latLng.longitude};
		} else {
			latLngDouble = new double[]{GoogleMapFragment.ARGMENT_NULL, GoogleMapFragment.ARGMENT_NULL};
		}
		googleMapArgment.putDoubleArray(GoogleMapFragment.LAT_LNG_ARGMENT, latLngDouble);
		googleMapFragment.setArguments(googleMapArgment);

		photoFragment = (PhotoFragment) fragmentPagerAdapter.getItem(1);
		Bundle photoFragmentArgment = new Bundle();
		Uri uri = memoDataBaseManager.findPictureUriById(id);
		String path;
		if (uri != null) {
			path = uri.getPath();
		} else {
			path = PhotoFragment.ARGMENT_NULL;
		}
		photoFragmentArgment.putString(PhotoFragment.PHTO_URI_ARGMENT, path);
		photoFragment.setArguments(photoFragmentArgment);

		memoFragment = (MemoFragment) fragmentPagerAdapter.getItem(2);
		Bundle memoFragmentArgment = new Bundle();
		String memo = memoDataBaseManager.findMemoById(id);
		if (memo == null) {
			memo = MemoFragment.ARGMENT_NULL;
		}
		memoFragmentArgment.putString(MemoFragment.MEMO_ARGMENT, memo);
		memoFragment.setArguments(memoFragmentArgment);
	}

	private void initializeToolbar() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar == null) {
			throw new IllegalStateException("Layout is required to include a Toolbar with id " +
					"'toolbar'");
		}

		toolbar.setTitle(memoDataBaseManager.findTitleById(id));
		toolbar.setTitleTextColor(Color.WHITE);
		toolbar.inflateMenu(R.menu.menu_memo_viewer);
		setSupportActionBar(toolbar);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MemoListActivity.class);
		startActivity(intent);

		googleMapFragment.animationCancel();
		memoDataBaseManager.updateMemoData(id, toolbar.getTitle().toString(), googleMapFragment.getLastLatLng(), photoFragment.getUri(), memoFragment.getMemo());

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

				editName.setText(toolbar.getTitle().toString());

				buttonOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						toolbar.setTitle(editName.getText().toString());
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