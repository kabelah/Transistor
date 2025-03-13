package errorhandling;

import javafx.application.Platform;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
class BadPostCodeTextFieldTest {

    private Label infoLabel;

    @Start
    public void start(Stage stage) {
        infoLabel = new Label();
        StackPane root = new StackPane(infoLabel);
        Scene scene = new Scene(root, 200, 100);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    void setUp() {
        infoLabel.setText("");
    }

    @Test
    void testError() throws InterruptedException {
        // Use CountDownLatch to wait for the JavaFX thread to update the UI
        CountDownLatch latch = new CountDownLatch(1);

        // Call the error method on the JavaFX Application Thread
        Platform.runLater(() -> {
            try {
                BadPostCodeTextField.error(infoLabel);
            } finally {
                latch.countDown();
            }
        });

        // Wait for the latch to count down, indicating the UI update is complete
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timeout waiting for UI update.");
        }

        // Verify that the error message is set on the label
        assertEquals("The postal codes are not correct!", infoLabel.getText());
    }
}
