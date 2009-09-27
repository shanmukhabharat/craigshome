package org.jtb.jrentrent;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Listing implements Serializable {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
	"M/dd/yyyy h:mm:ss");
	private static final DateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat(
	"EEE, MMM d hh:mma");
	private String id;
	private int price;
	private String url;
	private String header;
	private String address;
	private String city;
	private String zip;
	private Location location = new Location();
	private Date date;
	private int bedrooms = -1;
	private int bathrooms = -1;
	private String email;
	private String area;
	private List<String> imageUrls = new ArrayList<String>();

	public Listing(JSONObject lo) throws JSONException {
		id = lo.getString("AdPostingId");
		price = lo.getInt("AdPrice");
		url = lo.getString("AdUrl");
		header = lo.getString("AdHeader");
		address = lo.getString("AdAddress");
		city = lo.getString("AdCity");
		zip = lo.getString("AdZip");
		String[] images = lo.getString("AdImages").split(",");
		for (String i : images) {
			if (i.length() > 0) {
				imageUrls.add("http://images.craigslist.org/" + i);
			}
		}
		location.setLatitude(lo.getDouble("Y"));
		location.setLongitude(lo.getDouble("X"));
		date = parseDate(lo.getString("AdDate"));
		bedrooms = lo.getInt("AdBedroom");
		bathrooms = lo.getInt("AdBathroom");
		email = lo.getString("AdEmail");
		area = lo.getString("AdAreaCategory");
	}

	private static Date parseDate(String d) {
		try {
			Date date = DATE_FORMAT.parse(d);
			return date;
		} catch (ParseException pe) {
			return null;
		}
	}

	@Override
	public String toString() {
		return "{ id=" + id + ", price=" + price + ", url=" + url + ", header="
				+ header + ", address=" + address + ", city=" + city + ", zip="
				+ zip + ", imageUrls=" + imageUrls + ", location=" + location
				+ ", bedrooms=" + bedrooms + ", bathrooms=" + bathrooms
				+ ", date=" + DATE_FORMAT.format(date) + ", email=" + email
				+ ", area=" + area + " }";
	}

	public Location getLocation() {
		return location;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Listing)) {
			return false;
		}
		Listing other = (Listing) o;
		return id.equals(other.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public String getAddress() {
		return address;
	}

	public int getPrice() {
		return price;
	}

	public int getBedrooms() {
		return bedrooms;
	}

	public int getBathrooms() {
		return bathrooms;
	}

	public String getUrl() {
		return url;
	}

	public String getHeader() {
		return header;
	}

	public String getCity() {
		return city;
	}

	public String getZip() {
		return zip;
	}

	public String getEmail() {
		return email;
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}
	
	public String getDisplayDateString() {
		Calendar now = Calendar.getInstance();
		Calendar posted = Calendar.getInstance();
		posted.setTime(date);
		
		int nowDay = now.get(Calendar.DAY_OF_YEAR);
		int postedDay = posted.get(Calendar.DAY_OF_YEAR);
	
		int diff = nowDay - postedDay;
		
		StringBuilder sb = new StringBuilder();
		if (diff == 0) {
			sb.append("Posted today");	
		} else if (diff == 1) {
			sb.append("Posted yesterday");
		} else {
			sb.append("Posted ");
			sb.append(diff);
			sb.append(" days ago");
		}
		sb.append(" (");
		sb.append(DISPLAY_DATE_FORMAT.format(date));
		sb.append(")");
		
		return sb.toString();
	}
}
