/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crypto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 *
 * @author Raymond
 */
public class JEncrypRSA {

    private final PrivateKey priv_key;
    private final PublicKey my_pub_key;
    private PublicKey their_pub_key;
    private final Cipher cipher;

    // default key_size = 2048
    public JEncrypRSA(int key_size) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(key_size);
        KeyPair kp = kpg.generateKeyPair();
        my_pub_key = kp.getPublic();
        priv_key = kp.getPrivate();

        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    }

    public JEncrypRSA(String base_path) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException {
        /* Read all bytes from the private key file */
        Path path = Paths.get(base_path + ".key");
        byte[] bytes = Files.readAllBytes(path);

        /* Generate private key. */
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
        KeyFactory kf;
        kf = KeyFactory.getInstance("RSA");
        priv_key = kf.generatePrivate(ks);

        /* Read all the public key bytes */
        path = Paths.get(base_path + ".pub");
        bytes = Files.readAllBytes(path);

        /* Generate public key. */
        X509EncodedKeySpec ks_ = new X509EncodedKeySpec(bytes);
        my_pub_key = kf.generatePublic(ks_);

        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    }

    public JEncrypRSA(byte[] their_public_key, int key_size) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        this(key_size);
        set_their_pub_key(their_public_key);
    }

    public void set_their_pub_key(byte[] their_public_key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        their_pub_key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(their_public_key));
    }

    public PublicKey get_my_pub_key() {
        return my_pub_key;
    }

    public byte[] mul_encrypt(byte[][] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, their_pub_key);
        for (int i = 0; i < data.length - 1; i++) {
            cipher.update(data[i]);
        }
        return cipher.doFinal(data[data.length - 1]);
    }

    public byte[] encrypt(byte[] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, their_pub_key);
        return cipher.doFinal(data);
    }

    public byte[] sign(byte[] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, priv_key);
        return cipher.doFinal(data);
    }
    
    public byte[] unsign(byte[] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.DECRYPT_MODE, my_pub_key);
        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.DECRYPT_MODE, priv_key);
        return cipher.doFinal(data);
    }
}
