package com.bsk.Models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class EncryptedContentPackage {
    @Getter
    @Setter
    private byte[] encryptedContent;
    @Getter
    @Setter
    private byte[] encryptedSessionKey;
    @Getter
    @Setter
    private String blockMode;
    @Getter
    @Setter
    private String type;

    public EncryptedContentPackage(byte[] encryptedSessionKey, byte[] encryptedContent, String blockMode) {
        this.encryptedSessionKey = encryptedSessionKey;
        this.encryptedContent = encryptedContent;
        this.blockMode = blockMode;
    }
    public EncryptedContentPackage(){}
}
