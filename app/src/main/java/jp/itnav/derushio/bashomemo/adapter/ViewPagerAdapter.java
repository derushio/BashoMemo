package jp.itnav.derushio.bashomemo.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import jp.itnav.derushio.bashomemo.fragmentmemoviewer.GoogleMapFragment;
import jp.itnav.derushio.bashomemo.fragmentmemoviewer.MemoFragment;
import jp.itnav.derushio.bashomemo.fragmentmemoviewer.PhotoFragment;

/**
 * Created by derushio on 15/04/18.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
	private Context mContext;

	private String[] mTitles = {"地図", "写真", "メモ"};
	private Fragment[] mItems = {new GoogleMapFragment(), new PhotoFragment(), new MemoFragment()};

	public ViewPagerAdapter(Context context, FragmentManager fragmentManager) {
		super(fragmentManager);

		mContext = context;
	}

	@Override
	public Fragment getItem(int position) {
		return mItems[position];
	}

	@Override
	public int getCount() {
		return mItems.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mTitles[position];
	}
}
