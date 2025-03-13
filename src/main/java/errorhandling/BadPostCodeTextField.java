package errorhandling;

import javafx.scene.control.Label;

/**
 * This class is responsible for handling errors related to incorrect postal codes in a text field.
 * It provides a method to display an error message on a given label.
 */
public class BadPostCodeTextField {

    /**
     * Displays an error message on a given label indicating that the postal codes are not correct.
     *
     * @param infoLabel The label on which the error message is to be displayed.
     */
    public static void error(Label infoLabel){
        infoLabel.setText("The postal codes are not correct!");
    }
}
