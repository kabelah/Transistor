package ui.map;

import com.gluonhq.maps.MapView;
import databasehandling.DatabaseConnector;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DrawAllBusRoutes {
    public static void drawAllBusRoutes(MapView mapView) {

        String query = "SELECT shape_id, shape_pt_sequence,shape_pt_lat, shape_pt_lon FROM shapes ORDER BY shape_id,shape_pt_sequence ";
        try (Connection conn = DatabaseConnector.getConnection()) {
            long startTime = System.currentTimeMillis();
            PreparedStatement prepareStmt = conn.prepareStatement(query);
            ResultSet resultSet = prepareStmt.executeQuery();

            while (resultSet.next()) {
                double lat1 = 0, lat2 = 0, lon1 = 0, lon2 = 0;
                lat1 = resultSet.getDouble("shape_pt_lat");
                lon1 = resultSet.getDouble("shape_pt_lon");
                if (resultSet.next()) {
                    lat2 = resultSet.getDouble("shape_pt_lat");
                    lon2 = resultSet.getDouble("shape_pt_lon");
                }
                if (lat1 != 0 && lon1 != 0 && lat2 != 0 && lon2 != 0) {
                    mapView.addLayer(new MapLineLayer(lat1, lon1, lat2, lon2, Color.BLUE, 8));
                }

            }
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            System.out.println(STR."Done! Elapsed time: \{elapsedTime} ms");


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }

    }

}
