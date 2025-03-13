package datatypes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StopTest {

    @Test
    void testConstructor() {
        double lat = 12.3456;
        double lon = 98.7654;
        int stopId = 1;
        String stopName = "Main Stop";

        Stop stop = new Stop(lat, lon, stopId, stopName);

        Assertions.assertEquals(lat, stop.getLat());
        Assertions.assertEquals(lon, stop.getLon());
        Assertions.assertEquals(stopId, stop.getStop_id());
        Assertions.assertEquals(stopName, stop.getStop_name());
    }

    @Test
    void testSetLat() {
        Stop stop = new Stop(0, 0, 0, "");
        double newLat = 45.6789;

        stop.setLat(newLat);

        Assertions.assertEquals(newLat, stop.getLat());
    }

    @Test
    void testSetLon() {
        Stop stop = new Stop(0, 0, 0, "");
        double newLon = 23.4567;

        stop.setLon(newLon);

        Assertions.assertEquals(newLon, stop.getLon());
    }

    @Test
    void testSetStopId() {
        Stop stop = new Stop(0, 0, 0, "");
        int newStopId = 2;

        stop.setStop_id(newStopId);

        Assertions.assertEquals(newStopId, stop.getStop_id());
    }

    @Test
    void testSetStopName() {
        Stop stop = new Stop(0, 0, 0, "");
        String newStopName = "Secondary Stop";

        stop.setStop_name(newStopName);

        Assertions.assertEquals(newStopName, stop.getStop_name());
    }
}