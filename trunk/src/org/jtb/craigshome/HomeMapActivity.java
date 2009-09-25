package org.jtb.craigshome;

import java.util.List;

import org.jtb.jrentrent.Listing;
import org.jtb.jrentrent.Request;
import org.jtb.jrentrent.Response;
import org.jtb.jrentrent.Type;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class HomeMapActivity extends MapActivity {
	static final int LOADING_DIALOG = 0;
	static final int DISTANCE_DIALOG = 1;
	static final int HELP_DIALOG = 2;

	static final int LOADING_DIALOG_SHOW_WHAT = 0;
	static final int LOADING_DIALOG_DISMISS_WHAT = 1;
	static final int UPDATE_WHAT = 2;

	private static final int MYLOCATION_MENU = 0;
	private static final int DISTANCE_MENU = 1;
	private static final int HELP_MENU = 2;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOADING_DIALOG_SHOW_WHAT:
				showDialog(LOADING_DIALOG);
				break;
			case LOADING_DIALOG_DISMISS_WHAT:
				dismissDialog(LOADING_DIALOG);
				break;
			case UPDATE_WHAT:
				update();
				break;
			}
		}
	};

	private ProgressDialog mLoadingDialog;
	private AlertDialog mDistanceDialog;
	private AlertDialog mHelpDialog;

	private org.jtb.jrentrent.Location mLocation = new org.jtb.jrentrent.Location();
	private MapView mMapView;
	private Request mRequest;
	private Response mResponse;
	private HomeMapActivity mHomeMapActivity;
	
	private void setLocation() {
		synchronized (mMapView) {
			LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			String name = lm.getBestProvider(new Criteria(), true);
			if (name == null) {
				// TODO: error dialog an exit (this.finish())?
				Log.e(getClass().getSimpleName(),
						"no best location provider returned");
				mLocation = null;
				return;
			}
			// LocationProvider lp = lm.getProvider(name);
			Location l = lm.getLastKnownLocation(name);

			mLocation.setLatitude(l.getLatitude());
			mLocation.setLongitude(l.getLongitude());

			mMapView.getController().animateTo(mLocation.getGeoPoint());
		}
	}

	public void setLocation(GeoPoint gp) {
		mLocation.setLatitudeE6(gp.getLatitudeE6());
		mLocation.setLongitudeE6(gp.getLongitudeE6());
		mMapView.getController().animateTo(mLocation.getGeoPoint());
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		mHomeMapActivity = this;
		mMapView = (MapView) findViewById(R.id.map_view);
		mMapView.setBuiltInZoomControls(true);

		setLocation();
		Log.d(getClass().getSimpleName(), "mLocation: " + mLocation);

		mMapView.getController().setZoom(16);
		int latitudeE6 = (int) (mLocation.getLatitude() * Math.pow(10, 6));
		int longitudeE6 = (int) (mLocation.getLongitude() * Math.pow(10, 6));

		load();
	}

	private void setZoom(Request request) {
		int latMax = request.getLowerLeft().getLatitudeE6();
		int latMin = request.getUpperRight().getLatitudeE6();
		int lonMax = request.getUpperRight().getLongitudeE6();
		int lonMin = request.getLowerLeft().getLongitudeE6();
		mMapView.getController().zoomToSpan(latMax - latMin, lonMax - lonMin);
	}

	public void load() {
		new Thread(new Runnable() {
			public void run() {
				mHandler.sendMessage(Message.obtain(mHandler,
						LOADING_DIALOG_SHOW_WHAT));

				Prefs p = new Prefs(mHomeMapActivity);
				
				mRequest = new Request();
				mRequest.setExtent(mLocation, p.getDistance());
				mRequest.setType(Type.RENTAL_APARTMENTS_HOUSES);

				Log.d(getClass().getSimpleName(), "request url: " + mRequest);
				org.jtb.jrentrent.Handler handler = new org.jtb.jrentrent.Handler();

				try {
					mResponse = handler.getResponse(mRequest);
					if (mResponse.getStatus().isSuccess()) {
					} else {
						// TODO
						Log.e(getClass().getSimpleName(), "error loading: "
								+ mResponse.getStatus().getMessage());
					}
				} catch (Exception e) {
					Log.e(getClass().getSimpleName(), "error loading", e);
				}

				mHandler.sendMessage(Message.obtain(mHandler,
						LOADING_DIALOG_DISMISS_WHAT));
				mHandler.sendMessage(Message.obtain(mHandler, UPDATE_WHAT));
			}
		}).start();
	}

	private void update() {
		setZoom(mRequest);
		List<Overlay> mapOverlays = mMapView.getOverlays();
		mapOverlays.clear();

		Drawable drawable = getResources().getDrawable(R.drawable.home_16);
		for (Listing listing : mResponse.getListings()) {
			ListingOverlay itemizedOverlay = new ListingOverlay(drawable,
					mMapView, listing);
			OverlayItem overlayitem = new OverlayItem(listing.getLocation()
					.getGeoPoint(), "", "");
			itemizedOverlay.addOverlay(overlayitem);
			mapOverlays.add(itemizedOverlay);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case LOADING_DIALOG:
			mLoadingDialog = new ProgressDialog(this);
			mLoadingDialog.setMessage("Loading, please wait.");
			mLoadingDialog.setIndeterminate(true);
			mLoadingDialog.setCancelable(false);
			return mLoadingDialog;
		case DISTANCE_DIALOG:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setItems(new String[] { "2.5 miles", "5 miles", "10 miles", "25 miles" },
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Prefs p = new Prefs(getBaseContext());
							switch (which) {
							case 0:
								p.setDistance(toKm(2.5f));
								break;
							case 1:
								p.setDistance(toKm(5));
								break;
							case 2:
								p.setDistance(toKm(10));
								break;
							case 3:
								p.setDistance(toKm(25));
								break;
							}
							mHomeMapActivity.load();
						}
					});
			mDistanceDialog = builder.create();
			return mDistanceDialog;
		case HELP_DIALOG:
			builder = new HelpDialog.Builder(this);
			mHelpDialog = builder.create();
			return mHelpDialog;
		}

		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, MYLOCATION_MENU, 0, R.string.mylocation_menu).setIcon(
				R.drawable.mylocation);
		menu.add(0, DISTANCE_MENU, 1, R.string.distance_menu).setIcon(
				R.drawable.distance);
		menu.add(0, HELP_MENU, 2, R.string.help_menu).setIcon(
				R.drawable.help);
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MYLOCATION_MENU:
			setLocation();
			load();
			return true;
		case DISTANCE_MENU:
			showDialog(DISTANCE_DIALOG);
			return true;
		case HELP_MENU:
			showDialog(HELP_DIALOG);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private static int toKm(float miles) {
		return (int) (miles * 1.609344);
	}
}