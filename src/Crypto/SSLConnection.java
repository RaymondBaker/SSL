/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Raymond
 */
enum KeyExchange {
    RSA,
    DH
}

enum CipherAlgorithm {
    RC4,
    RC2,
    DES,
    _3DES,
    IDEA
}

enum MACAlgorithm {
    MD5,
    SHA_1
}

class ClientHelloData implements java.io.Serializable {

    public int nonce;
    public KeyExchange[] key_exchange_algs;
    public CipherAlgorithm[] cipher_algs;
    public MACAlgorithm[] mac_algs;
    // Compression Method
    // Not needed

    public ClientHelloData(KeyExchange[] key_exchange_algs,
            CipherAlgorithm[] cipher_algs, MACAlgorithm[] mac_algs) {
        nonce = (int) (Math.random() * Integer.MAX_VALUE);
        this.key_exchange_algs = key_exchange_algs;
        this.cipher_algs = cipher_algs;
        this.mac_algs = mac_algs;
    }

}

class ServerHelloData implements java.io.Serializable {

    public int nonce;
    public int sesh_id;
    public static transient int sesh_cnt = 0;
    public KeyExchange key_exchange_alg;
    public CipherAlgorithm cipher_alg;
    public MACAlgorithm mac_alg;
    // Compression Method
    // Not needed

    public ServerHelloData(KeyExchange key_exchange_alg,
            CipherAlgorithm cipher_alg, MACAlgorithm mac_alg) {
        nonce = (int) (Math.random() * Integer.MAX_VALUE);
        sesh_id = sesh_cnt++;
        this.key_exchange_alg = key_exchange_alg;
        this.cipher_alg = cipher_alg;
        this.mac_alg = mac_alg;
    }
}

public class SSLConnection {

    private JEncrypDES sesh_cipher;
    private final Socket sock;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private boolean handshake_complete;

    private final JEncrypRSA CA_cipher;

    public SSLConnection(Socket sock) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException {
        this.sock = sock;
        handshake_complete = false;
        out = new ObjectOutputStream(sock.getOutputStream());
        in = new ObjectInputStream(sock.getInputStream());

        CA_cipher = new JEncrypRSA("CA_Key");
    }

