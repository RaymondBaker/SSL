package GUI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private int messageCount;

    public void init() {
        Application.Parameters p = this.getParameters();
        //Map<String, String> namedParams = p.getNamed();
        this.id = p.getUnnamed().toString();
        //List<String> rawParams = p.getRaw();
        //String paramStr = "Named Parameters: " + namedParams + "\n" +
        //"Unnamed Parameters: " + unnamedParams + "\n" +
        //"Raw Parameters: " + rawParams;

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

        root.getStylesheets().add(getClass().getResource("Style.css").toExternalForm());

        if (id.equals("Client")) {
            cms = "Server";
        } else {
            cms = "Client";
        }

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
        downloadMessage("test");
    }

    public void downloadMessage(String s) {
        Label messagetoadd = new Label(s);
        messages.add(messagetoadd);
        chatBox.getChildren().add(messages.get(messageCount));
        messageCount++;
    }

    public File getImageFile() {
        return imageFile;
    }

    public String getTextBoxContent() {
        return lastMessageSent;
    }

    public static void main(String[] args) {

    }

}
