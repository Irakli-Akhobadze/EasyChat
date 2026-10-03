package Controller;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
	public void sendMessage() {

	    
	}
	
	@FXML
	public void initialize() {
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

	
	
	
	
}
