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
	private Context context;
	private View infoWindow;

	private TextView titleText;

	public CustomInfoAdapter(Context context) {
		this.context = context;
		infoWindow = LayoutInflater.from(context).inflate(R.layout.window_custom_info, null, false);
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}
	// 吹き出し無し

	@Override
	public View getInfoContents(Marker marker) {
		setInfo(marker, infoWindow);
		return infoWindow;
	}
	// 吹き出し有り

	private void setInfo(Marker marker, View view) {
		titleText = (TextView) view.findViewById(R.id.textViewTitle);
		titleText.setText(marker.getTitle());
	}
}
