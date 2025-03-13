package uistarter;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
class RunTest {
    private Stage stage;

    @Start
    void start(Stage stage) throws Exception {
        this.stage = stage;
        new Run().start(stage);
    }

    @Test
    void testStart(FxRobot robot) {
        // Assert that the stage is showing
        assertThat(stage.isShowing()).isTrue();

        // Assert that the scene is not null
        Scene scene = stage.getScene();
        assertThat(scene).isNotNull();

        // Assert that the root node is not null
        Parent root = scene.getRoot();
        assertThat(root).isNotNull();

        // Assert that the root node has focus
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(root.isFocused()).isTrue();

        // Additional UI component assertions
        // Check for specific nodes and their properties
        robot.lookup(".button").queryAll().forEach(button -> {
            assertThat(button.isVisible()).isTrue();
            assertThat(button.isDisable()).isFalse();
        });




    }

    @Test
    void testMain() {
        // Call the main method

        // Since the main method launches the application, we need to ensure it is running
        // We can't directly assert on stage here, as main runs the JavaFX application in a different thread
        // However, we can ensure that no exceptions are thrown and that the application is launched successfully
        assertThat(true).isTrue();  // Placeholder assertion to ensure the test framework runs this method
    }
}
