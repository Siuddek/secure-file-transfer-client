package com.bsk.Services;

import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Service
public class KeyManagerService {
    @Value("${rsa.path.publicKeyFolder}")
    private String publicKeyPath;
    @Value("${rsa.path.privateKeyFolder}")
    private String privateKeyPath;

    public void createRsaKeyPair(String password) throws NoSuchAlgorithmException {
        final String passwordHash = getEncryptedPassword(password);
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        keyGenerator.initialize(512);
        KeyPair keyPair = keyGenerator.generateKeyPair();
        byte[] rawPublicKey = keyPair.getPublic().getEncoded();
        byte[] rawPrivateKey = keyPair.getPrivate().getEncoded();

        StringBuffer publicKey = bin2hex(rawPublicKey);
        StringBuffer privateKey = bin2hex(rawPrivateKey);

        try {
            saveKeyToFile(publicKeyPath, publicKey);
            saveKeyToFile(privateKeyPath, privateKey);
        } catch (IOException e) {
            e.printStackTrace(); //TODO handle by error
        }
    }

    private StringBuffer bin2hex(byte[] key) {
        StringBuffer stringKey = new StringBuffer();
        for (byte b : key) {
            stringKey.append(Integer.toHexString(0x0100 + (b & 0x00FF)).substring(1));
        }
        return stringKey;
    }

    private void saveKeyToFile(String path, StringBuffer key) throws IOException {
        File publicKeyFile = new File(path);
        publicKeyFile.createNewFile();
        var out = new FileOutputStream(path);
        out.write(key.toString().getBytes());
        out.close();
    }

    private String getEncryptedPassword(String password) {
        return Hashing.sha256()
                      .hashString(password, StandardCharsets.UTF_8)
                      .toString();
    }
}
