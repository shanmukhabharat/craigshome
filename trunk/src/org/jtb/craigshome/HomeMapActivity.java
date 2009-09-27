package org.jtb.craigshome;

import java.io.IOException;
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
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class HomeMapActivity extends MapActivity {
	static final int LOADING_DIALOG = 0;
	static final int DISTANCE_DIALOG = 1;
	static final int TYPE_DIALOG = 2;
	static final int HELP_DIALOG = 3;
	static final int LOAD_ERROR_DIALOG = 4;
	static final int NO_LISTINGS_DIALOG = 5;
	static final int ZIP_DIALOG = 6;
	static final int GEOCODE_ERROR_DIALOG = 7;
	static final int LOCATION_ERROR_DIALOG = 8;

	static final int LOADING_DIALOG_SHOW_WHAT = 0;
	static final int LOADING_DIALOG_DISMISS_WHAT = 1;
	static final int LOAD_ERROR_DIALOG_SHOW_WHAT = 2;
	static final int NO_LISTINGS_DIALOG_SHOW_WHAT = 3;
	static final int UPDATE_WHAT = 4;

	private static final int MYLOCATION_MENU = 0;
	private static final int ZIP_MENU = 1;
	private static final int DISTANCE_MENU = 2;
	private static final int TYPE_MENU = 3;
	private static final int HELP_MENU = 4;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD_ERROR_DIALOG_SHOW_WHAT:
				showDialog(LOAD_ERROR_DIALOG);
				break;
			case NO_LISTINGS_DIALOG_SHOW_WHAT:
				showDialog(NO_LISTINGS_DIALOG);
				break;
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
	private AlertDialog mLoadErrorDialog;
	private AlertDialog mNoListingsDialog;
	private AlertDialog mZipDialog;
	private AlertDialog mGeocodeErrorDialog;
	private AlertDialog mLocationErrorDialog;

	private org.jtb.jrentrent.Location mLocation = new org.jtb.jrentrent.Location();
	private MapView mMapView;
	private Request mRequest;
	private Response mResponse;
	private HomeMapActivity mHomeMapActivity;

	private boolean setLocation() {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String name = lm.getBestProvider(new Criteria(), true);
		if (name == null) {
			Log.w(getClass().getSimpleName(), "no location provider returned");
			showDialog(LOCATION_ERROR_DIALOG);
			return false;
		}

		// LocationProvider lp = lm.getProvider(name);
		Location l = lm.getLastKnownLocation(name);

		mLocation.setLatitude(l.getLatitude());
		mLocation.setLongitude(l.getLongitude());

		mMapView.getController().animateTo(mLocation.getGeoPoint());
		return true;
	}

	private boolean setLocation(String addr) {
		Geocoder gc = new Geocoder(this);
		List<Address> addrs;
		try {
			addrs = gc.getFromLocationName(addr, 1);
			if (addrs.size() == 0) {
				Log.w(getClass().getSimpleName(), "could not geocode address: "
						+ addr);
				showDialog(GEOCODE_ERROR_DIALOG);
				return false;
			}
			Address a = addrs.get(0);
			setLocation(a.getLatitude(), a.getLongitude());
			mMapView.getController().animateTo(mLocation.getGeoPoint());
			return true;
		} catch (IOException e) {
			Log.w(getClass().getSimpleName(), "could not geocode address: "
					+ addr);
			showDialog(GEOCODE_ERROR_DIALOG);
			return false;
		}
	}

	public boolean setLocation(GeoPoint gp) {
		mLocation.setLatitudeE6(gp.getLatitudeE6());
		mLocation.setLongitudeE6(gp.getLongitudeE6());
		mMapView.getController().animateTo(mLocation.getGeoPoint());
		return true;
	}

	public boolean setLocation(double latitude, double longitude) {
		mLocation.setLatitude(latitude);
		mLocation.setLongitude(longitude);
		mMapView.getController().animateTo(mLocation.getGeoPoint());
		return true;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		mHomeMapActivity = this;
		mMapView = (MapView) findViewById(R.id.map_view);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setOnLongClickListener(new View.OnLongClickListener() {

			public boolean onLongClick(View v) {
				GeoPoint gp = mMapView.getMapCenter();
				setLocation(gp);
				load();
				return true;
			}
		});

		if (setLocation()) {
			load();
		}
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
				mRequest.setType(p.getType());

				Log.d(getClass().getSimpleName(), "request url: " + mRequest);
				org.jtb.jrentrent.Handler handler = new org.jtb.jrentrent.Handler();

				try {
					mResponse = handler.getResponse(mRequest);
					if (!mResponse.getStatus().isSuccess()) {
						// TODO
						Log.e(getClass().getSimpleName(), "error loading: "
								+ mResponse.getStatus().getMessage());
						mHandler.sendMessage(Message.obtain(mHandler,
								NO_LISTINGS_DIALOG_SHOW_WHAT));
					} else {
						mHandler.sendMessage(Message.obtain(mHandler,
								UPDATE_WHAT));
					}
				} catch (Exception e) {
					Log.e(getClass().getSimpleName(), "error loading", e);
					mHandler.sendMessage(Message.obtain(mHandler,
							LOAD_ERROR_DIALOG_SHOW_WHAT));
				} finally {
					mHandler.sendMessage(Message.obtain(mHandler,
							LOADING_DIALOG_DISMISS_WHAT));
				}

			}
		}).start();
	}

	private void update() {
		setZoom(mRequest);
		List<Overlay> mapOverlays = mMapView.getOverlays();
		mapOverlays.clear();

		Drawable drawable = getResources().getDrawable(R.drawable.home_16);
		for (Listing listing : mResponse.getListings()) {
			ListingOverlay itemizedOverlay = new ListingOverlay(drawable, this,
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
			builder.setItems(new String[] { "2.5 miles", "5 miles", "10 miles",
					"25 miles" }, new DialogInterface.OnClickListener() {
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
		case TYPE_DIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setItems(new String[] { "Apartments / Houses",
					"Rooms / Shared" }, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Prefs p = new Prefs(getBaseContext());
					switch (which) {
					case 0:
						p.setType(Type.RENTAL_APARTMENTS_HOUSES);
						break;
					case 1:
						p.setType(Type.RENTAL_ROOMS);
						break;
					}
					mHomeMapActivity.load();
				}
			});
			mDistanceDialog = builder.create();
			return mDistanceDialog;
		case ZIP_DIALOG:
			LayoutInflater factory = LayoutInflater.from(this);
			final View zipView = factory.inflate(R.layout.zip_dialog, null);
			final EditText zipEdit = (EditText) zipView.findViewById(R.id.zip);
			mZipDialog = new AlertDialog.Builder(this).setTitle("Go to Zip")
					.setView(zipView).setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									String zip = zipEdit.getText().toString();
									if (setLocation(zip)) {
										load();
									}
									dismissDialog(ZIP_DIALOG);
								}
							}).setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dismissDialog(ZIP_DIALOG);
								}
							}).create();
			return mZipDialog;
		case LOAD_ERROR_DIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Error");
			builder
					.setMessage("There was a problem loading listings. Ensure that you have a network connection.");
			builder.setNeutralButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dismissDialog(LOAD_ERROR_DIALOG);
						}
					});
			mLoadErrorDialog = builder.create();
			return mLoadErrorDialog;
		case LOCATION_ERROR_DIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Warning");
			builder
					.setMessage("Could not determine your location. Enable network or GPS location services, or use Menu>Go to Zip.");
			builder.setNeutralButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dismissDialog(LOCATION_ERROR_DIALOG);
						}
					});
			mLocationErrorDialog = builder.create();
			return mLocationErrorDialog;
		case GEOCODE_ERROR_DIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Error");
			builder.setMessage("Could not find location for address / zip.");
			builder.setNeutralButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dismissDialog(GEOCODE_ERROR_DIALOG);
						}
					});
			mGeocodeErrorDialog = builder.create();
			return mGeocodeErrorDialog;
		case NO_LISTINGS_DIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Warning");
			builder
					.setMessage("No listings found within the selected bounds. Try moving to a more populous area, or widening the search distance (Menu>Distance).");
			builder.setNeutralButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dismissDialog(NO_LISTINGS_DIALOG);
						}
					});
			mNoListingsDialog = builder.create();
			return mNoListingsDialog;
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
		menu.add(0, ZIP_MENU, 1, R.string.zip_menu).setIcon(R.drawable.zip);
		menu.add(0, DISTANCE_MENU, 2, R.string.distance_menu).setIcon(
				R.drawable.distance);
		menu.add(0, TYPE_MENU, 3, R.string.type_menu).setIcon(R.drawable.type);
		menu.add(0, HELP_MENU, 4, R.string.help_menu).setIcon(R.drawable.help);
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MYLOCATION_MENU:
			if (setLocation()) {
				load();
			}
			return true;
		case DISTANCE_MENU:
			showDialog(DISTANCE_DIALOG);
			return true;
		case ZIP_MENU:
			showDialog(ZIP_DIALOG);
			return true;
		case TYPE_MENU:
			showDialog(TYPE_DIALOG);
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