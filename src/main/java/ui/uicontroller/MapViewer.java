package ui.uicontroller;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;

/**
 * This class is responsible for creating and configuring a MapView.
 */
public class MapViewer {
    private static final MapPoint Maastricht = new MapPoint(50.8514, 5.6910);

    /**
     * Creates a MapView with a specific center point, zoom level, and minimum size.
     *
     * @return A MapView centered on Maastricht with a zoom level of 13 and a
     *         minimum size of 1920x900.
     */
    public static MapView createMapView() {
        MapView mapView = new MapView();
        mapView.setCenter(Maastricht);
        mapView.setZoom(13);
        mapView.setMinWidth(1920);
        mapView.setMinHeight(980);
        return mapView;
    }
}
