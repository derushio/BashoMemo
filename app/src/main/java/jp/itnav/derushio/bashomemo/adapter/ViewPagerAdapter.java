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

	private Fragment[] mItem = {new GoogleMapFragment(),new PhotoFragment(), new MemoFragment()};

	public ViewPagerAdapter(Context mContext, FragmentManager fm) {
		super(fm);

		this.mContext = mContext;
	}

	@Override
	public Fragment getItem(int position) {
		return mItem[position];
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch(position){
			case 0:
				return "地図";
			case 1:
				return "画像";
			case 2:
				return "メモ";
		}

		return "null";
	}
}
