package calculations.aerialdistance;

/**
 * This class is responsible for calculating the aerial distance between two points using the Haversine formula.
 */
public class AerialDistance {

    /**
     * Calculates the aerial distance between two points using the Haversine formula.
     *
     * @param x1 The latitude of the first point.
     * @param y1 The longitude of the first point.
     * @param x2 The latitude of the second point.
     * @param y2 The longitude of the second point.
     * @return The aerial distance between the two points in km.
     */
    public static double calculateDistance(double x1, double y1, double x2, double y2) {
        // Formula from <<https://www.movable-type.co.uk/scripts/latlong.html>>, expressed in km.

        // Radius of the Earth in km
        double R = 6371;
        final double PI = Math.PI;
        // Convert the coordinates from degrees to radians
        double alpha1 = x1 * PI / 180;
        double alpha2 = x2 * PI / 180;
        // Calculate the change in coordinates
        double deltaAlpha = (x2 - x1) * PI / 180;
        double deltaBeta = (y2 - y1) * PI / 180;
        // Calculate the square of half the chord length between the points
        double a = Math.sin(deltaAlpha / 2) * Math.sin(deltaAlpha / 2) + Math.cos(alpha1) * Math.cos(alpha2) * Math.sin(deltaBeta / 2) * Math.sin(deltaBeta / 2);
        // Calculate the angular distance in radians
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Return the aerial distance in km
        return R * c;
    }
}
