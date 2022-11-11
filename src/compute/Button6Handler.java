package compute;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class Button6Handler implements EventHandler<ActionEvent>
{
	private DataBaseHandler myDB;
	private TableView<Order> table;
	private Stage stage;
	
	public Button6Handler(DataBaseHandler myDB, Stage stage)
	{
		this.myDB = myDB;
		this.stage = stage;
	}
	
	@Override
	public void handle(ActionEvent arg0)
	{
		//table.setItems(myDB.getData());
//		PendingOrdersFactory kati = new PendingOrdersFactory();
//		kati.setDB(myDB);
		stage.close();
		PendingOrdersFactory kati = new PendingOrdersFactory();
		kati.setDB(myDB);
		kati.setStage(stage);
		kati.handle(arg0);
	}
	
	public void setTable(TableView<Order> table)
	{
		this.table = table;
	}
}
