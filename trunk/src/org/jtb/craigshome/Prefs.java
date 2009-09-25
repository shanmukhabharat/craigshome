package org.jtb.craigshome;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Prefs {
	private Context context = null;

	public Prefs(Context context) {
		this.context = context;
	}

	private String getString(String key, String def) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String s = prefs.getString(key, def);
		return s;
	}

	private int getInt(String key, int def) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		int i = Integer.parseInt(prefs.getString(key, Integer.toString(def)));
		return i;
	}

	private float getFloat(String key, float def) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		float f = Float.parseFloat(prefs.getString(key, Float.toString(def)));
		return f;
	}

	public long getLong(String key, long def) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		long l = Long.parseLong(prefs.getString(key, Long.toString(def)));
		return l;
	}

	private void setString(String key, String val) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor e = prefs.edit();
		e.putString(key, val);
		e.commit();
	}

	private void setBoolean(String key, boolean val) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor e = prefs.edit();
		e.putBoolean(key, val);
		e.commit();
	}

	private void setInt(String key, int val) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor e = prefs.edit();
		e.putString(key, Integer.toString(val));
		e.commit();
	}

	private void setLong(String key, long val) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor e = prefs.edit();
		e.putString(key, Long.toString(val));
		e.commit();
	}

	private boolean getBoolean(String key, boolean def) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean b = prefs.getBoolean(key, def);
		return b;
	}
	
	public int getDistance() {
		return getInt("distance", 5);
	}
	
	public void setDistance(int distance) {
		setInt("distance", distance);
	}
}
