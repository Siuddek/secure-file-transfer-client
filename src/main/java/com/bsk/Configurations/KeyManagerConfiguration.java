package com.bsk.Configurations;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "rsa")
public class KeyManagerConfiguration {

    private String publicKeyFolderPath;
    private String privateKeyFolderPath;
    private String privateKeyCommentBegin;
    private String privateKeyCommentEnd;
    private String publicKeyCommentBegin;
    private String publicKeyCommentEnd;
    private int keyLength;

}