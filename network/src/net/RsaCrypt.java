package net;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RsaCrypt {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    RsaCrypt() throws NoSuchAlgorithmException {
            KeyPair keyPair = buildKeyPair();
            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();
    }

    private KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.genKeyPair();
    }
    public byte[] encrypt(String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(message.getBytes());
    }

    public String decrypt(byte [] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(encrypted));
    }

    public byte[] getPublicKeyEncode() {
        return publicKey.getEncoded();
    }
    public void setPublicKey(byte[] publicKeyEncode) throws NoSuchAlgorithmException, InvalidKeySpecException {
        publicKey=KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyEncode));
    }
}
