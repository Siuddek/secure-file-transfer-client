package com.bsk.Controllers;

import com.bsk.Models.EncryptedContentPackage;
import com.bsk.Services.ContentEncryptService;
import com.bsk.Services.KeyExchangeService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequiredArgsConstructor
public class TextAreaController {

    @FXML
    public Button sendTextButton;
    @FXML
    public TextArea textArea;

    private final ContentEncryptService contentEncryptService;
    private final KeyExchangeService keyExchangeService;

    public void encryptText(ActionEvent actionEvent) {
        try {
            EncryptedContentPackage encryptedTextAndSessionKey = contentEncryptService.encrypt(textArea.getText());
            encryptedTextAndSessionKey.setType("text");
            keyExchangeService.sendEncryptedFileAndSessionKey(encryptedTextAndSessionKey);
        } catch (NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }
}
