package ui.uicontroller;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

/**
 * This class is responsible for adding a text formatter to a TextField.
 */
public class AddTextFormatter {

    /**
     * Adds a text formatter to the given TextField, this ensures that the input in
     * the TextField adheres to Maastrict's postcode format.
     *
     * @param textField The TextField to which the text formatter is to be added.
     */
    public static void addTextFormatter(TextField textField) {
        // Create a filter that checks if the input is correct
        final UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            
            // Allow deletion
            if (change.isDeleted()) {
                return change;
            }
            
            // Check postcode format
            if (newText.length() <= 6) {
                if (newText.length() >= 1) {
                    // First digit must be 6
                    if (newText.charAt(0) != '6') {
                        return null;
                    }
                }
                
                if (newText.length() >= 2) {
                    // Second digit must be 2
                    if (newText.charAt(1) != '2') {
                        return null;
                    }
                }
                
                if (newText.length() >= 3) {
                    // Third digit must be 1 or 2
                    char thirdChar = newText.charAt(2);
                    if (thirdChar != '1' && thirdChar != '2') {
                        return null;
                    }
                }
                
                if (newText.length() >= 4) {
                    // Fourth digit must be 1-9
                    char fourthChar = newText.charAt(3);
                    if (fourthChar < '1' || fourthChar > '9') {
                        return null;
                    }
                }
                
                if (newText.length() >= 5) {
                    // Fifth and sixth characters must be A-Z (case insensitive)
                    char fifthChar = newText.charAt(4);
                    if (!Character.isLetter(fifthChar)) {
                        return null;
                    }
                    
                    // Convert to uppercase if it's a lowercase letter
                    if (Character.isLowerCase(fifthChar)) {
                        change.setText(change.getText().toUpperCase());
                    }
                }
                
                if (newText.length() == 6) {
                    // Sixth character must be A-Z (case insensitive)
                    char sixthChar = newText.charAt(5);
                    if (!Character.isLetter(sixthChar)) {
                        return null;
                    }
                    
                    // Convert to uppercase if it's a lowercase letter
                    if (Character.isLowerCase(sixthChar)) {
                        change.setText(change.getText().toUpperCase());
                    }
                }
                
                return change;
            }
            
            // More than 6 characters is not allowed
            return null;
        };
        // Add the filter to the text field
        textField.setTextFormatter(new TextFormatter<>(filter));
    }
}
