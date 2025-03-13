package ui.uicontroller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.gluonhq.maps.MapView;

import apiinteraction.ApiCaller;
import dataprocessing.CheckForPostalCodeInExcel;
import dataprocessing.ExcelReader;
import dataprocessing.ExcelWriter;
import datatypes.JourneyData;
import datatypes.MapListData;
import datatypes.Stop;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import jfxtras.scene.control.LocalTimePicker;
import maphandlers.CheckPostalCodesFormat;
import maphandlers.DatabaseHandler;
import maphandlers.ShortestPath;
import databasehandling.DatabaseConnector;

public class UiController {

    public ShortestPath sp;
    public BorderPane borderPane;
    public LocalTimePicker timePicker;
    public TextField postCodeField1;
    public TextField postCodeField2;
    public StackPane mapStackPane;
    public Label walkTimeAndFirstStopLabel;
    public Label RouteDetailsLabel;
    public Label transferYesOrNoLabel;
    public Label transferAtStopsLabel;
    public Label totalJourneyTimeLabel;
    public TitledPane routeDetailsPane;
    public TitledPane transferDetailsPane;
    public Label arrivalTimeLabel;
    public VBox routeDetailsBox;
    public VBox transferDetailsBox;

    HashMap<String, String> hashMap;
    List<String> keySorted;
    private boolean isFirstTime = true;
    MapView mapView = MapViewer.createMapView();
    static DatabaseHandler dbHandler = new DatabaseHandler();
    private final double area = 1000; // max distance user can walk to a bus-stop

    public void initialize() {
        try {
            MapListData mapListData = ExcelReader.read();
            hashMap = mapListData.getMap();
            keySorted = mapListData.getKeySorted();
        } catch (Exception e) {
            System.out.println("Error reading postcode excel file");
            // Initialize empty data structures if file reading fails
            hashMap = new HashMap<>();
            keySorted = new ArrayList<>();
        }
        mapStackPane.getChildren().add(mapView);
        addTextFormatter();
        
        // Show database status message if there's no connection
        if (!DatabaseConnector.canConnectToDatabase()) {
            walkTimeAndFirstStopLabel.setText("Database connection unavailable - only walking routes will be calculated");
        }
    }

