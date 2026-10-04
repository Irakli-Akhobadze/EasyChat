package Controller;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import Server.ChatServer;
import Server.SocketWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
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
	
	private Socket socket;
	private ObjectInputStream oI;
	
	@FXML
	public void sendMessage() {

	    
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
	        socket = new Socket("localhost", 3063);
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
	                List<SocketWrapper> list = (List<SocketWrapper>) received;
	                javafx.application.Platform.runLater(() -> {
	                    userBox.setItems(
	                            FXCollections.observableArrayList(list)
	                    );
	                });
	            }

	        } catch (IOException | ClassNotFoundException e) {
	            e.printStackTrace();
	        }

	    });

	    thread.setDaemon(true);
	    thread.start();
	}

	
	
	
	
}
