package Control;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import Model.DataBaseHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class Button7Handler implements EventHandler<ActionEvent>
{
	private DataBaseHandler myDB;
	private Button6Handler refresh;
	
	public Button7Handler(DataBaseHandler myDB, Button6Handler refresh)
	{
		this.myDB = myDB;
		this.refresh = refresh;
	}
	

	@Override
	public void handle(ActionEvent arg0)
	{
		try {
			confirmationWindow();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void confirmationWindow() throws SQLException
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("����� ��������!");
		alert.setContentText("����� �������� ��� ���������� �� ���������� ��� �� �������� ��� ���������� ���� ����?\n������ 'OK' ��� ��������\n������ 'Cancel' ��� �������");

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
}
