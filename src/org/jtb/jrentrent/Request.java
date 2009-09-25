package org.jtb.jrentrent;

public class Request {
    private static final int MAXRECORDS = 1000;
    private static final double EPSILON = 0.000001;
    private static final double RADIUS_EARTH = 6371.01;
    private static final String ENDPOINT = "http://www.rentrent.org/RENT/Ads.aspx";
    private Location lowerLeft = new Location();
    private Location upperRight = new Location();
    private int bedrooms = -1;
    private int bathrooms = -1;
    private Type type;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ENDPOINT);
        sb.append('?');

        sb.append("xmin=");
        sb.append(getUpperRight().getLongitude());
        sb.append('&');
        sb.append("ymin=");
        sb.append(getUpperRight().getLatitude());
        sb.append('&');
        sb.append("xmax=");
        sb.append(getLowerLeft().getLongitude());
        sb.append('&');
        sb.append("ymax=");
        sb.append(getLowerLeft().getLatitude());

        if (bedrooms != -1) {
            sb.append('&');
            sb.append("bd=");
            sb.append(bedrooms);
        }

        if (bathrooms != -1) {
            sb.append('&');
            sb.append("ba=");
            sb.append(bathrooms);
        }

        sb.append('&');
        sb.append("type=");
        sb.append(type.getCode());

        sb.append('&');
        sb.append("maxrecords=");
        sb.append(MAXRECORDS);

        System.out.println(sb.toString());
        return sb.toString();
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setExtent(Location location, int distance) {
        lowerLeft = Request.getLocation(location, 315, distance);
        upperRight = Request.getLocation(location, 145, distance);

        System.out.println(lowerLeft);
        System.out.println(location);
        System.out.println(upperRight);

    }

    public static Location getLocation(Location center, double bearing, int distance) {
        double rLatitude1 = Math.toRadians(center.getLatitude());
        double rLongitude1 = Math.toRadians(center.getLongitude());
        double rBearing = Math.toRadians(bearing);
        double rDistance = distance / RADIUS_EARTH;


        double rLatitude;
        double rLongitude;

        rLatitude = Math.asin(Math.sin(rLatitude1) * Math.cos(rDistance) + Math.cos(rLatitude1) * Math.sin(rDistance)*Math.cos(rBearing));
        if (Math.cos(rLatitude) == 0 || Math.abs(Math.cos(rLatitude)) < EPSILON) {
            rLongitude = rLongitude1;
        } else {
            rLongitude = ((rLongitude1 - Math.asin(Math.sin(rBearing) * Math.sin(rDistance) / Math.cos(rLatitude)) + Math.PI ) % (2*Math.PI)) - Math.PI;
        }

        Location location = new Location();
        location.setLatitude(Math.toDegrees(rLatitude));
        location.setLongitude(Math.toDegrees(rLongitude));

        return location;
    }

    public Location getLowerLeft() {
        return lowerLeft;
    }

    public Location getUpperRight() {
        return upperRight;
    }
}
