package View;

import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.sql.SQLException;
import java.util.ArrayList;

import javafx.beans.binding.Bindings;
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
	private TableColumn<String, String> siloIDs;
	
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
	
	private GridPane gridPane;
    private int counter = 0;
    private String order;
    private VBox vbox2;
    private VBox vbox3;
    
    private RefreshHandler refreshHandler;
    private ChangeTimeHandler changeTimeCreationHandler;
    private ChangeTimeHandler changeTimeExecutionHandler;
    private ChangeShippingNumberHandler changeShippingNumberHandler;
    private ChangeHumidityHandler changeHumidityHandler;
    private ExecuteHandler executeHandler;
    private BackHandler backHandler; 
    private EmptyBaseHandler emptyBaseHandler;
    
    private VBox kati ;
    private ArrayList<String[]> changes;
    private ArrayList<Order> changes2;
    private HashMap<String, Order> changes3;
    private HashMap<String, HashMap<String, String>> currentHumidityValues = new HashMap<String, HashMap<String, String>>();
    private boolean flag = false; 
    private Stage dateStage;
    private Stage shippingStage;
    private Label tempLabel_1 = new Label("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    private Label temp;
    
//    Spinner spinner = new Spinner(0, 10, 0, 1); //min, max, start, step
    
    Spinner<Double> spinner = new Spinner<Double>();

    // Value factory.
    SpinnerValueFactory<Double> valueFactory = //
            new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, 0.0, 0.1);

    //https://o7planning.org/11185/javafx-spinner
    
    private ArrayList<Label> labelsArrayList;

    
	public PendingOrdersGUI(Stage stage)
	{
		changes3 = new HashMap<String, Order>();
//		currentHumidityValues = new HashMap<String, HashMap<String, String>>();
		this.myDB = DataBaseHandler.getInstance();
//		myDB.initialize();
		dateStage = new Stage();
		shippingStage = new Stage();
		this.stage = stage;
		
		//===================================================================================
		
		spinner.setValueFactory(valueFactory);
//		spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);	
		spinner.editorProperty().get().setAlignment(Pos.CENTER);
		
		//===================================================================================
	}
	
	public void initialize()
	{		
		createStage();
		createButtons();
		
		refreshHandler = new RefreshHandler(stage);
		changeTimeCreationHandler = new ChangeTimeHandler(changeTimeCreationButton, dateStage);
		changeTimeExecutionHandler = new ChangeTimeHandler(changeTimeExecutionButton, dateStage);
		changeShippingNumberHandler = new ChangeShippingNumberHandler(shippingStage);
		changeHumidityHandler = new ChangeHumidityHandler(shippingStage);
		executeHandler = new ExecuteHandler();
		backHandler = new BackHandler(stage);
		emptyBaseHandler = new EmptyBaseHandler(refreshHandler);
		
		changeTimeCreationHandler.setChanges3(changes3);
		changeTimeExecutionHandler.setChanges3(changes3);
		changeHumidityHandler.setChanges3(changes3);
		changeHumidityHandler.setCurrentHumidityValues(currentHumidityValues);
		executeHandler.setChanges3(changes3);
		executeHandler.setCurrentHumidityValues(currentHumidityValues);
		backHandler.setChanges3(changes3);
		
		BorderPane border = new BorderPane();
		refreshButton.setOnAction(refreshHandler);
		
		
		//=====================IT WORKS FINE===================================
		changeTimeCreationButton.setOnAction(changeTimeCreationHandler);
	    changeTimeExecutionButton.setOnAction(changeTimeExecutionHandler);
	  //=========================================================================

	    changeShippingNumberButton.setOnAction(changeShippingNumberHandler);
	    changeHumidityButton.setOnAction(changeHumidityHandler);
	    executeButton.setOnAction(executeHandler);
	    backButton.setOnAction(backHandler);
	    emptyBaseButton.setOnAction(emptyBaseHandler);;
		
		myDB.findAndParse();
		data = myDB.getData();		
		
		createTable();
        createAndFillCells();
        
        changeTimeCreationHandler.setTable(table);
        changeTimeExecutionHandler.setTable(table);
        changeHumidityHandler.setTable(table);
        executeHandler.setTable(table);
        refreshHandler.setTable(table);
        
        scene = new Scene(border);
        setLastThingsOnTable();
        
        
        
//        vbox2 = new VBox(15, refreshButton, changeTimeCreationButton, changeTimeExecutionButton, changeShippingNumberButton, changeHumidityButton, executeButton, backButton, tempLabel_1,  emptyBaseButton);
        
        vbox2 = new VBox(15, refreshButton, changeTimeCreationButton, changeTimeExecutionButton, changeShippingNumberButton, changeHumidityButton, executeButton, backButton, spinner, tempLabel_1, emptyBaseButton);

        
        border.setStyle("-fx-background-color: dodgerblue;");
		border.setPadding(new Insets(5));
        border.setCenter(table);
        border.setRight(vbox2); //gridPane

	    stage.setResizable(true);
        stage.setScene(scene);
	    stage.show();
	    
	    dateStage.initOwner(stage);
	    dateStage.initModality(Modality.WINDOW_MODAL);
	    
	    shippingStage.initOwner(stage);
	    shippingStage.initModality(Modality.WINDOW_MODAL);
	    
	    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) 
	          {
	        	  dateStage.close();
	              stage.close();
	              
	          }
	      });	
	}
	
	private void createButtons()
	{		
		refreshButton = new Button("Ανανέωση");
		changeTimeCreationButton = new Button("Αλλαγή Χρόνου Δημιουργίας");
		changeTimeExecutionButton = new Button("Αλλαγή Χρόνου Εκτέλεσης");
		changeShippingNumberButton = new Button("Αλλαγή Αριθμού Αποστολής");	
		changeHumidityButton = new Button("Υγρασίες");
		executeButton = new Button("Εκτέλεση");
		backButton = new Button("Πίσω");
		emptyBaseButton = new Button("Άδειασμα Βάσης");
		
		refreshButton.setMaxWidth(Double.MAX_VALUE);
		changeTimeCreationButton.setMaxWidth(Double.MAX_VALUE);
	    changeTimeExecutionButton.setMaxWidth(Double.MAX_VALUE);
	    changeShippingNumberButton.setMaxWidth(Double.MAX_VALUE);
	    changeHumidityButton.setMaxWidth(Double.MAX_VALUE);
	    executeButton.setMaxWidth(Double.MAX_VALUE);
	    backButton.setMaxWidth(Double.MAX_VALUE);
	    emptyBaseButton.setMaxWidth(Double.MAX_VALUE);
	    
	    refreshButton.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    changeTimeCreationButton.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    changeTimeExecutionButton.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    changeShippingNumberButton.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    changeHumidityButton.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    executeButton.setStyle("-fx-font-weight: bold; -fx-text-fill: green; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    backButton.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");	    
	    emptyBaseButton.setStyle("-fx-font-weight: bold; -fx-text-fill: red; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	}
	
	private void createStage()
	{
		//stage = new Stage();
        stage.setTitle("Λίστα Παραγγελιών σε Εκκρεμότητα -> "+myDB.getPath());
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        
        stage.setWidth(1366);
        stage.setHeight(768);
        
	}
	
	private void createTable()
	{
		table = new TableView<Order>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setEditable(true);
        table.setTableMenuButtonVisible(true);
	}

//	private void createGridPane()
//	{
//		
//		gridPane = new GridPane();
//		gridPane.setAlignment(Pos.BASELINE_CENTER);
//		gridPane.add(button1, 0, 0);
//		gridPane.add(button2, 0, 1);
//		gridPane.add(button3, 0, 2);
//		gridPane.add(button4, 0, 3);
//		
//		kati = new VBox(button1, button2, button3, button4);
//	}
	
	private void createAndFillCells()
	{

        OrderCode = new TableColumn<Order, String>("    ΚΩΔΙΚΟΣ\nΠΑΡΑΓΓΕΛΙΑΣ");
        OrderCode.setCellFactory(TextFieldTableCell.forTableColumn());
        OrderCode.setCellValueFactory(new PropertyValueFactory<Order, String>("OrderCode"));
        OrderCode.setEditable(false);
        
                
        RecipeCode = new TableColumn<Order, String>("ΣΥΝΘΕΣΗ");
        RecipeCode.setCellFactory(TextFieldTableCell.forTableColumn());
        RecipeCode.setCellValueFactory(new PropertyValueFactory<Order, String>("RecipeCode"));
        RecipeCode.setEditable(false);
        
        
        Quantity = new TableColumn<Order, String>("ΠΟΣΟΤΗΤΑ");
        Quantity.setCellFactory(TextFieldTableCell.forTableColumn());
        Quantity.setCellValueFactory(new PropertyValueFactory<Order, String>("Quantity"));
        Quantity.setEditable(false);
        
        
        ProjectCode = new TableColumn<Order, String>("ΚΩΔΙΚΟΣ\n  ΕΡΓΟΥ");
        ProjectCode.setCellFactory(TextFieldTableCell.forTableColumn());
        ProjectCode.setCellValueFactory(new PropertyValueFactory<Order, String>("ProjectCode"));
        ProjectCode.setEditable(false);
        
        
        CustomerCode = new TableColumn<Order, String>("ΚΩΔΙΚΟΣ\n ΠΕΛΑΤΗ");
        CustomerCode.setCellFactory(TextFieldTableCell.forTableColumn());
        CustomerCode.setCellValueFactory(new PropertyValueFactory<Order, String>("CustomerCode"));
        CustomerCode.setEditable(false);
        
        
        VehicleCode = new TableColumn<Order, String>("  ΚΩΔΙΚΟΣ\nΟΧΗΜΑΤΟΣ");
        VehicleCode.setCellFactory(TextFieldTableCell.forTableColumn());
        VehicleCode.setCellValueFactory(new PropertyValueFactory<Order, String>("VehicleCode"));
        VehicleCode.setEditable(false);
        
        
        DriverCode = new TableColumn<Order, String>("ΚΩΔΙΚΟΣ\nΟΔΗΓΟΥ");
        DriverCode.setCellFactory(TextFieldTableCell.forTableColumn());
        DriverCode.setCellValueFactory(new PropertyValueFactory<Order, String>("DriverCode"));
        DriverCode.setEditable(false);
        
        
        DateCreation = new TableColumn<Order, String>("ΗΜΕΡΟΜΗΝΙΑ\nΔΗΜΙΟΥΡΓΙΑΣ");
        DateCreation.setCellFactory(TextFieldTableCell.forTableColumn());
        DateCreation.setCellValueFactory(new PropertyValueFactory<Order, String>("DateCreation"));
        DateCreation.setEditable(false);
//        DateCreation.setOnEditCommit(
//            new EventHandler<CellEditEvent<Order, String>>() {
//                @Override
//                public void handle(CellEditEvent<Order, String> t) {
//                    ((Order) t.getTableView().getItems().get(
//                        t.getTablePosition().getRow())
//                        ).setDateCreation(t.getNewValue());
//                    try {
//						myDB.updateDataBase(t.getRowValue().getOrderCode(), "DateCreation", t.getNewValue());
//					} catch (SQLException e) 
//                    {
//						alertWindow();
//						e.printStackTrace();
//					}
//                }
//            }
//        );
        
        TimeCreation = new TableColumn<Order, String>("        ΩΡΑ\nΔΗΜΙΟΥΡΓΙΑΣ");
        TimeCreation.setCellFactory(TextFieldTableCell.forTableColumn());
        TimeCreation.setCellValueFactory(new PropertyValueFactory<Order, String>("TimeCreation"));
        TimeCreation.setEditable(false);
        
        ExecutionDate = new TableColumn<Order, String>("ΗΜΕΡΟΜΗΝΙΑ\n   ΕΚΤΕΛΕΣΗΣ");
        ExecutionDate.setCellFactory(TextFieldTableCell.forTableColumn());
    	ExecutionDate.setCellValueFactory(new PropertyValueFactory<Order, String>("ExecutionDate"));
    	ExecutionDate.setEditable(false);
//    	ExecutionDate.setOnEditCommit(
//            new EventHandler<CellEditEvent<Order, String>>() {
//                @Override
//                public void handle(CellEditEvent<Order, String> t) {
//                    ((Order) t.getTableView().getItems().get(
//                        t.getTablePosition().getRow())
//                        ).setDateCreation(t.getNewValue());
//                    try {
//    					myDB.updateDataBase(t.getRowValue().getOrderCode(), "ExecutionDate", t.getNewValue());
//    				} catch (SQLException e) 
//                    {
//    					alertWindow();
//    					e.printStackTrace();
//    				}
//                }
//            }
//        );
//        ExecutionDate.setCellValueFactory(new PropertyValueFactory<Order, String>("DateCreation"));      
    	
    	ExecutionTime = new TableColumn<Order, String>("      ΩΡΑ\nΕΚΤΕΛΕΣΗΣ");
    	ExecutionTime.setCellFactory(TextFieldTableCell.forTableColumn());
    	ExecutionTime.setCellValueFactory(new PropertyValueFactory<Order, String>("ExecutionTime"));
    	ExecutionTime.setEditable(false);
    	
    	Humidity = new TableColumn<Order, String>("ΣΥΜΠΕΡΙΛΗΨΗ\n     ΥΓΡΑΣΙΩΝ");
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
	    			}	
	    		});
	}
	
	
	private void alertWindow()
	{
		Alert alert = new Alert(Alert.AlertType.ERROR);
    	alert.setTitle("������ ���� ��� ����������");
    	alert.setHeaderText("�������� �� ��� ����");
    	alert.setContentText("������� � ����� ��� �������� �� ����� �����������");
    	alert.showAndWait();
	}
}