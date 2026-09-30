package Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class fxmlController {
	
	@FXML
	private Label title;
	
	@FXML
	private Button button;
	
	@FXML
	public void initialize() {
		Font.loadFont(getClass().getResourceAsStream("/Fonts/Bangers.ttf"), 30);
	}
}
