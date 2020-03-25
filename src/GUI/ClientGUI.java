/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import javafx.application.Application;

/**
 *
 * @author Raymond
 */
public class ClientGUI {
     public static void main(String[] args) {

        System.out.println("Reached Here!");
        Application.launch(ChatWindow.class, "Client" );        
    }
}
