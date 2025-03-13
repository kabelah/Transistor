package ui.map;

import com.gluonhq.maps.MapLayer;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class MapLineLayer extends MapLayer {
    private final Line marker;
    private final Double latitude1;
    private final Double longitude1;
    private final Double latitude2;
    private final Double longitude2;

    public MapLineLayer(String latitude1, String longitude1, String latitude2, String longitude2, Color c, int strokeWidth) {
        this.marker = new Line();
        this.marker.setStroke(c);
        this.marker.setStrokeWidth(strokeWidth);
        this.latitude1 = Double.parseDouble(latitude1);
        this.longitude1 = Double.parseDouble(longitude1);
        this.latitude2 = Double.parseDouble(latitude2);
        this.longitude2 = Double.parseDouble(longitude2);
        getChildren().add(marker);
    }

    public MapLineLayer(double latitude1, double longitude1, double latitude2, double longitude2, Color c, int strokeWidth) {
        this.marker = new Line();
        this.marker.setStroke(c);
        this.marker.setStrokeWidth(strokeWidth);
        this.latitude1 = latitude1;
        this.longitude1 = longitude1;
        this.latitude2 = latitude2;
        this.longitude2 = longitude2;
        getChildren().add(marker);
    }

    public MapLineLayer(String latitude1, String longitude1, double latitude2, double longitude2, Color c, int strokeWidth) {
        this.marker = new Line();
        this.marker.setStroke(c);
        this.marker.setStrokeWidth(strokeWidth);
        this.latitude1 = Double.parseDouble(latitude1);
        this.longitude1 = Double.parseDouble(longitude1);
        this.latitude2 = latitude2;
        this.longitude2 = longitude2;
        getChildren().add(marker);
    }

    @Override
    public void layoutLayer() {
        Point2D point1 = getMapPoint(latitude1, longitude1);
        Point2D point2 = getMapPoint(latitude2, longitude2);
        marker.setStartX(point1.getX());
        marker.setStartY(point1.getY());
        marker.setEndX(point2.getX());
        marker.setEndY(point2.getY());
    }
}

