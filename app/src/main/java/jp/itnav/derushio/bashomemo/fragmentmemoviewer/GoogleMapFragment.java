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
	private View rootView;
	private FrameLayout mapHolder;
	private ProgressDialog connectingDialog;

	private GoogleMap googleMap;
	private LocationClient locationClient;

	private String markerTitle = "Point";
	public LatLng lastLatLng;

	private MemoViewerActivity.OnLocationChangedListener onLocationChangedListener;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (rootView != null) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent != null) {
				parent.removeView(rootView);
			}
		}

		try {
			if (rootView == null) {
				rootView = inflater.inflate(R.layout.fragment_googlemap, container, false);
			}

			if (mapHolder == null) {
				mapHolder = (FrameLayout) rootView.findViewById(R.id.mapHolder);
			}

			if (googleMap == null) {
				googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();

				googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
					@Override
					public void onMapLongClick(LatLng latLng) {
						setMarker(latLng);
					}
				});

				googleMap.setMyLocationEnabled(true);

				googleMap.setInfoWindowAdapter(new CustomInfoAdapter(getActivity()));
				googleMap.setOnInfoWindowClickListener(new OnNavigateClickListener(getActivity()));

				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(35, 135), 4);
				googleMap.moveCamera(cameraUpdate);

				if(lastLatLng != null){
					setMarker(lastLatLng);
				}
			}

			locationClient = new LocationClient(getActivity(), this, null);
			return rootView;
		} catch (InflateException e) {
			e.printStackTrace();
		}
		return new View(getActivity());
	}

	public void setMarkerTitle(String markerTitle) {
		if (markerTitle.equals("")) {
			this.markerTitle = "point";
		} else {
			this.markerTitle = markerTitle;
		}

		if (lastLatLng != null) {
			setMarker(lastLatLng);
		}
	}

	public void setMarker(LatLng position) {

		if (locationClient != null) {
			locationClient.disconnect();
		}

		googleMap.clear();
		googleMap.addMarker(new MarkerOptions().position(position).title(markerTitle));
		lastLatLng = position;

		if (onLocationChangedListener != null) {
			onLocationChangedListener.onChange(position.latitude, position.longitude);
		}

		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 16);
		googleMap.animateCamera(cameraUpdate);
	}

	public LatLng getLastLatLng() {
		return this.lastLatLng;
	}

	@Override
	public void onConnected(Bundle bundle) {
		LocationRequest locationRequest = new LocationRequest();
		locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		locationClient.requestLocationUpdates(locationRequest, this);

		connectingDialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);
		connectingDialog.setMessage("現在地を取得中");
		connectingDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "キャンセル", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				locationClient.disconnect();
			}
		});
		connectingDialog.setCancelable(false);
		connectingDialog.show();
	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onLocationChanged(Location location) {
		connectingDialog.dismiss();

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



	public void animationCancel(){
		googleMap.stopAnimation();
	}
}