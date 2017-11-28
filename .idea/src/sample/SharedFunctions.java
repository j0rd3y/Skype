package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class SharedFunctions {
    public static ButtonType MessageBox (Alert.AlertType alertType, String title, String headerText, String contentText){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
        return alert.getResult();
    }
}
