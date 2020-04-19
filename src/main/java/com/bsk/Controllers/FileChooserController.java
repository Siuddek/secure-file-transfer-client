package com.bsk.Controllers;

import com.bsk.Services.FileEncryptService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;


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
            fileEncryptService.encryptFile(fileToEncrypt);
        } catch (IOException e) {
            e.printStackTrace(); //TODO handling
        }
    }
}
