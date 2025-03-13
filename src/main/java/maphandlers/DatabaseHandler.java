package maphandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import databasehandling.DatabaseConnector;
import datatypes.Stop;
import datatypes.Trip;

public class DatabaseHandler {
    

    List<Integer> stopIDS = new ArrayList<>();
    HashMap<Integer, List<Trip>> tripsThroughStopID = new HashMap<>();

    // Flag to track if we've shown the database error message
    private boolean dbErrorShown = false;

    public List<Stop> getBusStops(double distance, double[] midpoint, ArrayList<Stop> postalStops) throws SQLException {
        // If we already know the database connection will fail, return an empty list without attempting
        if (!DatabaseConnector.canConnectToDatabase()) {
            if (!dbErrorShown) {
                System.out.println("INFO: Database connection not available - returning empty list of bus stops");
                dbErrorShown = true;
            }
            return new ArrayList<>();
        }

        // for(Stop s : postalStops){
        //     stopIDS.add(s.getStop_id());
        // }

        String sql = "SELECT stop_id, stop_name, stop_lat, stop_lon FROM stops";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();   
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Stop> busStops = new ArrayList<>();
            while (rs.next()) {
                double stop_lat = rs.getDouble("stop_lat");
                double stop_lon = rs.getDouble("stop_lon");
                int stopID = rs.getInt("stop_id");
               if(calculations.aerialdistance.AerialDistance.calculateDistance(midpoint[0], midpoint[1], stop_lat, stop_lon) < distance/2){
                    busStops.add(new Stop(
                            stop_lat,
                            stop_lon,
                            stopID,
                            rs.getString("stop_name")
                    ));

                    stopIDS.add(stopID);
                }
            }
            return busStops;
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to get bus stops from database: " + e.getMessage());
            return new ArrayList<>(); // Return empty list instead of failing
        }
    }

    public ArrayList<Trip> getStopTimes(LocalTime start) throws SQLException {
        if (!DatabaseConnector.canConnectToDatabase()) {
            return new ArrayList<>();
        }
        
        String sql = "SELECT trip_id, stop_sequence, stop_id, arrival_time, departure_time " +
                     "FROM stop_times where arrival_time > ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, start.toString());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                ArrayList<Trip> stopTimes = new ArrayList<>();
                while (rs.next()) {
                    int stopID = rs.getInt("stop_id");
                    if(stopIDS.contains(stopID)){

                        Trip trip = new Trip(
                                rs.getInt("trip_id"),
                                rs.getInt("stop_sequence"),
                                stopID,
                                rs.getString("arrival_time"),
                                rs.getString("departure_time"));

                        stopTimes.add(trip);

                        tripsThroughStopID.computeIfAbsent(stopID, k -> new ArrayList<>()).add(trip);
                    }
                }
                return stopTimes;
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to get stop times from database: " + e.getMessage());
            return new ArrayList<>(); // Return empty list instead of failing
        }
    }

     public List<Integer> findTripIdWithStops(int stopId1, int stopId2, LocalTime start) {
        List<Integer> tripIds = new ArrayList<>();
        String query = "SELECT DISTINCT t1.trip_id " +
                       "FROM stop_times t1 " +
                       "JOIN stop_times t2 ON t1.trip_id = t2.trip_id " +
                       "WHERE t1.stop_id = ? AND t2.stop_id = ? AND t2.stop_sequence > t1.stop_sequence AND t1.arrival_time > ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)){
            
            // Set parameters
            stmt.setInt(1, stopId1);
            stmt.setInt(2, stopId2);
            stmt.setString(3, start.toString());
            
            // Execute query
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int tripId = rs.getInt("trip_id");
                    tripIds.add(tripId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return tripIds;
    }

    public static int getRouteID(int trip_id) {
        int route_id = 0;
        String query = "SELECT route_id " + 
                       "FROM trips " + 
                       "WHERE trip_id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Set parameters
            stmt.setInt(1, trip_id);
            
            // Execute query
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    route_id = rs.getInt("route_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return route_id;
    }


    public static String findRouteShortName(int trip_id) {
        int route_id = getRouteID(trip_id);
        String routeShortName = "";
        String query = "SELECT route_short_name from routes where route_id = ? ";
        try (Connection connection = DatabaseConnector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, route_id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                routeShortName = resultSet.getString("route_short_name");
            }
        } catch (Exception _) {

        }
        return routeShortName;
    }
    
    

  public static double[] calculateMidpoint(double lat1, double lon1, double lat2, double lon2) {
        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Calculate the midpoint coordinates
        double dLon = lon2Rad - lon1Rad;

        double Bx = Math.cos(lat2Rad) * Math.cos(dLon);
        double By = Math.cos(lat2Rad) * Math.sin(dLon);
        
        double lat3 = Math.atan2(Math.sin(lat1Rad) + Math.sin(lat2Rad),
                Math.sqrt((Math.cos(lat1Rad) + Bx) * (Math.cos(lat1Rad) + Bx) + By * By));
        double lon3 = lon1Rad + Math.atan2(By, Math.cos(lat1Rad) + Bx);

        // Convert the midpoint coordinates from radians back to degrees
        double midLat = Math.toDegrees(lat3);
        double midLon = Math.toDegrees(lon3);

        return new double[]{midLat, midLon};
    }

    public HashMap<Integer, List<Trip>> getStopIdTrips(){
        return tripsThroughStopID;
    }

    public double[] getStopCoordinates(int stopId) throws SQLException {
        if (!DatabaseConnector.canConnectToDatabase()) {
            // Return default coordinates for Maastricht center if database is unavailable
            return new double[]{50.8514, 5.6910};
        }
        
        String sql = "SELECT stop_lat, stop_lon FROM stops WHERE stop_id = ?";
    
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, stopId);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double stopLat = rs.getDouble("stop_lat");
                    double stopLon = rs.getDouble("stop_lon");
                    return new double[]{stopLat, stopLon};
                } else {
                    throw new SQLException("No stop found with stop_id: " + stopId);
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to get stop coordinates from database: " + e.getMessage());
            return new double[]{50.8514, 5.6910}; // Return Maastricht coordinates as default
        }
    }

    public static void checkStopsInArea(ArrayList<Stop> stopsNearSecond, ArrayList<Stop> stopsNearFirst, String lat1String, String lon1String, String lat2String, String lon2String, double area) {
        if (!DatabaseConnector.canConnectToDatabase()) {
            return; // Just return without attempting the database query
        }
        
        double lat1 = Double.parseDouble(lat1String);
        double lat2 = Double.parseDouble(lat2String);
        double lon1 = Double.parseDouble(lon1String);
        double lon2 = Double.parseDouble(lon2String);
        String query = "SELECT stop_lat, stop_lon, stop_id, stop_name FROM stops";
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement prepareStmt = conn.prepareStatement(query);
            ResultSet resultSet = prepareStmt.executeQuery();
            while (resultSet.next()) {

                double lat, lon;
                int stop_id;
                String stop_name;
                lat = Double.parseDouble(resultSet.getString("stop_lat"));
                lon = Double.parseDouble(resultSet.getString("stop_lon"));
                stop_id = resultSet.getInt("stop_id");
                stop_name = resultSet.getString("stop_name");
                if (calculations.aerialdistance.AerialDistance.calculateDistance(lat, lon, lat1, lon1) < (area / 1000)) {
                    stopsNearFirst.add(new Stop(lat, lon, stop_id, stop_name));
                }
                if (calculations.aerialdistance.AerialDistance.calculateDistance(lat, lon, lat2, lon2) < (area / 1000)) {
                    stopsNearSecond.add(new Stop(lat, lon, stop_id, stop_name));
                }
            }
         //   System.out.println(stopsNearFirst + "f" );
          //  System.out.println(stopsNearSecond + "s");
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to check stops in area: " + e.getMessage());
            // Continue without failing
        }
    }

    public String getStopName(int stopId) throws SQLException {
        if (!DatabaseConnector.canConnectToDatabase()) {
            return "Stop #" + stopId; // Return a default stop name
        }
        
        String sql = "SELECT stop_name FROM stops WHERE stop_id = ?";
    
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, stopId);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("stop_name");
                } else {
                    throw new SQLException("No stop found with stop_id: " + stopId);
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to get stop name from database: " + e.getMessage());
            return "Stop #" + stopId; // Return a default stop name
        }
    }

    
}
