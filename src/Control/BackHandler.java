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
		alert.setTitle("ΠΟΣΟΧΗ!");
		alert.setHeaderText("Μη αποθηκευμένες αλλαγές!");
		alert.setContentText("Έχετε κάνει αλλαγές για τις οποίες δεν έχετε επιλέξει ΄Εκτέλεση'.\nΕάν επιθυμείτε να αποχωρήσετε πατήστε το πλήκτρο 'ΟΚ'.\nΕάν επιθυμείτε να κάνετε και άλλες αλλαγές πατήστε 'Cancel'.");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			stage.close();
		} else {
		    alert.close();
		}
	}
	
}
