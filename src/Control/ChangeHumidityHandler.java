package Control;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Model.DataBaseHandler;
import Model.Order;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ChangeHumidityHandler implements EventHandler<ActionEvent>
{
	private Stage stage;
	private DataBaseHandler myDB;
	
	private Button select;
	private Button back;
	private Button cancel;
	
	private HBox hbox;
	private VBox vbox;
	
	private TextField textField = new TextField();;
	
	private Order order;
	private TableView<Order> table;
	
	private ArrayList<String> siloIDsPerOrder;
	private boolean flag;
	
	private HashMap<String, Order> changes3;
	private HashMap<String, HashMap<String, String>> currentHumidityValues;
	private HashMap<String, String> innerHashMap = new HashMap<String, String>();
    
    private ComboBox comboTest;
    private String tempString;
    private int selectClicks;
    
	
	public ChangeHumidityHandler(Stage stage)
	{
		this.stage = stage;
		this.myDB = DataBaseHandler.getInstance();
		flag = false;
//		innerHashMap = new HashMap<String, String>();
//		currentHumidityValues = new HashMap<String, HashMap<String, String>>();
		
	}
	
	@Override
	public void handle(ActionEvent arg0) 
	{
		selectClicks=0;
		
		textField.setText("0,0");
		
		order = table.getSelectionModel().getSelectedItem();	
		innerHashMap = new HashMap<String, String>();
		
		if(flag == false)
		{
			warningWindowForFlag();
		}else if(order.getHumidity().equals("ΟΧΙ"))
		{
			if(true)//check if order includes sensor for humidity
			{
				order.setHumidity("ΝΑΙ");
				table.getItems().set(table.getSelectionModel().getSelectedIndex(), order);
			}
		}else if(order.getHumidity().equals("ΝΑΙ"))
		{
			order.setHumidity("ΟΧΙ");
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
	
	private void prepareCurrentHumidityValues()
	{
		if(currentHumidityValues.get(order.getOrderCode())==null)
		{
			for(int i=0; i<myDB.getHumiditySilosPerOrder(order.getOrderCode()).size(); i++)
			{
				innerHashMap.put(myDB.getHumiditySilosPerOrder(order.getOrderCode()).get(i), "0,0");
//				System.out.println(myDB.getHumiditySilosPerOrder(order.getOrderCode()).get(i));
			}
			currentHumidityValues.put(order.getOrderCode(), innerHashMap);
		}else
		{
			warningWindowForFlag();
		}
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
    	alert.setTitle("Προειδοποίηση");
    	alert.setHeaderText("Καμία επιλογή");
    	alert.setContentText("Δεν έχει επιλεγεί καμία απο τις παραγγελίες\nΠαρακαλώ επιλέξτε κάποια πρωτού συνεχίσετε");
    	alert.showAndWait();
	}
}
