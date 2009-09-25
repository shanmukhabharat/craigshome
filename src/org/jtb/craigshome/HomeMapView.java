package org.jtb.craigshome;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

public class HomeMapView extends MapView {
	private long lastTouch = 0;
	private boolean center = false;
	private float xClick = 0;
	private float yClick = 0;
	
	public HomeMapView(Context context, String apiKey) {
		super(context, apiKey);
	}

	public HomeMapView(Context context, AttributeSet set) {
		super(context, set);
	}

	public  boolean onTouchEvent(android.view.MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			center = true;
		} else if (ev.getAction() == MotionEvent.ACTION_UP) {
			if (center && lastTouch + 1000 > System.currentTimeMillis() && ev.getX() == xClick && ev.getY() == yClick) {
				Projection projection = getProjection();
				GeoPoint gp = projection.fromPixels(((int) ev.getX()),
						((int) ev.getY()));

				HomeMapActivity hma = (HomeMapActivity) getContext();
				hma.setLocation(gp);
				hma.load();
			}
			xClick = ev.getX();
			yClick = ev.getY();			
			lastTouch = System.currentTimeMillis();
			center = false;
		} else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			xClick = yClick = 0;
			center = false;
		}

		return super.onTouchEvent(ev);
	}
}
