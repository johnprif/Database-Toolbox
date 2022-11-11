package Control;

import Model.DataBaseHandler;
import Model.Order;
import Model.TakeThePath;
import View.PendingOrdersGUI;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class PendingOrdersFactory implements EventHandler<ActionEvent>
{
	private ObservableList<Order> data;
	private DataBaseHandler myDB;
	private TakeThePath takeThePath;
	private Stage stage;
	
	public PendingOrdersFactory()
	{
		takeThePath = new TakeThePath();
	}

	public void setDB(DataBaseHandler myDB)
	{
		this.myDB = myDB;
		
	}
	
	public void setStage(Stage stage)
	{
		this.stage = stage;
	}
	
	@Override
	public void handle(ActionEvent arg0)
	{	
		myDB.clearOldData();
		myDB.setPath(takeThePath.getPath());
		myDB.initialize();
		PendingOrdersGUI pendingOrdersGUI = new PendingOrdersGUI(myDB, stage);
		pendingOrdersGUI.initialize();
	}
	
	private void checkIfFileIsLoaded()
	{
		
	}
}
