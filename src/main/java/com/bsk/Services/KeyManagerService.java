package com.bsk.Services;

import com.bsk.Configurations.KeyManagerConfiguration;
import com.google.common.hash.Hashing;
import javafx.scene.control.Alert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class KeyManagerService {

    private final KeyManagerConfiguration configuration;

    public void createRsaKeyPair(String password) throws NoSuchAlgorithmException {
        final String passwordHash = getEncryptedPassword(password); //TODO encode key files with user password
        KeyPair keyPair = generateKeyPair();
        Key publicKey = keyPair.getPublic();
        Key privateKey = keyPair.getPrivate();

        try {
            deletePreviousKeys();
            saveKeyToFile(configuration.getPublicKeyFolderPath(), publicKey);
            saveKeyToFile(configuration.getPrivateKeyFolderPath(), privateKey);
        } catch (IOException e) {
            e.printStackTrace(); //TODO handling
        }
        showSuccessDialog();
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        keyGenerator.initialize(configuration.getKeyLength());
        return keyGenerator.generateKeyPair();
    }

    private void showSuccessDialog() {
        Alert successDialog = new Alert(Alert.AlertType.INFORMATION);
        successDialog.setTitle("Success!");
        successDialog.setHeaderText("RSA keys pair has been successfully generated");
        successDialog.setContentText("Use typed password to decode keys");
        successDialog.showAndWait();
    }

    private void deletePreviousKeys() {
        File privateKeyFile = new File(configuration.getPrivateKeyFolderPath());
        File publicKeyFile = new File(configuration.getPublicKeyFolderPath());
        publicKeyFile.delete();
        privateKeyFile.delete();
    }

    private void saveKeyToFile(String path, Key key) throws IOException {
        var out = new FileOutputStream(path);
        out.write(key.getEncoded());
        out.close();
        System.err.println("format: " + key.getFormat());
    }

    private String getEncryptedPassword(String password) {
        return Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
    }
}
