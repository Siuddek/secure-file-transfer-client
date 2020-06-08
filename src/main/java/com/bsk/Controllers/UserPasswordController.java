package com.bsk.Controllers;

import com.bsk.Configurations.KeyManagerConfiguration;
import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPasswordController {

    private final KeyManagerConfiguration keyManagerConfiguration;

    @EventListener(ApplicationReadyEvent.class)
    public void askForPassword() {
        if (doesPrivateKeyExist()) {
            Platform.runLater(() -> {
                TextInputDialog textInputDialog = new TextInputDialog();
                textInputDialog.setHeaderText("Enter private key password");
                Optional<String> password = textInputDialog.showAndWait();
                keyManagerConfiguration.setPassword(password.get());
            });
        }
    }

    private boolean doesPrivateKeyExist() {
        File privateKeyFile = new File(keyManagerConfiguration.getPrivateKeyFolderPath());
        return privateKeyFile.exists();
    }
}