    private void addTextFormatter() {
        AddTextFormatter.addTextFormatter(postCodeField1);
        AddTextFormatter.addTextFormatter(postCodeField2);
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

    @FXML
    public void calculate() {
        resetLabels();
        if (!isFirstTime) {
            mapStackPane.getChildren().clear();
            mapView = MapViewer.createMapView();
            mapStackPane.getChildren().add(mapView);
        }
        isFirstTime = false;
        LocalTime localTime = timePicker.getLocalTime();
        String postCode1 = postCodeField1.getText();
        String postCode2 = postCodeField2.getText();
        
        String[] postCode1Coordinates = new String[2];
        String[] postCode2Coordinates = new String[2];
        boolean firstInFile = checkPostalCodes(postCode1);
        boolean secondInFile = checkPostalCodes(postCode2);
        
        try {
            if (CheckPostalCodesFormat.check(postCodeField1, postCodeField2)) {
                // Check if the postal codes are in the Excel file
                if (firstInFile) {
                    postCode1Coordinates = hashMap.get(postCode1).split(",");
                } else {
                    // Get the coordinates of the first postal code
                    try {
                        postCode1Coordinates = ApiCaller.getCoordinates(postCode1);
                        if (ApiCaller.error.isEmpty()) {
                            hashMap.put(postCode1, String.format("%s,%s", postCode1Coordinates[0], postCode1Coordinates[1]));
                            keySorted.add(postCode1);
                            Collections.sort(keySorted);
                        }
                    } catch (Exception e) {
                        walkTimeAndFirstStopLabel.setText("Error getting coordinates for postal code 1: " + e.getMessage());
                        return;
                    }
                }
                
                if (secondInFile) {
                    postCode2Coordinates = hashMap.get(postCode2).split(",");
                } else {
                    // Get the coordinates of the second postal code
                    try {
                        postCode2Coordinates = ApiCaller.getCoordinates(postCode2);
                        if (ApiCaller.error.isEmpty()) {
                            hashMap.put(postCode2, String.format("%s,%s", postCode2Coordinates[0], postCode2Coordinates[1]));
                            keySorted.add(postCode2);
                            Collections.sort(keySorted);
                        }
                    } catch (Exception e) {
                        walkTimeAndFirstStopLabel.setText("Error getting coordinates for postal code 2: " + e.getMessage());
                        return;
                    }
                }
                
                // Update the Excel file with the new postal codes (if needed)
                if ((!firstInFile && ApiCaller.error.isEmpty()) || (!secondInFile && ApiCaller.error.isEmpty())) {
                    try {
                        ExcelWriter.write(hashMap, keySorted);
                    } catch (Exception e) {
                        System.out.println("Error writing to Excel file: " + e.getMessage());
                    }
                }
                
                boolean samePost = false;
                ArrayList<Stop> stopsNearStart = new ArrayList<>();
                ArrayList<Stop> stopsNearEnd = new ArrayList<>();

                sp = new ShortestPath();

                if (!DatabaseConnector.canConnectToDatabase()) {
                    // If database is not available, directly show walking route
                    try {
                        double pc1lat = Double.parseDouble(postCode1Coordinates[0]);
                        double pc1lon = Double.parseDouble(postCode1Coordinates[1]);
                        double pc2lat = Double.parseDouble(postCode2Coordinates[0]);
                        double pc2lon = Double.parseDouble(postCode2Coordinates[1]);
                        double[] midpoint = calculateMidpoint(pc1lat, pc1lon, pc2lat, pc2lon);
                        
                        sp.GetPath(stopsNearStart, stopsNearEnd, area, localTime, midpoint, postCode1Coordinates, postCode2Coordinates);
                        JourneyData journeyData = sp.getJourneyData();
                        
                        boolean walkRoute = sp.drawRoute(mapView, postCode1Coordinates, postCode2Coordinates, journeyData);
                        walkTimeAndFirstStopLabel.setText("Database unavailable - showing walking route: " + sp.getTotalDuration() + " minutes");
                    } catch (Exception e) {
                        walkTimeAndFirstStopLabel.setText("Error calculating walking route: " + e.getMessage());
                    }
                    return;
                }

                try {
                    // Check if the postal codes are in the database or API
                    DatabaseHandler.checkStopsInArea(stopsNearEnd, stopsNearStart, postCode1Coordinates[0], postCode1Coordinates[1], postCode2Coordinates[0], postCode2Coordinates[1], area);
                } catch (Exception e) {
                    System.out.println("Error checking stops in area: " + e.getMessage());
                    // Continue even if this fails
                }

                if (postCode1Coordinates[0].equals(postCode2Coordinates[0]) && postCode1Coordinates[1].equals(postCode2Coordinates[1])) {
                    samePost = true;
                } else {
                    double pc1lat = Double.parseDouble(postCode1Coordinates[0]);
                    double pc1lon = Double.parseDouble(postCode1Coordinates[1]);
                    double pc2lat = Double.parseDouble(postCode2Coordinates[0]);
                    double pc2lon = Double.parseDouble(postCode2Coordinates[1]);

                    double[] midpoint = calculateMidpoint(pc1lat, pc1lon, pc2lat, pc2lon);

                    try {
                        sp.GetPath(stopsNearStart, stopsNearEnd,
                                calculations.aerialdistance.AerialDistance.calculateDistance(pc1lat, pc1lon, pc2lat, pc2lon),
                                timePicker.getLocalTime(), midpoint, postCode1Coordinates, postCode2Coordinates);
                    } catch (Exception e) {
                        System.out.println("Error calculating path: " + e.getMessage());
                        e.printStackTrace();
                        // Continue even if this fails
                    }
                }

                JourneyData journeyData = sp.getJourneyData();

                try {
                    boolean walkroute = sp.drawRoute(mapView, postCode1Coordinates, postCode2Coordinates, journeyData);

                    if (journeyData == null) {
                        if (samePost)
                            walkTimeAndFirstStopLabel.setText("Please enter 2 different postal codes");
                        else
                            walkTimeAndFirstStopLabel.setText("No route found, sorry.");
                        return;
                    } else if (walkroute) {
                        walkTimeAndFirstStopLabel.setText("Fastest route is by walking: " + sp.getTotalDuration() + " minutes");
                    } else {
                        updateLabels(journeyData);
                    }
                } catch (Exception e) {
                    System.out.println("Error drawing route: " + e.getMessage());
                    e.printStackTrace();
                    walkTimeAndFirstStopLabel.setText("Error drawing route: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            walkTimeAndFirstStopLabel.setText("Unexpected error: " + e.getMessage());
        }
    }

    private boolean checkPostalCodes(String postCode) {
        boolean isInFile = false;
        if (CheckForPostalCodeInExcel.contains(keySorted, postCode))
            isInFile = true;
        return isInFile;
    }

    private void resetLabels() {
        walkTimeAndFirstStopLabel.setText("");
        RouteDetailsLabel.setText("");
        transferYesOrNoLabel.setText("");
        transferAtStopsLabel.setText("");
        totalJourneyTimeLabel.setText("");
        arrivalTimeLabel.setText("");
    }

    private void updateLabels(JourneyData journeyData) {
        walkTimeAndFirstStopLabel.setText(journeyData.getDetailMessage());
        totalJourneyTimeLabel.setText("Total Journey Time: " + journeyData.getTotal() + " minutes");
        
        // Display bus route information if available
        if (journeyData.getBusRoutes() != null && !journeyData.getBusRoutes().isEmpty()) {
            StringBuilder routeDetails = new StringBuilder("Bus Routes: ");
            journeyData.getBusRoutes().forEach((stop, time) -> 
                routeDetails.append(stop).append(" (").append(time).append(" min), "));
            RouteDetailsLabel.setText(routeDetails.toString());
        }
        
        // Display transfer information if available
        if (journeyData.hasTransfers()) {
            transferYesOrNoLabel.setText("Transfers: Yes");
            if (journeyData.getTransferDetails() != null && !journeyData.getTransferDetails().isEmpty()) {
                transferAtStopsLabel.setText("Transfer at: " + String.join(", ", journeyData.getTransferDetails()));
            }
        } else {
            transferYesOrNoLabel.setText("Transfers: No");
        }
        
        // Display arrival time if available
        if (journeyData.getArrivalTime() != null) {
            arrivalTimeLabel.setText("Arrival Time: " + journeyData.getArrivalTime());
        }
    }
}