    public boolean client_handshake() throws IOException {
        try {
            // ----Phase 1----
            // send client_hello
            out.writeObject(new ClientHelloData(
                    new KeyExchange[]{KeyExchange.RSA},
                    new CipherAlgorithm[]{CipherAlgorithm.DES},
                    new MACAlgorithm[]{MACAlgorithm.MD5}));
            System.out.println("\tSent Client Hello");

            // recv server_hello
            ServerHelloData resp = (ServerHelloData) in.readObject();
            System.out.println("\tRecieved Server Hello");

            // TODO: check nonce
            KeyExchange key_exchange_alg = resp.key_exchange_alg;
            CipherAlgorithm cipher_alg = resp.cipher_alg;
            MACAlgorithm mac_alg = resp.mac_alg;

            // ----Phase 2----
            // recv cert
            byte[] cert = (byte[]) in.readObject();

            switch (key_exchange_alg) {
                case RSA: {
                    JEncrypRSA server_pub = new JEncrypRSA(CA_cipher.unsign(cert), 1024);
                    System.out.println("\tRecieved Server Public Key");

                    // recv cert_request
                    int cert_req = (int) in.readObject();
                    System.out.println("\tRecieved Certificate Request: " + cert_req);
                    // recv server_hello_done
                    in.readObject();
                    System.out.println("\tRecieved Server Hello Done");

                    // ----Phase 3----
                    // send cert
                    if (cert_req == 1) {
                        System.err.println("Not Implemented");
                        return false;
                    }
                    // send client_key_exchange
                    // TODO: check if DES is actually chosen
                    sesh_cipher = new JEncrypDES();

                    out.writeObject(server_pub.encrypt(sesh_cipher.get_encrypt_key().getEncoded()));
                    System.out.println("\tSent Session Key");
                    // send cert_verify
                    if (cert_req == 1) {
                        System.err.println("Not Implemented");
                        return false;
                    }
                    break;
                }
                case DH: {
                    System.err.println("Not Implemented");
                    return false;
                }
            }

            // ----Phase 4----
            // send change_cipher_spec
            out.writeObject(0);
            System.out.println("\tSent Change Cipher Spec");
            // send finished
            out.writeObject(1);
            System.out.println("\tSent Finished");
            // recv change_cipher_spec
            in.readObject();
            System.out.println("\tReceived Change Cipher Spec");
            // recv finished
            in.readObject();
            System.out.println("\tReceived Finished");

            handshake_complete = true;
            return true;
        } catch (ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | InvalidKeySpecException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(SSLConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean server_handshake() throws IOException {
        try {
            // ----Phase 1----
            // recv client_hello

            ClientHelloData resp = (ClientHelloData) in.readObject();
            System.out.println("\tReceived Client Hello");

            // TODO: chose cipher suite
            // TODO: check nonce
            KeyExchange key_exchange_alg = resp.key_exchange_algs[0];
            CipherAlgorithm cipher_alg = resp.cipher_algs[0];
            MACAlgorithm mac_alg = resp.mac_algs[0];

            // send server_hello
            out.writeObject(new ServerHelloData(
                    key_exchange_alg, cipher_alg, mac_alg));
            System.out.println("\tSent Server Hello");

            // ----Phase 2----
            // send cert
            JEncrypRSA rsa_transfer_cipher;

            switch (key_exchange_alg) {
                case RSA: {
                    rsa_transfer_cipher = new JEncrypRSA(1024);
                    PublicKey my_pubkey = rsa_transfer_cipher.get_my_pub_key();

                    out.writeObject(CA_cipher.sign(my_pubkey.getEncoded()));
                    System.out.println("\tSent Server Public Key");

                    // send cert_request
                    // 0 representing false. Server doesn't want a client cert
                    out.writeObject(0);
                    System.out.println("\tSent Certificate Request: 0");

                    // send server_hello_done
                    out.writeObject(1);
                    System.out.println("\tSent Server Hello Done");

                    // ----Phase 3----
                    // recv cert
                    // Don't do anything because we didn't request a cert
                    // recv client_key_exchange
                    // recv cert_verify
                    // Don't do anything because we didn't request a cert
                    byte[] recv_data = (byte[]) in.readObject();
                    byte[] key_data = rsa_transfer_cipher.decrypt(recv_data);
                   
                    SecretKey sesh_key = new SecretKeySpec(key_data, 0, key_data.length, "DES");
                    System.out.println("\tReceived Session Key");
                    sesh_cipher = new JEncrypDES(sesh_key);

                    break;
                }
                case DH: {
                    System.err.println("DH Not Implemented");
                    return false;
                }
            }

            // ----Phase 4----
            // recv change_cipher_spec
            in.readObject();
            System.out.println("\tReceived Change Cipher Spec");
            // recv finished
            in.readObject();
            System.out.println("\tReceived Finished");
            // send change_cipher_spec
            out.writeObject(0);
            System.out.println("\tSent Change Cipher Spec");
            // send finished
            out.writeObject(1);
            System.out.println("\tSent Finished");

            handshake_complete = true;
            return true;
        } catch (ClassNotFoundException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException
                | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException ex) {
            Logger.getLogger(SSLConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void send(byte[] data) throws IOException {
        if (!handshake_complete) {
            throw new IOException("Handshake Not Complete");
        }

        try {
            byte[] encrypted = sesh_cipher.encrypt(data);
            out.writeObject(encrypted);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(SSLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] recv() throws IOException, ClassNotFoundException {
        if (!handshake_complete) {
            throw new IOException("Handshake Not Complete");
        }
        byte[] data = (byte[]) in.readObject();
        try {
            return sesh_cipher.decrypt(data);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(SSLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void sendObject(java.io.Serializable obj) throws IOException {
        if (!handshake_complete) {
            throw new IOException("Handshake Not Complete");
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream obj_out = null;
        try {
            obj_out = new ObjectOutputStream(bos);
            obj_out.writeObject(obj);
            obj_out.flush();
            byte[] data = bos.toByteArray();
            byte[] encrypted = sesh_cipher.encrypt(data);
            out.writeObject(encrypted);

        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(SSLConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            bos.close();
            if (obj_out != null) {
                obj_out.close();
            }
        }
    }

    public Object recvObject() throws IOException, ClassNotFoundException {
        if (!handshake_complete) {
            throw new IOException("Handshake Not Complete");
        }
 
        try {
            byte[] data = (byte[]) in.readObject();
            byte[] decrypted = sesh_cipher.decrypt(data);

            ByteArrayInputStream bis = new ByteArrayInputStream(decrypted);
            ObjectInput obj_in = new ObjectInputStream(bis);

            Object obj = obj_in.readObject();
            obj_in.close();
            bis.close();

            return obj;

        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(SSLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
