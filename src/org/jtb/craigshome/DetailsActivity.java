package org.jtb.craigshome;

import org.jtb.jrentrent.Listing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DetailsActivity extends Activity {
	private static final int CL_MENU = 0;
	
	private Listing mListing = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);

		mListing = savedInstanceState != null ? (Listing) savedInstanceState
				.get("org.jtb.craigshome.listing") : null;
		if (mListing == null) {
			Bundle extras = getIntent().getExtras();
			mListing = extras != null ? (Listing) extras
					.get("org.jtb.craigshome.listing") : null;
		}

		TextView headerTv = (TextView) findViewById(R.id.header);
		headerTv.setText(mListing.getHeader());

		TextView brTv = (TextView) findViewById(R.id.bedrooms);
		int br = mListing.getBedrooms();
		if (br == 0) {
			brTv.setText("?");
		} else {
			brTv.setText(Integer.toString(br));
		}

		TextView baTv = (TextView) findViewById(R.id.bathrooms);
		int ba = mListing.getBedrooms();
		if (ba == 0) {
			baTv.setText("?");
		} else {
			baTv.setText(Integer.toString(ba));
		}

		TextView addrTv = (TextView) findViewById(R.id.address);
		String addr = mListing.getAddress();
		if (addr == null || addr.length() == 0) {
			addrTv.setText("?");
		} else {
			addrTv.setText(addr);
		}

		TextView cityTv = (TextView) findViewById(R.id.city);
		String city = mListing.getCity();
		if (addr.contains(city)) {
			cityTv.setVisibility(View.GONE);
		} else if (city == null || city.length() == 0) {
			cityTv.setText("?");
		} else {
			cityTv.setText(city);
		}

		TextView zipTv = (TextView) findViewById(R.id.zip);
		String zip = mListing.getZip();
		if (zip == null || zip.length() == 0) {
			zipTv.setVisibility(View.GONE);
		} else {
			zipTv.setText(city);
		}

		TextView emailTv = (TextView) findViewById(R.id.contact);
		String email = mListing.getEmail();
		if (email == null || email.length() == 0) {
			emailTv.setText("?");
		} else {
			emailTv.setText(email);
		}

		Gallery gV = (Gallery) findViewById(R.id.gallery);
		gV.setAdapter(new ImageAdapter(this, mListing));
		gV.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(mListing
						.getImageUrls().get(position)));
				startActivity(i);
			}
		});

		TextView noPicsTv = (TextView) findViewById(R.id.no_pictures);
		if (mListing.getImageUrls().size() == 0) {
			noPicsTv.setVisibility(View.VISIBLE);
		} else {
			noPicsTv.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, CL_MENU, 0, R.string.cl_menu).setIcon(
				R.drawable.cl);
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CL_MENU:
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(mListing.getUrl()));
			startActivity(i);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	
}
