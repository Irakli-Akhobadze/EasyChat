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
import java.util.ArrayList;
import java.util.List;

import Server.AttachedFile;
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
	
	private List<File> selectedFiles = new ArrayList<>();
	private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;
	
	private void sendMessage(String message) {

	    SocketWrapper selected = userBox.getValue();

	    if (selected == null) {
	        return;
	    }

	    // Remove attachment names from the text
	    String actualMessage = message;

	    for (File file : selectedFiles) {

	        actualMessage =
	                actualMessage.replaceFirst(
	                        java.util.regex.Pattern.quote(file.getName()),
	                        ""
	                );
	    }

	    actualMessage = actualMessage.trim();

	    if (actualMessage.isEmpty() && selectedFiles.isEmpty()) {
	        return;
	    }

	    try {

	        List<AttachedFile> attachments =
	                new ArrayList<>();

	        for (File file : selectedFiles) {

	            byte[] data =
	                    java.nio.file.Files.readAllBytes(
	                            file.toPath()
	                    );

	            attachments.add(
	                    new AttachedFile(
	                            file.getName(),
	                            data
	                    )
	            );
	        }

	        ChatMessage chatMessage =
	                new ChatMessage(
	                        "Connected User",
	                        selected.getName(),
	                        actualMessage,
	                        attachments
	                );

	        oO.writeObject(chatMessage);
	        oO.flush();

	        displayMessage("Connected User", actualMessage, true, attachments);

	        selectedFiles.clear();
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
	        boolean myMessage,
	        List<AttachedFile> files) {

	    Image image = new Image(
	            getClass().getResourceAsStream(
	                    "/Images/userLogo.png"
	            )
	    );

	    ImageView avatar = new ImageView(image);

	    avatar.setFitWidth(40);
	    avatar.setFitHeight(40);
	    avatar.setPreserveRatio(true);

	    Circle clip = new Circle(20, 20, 20);
	    avatar.setClip(clip);

	    Label name = new Label(username);

	    name.setStyle(
	            "-fx-font-weight: bold;"
	    );

	    VBox messageInfo = new VBox(3);

	    messageInfo.getChildren().add(name);

	    // MESSAGE TEXT
	    if (!message.trim().isEmpty()) {

	        Label text = new Label(message);

	        text.setWrapText(true);
	        text.setMaxWidth(350);

	        text.setStyle(
	                "-fx-background-color: #eeeeee;" +
	                "-fx-background-radius: 15;" +
	                "-fx-padding: 8 12 8 12;"
	        );

	        messageInfo.getChildren().add(text);
	    }

	    // FILES
	    for (AttachedFile file : files) {

	        Label fileLabel =
	                new Label("📎 " + file.getFileName());

	        fileLabel.setStyle(
	                "-fx-background-color: #dddddd;" +
	                "-fx-background-radius: 10;" +
	                "-fx-padding: 8 12 8 12;" +
	                "-fx-cursor: hand;"
	        );

	        fileLabel.setOnMouseClicked(e -> {

	            FileChooser chooser = new FileChooser();

	            chooser.setTitle("Save file");

	            chooser.setInitialFileName(
	                    file.getFileName()
	            );

	            File saveLocation =
	                    chooser.showSaveDialog(
	                            chatBox.getScene().getWindow()
	                    );

	            if (saveLocation != null) {

	                try {

	                    java.nio.file.Files.write(
	                            saveLocation.toPath(),
	                            file.getData()
	                    );

	                } catch (IOException ex) {

	                    ex.printStackTrace();
	                }
	            }
	        });

	        messageInfo.getChildren().add(fileLabel);
	    }

	    HBox messageBox = new HBox(10);

	    messageBox.setAlignment(Pos.TOP_LEFT);

	    messageBox.getChildren().addAll(
	            avatar,
	            messageInfo
	    );

	    if (myMessage) {
	        messageBox.setAlignment(Pos.TOP_RIGHT);
	    }

	    chatBox.getChildren().add(messageBox);

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

	        boolean hasText =
	                !newText.trim().isEmpty()
	                || !selectedFiles.isEmpty();

	        sendButton.setVisible(hasText);
	        sendButton.setManaged(hasText);
	    });
	    
	    connectToServer();
	    
	    
	}
	
	@FXML
	public void attachFiles() {

	    FileChooser fileChooser = new FileChooser();

	    fileChooser.setTitle("Choose files");

	    List<File> files =
	            fileChooser.showOpenMultipleDialog(
	                    button.getScene().getWindow()
	            );

	    if (files == null || files.isEmpty()) {
	        return;
	    }

	    long currentSize = 0;

	    // Size of files already selected
	    for (File file : selectedFiles) {
	        currentSize += file.length();
	    }

	    // Check newly selected files
	    for (File file : files) {

	        long newSize = currentSize + file.length();

	        if (newSize > MAX_FILE_SIZE) {

	            javafx.scene.control.Alert alert =
	                    new javafx.scene.control.Alert(
	                            javafx.scene.control.Alert.AlertType.WARNING
	                    );

	            alert.setTitle("File size limit");
	            alert.setHeaderText("20 MB limit exceeded");

	            alert.setContentText(
	                    "The total size of attached files cannot be more than 20 MB."
	            );

	            alert.showAndWait();

	            return;
	        }

	        selectedFiles.add(file);

	        currentSize = newSize;
	    }

	    updateMessageField();
	}
	
	private void updateMessageField() {

	    StringBuilder text = new StringBuilder();

	    // Show attached files
	    for (File file : selectedFiles) {

	        text.append(file.getName());
	        text.append("\n");
	    }

	    messageField.setText(text.toString());

	    // Put cursor after the file names
	    messageField.positionCaret(
	            messageField.getText().length()
	    );
	}
	
	private void connectToServer() {

	    try {
	        socket = new Socket("localhost", 3079);

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

	                        displayMessage(message.getSender(), message.getMessage(), false,message.getFiles());

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
