package datatypes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TripTest {

    @Test
    void testConstructor() {
        int tripId = 1;
        int startId = 10;
        int endId = 20;
        String arrivalTime = "10:30";
        String departureTime = "10:00";

        Trip trip = new Trip(tripId, startId, endId, arrivalTime, departureTime);

        Assertions.assertEquals(tripId, trip.getTrip_id());
        Assertions.assertEquals(startId, trip.getStart_id());
        Assertions.assertEquals(endId, trip.getEnd_id());
        Assertions.assertEquals(arrivalTime, trip.getArrival_time());
        Assertions.assertEquals(departureTime, trip.getDeparture_time());
    }
}
