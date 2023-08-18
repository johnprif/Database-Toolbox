package View;

import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap; // import the HashMap class
import Control.ChangeTimeHandler;
import Control.ExecuteHandler;
import Control.BackHandler;
import Control.ChangeHumidityHandler;
import Control.ChangeShippingNumberHandler;
import Control.RefreshHandler;
import Control.EmptyBaseHandler;
import Model.DataBaseHandler;
import Model.Order;

public class PendingOrdersGUI 
{
	private ObservableList<Order> data;
	private DataBaseHandler myDB;
	
	private TableColumn<Order, String> OrderCode;
	private TableColumn<Order, String> RecipeCode;
	private TableColumn<Order, String> Quantity;
	private TableColumn<Order, String> ProjectCode;
	private TableColumn<Order, String> CustomerCode;
	private TableColumn<Order, String> VehicleCode;
	private TableColumn<Order, String> DriverCode;
	private TableColumn<Order, String> DateCreation;
	private TableColumn<Order, String> TimeCreation;
	private TableColumn<Order, String> ExecutionDate;    
	private TableColumn<Order, String> ExecutionTime;
	private TableColumn<Order, String> Humidity;
	
	private Scene scene;
	private Stage stage;	
    private TableView<Order> table;
    
    private Button refreshButton;
    private Button changeTimeCreationButton;
	private Button changeTimeExecutionButton;	
	private Button changeShippingNumberButton;
	private Button changeHumidityButton;
	private Button executeButton;
	private Button backButton;
	private Button emptyBaseButton;

    private VBox vbox2;
    private VBox vbox4;
    
    private RefreshHandler refreshHandler;
    private ChangeTimeHandler changeTimeCreationHandler;
    private ChangeTimeHandler changeTimeExecutionHandler;
    private ChangeShippingNumberHandler changeShippingNumberHandler;
    private ChangeHumidityHandler changeHumidityHandler;
    private ExecuteHandler executeHandler;
    private BackHandler backHandler; 
    private EmptyBaseHandler emptyBaseHandler;

    private HashMap<String, Order> changes3;

