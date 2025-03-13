package maphandlers;

import javafx.scene.control.TextField;
import javafx.scene.text.TextBoundsType;

/**
 * This class is responsible for checking the format of postal codes.
 */
public class CheckPostalCodesFormat {

    /**
     * Checks if the postal codes in two text fields have the correct length.
     *
     * @param postCodeField1 The first text field containing a postal code.
     * @param postCodeField2 The second text field containing a postal code.
     * @return True if both postal codes have the correct length, false otherwise.
     */
    public static boolean check(TextField postCodeField1, TextField postCodeField2) {
        return (postCodeField1.getText().length() == 6) && (postCodeField2.getText().length() == 6);
    }
}
