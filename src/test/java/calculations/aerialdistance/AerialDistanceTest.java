package calculations.aerialdistance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AerialDistanceTest {

    @Test
    void testCalculateDistance_samePoint() {
        double x1 = 40.712776;
        double y1 = -74.005974;
        double x2 = 40.712776;
        double y2 = -74.005974;

        double distance = AerialDistance.calculateDistance(x1, y1, x2, y2);

        Assertions.assertEquals(0, distance, 0.001);
    }

    @Test
    void testCalculateDistance_differentPoints() {
        double x1 = 40.712776;
        double y1 = -74.005974;
        double x2 = 34.052235;
        double y2 = -118.243683;

        double distance = AerialDistance.calculateDistance(x1, y1, x2, y2);

        Assertions.assertEquals(3935.745595241378, distance, 0.001);
    }

    @Test
    void testCalculateDistance_oppositePoints() {
        double x1 = 0;
        double y1 = 0;
        double x2 = 0;
        double y2 = 180;

        double distance = AerialDistance.calculateDistance(x1, y1, x2, y2);

        Assertions.assertEquals(20015.086, distance, 0.001);
    }

    @Test
    void testCalculateDistance_polesToEquator() {
        double x1 = 90;
        double y1 = 0;
        double x2 = 0;
        double y2 = 0;

        double distance = AerialDistance.calculateDistance(x1, y1, x2, y2);

        Assertions.assertEquals(10007.543, distance, 0.001);
    }
}