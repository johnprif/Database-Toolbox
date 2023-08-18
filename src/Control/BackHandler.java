package Control;

import java.util.HashMap;
import java.util.Optional;
import Model.Order;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class BackHandler implements EventHandler<ActionEvent>
{
	private Stage stage;
	private HashMap<String, Order> changes3;
	
	public BackHandler(Stage stage)
	{
		this.stage = stage;
	}
	
	@Override
	public void handle(ActionEvent arg0)
	{	
		if(changes3.size() == 0)
		{
			stage.close();
		}else
		{
			alertWindow();
		}
		
	}
	
	public void setChanges3(HashMap<String, Order> changes3)
	{
		this.changes3 = changes3;
	}
	
	private void alertWindow()
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("ATTENTION!");
		alert.setHeaderText("Unsaved changes!");
		alert.setContentText("You have made changes for which you have not selected 'Execute'. \nIf you wish to exit press the 'OK' button. \nIf you wish to make further changes press 'Cancel'.");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			stage.close();
		} else {
		    alert.close();
		}
	}
	
}
