package GUI;

import Application.ClientMain;
import Application.ServerMain;
import Crypto.SSLConnection;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javax.crypto.NoSuchPaddingException;

public class ChatWindow extends Application {

    private final Pane centerPane = new Pane();
    private final BorderPane root = new BorderPane();
    private Scene scene;

    private String cms;
    private String id;
    private String lastMessageSent;
    private Label connectionMessage;

    private Button send;
    private Button image;
    private Button quit;
    private VBox chatBox;
    private HBox bottom;
    private File imageFile;
    private TextField message;
    private List<Label> messages;
    private ScrollPane scrollP;

    private BlockingQueue<String> message_queue;
    private SSLConnection ssl_con;
    private Thread recv_thread;

    private int messageCount;

    @Override
    public void init() {
        Application.Parameters p = this.getParameters();
        //Map<String, String> namedParams = p.getNamed();
        this.id = p.getUnnamed().toString();
        //List<String> rawParams = p.getRaw();
        //String paramStr = "Named Parameters: " + namedParams + "\n" +
        //"Unnamed Parameters: " + unnamedParams + "\n" +
        //"Raw Parameters: " + rawParams;
        message_queue = new LinkedBlockingQueue<>(10);

        if (id.equals(
                "[Client]")) {
            cms = "Server";
            try {
                Socket sock = new Socket("localhost", 6666);

                ssl_con = new SSLConnection(sock);

                System.out.println("Establishing Secure Connection");
                if (ssl_con.client_handshake()) {
                    System.out.println("Done");
                } else {
                    System.err.println("Failed!");
                }
            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException ex) {
                Logger.getLogger(ChatWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            cms = "Client";
            try {
                int port = 6666;
                ServerSocket listen_soc = new ServerSocket(port);

                System.out.println("Listening on port: " + port);
                Socket sock = listen_soc.accept();
                System.out.println("Received connection");

                ssl_con = new SSLConnection(sock);

                System.out.println("Establishing Secure Connection");
                if (ssl_con.server_handshake()) {
                    System.out.println("Done");
                } else {
                    System.err.println("Failed!");
                }
            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException ex) {
                Logger.getLogger(ChatWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
         recv_thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    Object recv = ssl_con.recvObject();
                    if (recv != null)
                        message_queue.put((String)recv);
                } catch (IOException | ClassNotFoundException | InterruptedException ex) {
                    Logger.getLogger(ChatWindow.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }
            }
        });
        recv_thread.start();

        //System.out.println(paramStr);
    }

    @Override
    public void start(Stage stage) throws Exception {
        send = new Button("Send");
        image = new Button("Image");
        quit = new Button("Quit");
        chatBox = new VBox(5);
        bottom = new HBox(3);
        message = new TextField();
        messages = new ArrayList<>();
        scrollP = new ScrollPane();
        scrollP.setPrefSize(500, 475);
        scrollP.setContent(chatBox);
        chatBox.getStyleClass().add("chatbox");
        bottom.getStyleClass().add("bottom");

        send.setOnAction(evt -> {
            uploadMessage();
        });

        message.setOnAction(evt -> {
            uploadMessage();
        });

        image.setOnAction(evt -> {
            FileChooser fileChooser = new FileChooser();

            fileChooser.setTitle("Open Image File...");
            imageFile = fileChooser.showOpenDialog(null);

            if (imageFile != null) {
                System.out.println("File selected: " + imageFile.getName());
            } else {
                System.out.println("File selection cancelled.");
            }
        });

        quit.setOnAction(evt -> {
            stage.close();

        });

        // toggle the visibility of 'rect' every 500ms
        Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent event) -> {
            downloadMessage();
        }));
        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsWonder.play();

        root.getStylesheets().add(getClass().getResource("Style.css").toExternalForm());

        connectionMessage = new Label(("You are connected to: ".concat(cms)).concat("!"));
        connectionMessage.getStyleClass().add("connectionmessage");

        bottom.getChildren().addAll(message, send, image, quit, connectionMessage);
        centerPane.getChildren().addAll(scrollP);
        root.setCenter(centerPane);
        root.setBottom(bottom);
        centerPane.getStyleClass().add("bottom");
        scene = new Scene(root, 500, 525);
        stage.setTitle(id + " Window");
        stage.setScene(scene);
        stage.show();

    }

    public void uploadMessage() {
        lastMessageSent = message.getText();
        Label messagetoadd = new Label(lastMessageSent);
        message.clear();
        messages.add(messagetoadd);
        messages.get(messageCount).setStyle("-fx-translate-x: 285;");
        chatBox.getChildren().add(messages.get(messageCount));
        messageCount++;
        
        try {
            ssl_con.sendObject(lastMessageSent);
        } catch (IOException ex) {
            Logger.getLogger(ChatWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void downloadMessage() {

        while (!message_queue.isEmpty()) {
            try {
                String s = (String)message_queue.take();
                Label messagetoadd = new Label(s);
                messages.add(messagetoadd);
                chatBox.getChildren().add(messages.get(messageCount));
                messageCount++;
            } catch (InterruptedException ex) {
                Logger.getLogger(ChatWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public File getImageFile() {
        return imageFile;
    }

    public String getTextBoxContent() {
        return lastMessageSent;
    }

}
