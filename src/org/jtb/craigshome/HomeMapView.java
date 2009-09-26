package org.jtb.craigshome;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

public class HomeMapView extends MapView {	
	private static float MARGIN = 50;

	private long down = -1;
	private float x = 0, y = 0;
	
	public HomeMapView(Context context, String apiKey) {
		super(context, apiKey);
	}

	public HomeMapView(Context context, AttributeSet set) {
		super(context, set);
		this.setLongClickable(true);		
	}

	public boolean onTouchEvent(android.view.MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			down = System.currentTimeMillis();	
			x = ev.getRawX(); y = ev.getRawY();
		} else if (ev.getAction() == MotionEvent.ACTION_UP) {
			if (down != -1 && down+1500 < System.currentTimeMillis()) {
				Projection projection = getProjection();
				GeoPoint gp = projection.fromPixels(((int) ev.getX()),
						((int) ev.getY()));

				HomeMapActivity hma = (HomeMapActivity) getContext();
				hma.setLocation(gp);
				hma.load();
			} else {
				down = -1;
			}
		} else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			float x2 = ev.getRawX();
			float y2 = ev.getRawY();
			
			float xDiff = Math.abs(x - x2);
			float yDiff = Math.abs(y - y2);
			
			//Log.d(getClass().getSimpleName(), "x,y=" + x + "," + y);
			//Log.d(getClass().getSimpleName(), "x2,y2=" + x2 + "," + y2);
			//Log.d(getClass().getSimpleName(), "xDiff,yDiff=" + xDiff + "," + yDiff);
			if (xDiff > MARGIN || yDiff > MARGIN) {
				down = -1;
			}
		}

		return super.onTouchEvent(ev);
	}
}
