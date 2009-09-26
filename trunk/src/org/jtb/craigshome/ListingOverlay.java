package org.jtb.craigshome;

import java.util.ArrayList;
import java.util.List;

import org.jtb.jrentrent.Listing;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class ListingOverlay extends ItemizedOverlay {
	private static final int TEXTSIZE = 12;
	private static final int TEXTLINES = 3;
	private static final int DRAW_OFFSET_Y = -90;
	private static final int PADDING = 2;
	private static final int TEXTPADDING = 2;

	private List<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Listing listing;
	private boolean tapped = false;
	private MapView mMapView;
	private Context mContext;

	public ListingOverlay(Drawable defaultMarker, Context context,
			MapView mapView, Listing listing) {
		super(boundCenterBottom(defaultMarker));
		this.mMapView = mapView;
		this.listing = listing;
		this.mContext = context;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	@Override
	protected boolean onTap(int i) {
		Log.d(getClass().getSimpleName(), "listing: " + listing);
		Log.d(getClass().getSimpleName(), "url: " + listing.getUrl());

		boolean alreadyTapped = tapped;
		tapped = !tapped;

		if (tapped) {
			Projection projection = mMapView.getProjection();
			GeoPoint gp = listing.getLocation().getGeoPoint();
			mMapView.getController().animateTo(gp);
		}

		if (alreadyTapped) {
			Intent intent = new Intent(mContext, DetailsActivity.class);
			intent.putExtra("org.jtb.craigshome.listing", listing);
			mContext.startActivity(intent);
		}

		return (true);
	}

	private static String truncate(String s, int l) {
		if (s.length() > l + 3) {
			s = s.substring(0, 30) + "...";
		}

		return s;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		Projection projection = mapView.getProjection();
		Point point = new Point();
		projection.toPixels(listing.getLocation().getGeoPoint(), point);

		if (shadow) {
			super.draw(canvas, mapView, false);
			if (listing.getImageUrls().size() > 0) {
				Bitmap b = BitmapFactory.decodeResource(
						mContext.getResources(), R.drawable.camera);
				canvas.drawBitmap(b, point.x - 15, point.y - 25, new Paint());
			}
			return;
		}
		if (!tapped) {
			return;
		}

		Paint bgOutlinePaint = new Paint();
		Paint bgGlowPaint = new Paint();
		Paint bgPaint = new Paint();

		bgPaint.setColor(Color.WHITE);
		bgPaint.setAntiAlias(true);

		bgOutlinePaint.setColor(Color.parseColor("#0033ff"));
		// bgOutlinePaint.setStyle(Paint.Style.STROKE);
		// bgOutlinePaint.setStrokeWidth(1);
		bgOutlinePaint.setAlpha(128);
		bgOutlinePaint.setAntiAlias(true);

		bgGlowPaint.setColor(Color.parseColor("#ccccff"));
		// bgGlowPaint.setStyle(Paint.Style.STROKE);
		// bgGlowPaint.setStrokeWidth(2);
		bgGlowPaint.setAlpha(128);
		bgGlowPaint.setAntiAlias(true);

		Paint textPaint = new Paint();
		Paint boldTextPaint = new Paint();

		textPaint.setAntiAlias(true);
		textPaint.setColor(Color.parseColor("#0033ff"));
		textPaint.setTextSize(TEXTSIZE);
		textPaint.setFakeBoldText(false);

		boldTextPaint.setAntiAlias(true);
		boldTextPaint.setColor(Color.parseColor("#0033ff"));
		boldTextPaint.setTextSize(TEXTSIZE);
		boldTextPaint.setFakeBoldText(true);
		boldTextPaint.setUnderlineText(true);

		String s1 = truncate(listing.getAddress(), 30);
		String s2 = truncate(listing.getHeader(), 30);
		int beds = listing.getBedrooms();
		int baths = listing.getBathrooms();
		String s3 = "Beds: " + ((beds == 0) ? "?" : beds) + ", Baths: "
				+ ((baths == 0) ? "?" : baths);

		int labelWidth = (int) (1 + Math.max(textPaint.measureText(s1),
				textPaint.measureText(s2)));

		RectF rect = new RectF();
		int rx1 = point.x - labelWidth / 2;
		int ry1 = point.y + DRAW_OFFSET_Y;
		int rx2 = rx1 + labelWidth + PADDING * 2;
		int ry2 = ry1 + TEXTSIZE * TEXTLINES + PADDING * 2 + TEXTPADDING;
		rect.set(rx1, ry1, rx2, ry2);

		RectF rectOutline = new RectF();
		int rox1 = rx1 - 2;
		int roy1 = ry1 - 2;
		int rox2 = rx2 + 2;
		int roy2 = ry2 + 2;
		rectOutline.set(rox1, roy1, rox2, roy2);

		RectF rectGlow = new RectF();
		int rgx1 = rox1 - 2;
		int rgy1 = roy1 - 2;
		int rgx2 = rox2 + 2;
		int rgy2 = roy2 + 2;
		rectGlow.set(rgx1, rgy1, rgx2, rgy2);

		int t1x = rx1 + PADDING;
		int t1y = ry1 + TEXTSIZE + PADDING;

		int t2x = t1x;
		int t2y = t1y + TEXTSIZE;

		int t3x = t2x;
		int t3y = t2y + TEXTSIZE;

		RectF rectCall = new RectF();
		int rcx1 = point.x - 2;
		int rcy1 = point.y - 50;
		int rcx2 = point.x + 2;
		int rcy2 = point.y - 30;
		rectCall.set(rcx1, rcy1, rcx2, rcy2);

		RectF rectCallOutline = new RectF();
		int rcox1 = rcx1 - 2;
		int rcoy1 = rcy1 - 2;
		int rcox2 = rcx2 + 2;
		int rcoy2 = rcy2 + 2;
		rectCallOutline.set(rcox1, rcoy1, rcox2, rcoy2);

		RectF rectCallGlow = new RectF();
		int rcgx1 = rcox1 - 2;
		int rcgy1 = rcoy1 - 2;
		int rcgx2 = rcox2 + 2;
		int rcgy2 = rcoy2 + 2;
		rectCallGlow.set(rcgx1, rcgy1, rcgx2, rcgy2);

		canvas.drawRoundRect(rectCallGlow, 2, 2, bgGlowPaint);
		canvas.drawRoundRect(rectCallOutline, 2, 2, bgOutlinePaint);

		canvas.drawRoundRect(rectGlow, 2, 2, bgGlowPaint);
		canvas.drawRoundRect(rectOutline, 2, 2, bgOutlinePaint);
		canvas.drawRoundRect(rect, 2, 2, bgPaint);

		canvas.drawRoundRect(rectCall, 2, 2, bgPaint);

		canvas.drawText(s1, t1x, t1y, boldTextPaint);
		canvas.drawText(s2, t2x, t2y, textPaint);
		canvas.drawText(s3, t3x, t3y, textPaint);
	}
}
