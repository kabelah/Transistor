package ui.map;

import com.gluonhq.maps.MapView;
import databasehandling.DatabaseConnector;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DrawAllBusRoutesTest {

    private MapView mockMapView;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        mockMapView = mock(MapView.class);
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    void testDrawAllBusRoutes() throws Exception {
        // Mock the result set behavior
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getDouble("shape_pt_lat")).thenReturn(50.8503, 50.8504);
        when(mockResultSet.getDouble("shape_pt_lon")).thenReturn(4.3517, 4.3518);

        // Mock the database connector to return the mock connection
        try (MockedStatic<DatabaseConnector> mockedDatabaseConnector = Mockito.mockStatic(DatabaseConnector.class)) {
            mockedDatabaseConnector.when(DatabaseConnector::getConnection).thenReturn(mockConnection);

            // Call the method to be tested
            DrawAllBusRoutes.drawAllBusRoutes(mockMapView);

            // Verify that the MapView's addLayer method was called with the expected arguments
            verify(mockMapView, times(1)).addLayer(any(MapLineLayer.class));
        }
    }

    @Test
    void testDrawAllBusRoutesNoResults() throws Exception {
        // Mock the result set behavior to return no results
        when(mockResultSet.next()).thenReturn(false);

        // Mock the database connector to return the mock connection
        try (MockedStatic<DatabaseConnector> mockedDatabaseConnector = Mockito.mockStatic(DatabaseConnector.class)) {
            mockedDatabaseConnector.when(DatabaseConnector::getConnection).thenReturn(mockConnection);

            // Call the method to be tested
            DrawAllBusRoutes.drawAllBusRoutes(mockMapView);

            // Verify that the MapView's addLayer method was not called
            verify(mockMapView, times(0)).addLayer(any(MapLineLayer.class));
        }
    }
}
