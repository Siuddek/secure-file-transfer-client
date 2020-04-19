package com.bsk.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KeyExchangeService {

    private final RestTemplate restTemplate;
    @Value("${receiver.publicKeyUrl}")
    private String receiverUrl;

    public String getPublicKey() { //TODO decide if use url as class filed or parameter
        return restTemplate.getForEntity(receiverUrl, String.class).getBody();
    }
}
