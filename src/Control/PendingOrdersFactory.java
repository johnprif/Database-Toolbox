package Control;

import Model.DataBaseHandler;
import View.PendingOrdersGUI;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

public class PendingOrdersFactory implements EventHandler<ActionEvent>
{
	private DataBaseHandler myDB;
	private PathHandler takeThePath;
	private Stage stage;
	
	public PendingOrdersFactory()
	{
		takeThePath = PathHandler.getInstance();
	}

	public void setDB()
	{
		this.myDB = DataBaseHandler.getInstance();
		
	}
	
	@Override
	public void handle(ActionEvent arg0)
	{	
		stage = new Stage();
		myDB.clearOldData();
		myDB.setPath(takeThePath.getPath());
		myDB.initialize();
		PendingOrdersGUI pendingOrdersGUI = new PendingOrdersGUI(stage);
		pendingOrdersGUI.initialize();
	}
	
	
}
