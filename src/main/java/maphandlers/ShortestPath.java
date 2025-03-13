package maphandlers;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapView;

import datatypes.JourneyData;
import datatypes.Stop;
import datatypes.Trip;
import javafx.scene.paint.Color;
import ui.map.MapLineLayer;
import ui.map.MapPinLayer;
import databasehandling.DatabaseConnector;

public class ShortestPath {

    static List<Stop> busStops;
    static DatabaseHandler dbHandler = new DatabaseHandler();
    private static List<Integer> path = new ArrayList<Integer>();
    private static final Walk walk = new Walk();
    private static Graph g;
    

    public JourneyData journeyData;

    public void GetPath(ArrayList<Stop> start, ArrayList<Stop> end, double radius, LocalTime startTime, double[] midpoint, String[] postCode1coords, String[] postCode2coords){

        try {
            // If database connection is not available, set up a direct walking path without bus stops
            if (!DatabaseConnector.canConnectToDatabase()) {
                setupWalkingOnlyPath(postCode1coords, postCode2coords);
                return;
            }

            // merge start and end
            double pc1lat = Double.parseDouble(postCode1coords[0]);
            double pc1lon = Double.parseDouble(postCode1coords[1]);
            double pc2lat = Double.parseDouble(postCode2coords[0]);
            double pc2lon = Double.parseDouble(postCode2coords[1]);

            ArrayList<Stop> postalStops = new ArrayList<>();

            postalStops.add(new Stop(pc1lat, pc1lon, 0, "Start postcode"));
            postalStops.add(new Stop(pc2lat, pc2lon, 1, "end postcode"));

            g = new Graph();

            // make connections from start postcode to all stops near start

            // edge from start to end postal code:

            int pc1to2 = Walk.findWalkTime(postCode1coords[0], postCode1coords[1], postCode2coords[0], postCode2coords[1]);

             g.addEdge(0, 1, pc1to2, new Trip(0, pc1to2, 0, null, null), null);

            for(Stop s : start){
                int duration = Walk.findWalkTime(postCode1coords[0], postCode1coords[1], s.getLat(), s.getLon());
                g.addEdge(0, s.getStop_id(), duration, new Trip(0, duration, 0, null, null), null );
            }

            for(Stop e : end){
                int duration = Walk.findWalkTime(postCode2coords[0], postCode2coords[1], e.getLat(), e.getLon());
                g.addEdge(e.getStop_id(), 1, duration, new Trip(1 , duration, 0, null, null), null);
            }

            postalStops.addAll(start);
            postalStops.addAll(end);

            busStops = dbHandler.getBusStops(radius, midpoint, postalStops);

            ArrayList<Trip> stopTimes = dbHandler.getStopTimes(startTime);

            stopTimes.sort(Comparator.comparing(Trip::getArrival_time));

            HashMap<Integer, List<Trip>> stopIDTrips = dbHandler.getStopIdTrips();

            for (Trip stopTime : stopTimes) {
                Trip nextStopTime = stopTimes.stream()
                        .filter(st -> st.getTrip_id() == stopTime.getTrip_id() && st.getStart_id() > stopTime.getStart_id())
                        .findFirst()
                        .orElse(null);

                stopTime.setNext(nextStopTime);

                if (nextStopTime != null) {
                    g.addEdge(stopTime.getEnd_id(), nextStopTime.getEnd_id(), calculateTime(stopTime.getArrival_time(), nextStopTime.getArrival_time()), stopTime, nextStopTime);
                }
            }

            for(Trip stopTime : stopTimes){
                Trip transfer = stopTimes.stream()
                        .filter(st -> st.getTrip_id() != stopTime.getTrip_id() && st.getEnd_id() == stopTime.getEnd_id())
                        .findFirst()
                        .orElse(null);

                if(transfer != null){
                    if(transfer.getNext()!=null)
                        g.addEdge(stopTime.getEnd_id(), transfer.getNext().getEnd_id(), calculateTime(stopTime.getArrival_time(), transfer.getNext().getArrival_time()), transfer, transfer.getNext());
                }
            }

            g.dijkstra(0, 1, startTime
            .truncatedTo(ChronoUnit.MINUTES)
            .format(DateTimeFormatter.ISO_LOCAL_TIME));


            System.out.println(g.getPath());
            if(g.getMin()!= Integer.MAX_VALUE)
                System.out.println(g.getMin() + " total time in minutes");

            path = g.getPath();

            if (path != null) {
                g.calculateJourneyDetails();
                journeyData = g.getJourneyData();
            }

        } catch (SQLException e) {
            System.out.println("ERROR: Database connection error in GetPath: " + e.getMessage());
            setupWalkingOnlyPath(postCode1coords, postCode2coords);
        }
    }
    
    // Create a simple direct walking path when database is unavailable
    private void setupWalkingOnlyPath(String[] postCode1coords, String[] postCode2coords) {
        g = new Graph();
        path = new ArrayList<>();
        path.add(0);
        path.add(1);
        
        int walkTime = Walk.findWalkTime(postCode1coords[0], postCode1coords[1], postCode2coords[0], postCode2coords[1]);
        
        // Create a simple journey data with just walking
        List<String> busNumbers = new ArrayList<>();
        List<Integer> durations = new ArrayList<>();
        durations.add(walkTime);
        List<String> stopNames = new ArrayList<>();
        stopNames.add("Start");
        stopNames.add("End");
        
        journeyData = new JourneyData(busNumbers, durations, stopNames, "No buses used - walking only", walkTime);
    }

    public JourneyData getJourneyData() {
        return journeyData;
    }

    private static int calculateTime(String start, String end){

        String[] parts = start.split(":");
        String[] parts1 = end.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);

