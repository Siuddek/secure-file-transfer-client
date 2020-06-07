package com.bsk.Controllers;

import com.bsk.Models.EncryptedContentPackage;
import com.bsk.Services.ContentEncryptService;
import com.bsk.Services.KeyExchangeService;
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
import java.security.spec.InvalidKeySpecException;


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
        File fileToEncrypt = chooser.showOpenDialog(new Stage());
        try {
            EncryptedContentPackage encryptedFileAndSessionKey = contentEncryptService.encrypt(new String(Files.readAllBytes(Paths.get(fileToEncrypt.getPath()))));
            encryptedFileAndSessionKey.setType("file"); // TODO file extension
            keyExchangeService.sendEncryptedFileAndSessionKey(encryptedFileAndSessionKey);
        } catch (IOException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }
}
