package com.bsk.Controllers;
import com.bsk.Services.KeyManagerService;
import com.google.common.hash.Hashing;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Component
@RequiredArgsConstructor
public class KeyManagerController {
    @FXML
    private Button sendPasswordButton;
    @FXML
    private PasswordField passwordBox;
    private final KeyManagerService keyManagerService;
    private final int MINIMAL_PASSWORD_LENGTH = 8;

    public void generateKeys(ActionEvent actionEvent) {
        String password = passwordBox.getText();
        if (password.length() < MINIMAL_PASSWORD_LENGTH) {
            showShortPasswordError();
        }
        try {
            keyManagerService.createRsaKeyPair(sendPasswordButton.getText());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace(); //TODO handle by javafx alter
        }

    }

    private void showShortPasswordError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Password Error");
        alert.setHeaderText("Password is too short");
        alert.setContentText("Password has to have at least 8 characters");
        alert.showAndWait();
    }
}
