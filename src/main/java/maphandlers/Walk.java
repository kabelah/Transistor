package maphandlers;

import static calculations.aerialdistance.AerialDistance.calculateDistance;

public class Walk {
    public static int findWalkTime(String lat1String, String lon1String, Double lat2, Double lon2) {
        double lat1 = Double.parseDouble(lat1String);
        double lon1 = Double.parseDouble(lon1String);
        double distance = calculateDistance(lat1, lon1, lat2, lon2);

        return (int) Math.ceil(distance / 4.82 * 60);
    }

    public static int findWalkTime(String lat1String, String lon1String, String lat2String, String lon2String) {
        double lat1 = Double.parseDouble(lat1String);
        double lon1 = Double.parseDouble(lon1String);
        double lat2 = Double.parseDouble(lat2String);
        double lon2 = Double.parseDouble(lon2String);
        double distance = calculateDistance(lat1, lon1, lat2, lon2);

        return (int) Math.ceil(distance / 4.82 * 60);
    }
}
