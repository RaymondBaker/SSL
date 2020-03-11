/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crypto;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            CipherAlgorithm[] cipher_algs,  MACAlgorithm[] mac_algs) {
        nonce = (int)(Math.random() * Integer.MAX_VALUE);
        this.key_exchange_algs = key_exchange_algs;
        this.cipher_algs = cipher_algs;
        this.mac_algs = mac_algs;
    }

}

class ServerHelloData implements java.io.Serializable {
    public int nonce;
    public int sesh_id;
    public static int sesh_cnt = 0;
    public KeyExchange key_exchange_alg;
    public CipherAlgorithm cipher_alg;
    public MACAlgorithm mac_alg;
    // Compression Method
    // Not needed
    
    public ServerHelloData (KeyExchange key_exchange_alg, 
            CipherAlgorithm cipher_alg,  MACAlgorithm mac_alg) {
        nonce = (int)(Math.random() * Integer.MAX_VALUE);
        sesh_id = sesh_cnt++;
        this.key_exchange_alg = key_exchange_alg;
        this.cipher_alg = cipher_alg;
        this.mac_alg = mac_alg;
    }
}

class ServerCertData implements java.io.Serializable {
    
}

public class SSLConnection {
    
    private JEncrypDES cipher;
    private final Socket sock;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private boolean handshake_complete;
    
    public SSLConnection(Socket sock) throws IOException {
        this.sock = sock;
        handshake_complete = false;
        out = new ObjectOutputStream(sock.getOutputStream());
        in = new ObjectInputStream(sock.getInputStream());
    }
    
    public void client_handshake() throws IOException {
        try {
            // ----Phase 1----
            // send client_hello
            out.writeObject(new ClientHelloData(
                new KeyExchange[] {KeyExchange.RSA},
                new CipherAlgorithm[] {CipherAlgorithm.DES},
                new MACAlgorithm[] {MACAlgorithm.MD5}));
        
            // recv server_hello
            ServerHelloData resp = (ServerHelloData) in.readObject();
            
            // TODO: check nonce
            
            KeyExchange key_exchange_alg = resp.key_exchange_alg;
            CipherAlgorithm cipher_alg = resp.cipher_alg;
            MACAlgorithm mac_alg = resp.mac_alg;

            // ----Phase 2----
            // recv cert
            // recv cert_request
            // recv server_hello_done

            // ----Phase 3----
            // send cert
            // send client_key_exchange
            // send cert_verify

            // ----Phase 4----
            // send change_cipher_spec
            // send finished
            // recv change_cipher_spec
            // recv finished

            handshake_complete = true;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SSLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void server_handshake() throws IOException {
        try {
            // ----Phase 1----
            // recv client_hello
            
            ClientHelloData resp = (ClientHelloData) in.readObject();
            
            // TODO: chose cipher suite
            // TODO: check nonce
            KeyExchange key_exchange_alg = resp.key_exchange_algs[0];
            CipherAlgorithm cipher_alg = resp.cipher_algs[0];
            MACAlgorithm mac_alg = resp.mac_algs[0];
            
            // send server_hello
            out.writeObject(new ServerHelloData(
                    key_exchange_alg, cipher_alg, mac_alg));
            
            
            // ----Phase 2----
            // send cert
            // send cert_request
            // send server_hello_done
            
            
            // ----Phase 3----
            // recv cert
            // recv client_key_exchange
            // recv cert_verify
            
            // ----Phase 4----
            // recv change_cipher_spec
            // recv finished
            // send change_cipher_spec
            // send finished

            handshake_complete = true;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SSLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void send() throws Exception {
        if (!handshake_complete)
            throw new Exception("Handshake Not Complete");
    }
    
    public void recv() throws Exception {
        if (!handshake_complete)
            throw new Exception("Handshake Not Complete");
    }
}
