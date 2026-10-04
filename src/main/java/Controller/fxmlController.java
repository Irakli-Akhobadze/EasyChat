package Controller;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import Server.ChatMessage;
import Server.ChatServer;
import Server.SocketWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

public class fxmlController {
	
	@FXML
	private Label title;
	
	@FXML
	private Button button;
	
	@FXML
	private TextArea messageField;
	
	@FXML
	private ScrollPane scrollPane;
	
	@FXML
	private Button sendButton;
	
	@FXML
	private ComboBox<SocketWrapper> userBox;
	
	@FXML
	private VBox chatBox;
	
	private Socket socket;
	private ObjectInputStream oI;
	private ObjectOutputStream oO;
	
	private void sendMessage(String message) {

	    SocketWrapper selected = userBox.getValue();

	    if (selected == null || message.trim().isEmpty()) {
	        return;
	    }

	    try {

	        ChatMessage chatMessage = new ChatMessage(
	                "Connected User",
	                selected.getName(),
	                message
	        );

	        oO.writeObject(chatMessage);
	        oO.flush();

	        // Display my message immediately
	        displayMessage(
	                "Connected User",
	                message,
	                true
	        );

	        messageField.clear();

	    } catch (IOException e) {

	        e.printStackTrace();
	    }
	}

	@FXML
	public void sendMessage() {
	    sendMessage(messageField.getText());
	}
	
	private void displayMessage(
	        String username,
	        String message,
	        boolean myMessage) {

	    // -------------------------
	    // USER IMAGE
	    // -------------------------

	    Image image = new Image(getClass().getResourceAsStream("/Images/userLogo.png"));

	    ImageView avatar = new ImageView(image);

	    avatar.setFitWidth(40);
	    avatar.setFitHeight(40);
	    avatar.setPreserveRatio(true);

	    // Make avatar circular
	    Circle clip = new Circle(20, 20, 20);
	    avatar.setClip(clip);


	    // -------------------------
	    // USERNAME
	    // -------------------------

	    Label name = new Label(username);

	    name.setStyle(
	            "-fx-font-weight: bold;"
	    );


	    // -------------------------
	    // MESSAGE
	    // -------------------------

	    Label text = new Label(message);

	    text.setWrapText(true);
	    text.setMaxWidth(350);

	    text.setStyle(
	            "-fx-background-color: #eeeeee;" +
	            "-fx-background-radius: 15;" +
	            "-fx-padding: 8 12 8 12;"
	    );


	    // -------------------------
	    // NAME + MESSAGE
	    // -------------------------

	    VBox messageInfo = new VBox(3);
	    messageInfo.getChildren().addAll(name, text);
	    
	    // avatar + message

	    HBox messageBox = new HBox(10);

	    messageBox.setAlignment(Pos.TOP_LEFT);
	    messageBox.getChildren().addAll(
	            avatar,
	            messageInfo
	    );

	    if (myMessage) {
	        messageBox.setAlignment(Pos.TOP_RIGHT);
	    }

	    // Add to chat
	    chatBox.getChildren().add(messageBox);

	    // Scroll to bottom
	    scrollPane.setVvalue(1.0);
	}
	
	@FXML
	public void initialize() throws ClassNotFoundException {
		Font.loadFont(getClass().getResourceAsStream("/Fonts/Bangers.ttf"), 30);
		Tooltip tooltip = new Tooltip("Attach");
		Tooltip tooltipSend = new Tooltip("Send");
	    tooltip.setShowDelay(javafx.util.Duration.millis(100));
	    tooltip.setShowDuration(javafx.util.Duration.seconds(5));
	    tooltipSend.setShowDelay(javafx.util.Duration.millis(100));
	    tooltipSend.setShowDuration(javafx.util.Duration.seconds(5));
	    button.setTooltip(tooltip);
	    sendButton.setTooltip(tooltipSend);
	    
	    messageField.textProperty().addListener((obs, oldText, newText) -> {
	        int lines = newText.split("\n", -1).length;
	        double height = 35 + (lines - 1) * 20;
	        height = Math.min(height, 120);
	        messageField.setPrefHeight(height);
	        boolean hasText = !newText.trim().isEmpty();
	        sendButton.setVisible(hasText);
	        sendButton.setManaged(hasText);
	    });
	    
	    connectToServer();
	    
	    
	}
	
	@FXML
	public void attachFiles() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose a file");
		File selectedFile = fileChooser.showOpenDialog(button.getScene().getWindow());
		
		if (selectedFile != null) {
	        messageField.setText(selectedFile.getName());
	    }
	}
	
	private void connectToServer() {

	    try {
	        socket = new Socket("localhost", 3073);

	        oO = new ObjectOutputStream(socket.getOutputStream());
	        oO.flush();

	        oI = new ObjectInputStream(socket.getInputStream());

	        listenForUsers();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	private void listenForUsers() {

	    Thread thread = new Thread(() -> {

	        try {

	            while (true) {

	                Object received = oI.readObject();

	                if (received instanceof List) {

	                    List<SocketWrapper> list =
	                            (List<SocketWrapper>) received;

	                    javafx.application.Platform.runLater(() -> {

	                        userBox.setItems(
	                                FXCollections.observableArrayList(list)
	                        );

	                    });

	                } else if (received instanceof ChatMessage) {

	                    ChatMessage message =
	                            (ChatMessage) received;

	                    javafx.application.Platform.runLater(() -> {

	                        displayMessage(
	                                message.getSender(),
	                                message.getMessage(),
	                                false
	                        );

	                    });
	                }
	            }

	        } catch (IOException | ClassNotFoundException e) {

	            e.printStackTrace();
	        }
	    });

	    thread.setDaemon(true);
	    thread.start();
	}

	
	
	
	
}
