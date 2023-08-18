package Control;

import java.util.ArrayList;
import java.util.HashMap;
import Model.DataBaseHandler;
import Model.Order;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class ChangeHumidityHandler implements EventHandler<ActionEvent>
{
	private Stage stage;
	private DataBaseHandler myDB;
	
	private Order order;
	private TableView<Order> table;
	private boolean flag;
	
	private HashMap<String, Order> changes3;
	private HashMap<String, HashMap<String, String>> currentHumidityValues;
	private HashMap<String, String> innerHashMap = new HashMap<String, String>();
	
	public ChangeHumidityHandler()
	{
		this.myDB = DataBaseHandler.getInstance();
		flag = false;	
	}
	
	@Override
	public void handle(ActionEvent arg0) 
	{	
		order = table.getSelectionModel().getSelectedItem();	
		innerHashMap = new HashMap<String, String>();
		
		if(flag == false)
		{
			warningWindowForFlag();
		}else if(order.getHumidity().equals("NO"))
		{
			if(true)//check if order includes sensor for humidity
			{
				order.setHumidity("YES");
				table.getItems().set(table.getSelectionModel().getSelectedIndex(), order);
			}
		}else if(order.getHumidity().equals("YES"))
		{
			order.setHumidity("NO");
			table.getItems().set(table.getSelectionModel().getSelectedIndex(), order);
		}		
	}
	
	public HashMap<String, String> getHumiditySilos()
	{
		return myDB.getHumiditySilos();
	}
	
	public ArrayList<String> getHumidityIDs()
	{
		return myDB.getHumidityIDs();
	}
	
	public void setCurrentHumidityValues(HashMap<String, HashMap<String, String>> currentHumidityValues)
	{
		this.currentHumidityValues = currentHumidityValues;
	}

	public void setChanges3(HashMap<String, Order> changes3)
	{
		this.changes3 = changes3;
	}
	
	public void setTable(TableView<Order> table)
	{
		this.table = table;
	}
	
	public void setFlag(boolean flag)
	{
		this.flag = flag;
	}
	
	private void warningWindowForFlag()
	{
		Alert alert = new Alert(Alert.AlertType.WARNING);
    	alert.setTitle("Warning");
    	alert.setHeaderText("Empty choice!");
    	alert.setContentText("None of the orders have been selected. \nPlease select one before continuing.");
    	alert.showAndWait();
	}
}
