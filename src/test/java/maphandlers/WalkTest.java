package maphandlers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WalkTest {

    @Test
    void testFindWalkTimeWithDoubleCoordinates() {
        String lat1String = "50.8503";
        String lon1String = "4.3517";
        Double lat2 = 50.8500;
        Double lon2 = 4.3520;

        int walkTime = Walk.findWalkTime(lat1String, lon1String, lat2, lon2);

        assertTrue(walkTime > 0, "Walk time should be greater than 0");
    }

    @Test
    void testFindWalkTimeWithStringCoordinates() {
        String lat1String = "50.8503";
        String lon1String = "4.3517";
        String lat2String = "50.8500";
        String lon2String = "4.3520";

        int walkTime = Walk.findWalkTime(lat1String, lon1String, lat2String, lon2String);

        assertTrue(walkTime > 0, "Walk time should be greater than 0");
    }

    @Test
    void testFindWalkTimeWithSameCoordinates() {
        String lat1String = "50.8503";
        String lon1String = "4.3517";
        String lat2String = "50.8503";
        String lon2String = "4.3517";

        int walkTime = Walk.findWalkTime(lat1String, lon1String, lat2String, lon2String);

        assertEquals(0, walkTime, "Walk time should be 0 for same coordinates");
    }

    @Test
    void testFindWalkTimeWithLongDistance() {
        String lat1String = "50.8503";
        String lon1String = "4.3517";
        String lat2String = "52.3676";
        String lon2String = "4.9041";

        int walkTime = Walk.findWalkTime(lat1String, lon1String, lat2String, lon2String);

        assertTrue(walkTime > 0, "Walk time should be greater than 0 for long distance");
    }
}
