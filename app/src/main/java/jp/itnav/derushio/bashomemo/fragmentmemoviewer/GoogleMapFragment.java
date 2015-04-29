package jp.itnav.derushio.bashomemo.fragmentmemoviewer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import jp.itnav.derushio.bashomemo.MemoViewerActivity;
import jp.itnav.derushio.bashomemo.R;
import jp.itnav.derushio.bashomemo.adapter.CustomInfoAdapter;

/**
 * Created by derushio on 14/11/30.
 */
public class GoogleMapFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks, LocationListener {
	public static final String LAT_LNG_ARGMENT = "LatLng";
	public static final double ARGMENT_NULL = -1D;

	private View mRootView;
	private FrameLayout mMapHolder;
	private ProgressDialog mConnectingDialog;

	private GoogleMap mGoogleMap;
	private LocationClient mLocationClient;

	private String mMarkerTitle = "Point";
	private LatLng mLastLatLng;

	private MemoViewerActivity.OnLocationChangedListener mOnLocationChangedListener;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) {
				parent.removeView(mRootView);
			}
		}

		try {
			if (mRootView == null) {
				mRootView = inflater.inflate(R.layout.fragment_googlemap, container, false);
			}

			if (mMapHolder == null) {
				mMapHolder = (FrameLayout) mRootView.findViewById(R.id.mapHolder);
			}

			if (mGoogleMap == null) {
				mGoogleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();

				mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
					@Override
					public void onMapLongClick(LatLng latLng) {
						setMarker(latLng);
					}
				});

				mGoogleMap.setMyLocationEnabled(true);

				mGoogleMap.setInfoWindowAdapter(new CustomInfoAdapter(getActivity()));
				mGoogleMap.setOnInfoWindowClickListener(new OnNavigateClickListener(getActivity()));

				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(35, 135), 4);
				mGoogleMap.moveCamera(cameraUpdate);

				double[] latLngDouble = getArguments().getDoubleArray(LAT_LNG_ARGMENT);
				if (latLngDouble[0] != ARGMENT_NULL && latLngDouble[1] != ARGMENT_NULL) {
					LatLng latLng = new LatLng(latLngDouble[0], latLngDouble[1]);
					setlatLng(latLng);
				}
			}

			mLocationClient = new LocationClient(getActivity(), this, null);
			return mRootView;
		} catch (InflateException e) {
			e.printStackTrace();
		}
		return new View(getActivity());
	}

	public void setMarkerTitle(String markerTitle) {
		if (markerTitle.equals("")) {
			this.mMarkerTitle = "point";
		} else {
			this.mMarkerTitle = markerTitle;
		}

		if (mLastLatLng != null) {
			setMarker(mLastLatLng);
		}
	}

	public void setMarker(LatLng position) {

		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}

		mGoogleMap.clear();
		mGoogleMap.addMarker(new MarkerOptions().position(position).title(mMarkerTitle));
		mLastLatLng = position;

		if (mOnLocationChangedListener != null) {
			mOnLocationChangedListener.onChange(position.latitude, position.longitude);
		}

		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 16);
		mGoogleMap.animateCamera(cameraUpdate);
	}

	@Override
	public void onConnected(Bundle bundle) {
		LocationRequest locationRequest = new LocationRequest();
		locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		mLocationClient.requestLocationUpdates(locationRequest, this);

		mConnectingDialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);
		mConnectingDialog.setMessage("現在地を取得中");
		mConnectingDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "キャンセル", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mLocationClient.disconnect();
			}
		});
		mConnectingDialog.setCancelable(false);
		mConnectingDialog.show();
	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onLocationChanged(Location location) {
		mConnectingDialog.dismiss();

		LatLng nowPosition = new LatLng(location.getLatitude(), location.getLongitude());
		setMarker(nowPosition);
	}

	private class OnNavigateClickListener implements GoogleMap.OnInfoWindowClickListener {
		private Context context;

		private OnNavigateClickListener(Context context) {
			this.context = context;
		}

		@Override
		public void onInfoWindowClick(final Marker marker) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setTitle("確認");
			alertDialogBuilder.setMessage("ナビしますか?");
			alertDialogBuilder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Uri uri = Uri.parse("geo:" + marker.getPosition().latitude + "," + marker.getPosition().longitude
							+ "?q=" + marker.getPosition().latitude + "," + marker.getPosition().longitude);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);

					intent.setData(uri);
					context.startActivity(intent);
				}
			});
			alertDialogBuilder.show();
		}
	}

	public void animationCancel() {
		mGoogleMap.stopAnimation();
	}

	public LatLng getLastLatLng() {
		return mLastLatLng;
	}

	public void setlatLng(LatLng latLng) {
		if (mGoogleMap != null) {
			setMarker(latLng);
		}
	}
}