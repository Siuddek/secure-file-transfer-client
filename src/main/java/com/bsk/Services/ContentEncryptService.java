package com.bsk.Services;

import com.bsk.Models.BlockCipherState;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ContentEncryptService {

    private final KeyExchangeService keyExchangeService;
    private final BlockCipherState blockCipherState;

    public Pair<String, String> encrypt(String contentToEncrypt) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        String publicKey = keyExchangeService.getPublicKey();
        SecretKey sessionKey = getSessionKey();
        String encryptedSessionKey = getEncryptedSessionKey(publicKey, sessionKey);
        String encryptedFile = getEncryptedFile(contentToEncrypt, sessionKey);
        return new Pair<>(encryptedSessionKey, encryptedFile);
    }

    private String getEncryptedFile(String fileToEncryptContent, SecretKey sessionKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/" + blockCipherState.getCurrentBlockCipherState().toString() + "/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, sessionKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(fileToEncryptContent.getBytes()));
    }

    private SecretKey getSessionKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(new SecureRandom());
        return keyGenerator.generateKey();
    }


    private String getEncryptedSessionKey(String publicKey, SecretKey sessionKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, createPublicKeyFromString(publicKey));
        return Base64.getEncoder().encodeToString(cipher.doFinal(sessionKey.getEncoded()));
    }

    private PublicKey createPublicKeyFromString(String base64PublicKey) {
        try{
            PublicKey publicKey = null;
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }
}
