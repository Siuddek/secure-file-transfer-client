package com.bsk.Services;

import com.bsk.Configurations.KeyManagerConfiguration;
import com.google.common.hash.Hashing;
import javafx.scene.control.Alert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

@Service
@RequiredArgsConstructor
public class KeyManagerService {

    private final KeyManagerConfiguration configuration;
    private static final byte[] salt = {
            (byte) 0x43, (byte) 0x76, (byte) 0x95, (byte) 0xc7,
            (byte) 0x5b, (byte) 0xd7, (byte) 0x45, (byte) 0x17
    };

    public void createRsaKeyPair(String password) throws NoSuchAlgorithmException, IOException {
        String pass = getEncryptedPassword(password); //TODO encode key files with user password
        savePassword(pass);
        KeyPair keyPair = generateKeyPair();
        Key publicKey = keyPair.getPublic();
        Key privateKey = keyPair.getPrivate();

        try {
            deletePreviousKeys();
            saveKeyToFile(configuration.getPublicKeyFolderPath(), publicKey);
            saveEncryptedPrivateKey(privateKey);
            showSuccessDialog();
        } catch (Exception e) {
            e.printStackTrace(); //TODO handling
        }
    }

    private void savePassword(String password) {
        configuration.setPassword(password);
    }

    private char[] loadPassword() {
        return configuration.getPassword().toCharArray();
    }

    private void saveEncryptedPrivateKey(Key privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        PBEKeySpec keySpec = new PBEKeySpec(loadPassword());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(keySpec);
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 42);
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        cipher.init(Cipher.ENCRYPT_MODE, key, pbeParamSpec);
        byte[] encryptedPrivateKey = cipher.doFinal(privateKey.getEncoded());

        FileOutputStream out = new FileOutputStream(configuration.getPrivateKeyFolderPath());
        out.write(encryptedPrivateKey);
        out.close();
    }

    public byte[] loadEncryptedPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        File privateKeyFile = new File(configuration.getPrivateKeyFolderPath());

        PBEKeySpec keySpec = new PBEKeySpec(loadPassword());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(keySpec);
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 42);
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        cipher.init(Cipher.DECRYPT_MODE, key, pbeParamSpec);

        FileInputStream inputStream = new FileInputStream(privateKeyFile);
        byte[] encryptedKey = new byte[(int)privateKeyFile.length()];
        inputStream.read(encryptedKey);
        inputStream.close();

        return cipher.doFinal(encryptedKey);
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
