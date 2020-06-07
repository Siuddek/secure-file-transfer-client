package com.bsk.Services;

import com.bsk.Models.EncryptedContentPackage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KeyExchangeService {

    private final RestTemplate restTemplate;
    @Value("${receiver.publicKeyUrl}")
    private String receiverPublicKeyUrl;
    @Value("${receiver.sessionKeyUrl}")
    private String receiverSessionKeyUrl;
    @Value("${receiver.encryptedContentUrl}")
    private String receiverEncryptedContentUrl;

    public byte[] getPublicKey() { //TODO decide if use url as class filed or parameter
        return restTemplate.getForEntity(receiverPublicKeyUrl, byte[].class).getBody();
    }

    public void sendEncryptedFileAndSessionKey(EncryptedContentPackage encryptedContentPackage) {
        restTemplate.postForEntity(receiverSessionKeyUrl, encryptedContentPackage.getEncryptedSessionKey(), String.class);
        restTemplate.postForEntity(receiverEncryptedContentUrl, encryptedContentPackage.getEncryptedContent(), String.class);
    }
}
