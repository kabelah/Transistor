package errorhandling;

import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
import javafx.stage.Stage;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiInteractionErrorTest extends ApplicationTest {

    private Label infoLabel;

    @Start
    public void start(Stage stage) {
        infoLabel = new Label();
        stage.setScene(new javafx.scene.Scene(infoLabel, 100, 100));
        stage.show();
    }

    @BeforeEach
    void setUp() {
        // Any setup before each test can be done here
    }

    @Test
    void testErrorWithException() {
        Exception exception = new Exception("Test Exception");
        ApiInteractionError.error(exception, infoLabel);

        // Use WaitForAsyncUtils to ensure that Platform.runLater has completed
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("java.lang.Exception: Test Exception", infoLabel.getText());
    }

    @Test
    void testErrorWithString() {
        String errorMessage = "Test Error Message";
        ApiInteractionError.error(errorMessage, infoLabel);

        // Use WaitForAsyncUtils to ensure that Platform.runLater has completed
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(errorMessage, infoLabel.getText());
    }
}
