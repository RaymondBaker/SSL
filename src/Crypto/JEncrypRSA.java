/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crypto;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
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
     public JEncrypRSA (int key_size) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        KeyPairGenerator kpg =  KeyPairGenerator.getInstance("RSA");
        kpg.initialize(key_size);
        KeyPair kp = kpg.generateKeyPair();
        my_pub_key = kp.getPublic();
        priv_key = kp.getPrivate();
                
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    }
    
    public JEncrypRSA (byte[] their_public_key, int key_size) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        this(key_size);
        set_their_pub_key(their_public_key);
    }
    
    public void set_their_pub_key(byte[] their_public_key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        their_pub_key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(their_public_key));
    }
       
    public PublicKey get_my_pub_key() {
        return my_pub_key;
    }
    
     public byte[] mul_encrypt( byte [][] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, their_pub_key);
        for (int i = 0; i < data.length-1; i++) {
            cipher.update(data[i]);
        }
        return cipher.doFinal(data[data.length-1]);
    }
     
    public byte[] sign_and_encrypt( SecretKey key ) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.WRAP_MODE, priv_key);
        byte [] signed = cipher.wrap(key);
        
        cipher.init(Cipher.ENCRYPT_MODE, their_pub_key);
        System.out.println("Signed length: " + signed.length);
                
        return cipher.doFinal(signed);
    }


    public SecretKey decrypt_and_unsign( byte [] data ) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.DECRYPT_MODE, priv_key);
        byte [] decoded = cipher.doFinal(data);
        
        cipher.init(Cipher.UNWRAP_MODE, their_pub_key);
        try {
            return (SecretKey) cipher.unwrap(decoded,"DES",Cipher.SECRET_KEY);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(JEncrypRSA.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public byte[] encrypt( byte [] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, their_pub_key);
        return cipher.doFinal(data);
    }
    
     public byte[] decrypt( byte [] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.DECRYPT_MODE, priv_key);
        return cipher.doFinal(data);
    }
}
