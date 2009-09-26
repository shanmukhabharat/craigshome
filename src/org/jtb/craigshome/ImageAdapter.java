package org.jtb.craigshome;

import java.net.HttpURLConnection;
import java.net.URL;

import org.jtb.jrentrent.Listing;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private Listing mListing;
    int mGalleryItemBackground;
    
    public ImageAdapter(Context c, Listing listing) {
        mContext = c;
        mListing = listing;
        
        TypedArray a = mContext.obtainStyledAttributes(R.styleable.default_gallery);
             mGalleryItemBackground = a.getResourceId(
               R.styleable.default_gallery_android_galleryItemBackground, 0);
             a.recycle();        
    }

    public int getCount() {
        return mListing.getImageUrls().size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView i = new ImageView(mContext);

        setImageViewBitmap(i, mListing.getImageUrls().get(position));
        i.setLayoutParams(new Gallery.LayoutParams(150, 100));
        i.setScaleType(ImageView.ScaleType.FIT_XY);
        i.setBackgroundResource(mGalleryItemBackground);       
        
        return i;
    }
    
    private void setImageViewBitmap(ImageView iv, String url) {
		try {
			URL u = new URL(url);
	        HttpURLConnection uc = (HttpURLConnection) u.openConnection();
	        uc.setReadTimeout(30 * 1000); // 30 seconds

	        if (uc.getResponseCode() != 200) {
				Log.e(getClass().getSimpleName(), "could not read image, response code: " + uc.getResponseCode());
	            return;
	        }

	        Bitmap bm = BitmapFactory.decodeStream(uc.getInputStream());
	        uc.disconnect();
	        iv.setImageBitmap(bm);
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "could not read image", e);
		}
        
    }
    
}
