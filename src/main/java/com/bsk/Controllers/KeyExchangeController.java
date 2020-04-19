package com.bsk.Controllers;


import com.bsk.Configurations.KeyManagerConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
public class KeyExchangeController {

    private final KeyManagerConfiguration configuration;

    @GetMapping("publicKey")
    public String getPublicKey() {  //TODO decrypt file
        try {
            return new String(Files.readAllBytes(Paths.get(configuration.getPublicKeyFolderPath())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
