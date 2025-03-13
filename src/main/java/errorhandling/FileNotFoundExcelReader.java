package errorhandling;

import javafx.scene.control.Label;

/**
 * This class is responsible for handling errors related to the Excel file not being found during the reading process.
 * It provides a method to display an error message on a given label.
 */
public class FileNotFoundExcelReader {

    /**
     * Displays an error message on a given label indicating that the Excel file was not found.
     *
     * @param infoLabel The label on which the error message is to be displayed.
     */
    public static void error(Label infoLabel){
        infoLabel.setText("The excel file was not found! Api will be used instead.");
    }
}
