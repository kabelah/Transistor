package ui.map;

import com.gluonhq.maps.MapLayer;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * This class is responsible for creating a pin layer on the map.
 * It extends the MapLayer class from the Gluon Maps library.
 */
public class MapPinLayer extends MapLayer {
    private final Node marker;
    private final Double latitude;
    private final Double longitude;

    /**
     * Constructs a new MapPinLayer object with the given latitude and longitude
     * coordinates and color.
     *
     * @param latitude  The latitude of the pin.
     * @param longitude The longitude of the pin.
     * @param c         The color of the pin.
     */

    public MapPinLayer(double latitude, double longitude, Color c) {
        marker = new Circle(6, c);
        this.latitude = latitude;
        this.longitude = longitude;
        getChildren().add(marker);
    }

    /**
     * Lays out the pin layer on the map.
     * This method is called whenever the map is panned or zoomed.
     */
    @Override
    public void layoutLayer() {
        // Get the point on the map corresponding to the latitude and longitude
        // coordinates
        Point2D point = getMapPoint(latitude, longitude);
        // Set the position of the marker on the map
        marker.setTranslateX(point.getX());
        marker.setTranslateY(point.getY());
    }
}
