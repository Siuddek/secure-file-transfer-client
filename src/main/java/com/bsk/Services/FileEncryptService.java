package com.bsk.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileEncryptService {

    private final KeyExchangeService keyExchangeService;

    public String encryptFile(File fileToEncrypt) throws IOException {
        String fileToEncryptContent = new String(Files.readAllBytes(Paths.get(fileToEncrypt.getPath())));
        String publicKey = keyExchangeService.getPublicKey();
        return ""; //TODO sesion key + sending
    }
}
