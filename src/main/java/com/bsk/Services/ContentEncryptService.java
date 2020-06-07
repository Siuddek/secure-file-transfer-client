package com.bsk.Services;

import com.bsk.Configurations.KeyManagerConfiguration;
import com.bsk.Models.BlockCipherState;
import com.bsk.Models.EncryptedContentPackage;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ContentEncryptService {

    private final KeyExchangeService keyExchangeService;
    private final BlockCipherState blockCipherState;
    private final KeyManagerConfiguration configuration;

    public EncryptedContentPackage encrypt(String contentToEncrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        byte[] publicKey = keyExchangeService.getPublicKey();
        SecretKey sessionKey = getSessionKey();
        byte[] encryptedSessionKey = getEncryptedSessionKey(publicKey, sessionKey);
        byte[] encryptedContent = getEncryptedContent(contentToEncrypt, sessionKey);
        return new EncryptedContentPackage(encryptedSessionKey, encryptedContent);
    }

    private SecretKey getSessionKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(new SecureRandom());
        return keyGenerator.generateKey();
    }

    private byte[] getEncryptedSessionKey(byte[] publicKey, SecretKey sessionKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        Cipher cipher = Cipher.getInstance("RSA");
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, keyFactory.generatePublic(spec));
        return cipher.doFinal(sessionKey.getEncoded());
    }

    private byte[] getEncryptedContent(String fileToEncryptContent, SecretKey sessionKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/" + blockCipherState.getCurrentBlockCipherState().toString() + "/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, sessionKey);
        return cipher.doFinal(fileToEncryptContent.getBytes());
    }

    private PublicKey createPublicKeyFromString(String base64PublicKey) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decryptSessionKey(byte[] encryptedContent) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(configuration.getPrivateKeyFolderPath()));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(spec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedContent);
    }

    private PrivateKey createPrivateKeyFromString(String base64PrivateKey) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }
}
