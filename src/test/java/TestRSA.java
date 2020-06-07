import com.bsk.Services.ContentEncryptService;
import org.junit.Test;

import javax.crypto.*;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class TestRSA {

    String publicKeyPath = "/home/siudek/College/secure-file-transfer-client/bsk/keys/publick/public.pub";
    String privateKeyPath = "/home/siudek/College/secure-file-transfer-client/bsk/keys/privatek/private.key";

    @Test
    public void testPadding() throws Exception {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        keyGenerator.initialize(1024);
        KeyPair keyPair = keyGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();

        savePublicKey(publicKey, publicKeyPath);

        byte[] keyBytes = Files.readAllBytes(Paths.get(publicKeyPath));

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pk = kf.generatePublic(spec);

        var pkFromFile = pk.getEncoded();
        var pkFromMemory = publicKey.getEncoded();

        System.out.println(Arrays.equals(pkFromFile, pkFromMemory));
    }

    private void savePublicKey(PublicKey publicKey, String publicKeyPath) throws IOException {
        var out = new FileOutputStream(publicKeyPath);
        out.write(publicKey.getEncoded());
        out.close();
    }

    @Test
    public void testSessionKeyEncryption() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        SecretKey sessionKey = getSessionKey();

        byte[] keyBytes = Files.readAllBytes(Paths.get(publicKeyPath));

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(spec);

        byte[] encryptedSessionKey = getEncryptedSessionKey(publicKey.getEncoded(), sessionKey);
        byte[] decryptedSessionKey = decryptSessionKey(encryptedSessionKey);
    }

    private SecretKey getSessionKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }

    private byte[] getEncryptedSessionKey(byte[] publicKey, SecretKey sessionKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keyFactory.generatePublic(spec));
        return cipher.doFinal(sessionKey.getEncoded());
    }

    public byte[] decryptSessionKey(byte[] encryptedContent) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(privateKeyPath));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(spec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedContent);
    }
}
