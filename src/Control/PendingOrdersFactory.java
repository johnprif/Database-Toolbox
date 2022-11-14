package Control;

import Model.DataBaseHandler;
import Model.Order;
import Model.TakeThePath;
import View.PendingOrdersGUI;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

public class PendingOrdersFactory implements EventHandler<ActionEvent>
{
	private ObservableList<Order> data;
	private DataBaseHandler myDB;
	private TakeThePath takeThePath;
	private Stage stage;
	
	public PendingOrdersFactory()
	{
		takeThePath = TakeThePath.getInstance();
	}

	public void setDB()
	{
		this.myDB = DataBaseHandler.getInstance();
		
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
		PendingOrdersGUI pendingOrdersGUI = new PendingOrdersGUI(stage);
		pendingOrdersGUI.initialize();
	}
	
	private void checkIfFileIsLoaded()
	{
		
	}
}
