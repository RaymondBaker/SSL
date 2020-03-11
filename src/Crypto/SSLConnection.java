/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crypto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Raymond
 */

enum CipherSuite {
    RSA,
    DH,
    RC4,
    RC2,
    DES,
    _3DES,
    IDEA,
    MD5,
    SHA_1
}

class ClientHelloData implements java.io.Serializable {
    public int nonce;
    public CipherSuite[] cipher_suites;
    // Compression Method
    // Not needed
}

class ServerHelloData implements java.io.Serializable {
    public int nonce;
    public int sesh_id;
    public CipherSuite cipher_suite;
    // Compression Method
    // Not needed
}

public class SSLConnection {
    
    private JEncrypDES cipher;
    private final Socket sock;
    private final DataOutputStream out;
    private final DataInputStream in;
    private boolean handshake_complete;
    
    public SSLConnection(Socket sock) throws IOException {
        this.sock = sock;
        handshake_complete = false;
        out = new DataOutputStream(sock.getOutputStream());
        in = new DataInputStream(sock.getInputStream());
    }
    
    public void client_handshake() {
        // ----Phase 1----
        // send client_hello
        
        //out.write();
        
        // recv server_hello
        
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

    }
    
    public void server_handshake() {
        // ----Phase 1----
        // recv client_hello
        // send server_hello
        
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
