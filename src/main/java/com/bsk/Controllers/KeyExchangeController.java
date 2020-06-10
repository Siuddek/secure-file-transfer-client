package com.bsk.Controllers;


import com.bsk.Configurations.KeyManagerConfiguration;
import com.bsk.Models.EncryptedContentPackage;
import com.bsk.Services.ContentEncryptService;
import javafx.scene.control.TextArea;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.validation.Valid;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

@RestController
@RequiredArgsConstructor
public class KeyExchangeController {

    private final KeyManagerConfiguration configuration;
    private final ContentEncryptService contentEncryptService;
    public TextArea readArea;
    private byte[] decryptedSessionKey;
    @Value("${outputDirectory}")
    private String outputDirectory;

    @GetMapping("/publicKey")
    public byte[] getPublicKey() {  //TODO decrypt RSA public key file
        try {
            byte[] keyBytes = Files.readAllBytes(Paths.get(configuration.getPublicKeyFolderPath()));
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey publicKey = kf.generatePublic(spec);
            return publicKey.getEncoded();
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/sessionKey")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveSessionKey(@RequestBody @Valid byte[] sessionKey) {
        try {
            decryptedSessionKey = contentEncryptService.decryptSessionKey(sessionKey);
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidKeySpecException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("encryptedContent")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveEncryptedContent(@RequestBody @Valid EncryptedContentPackage encryptedContentPackage) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        String decrypted = contentEncryptService.decryptFile(decryptedSessionKey, encryptedContentPackage);
        System.out.println("got it!");
        if (encryptedContentPackage.getType()
                                   .equals("text")) {
            readArea.setText(decrypted);
        } else {
            System.out.println(decrypted);
            saveToFile(encryptedContentPackage, decrypted);
        }
    }

    private void saveToFile(EncryptedContentPackage encryptedContentPackage, String message) {
        try {
            File fileToSave = new File(outputDirectory + encryptedContentPackage.getType());
            fileToSave.createNewFile();
            FileWriter writer = new FileWriter(fileToSave);
            writer.write(message);
            writer.close();
        } catch (IOException e) {
            System.out.println("File already exists");
        }

    }

}