    private boolean flag = false; 
    private Label tempLabel_1 = new Label("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    private Label humidityLabel;
    
    private ArrayList<Spinner<Double>> spinners;
    private ArrayList<SpinnerValueFactory<Double>> spinnersValueFactories;
    private ArrayList<Label> spinnersLabels;
       
    private FileInputStream inputstreamRefreshButton;
  	private Image imageRefreshButton;
    private ImageView viewRefreshButton; 
    
    private FileInputStream inputstreamChangeTimeCreationButton;
  	private Image imageChangeTimeCreationButton;
    private ImageView viewChangeTimeCreationButton;
    
    private FileInputStream inputstreamChangeTimeExecutionButton;
  	private Image imageChangeTimeExecutionButton;
    private ImageView viewChangeTimeExecutionButton;
    
    private FileInputStream inputstreamChangeShippingNumberButton;
  	private Image imageChangeShippingNumberButton;
    private ImageView viewChangeShippingNumberButton;
    
    private FileInputStream inputstreamChangeHumidityButton;
  	private Image imageChangeHumidityButton;
    private ImageView viewChangeHumidityButton;
      
    private FileInputStream inputstreamExecuteButton;
	private Image imageExecuteButton;
    private ImageView viewExecuteButton;
    
    private FileInputStream inputstreamEmptyBaseButton;
	private Image imageEmptyBaseButton;
    private ImageView viewEmptyBaseButton;
    
    private FileInputStream inputstreamBackButton;
	private Image imageBackButton;
    private ImageView viewBackButton;
    
    //Spinner spinner = new Spinner(0, 10, 0, 1); //min, max, start, step
    //https://o7planning.org/11185/javafx-spinner
    
	public PendingOrdersGUI(Stage stage)
	{
		changes3 = new HashMap<String, Order>();
		this.myDB = DataBaseHandler.getInstance();
		this.stage = stage;
	}
	
	public void initialize()
	{		
		
		flag = false;
		
		createStage();
		createIcons();
		createButtons();
		createHumidityLabel();		
		createHandlers();		
		setChanges3();		
		
		BorderPane border = new BorderPane();
		border.setId("pendingBorder");
		
		setHandlers();
		
		myDB.findAndParse();
		data = myDB.getData();		
		
		createHumidityCells();		
		createTable();
        createAndFillCells();
        setTable();
              
        scene = new Scene(border);
        scene.getStylesheets().add("application.css");
        setLastThingsOnTable();
        
        vbox2 = new VBox(15, refreshButton, changeTimeCreationButton, changeTimeExecutionButton, changeShippingNumberButton, changeHumidityButton, executeButton, backButton, humidityLabel, vbox4, tempLabel_1, emptyBaseButton);      
        vbox2.setId("vbox2");
                
        border.setCenter(table);
        border.setRight(vbox2); //gridPane
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
	    stage.show();	    
	}
	
	private void createIcons()
	{
		refreshButtonIcon();
		changeTimeCreationButtonIcon();
		changeTimeExecutionButtonIcon();
		changeShippingNumberButtonIcon();	
		changeHumidityButtonIcon();
		executeButtonIcon();
		backButtonIcon();
		emptyBaseButtonIcon();
	}
	
	private void refreshButtonIcon()
	{	
		URL url = getClass().getResource("/Icons/RefreshButton.png");			
		imageRefreshButton = new Image(url.toString()); 
		viewRefreshButton = new ImageView(imageRefreshButton);
		viewRefreshButton.setFitHeight(20); 
		viewRefreshButton.setFitWidth(20);
	}
	
	private void changeTimeCreationButtonIcon()
	{	
		URL url = getClass().getResource("/Icons/CreateTimeButton.png");	
		imageChangeTimeCreationButton = new Image(url.toString()); 
		viewChangeTimeCreationButton = new ImageView(imageChangeTimeCreationButton);
		viewChangeTimeCreationButton.setFitHeight(20); 
		viewChangeTimeCreationButton.setFitWidth(20);
	}
	
	private void changeTimeExecutionButtonIcon()
	{
		URL url = getClass().getResource("/Icons/ExecuteTimeButton.png");						
		imageChangeTimeExecutionButton = new Image(url.toString()); 
		viewChangeTimeExecutionButton = new ImageView(imageChangeTimeExecutionButton);
		viewChangeTimeExecutionButton.setFitHeight(20); 
		viewChangeTimeExecutionButton.setFitWidth(20);
	}
	
	private void changeShippingNumberButtonIcon()
	{		
		URL url = getClass().getResource("/Icons/ShippingButton.png");						
		imageChangeShippingNumberButton = new Image(url.toString()); 
		viewChangeShippingNumberButton = new ImageView(imageChangeShippingNumberButton);
		viewChangeShippingNumberButton.setFitHeight(20); 
		viewChangeShippingNumberButton.setFitWidth(20);
	}
	
	private void changeHumidityButtonIcon()
	{	
		URL url = getClass().getResource("/Icons/HumidityButton.png");			
		imageChangeHumidityButton = new Image(url.toString()); 
		viewChangeHumidityButton = new ImageView(imageChangeHumidityButton);
		viewChangeHumidityButton.setFitHeight(20); 
		viewChangeHumidityButton.setFitWidth(20);
	}
	
	private void executeButtonIcon() 
	{    
		URL url = getClass().getResource("/Icons/ExecuteButton.png");			
		imageExecuteButton = new Image(url.toString()); 
		viewExecuteButton = new ImageView(imageExecuteButton);
		viewExecuteButton.setFitHeight(20); 
		viewExecuteButton.setFitWidth(20);
	}
	
	private void backButtonIcon()
	{		
		URL url = getClass().getResource("/Icons/BackButton.png");				
		imageBackButton = new Image(url.toString()); 
		viewBackButton = new ImageView(imageBackButton);
		viewBackButton.setFitHeight(20); 
		viewBackButton.setFitWidth(20);
	}
	
	private void emptyBaseButtonIcon()
	{   		
		URL url = getClass().getResource("/Icons/DeleteButton.png");			
		imageEmptyBaseButton = new Image(url.toString()); 
		viewEmptyBaseButton = new ImageView(imageEmptyBaseButton);
		viewEmptyBaseButton.setFitHeight(20); 
		viewEmptyBaseButton.setFitWidth(20);
	}
	
	private void createButtons()
	{		
		refreshButton = new Button("Refresh");
		changeTimeCreationButton = new Button("Change Creation Time");
		changeTimeExecutionButton = new Button("Change Execution Time");
		changeShippingNumberButton = new Button("Change Shipping Number");	
		changeHumidityButton = new Button("Include humidities");
		executeButton = new Button("Execute");
		backButton = new Button("Back");
		emptyBaseButton = new Button("Empty Database");
		
		executeButton.setId("executeButton");
		emptyBaseButton.setId("exitButton");
		
		refreshButton.setMaxWidth(Double.MAX_VALUE);
		changeTimeCreationButton.setMaxWidth(Double.MAX_VALUE);
	    changeTimeExecutionButton.setMaxWidth(Double.MAX_VALUE);
	    changeShippingNumberButton.setMaxWidth(Double.MAX_VALUE);
	    changeHumidityButton.setMaxWidth(Double.MAX_VALUE);
	    executeButton.setMaxWidth(Double.MAX_VALUE);
	    backButton.setMaxWidth(Double.MAX_VALUE);
	    emptyBaseButton.setMaxWidth(Double.MAX_VALUE);
	    	    
	    refreshButton.setGraphic(viewRefreshButton);
	    changeTimeCreationButton.setGraphic(viewChangeTimeCreationButton);
	    changeTimeExecutionButton.setGraphic(viewChangeTimeExecutionButton);
	    changeShippingNumberButton.setGraphic(viewChangeShippingNumberButton);
	    changeHumidityButton.setGraphic(viewChangeHumidityButton);
	    executeButton.setGraphic(viewExecuteButton);
	    backButton.setGraphic(viewBackButton);
	    emptyBaseButton.setGraphic(viewEmptyBaseButton);
	    
	    refreshButton.setCursor(Cursor.HAND);
	    changeTimeCreationButton.setCursor(Cursor.HAND);
	    changeTimeExecutionButton.setCursor(Cursor.HAND);
	    changeShippingNumberButton.setCursor(Cursor.HAND);
	    changeHumidityButton.setCursor(Cursor.HAND);
	    executeButton.setCursor(Cursor.HAND);
	    backButton.setCursor(Cursor.HAND);
	    emptyBaseButton.setCursor(Cursor.HAND);	    
	}
	
	private void createHumidityLabel()
	{
		humidityLabel = new Label("HUMIDITIES");
		humidityLabel.setId("humidityLabel");
		humidityLabel.setMaxWidth(Double.MAX_VALUE);
	}
	
	private void createHandlers()
	{
		refreshHandler = new RefreshHandler(stage);
		changeTimeCreationHandler = new ChangeTimeHandler(changeTimeCreationButton);
		changeTimeExecutionHandler = new ChangeTimeHandler(changeTimeExecutionButton);
		changeShippingNumberHandler = new ChangeShippingNumberHandler();
		changeHumidityHandler = new ChangeHumidityHandler();
		executeHandler = new ExecuteHandler();
		backHandler = new BackHandler(stage);
		emptyBaseHandler = new EmptyBaseHandler(refreshHandler);
	}
	
	private void setChanges3()
	{
		changeTimeCreationHandler.setChanges3(changes3);
		changeTimeExecutionHandler.setChanges3(changes3);
		changeHumidityHandler.setChanges3(changes3);
		executeHandler.setChanges3(changes3);
		backHandler.setChanges3(changes3);
	}
	
	private void setHandlers()
	{
		refreshButton.setOnAction(refreshHandler);
		
		//=====================IT WORKS FINE===================================
		changeTimeCreationButton.setOnAction(changeTimeCreationHandler);
	    changeTimeExecutionButton.setOnAction(changeTimeExecutionHandler);
	    //=====================================================================

	    changeShippingNumberButton.setOnAction(changeShippingNumberHandler);
	    changeHumidityButton.setOnAction(changeHumidityHandler);
	    executeButton.setOnAction(executeHandler);
	    backButton.setOnAction(backHandler);
	    emptyBaseButton.setOnAction(emptyBaseHandler);
	    
	    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) 
	          {
//	        	  backHandler.handle(null);
//	        	  dateStage.close();
//	              stage.close();	              
	          }
	      });	
	}
	
	private void createHumidityCells()
	{
		vbox4 = new VBox(15);
		vbox4.setMaxWidth(Double.MAX_VALUE);
		vbox4.setId("vbox4");
		
		HashMap <String, String> humiditySilos = new HashMap<String, String>(changeHumidityHandler.getHumiditySilos());
		ArrayList <String> humidityIDs = new ArrayList<String>(changeHumidityHandler.getHumidityIDs());
			
		spinners = new ArrayList<Spinner<Double>>();	
		spinnersValueFactories = new ArrayList<SpinnerValueFactory<Double>>();
		spinnersLabels = new ArrayList<Label>();
		
		
		if(humidityIDs.size()==0)
		{
			Label label2 = new Label("The humidity cannot be modified in");
			
			Label label3 = new Label("any order because there are no");
			
			Label label4 = new Label("sensors because there are no sensors");
			
			Label label5 = new Label("and it cannot be adjusted manually");
			
			vbox4.getChildren().addAll(label2, label3, label4, label5);
		}else
		{
			for(int i=0; i<humidityIDs.size(); i++)
			{
				Label label1 = new Label(humiditySilos.get(humidityIDs.get(i)));
				
				Spinner<Double> spinner1 = new Spinner<Double>();	
				SpinnerValueFactory<Double> valueFactory1 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, 0.0, 0.1);;
				spinner1.setValueFactory(valueFactory1);
				spinner1.editorProperty().get().setAlignment(Pos.CENTER);
				spinner1.setCursor(Cursor.HAND);
				
				spinners.add(spinner1);
				spinnersValueFactories.add(valueFactory1);
				spinnersLabels.add(label1);
				
				vbox4.getChildren().add(label1);
				vbox4.getChildren().add(spinner1);
			}
		}				
	}
	
	private void setTable()
	{
		changeTimeCreationHandler.setTable(table);
        changeTimeExecutionHandler.setTable(table);
        changeHumidityHandler.setTable(table);
        executeHandler.setTable(table);
        executeHandler.setSpinners(spinners);
        refreshHandler.setTable(table);
	}
	
	private void createStage()
	{
        stage.setTitle("Pending Orders List -> "+myDB.getPath());
        stage.setMinWidth(800);
        stage.setMinHeight(600);        
        stage.setWidth(1366);
        stage.setHeight(768);
        stage.setResizable(true);
	}
	
	private void createTable()
	{
		table = new TableView<Order>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setEditable(true);
        table.setTableMenuButtonVisible(true);
	}
	
	private void createAndFillCells()
	{

        OrderCode = new TableColumn<Order, String>("CUSTOMER\nCODE");
        OrderCode.setCellFactory(TextFieldTableCell.forTableColumn());
        OrderCode.setCellValueFactory(new PropertyValueFactory<Order, String>("OrderCode"));
        OrderCode.setEditable(false);  
                
        RecipeCode = new TableColumn<Order, String>("RECIPE\nCODE");
        RecipeCode.setCellFactory(TextFieldTableCell.forTableColumn());
        RecipeCode.setCellValueFactory(new PropertyValueFactory<Order, String>("RecipeCode"));
        RecipeCode.setEditable(false);
               
        Quantity = new TableColumn<Order, String>("QUANTITY");
        Quantity.setCellFactory(TextFieldTableCell.forTableColumn());
        Quantity.setCellValueFactory(new PropertyValueFactory<Order, String>("Quantity"));
        Quantity.setEditable(false);
               
        ProjectCode = new TableColumn<Order, String>("PROJECT\nCODE");
        ProjectCode.setCellFactory(TextFieldTableCell.forTableColumn());
        ProjectCode.setCellValueFactory(new PropertyValueFactory<Order, String>("ProjectCode"));
        ProjectCode.setEditable(false);
               
        CustomerCode = new TableColumn<Order, String>("CUSTOMER\nCODE");
        CustomerCode.setCellFactory(TextFieldTableCell.forTableColumn());
        CustomerCode.setCellValueFactory(new PropertyValueFactory<Order, String>("CustomerCode"));
        CustomerCode.setEditable(false);
               
        VehicleCode = new TableColumn<Order, String>("VEHICLE\nCODE");
        VehicleCode.setCellFactory(TextFieldTableCell.forTableColumn());
        VehicleCode.setCellValueFactory(new PropertyValueFactory<Order, String>("VehicleCode"));
        VehicleCode.setEditable(false);
               
        DriverCode = new TableColumn<Order, String>("DRIVER\nCODE");
        DriverCode.setCellFactory(TextFieldTableCell.forTableColumn());
        DriverCode.setCellValueFactory(new PropertyValueFactory<Order, String>("DriverCode"));
        DriverCode.setEditable(false);
              
        DateCreation = new TableColumn<Order, String>("CREATION\nDATE");
        DateCreation.setCellFactory(TextFieldTableCell.forTableColumn());
        DateCreation.setCellValueFactory(new PropertyValueFactory<Order, String>("DateCreation"));
        DateCreation.setEditable(false);
        
        TimeCreation = new TableColumn<Order, String>("CREATION\nTIME");
        TimeCreation.setCellFactory(TextFieldTableCell.forTableColumn());
        TimeCreation.setCellValueFactory(new PropertyValueFactory<Order, String>("TimeCreation"));
        TimeCreation.setEditable(false);
        
        ExecutionDate = new TableColumn<Order, String>("EXECUTION\nDATE");
        ExecutionDate.setCellFactory(TextFieldTableCell.forTableColumn());
    	ExecutionDate.setCellValueFactory(new PropertyValueFactory<Order, String>("ExecutionDate"));
    	ExecutionDate.setEditable(false);
    	
    	ExecutionTime = new TableColumn<Order, String>("EXECUTION\nTIME");
    	ExecutionTime.setCellFactory(TextFieldTableCell.forTableColumn());
    	ExecutionTime.setCellValueFactory(new PropertyValueFactory<Order, String>("ExecutionTime"));
    	ExecutionTime.setEditable(false);
    	
    	Humidity = new TableColumn<Order, String>("INCLUDE\nHUMIDITIES");
    	Humidity.setCellFactory(TextFieldTableCell.forTableColumn());
    	Humidity.setCellValueFactory(new PropertyValueFactory<Order, String>("Humidity"));
    	Humidity.setEditable(false);
	}
		
	private void setLastThingsOnTable()
	{		
		table.setItems(data);
        table.getColumns().addAll(OrderCode, RecipeCode, Quantity, ProjectCode, CustomerCode, VehicleCode, DriverCode, DateCreation, TimeCreation, ExecutionDate, ExecutionTime, Humidity);
                
        table.getSelectionModel().setCellSelectionEnabled(false);
        
        ObservableList<Order> orderList = table.getSelectionModel().getSelectedItems();	  
        
	    orderList.addListener(new ListChangeListener()
	    		{
	    			@Override
	    			public void onChanged(Change c)
	    			{
	    				flag = true;
	    				changeTimeCreationHandler.setFlag(flag);
	    				changeTimeExecutionHandler.setFlag(flag);
	    				changeHumidityHandler.setFlag(flag);
	    				executeHandler.setFlag(flag);
	    			}	
	    		});
	}
}