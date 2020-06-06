package com.bsk.Controllers;

import com.bsk.Services.ContentEncryptService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Component
@RequiredArgsConstructor
public class FileChooserController {

    @FXML
    public Button fileChooser;
    @FXML
    private ProgressBar progressBar;
    private final ContentEncryptService contentEncryptService;

    public void chooseAndEncryptFile(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        File fileToEncrypt = chooser.showOpenDialog(new Stage());
        try {
            Pair<String, String> encryptedFileAndSessionKey = contentEncryptService.encrypt(new String(Files.readAllBytes(Paths.get(fileToEncrypt.getPath()))));
        } catch (IOException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
