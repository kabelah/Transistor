package datatypes;

public class Stop {
    private double lat, lon;
    private int stop_id;
    private String stop_name;

    public Stop(double lat, double lon, int stop_id, String stop_name) {
        this.lat = lat;
        this.lon = lon;
        this.stop_id = stop_id;
        this.stop_name = stop_name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getStop_id() {
        return stop_id;
    }

    public void setStop_id(int stop_id) {
        this.stop_id = stop_id;
    }

    public String getStop_name() {
        return stop_name;
    }

    public void setStop_name(String stop_name) {
        this.stop_name = stop_name;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return Integer.toString(getStop_id());
    }
}
