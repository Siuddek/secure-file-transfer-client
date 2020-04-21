package com.bsk.Controllers;

import com.bsk.Services.FileEncryptService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@Component
@RequiredArgsConstructor
public class FileChooserController {

    @FXML
    public Button fileChooser;
    private final FileEncryptService fileEncryptService;

    public void chooseAndEncryptFile(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        File fileToEncrypt = chooser.showOpenDialog(new Stage());
        try {
            Pair encryptedFileAndSessionKey = fileEncryptService.encryptFile(fileToEncrypt); //TODO !IMPORTANT send session key and file
        } catch (IOException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace(); //TODO handling
        }
    }
}
