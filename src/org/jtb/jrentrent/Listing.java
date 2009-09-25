package org.jtb.jrentrent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

public class Listing {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("M/dd/yyyy h:mm:ss");
    private String id;
    private int price;
    private String url;
    private String header;
    private String address;
    private String city;
    private String zip;
    private String images;
    private Location location = new Location();
    private Date date;
    private int bedrooms = -1;
    private int bathrooms = -1;
    private String email;
    private String area;

    public Listing(JSONObject lo) throws JSONException {
        id = lo.getString("AdPostingId");
        price = lo.getInt("AdPrice");
        url = lo.getString("AdUrl");
        header = lo.getString("AdHeader");
        address = lo.getString("AdAddress");
        city = lo.getString("AdCity");
        zip = lo.getString("AdZip");
        images = lo.getString("AdImages");
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
        return "{ id=" + id + ", price=" + price + ", url=" + url + ", header=" + header + ", address=" + address + ", city=" + city + ", zip=" + zip + ", images=" + images + ", location=" + location + ", bedrooms=" + bedrooms + ", bathrooms=" + bathrooms + ", date=" + DATE_FORMAT.format(date) + ", email=" + email + ", area=" + area + " }";
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
    	Listing other = (Listing)o;
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
}
