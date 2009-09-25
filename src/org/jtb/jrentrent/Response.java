package org.jtb.jrentrent;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Response {

    private Status status;
    private List<Listing> listings;

    public Response(String s) throws JSONException {
        JSONObject jo;
        jo = new JSONObject(s);
        //System.out.println(jo);

        status = new Status(jo);
        if (status.isSuccess()) {
            listings = new ArrayList<Listing>();
            JSONObject data = jo.getJSONObject("Data");
            JSONArray ids = data.names();
            for (int i = 0; i < ids.length(); i++) {
                String id = ids.getString(i);
                JSONObject lo = data.getJSONObject(id);
                Listing listing = new Listing(lo);
                listings.add(listing);
            }
        }
    }

    public Status getStatus() {
        return status;
    }

    public List<Listing> getListings() {
        return listings;
    }
}