        int hours1 = Integer.parseInt(parts1[0]);
        int minutes1 = Integer.parseInt(parts1[1]);
        int seconds1 = Integer.parseInt(parts1[2]);
        
        // Adjust the hours if it's 24
        if (hours >= 24) {
            hours = hours-24; 
        }

        if (hours1 >= 24) {
            hours1 = hours1-24; 
        }
        
        // Create a LocalTime object
        LocalTime time1 = LocalTime.of(hours, minutes, seconds);
        LocalTime time2 = LocalTime.of(hours1, minutes1, seconds1);
        // Calculate the duration between time1 and time2
        Duration duration = Duration.between(time1, time2);


        return duration.toMinutesPart();
    }


    public boolean drawRoute(MapView mapView, String[] postCode1coords, String[] postCode2coords, JourneyData journeyData) throws SQLException {
        // Handle null journeyData case
        if (journeyData == null) {
            // Just draw a direct walking path between start and end
            drawDirectWalkingPath(mapView, postCode1coords, postCode2coords);
            return true;
        }
        
        List<MapLayer> pinLayers = new ArrayList<>();
        List<MapLayer> lineLayers = new ArrayList<>();
        Color walkingColor = Color.GRAY;

        if(path.size()==2){
            double startLat = Double.parseDouble(postCode1coords[0]);
            double startLon = Double.parseDouble(postCode1coords[1]);
            pinLayers.add(new MapPinLayer(startLat, startLon, Color.GREEN));

            double endLat = Double.parseDouble(postCode2coords[0]);
            double endLon = Double.parseDouble(postCode2coords[1]);
            pinLayers.add(new MapPinLayer(endLat, endLon, Color.RED));

            lineLayers.add(new MapLineLayer(startLat, startLon, endLat, endLon, walkingColor,8));

            for (MapLayer layer : lineLayers) {
                mapView.addLayer(layer);
            }
    
            // Add all pin layers last
            for (MapLayer layer : pinLayers) {
                mapView.addLayer(layer);
            }

            return true;
        }


        if (path == null) {
            System.out.println("No possible route");
            return false;
        }

        List<Color> colors = Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.CYAN, Color.MAGENTA);
        int colorIndex = 0;


        ArrayList<String> busNumbers = journeyData.getBusNumbers();
        
        double tempCoord1 = 0;
        double tempCoord2 = 0;
        String currentBusNumber = null;

        try {
            for (int i = 2; i < path.size() - 1; i++) {
                int stopIndex = path.get(i);
    
                double[] stop = dbHandler.getStopCoordinates(stopIndex);
    
                if (i - 2 < busNumbers.size()) {
                    if (i == 2 || !busNumbers.get(i - 2).equals(currentBusNumber)) {
                        currentBusNumber = busNumbers.get(i - 2);
                        colorIndex = (colorIndex + 1) % colors.size();
                    }
                }
    
                if (i == 2) {
                    lineLayers.add(new MapLineLayer(Double.parseDouble(postCode1coords[0]), Double.parseDouble(postCode1coords[1]), 
                                                  stop[0], stop[1], walkingColor, 8));
                } else if (i == path.size() - 2) {
                    if (tempCoord1 != 0) {
                        lineLayers.add(new MapLineLayer(tempCoord1, tempCoord2, stop[0], stop[1], colors.get(colorIndex), 10));
                    }
                    double postCode2Lat = Double.parseDouble(postCode2coords[0]);
                    double postCode2Lon = Double.parseDouble(postCode2coords[1]);
                    lineLayers.add(new MapLineLayer(stop[0], stop[1], postCode2Lat, postCode2Lon, walkingColor, 8));
                } else {
                    if (tempCoord1 != 0) {
                        lineLayers.add(new MapLineLayer(tempCoord1, tempCoord2, stop[0], stop[1], colors.get(colorIndex), 10));
                    }
                }
                
                // Add pin for each bus stop
                pinLayers.add(new MapPinLayer(stop[0], stop[1], Color.BLACK));
                
                tempCoord1 = stop[0];
                tempCoord2 = stop[1];
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Database error while drawing route: " + e.getMessage());
            // In case of error, fall back to a simple walking path
            drawDirectWalkingPath(mapView, postCode1coords, postCode2coords);
            return true;
        }
            
        // Add pins for start and end postcodes
        pinLayers.add(new MapPinLayer(Double.parseDouble(postCode1coords[0]), Double.parseDouble(postCode1coords[1]), Color.GREEN));
        pinLayers.add(new MapPinLayer(Double.parseDouble(postCode2coords[0]), Double.parseDouble(postCode2coords[1]), Color.RED));
        
        // Add all line layers first
        for (MapLayer layer : lineLayers) {
            mapView.addLayer(layer);
        }

        // Add all pin layers last
        for (MapLayer layer : pinLayers) {
            mapView.addLayer(layer);
        }
        
        return false;
    }
    
    // Draw a direct walking path when database or route info is unavailable
    private void drawDirectWalkingPath(MapView mapView, String[] postCode1coords, String[] postCode2coords) {
        double startLat = Double.parseDouble(postCode1coords[0]);
        double startLon = Double.parseDouble(postCode1coords[1]);
        double endLat = Double.parseDouble(postCode2coords[0]);
        double endLon = Double.parseDouble(postCode2coords[1]);
        
        // Add start and end pins
        mapView.addLayer(new MapPinLayer(startLat, startLon, Color.GREEN));
        mapView.addLayer(new MapPinLayer(endLat, endLon, Color.RED));
        
        // Add direct walking path
        mapView.addLayer(new MapLineLayer(startLat, startLon, endLat, endLon, Color.GRAY, 8));
    }

    public int getTotalDuration(){
        if (journeyData == null) {
            return 0;
        }
        return journeyData.getTotal();
    }
}
