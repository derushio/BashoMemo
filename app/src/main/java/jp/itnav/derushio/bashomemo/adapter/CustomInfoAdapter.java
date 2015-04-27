package jp.itnav.derushio.bashomemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import jp.itnav.derushio.bashomemo.R;

/**
 * Created by derushio on 14/12/06.
 */
public class CustomInfoAdapter implements GoogleMap.InfoWindowAdapter {
	/**
	 * GoogleMapの吹き出しを定義するClass
	 */
	private Context mContext;
	private View mInfoWindow;

	private TextView mTitleText;

	public CustomInfoAdapter(Context context) {
		this.mContext = context;
		mInfoWindow = LayoutInflater.from(context).inflate(R.layout.window_custom_info, null);
	}

	@Override
	public View getInfoWindow(Marker marker) {
		render(marker, mInfoWindow);
		return mInfoWindow;
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	private void render(Marker marker, View view) {
		mTitleText = (TextView) view.findViewById(R.id.textViewTitle);
		mTitleText.setText(marker.getTitle());
	}
}
