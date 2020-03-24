/*
 * To change this license header, choose License Headers in ClientMain Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import Crypto.SSLConnection;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author Raymond
 */
public class ClientMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Socket sock = new Socket("localhost", 6666);

            SSLConnection ssl_con = new SSLConnection(sock);

            System.out.println("Establishing Secure Connection");
            if (ssl_con.client_handshake()) {
                System.out.println("Done");
            } else {
                System.err.println("Failed!");
                return;
            }

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
