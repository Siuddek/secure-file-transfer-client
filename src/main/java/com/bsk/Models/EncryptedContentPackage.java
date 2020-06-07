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

    public EncryptedContentPackage(byte[] encryptedSessionKey, byte[] encryptedContent) {
        this.encryptedSessionKey = encryptedSessionKey;
        this.encryptedContent = encryptedContent;
    }
    public EncryptedContentPackage(){}
}
