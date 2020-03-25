/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ServerGUI {
    
    

    
    public static void main(String[] args) {
        
        //ChatWindow test = new ChatWindow("Client");
       
        System.out.println("Reached Here!");
        Application.launch(ChatWindow.class, "Server" );
        //Application.launch(ChatWindow.class, "Server" );     
        
    }
    
}