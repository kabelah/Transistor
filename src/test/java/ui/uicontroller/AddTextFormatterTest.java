package ui.uicontroller;

import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
import javafx.stage.Stage;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddTextFormatterTest extends ApplicationTest {

    private TextField textField;

    @Start
    public void start(Stage stage) {
        textField = new TextField();
        AddTextFormatter.addTextFormatter(textField);
        stage.setScene(new javafx.scene.Scene(textField, 300, 100));
        stage.show();
    }

    @BeforeEach
    void setUp() {
        // Any setup before each test can be done here
    }

    @Test
    void testValidPostcode() {
        clickOn(textField).write("6211AB");
        assertEquals("6211AB", textField.getText());
    }

    @Test
    void testInvalidPostcode() {
        clickOn(textField).write("1234XY");
        assertEquals("", textField.getText());
    }

    @Test
    void testMixedCasePostcode() {
        clickOn(textField).write("6211ab");
        assertEquals("6211AB", textField.getText());
    }

    @Test
    void testPartialValidPostcode() {
        clickOn(textField).write("62");
        assertEquals("62", textField.getText());
    }

    @Test
    void testPartialInvalidPostcode() {
        clickOn(textField).write("63");
        assertEquals("6", textField.getText());
    }
}
