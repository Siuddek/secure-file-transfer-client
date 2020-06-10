package com.bsk.Controllers;

import com.bsk.Models.EncryptedContentPackage;
import com.bsk.Services.ContentEncryptService;
import com.bsk.Services.KeyExchangeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;


@Component
@RequiredArgsConstructor
public class FileChooserController {

    @FXML
    public Button fileChooser;
    @FXML
    private ProgressBar progressBar;
    private final ContentEncryptService contentEncryptService;
    private final KeyExchangeService keyExchangeService;

    public void chooseAndEncryptFile(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        File fileToEncrypt = chooser.showOpenDialog(new Stage());
        try {
            long startTime = System.nanoTime();
            EncryptedContentPackage encryptedFileAndSessionKey = contentEncryptService.encrypt(new String(Files.readAllBytes(Paths.get(fileToEncrypt.getPath()))));
            encryptedFileAndSessionKey.setType(FilenameUtils.getExtension(fileToEncrypt.getName()));
            keyExchangeService.sendEncryptedFileAndSessionKey(encryptedFileAndSessionKey);
            long estimatedTime = System.nanoTime() - startTime;
            showTimePopUp(estimatedTime);
            progressBar.setProgress(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showTimePopUp(long time) {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("File send");
        infoAlert.setHeaderText(String.format("File send in %s milliseconds", time));
        infoAlert.showAndWait();
    }
}
