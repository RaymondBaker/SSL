/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crypto;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Raymond
 */
public class JEncrypDES {

    private Cipher cipher;
    private SecretKey encrypt_key, decrypt_key;

    public JEncrypDES() {
        try {
            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
            encrypt_key = decrypt_key = keygenerator.generateKey();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to Gen Cipher or keys");
        }
    }
    
    public JEncrypDES(SecretKey key) {
        try {
            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            encrypt_key = decrypt_key = key;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to Gen Cipher or keys");
        }
    }
    
    public JEncrypDES(byte [] key) {
        try {
            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            encrypt_key = decrypt_key = new SecretKeySpec(key, 0, key.length, "DES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to Gen Cipher or keys");
        }
    }

    public JEncrypDES(String key_base) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException {
        cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        DESKeySpec key_spec = new DESKeySpec(key_base.getBytes());
        SecretKeyFactory keygenerator = SecretKeyFactory.getInstance("DES");
        encrypt_key = decrypt_key = keygenerator.generateSecret(key_spec);
    }

    public JEncrypDES(String encrypt_key_base, String decrypt_key_base) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException {
        cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        DESKeySpec encrypt_key_spec = new DESKeySpec(encrypt_key_base.getBytes());
        DESKeySpec decrypt_key_spec = new DESKeySpec(decrypt_key_base.getBytes());
        SecretKeyFactory keygenerator = SecretKeyFactory.getInstance("DES");
        encrypt_key = keygenerator.generateSecret(encrypt_key_spec);
        decrypt_key = keygenerator.generateSecret(decrypt_key_spec);
    }
    
    public SecretKey get_encrypt_key() {
        return encrypt_key;
    }
    
    public SecretKey get_decrypt_key() {
        return decrypt_key;
    }

    public byte[] encrypt(byte[] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, encrypt_key);
        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] cipherData) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.DECRYPT_MODE, decrypt_key);
        return cipher.doFinal(cipherData);
    }
}
