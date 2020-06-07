package com.bsk.Controllers;


import com.bsk.Configurations.KeyManagerConfiguration;
import com.bsk.Models.EncryptedContentPackage;
import com.bsk.Services.ContentEncryptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class KeyExchangeController {

    private final KeyManagerConfiguration configuration;
    private final ContentEncryptService contentEncryptService;
    private byte[] decryptedSessionKey;

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
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("encryptedContent")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveEncryptedContent(@RequestBody @Valid byte[] encryptedContent) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        String decrypted = contentEncryptService.decryptFile(decryptedSessionKey, encryptedContent);
        System.out.println(decrypted);
    }

}
