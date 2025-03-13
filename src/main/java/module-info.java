module ProjectOTReformed {
    requires javafx.graphics;
    requires javafx.fxml;
    requires com.gluonhq.maps;
    requires javafx.controls;
    requires com.gluonhq.attach.storage;
    requires com.gluonhq.attach.util;
    requires org.apache.poi.ooxml;
    requires okhttp3;
    requires java.sql;
    requires mysql.connector.java;
    requires commons.math3;
    requires kdtree;
    requires jfxtras.controls;
    requires java.desktop;
    opens uistarter to javafx.fxml, javafx.graphics;
    opens ui.uicontroller to javafx.fxml, javafx.graphics;
    opens maphandlers to javafx.fxml, javafx.graphics;

}
