package uistarter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Run extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win") || osName.contains("mac") || osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            System.setProperty("javafx.platform", "Desktop");
        }
        final String uiFXMLPath = "ui.fxml";
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(uiFXMLPath)));
        stage.setScene(new Scene(root));
        stage.show();
        root.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }


}
