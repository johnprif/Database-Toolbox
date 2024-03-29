package Control;

import java.sql.SQLException;
import java.util.Optional;

import Model.DataBaseHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class EmptyBaseHandler implements EventHandler<ActionEvent>
{
	private DataBaseHandler myDB;
	private RefreshHandler refresh;
	
	public EmptyBaseHandler(RefreshHandler refresh)
	{
		this.myDB = DataBaseHandler.getInstance();
		this.refresh = refresh;
	}
	

	@Override
	public void handle(ActionEvent arg0)
	{
		try {
			emptyDBconfirmationWindow();
		} catch (SQLException e) {
			errorWindow(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void emptyDBconfirmationWindow() throws SQLException
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Προειδοποίηση");
		alert.setHeaderText("Ολική Διαγραφή!");
		alert.setContentText("Είστε σίγουρος ότι επιθυμείτε να διαγράψετε όλα τα δεδομένα που βρίσκονται στην βάση; \nΠιέστε 'ΟΚ' για να συνέχεια \nΠιέστε 'Cancel' για ακύρωση");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK)
		{
			myDB.cleanDataBase();
			refresh.handle(null);
 		    // ... user chose OK
		} else {
		    // ... user chose CANCEL or closed the dialog
		}
	}
	
	private void errorWindow(String errorMessage)
	{
		Alert alert = new Alert(Alert.AlertType.ERROR);
    	alert.setTitle("Σφάλμα");
    	alert.setHeaderText(null);
    	alert.setContentText(errorMessage);
    	alert.showAndWait();
	}
}
