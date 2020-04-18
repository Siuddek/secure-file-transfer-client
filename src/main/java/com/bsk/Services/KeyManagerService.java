package com.bsk.Services;

import com.bsk.Configurations.KeyManagerConfiguration;
import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class KeyManagerService {

    private final KeyManagerConfiguration configuration;

    public void createRsaKeyPair(String password) throws NoSuchAlgorithmException {
        final String passwordHash = getEncryptedPassword(password); //TODO encode key files with user password
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        keyGenerator.initialize(configuration.getKeyLength());
        KeyPair keyPair = keyGenerator.generateKeyPair();
        Key publicKey = keyPair.getPublic();
        Key privateKey = keyPair.getPrivate();

        try {
            saveKeyToFile(configuration.getPublicKeyFolderPath(), publicKey, configuration.getPublicKeyCommentBegin(), configuration.getPublicKeyCommentEnd());
            saveKeyToFile(configuration.getPrivateKeyFolderPath(), privateKey, configuration.getPrivateKeyCommentBegin(), configuration.getPrivateKeyCommentEnd());
        } catch (IOException e) {
            e.printStackTrace(); //TODO handling
        }
    }

    private void saveKeyToFile(String path, Key key, String beginComment, String endComment) throws IOException {
        Base64.Encoder encoder = Base64.getEncoder();
        Writer out = new FileWriter(path);
        out.write(beginComment);
        out.write(encoder.encodeToString(key.getEncoded()));
        out.write(endComment);
        out.close();
    }

    private String getEncryptedPassword(String password) {
        return Hashing.sha256()
                      .hashString(password, StandardCharsets.UTF_8)
                      .toString();
    }
}
