package errorhandling;

import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 * This class is responsible for handling errors during API interaction.
 * It provides methods to display error messages on a given label.
 */
public class ApiInteractionError {

    /**
     * Displays the error message of a given exception on a given label.
     *
     * @param e The exception whose error message is to be displayed.
     * @param infoLabel The label on which the error message is to be displayed.
     */
    public static void error(Exception e, Label infoLabel) {
        Platform.runLater(() -> infoLabel.setText(e.toString()));
    }

    /**
     * Displays a given error message on a given label.
     *
     * @param e The error message to be displayed.
     * @param infoLabel The label on which the error message is to be displayed.
     */
    public static void error(String e, Label infoLabel) {
        Platform.runLater(() -> infoLabel.setText(e));
    }
}
