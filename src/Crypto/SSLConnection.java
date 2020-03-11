/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crypto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 *
 * @author Raymond
 */
public class SSLConnection {
    
    private JEncrypDES cipher;
    private final Socket sock;
    DataOutputStream out;
    DataInputStream in;
    private boolean handshake_complete;
    
    public SSLConnection(Socket sock) {
        this.sock = sock;
        handshake_complete = false;
    }
    
    public void client_handshake() {
        // ----Phase 1----
        // send client_hello
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
