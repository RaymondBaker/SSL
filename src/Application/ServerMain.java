/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import Crypto.SSLConnection;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author Raymond
 */
public class ServerMain {
    
    /**
    * @param args the command line arguments
    */
    public static void main(String[] args) {
     try {
            int port = 6666;
            ServerSocket listen_soc = new ServerSocket(port);
            
            System.out.println("Listening on port: " + port);
            Socket sock = listen_soc.accept();
            System.out.println("Received connection");

            
            SSLConnection ssl_con = new SSLConnection(sock);

            System.out.println("Establishing Secure Connection");
            if (ssl_con.server_handshake()) {
                System.out.println("Done");
            } else {
                System.err.println("Failed!");
                return;
            }

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException |
                IllegalBlockSizeException | BadPaddingException | InvalidKeyException ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
