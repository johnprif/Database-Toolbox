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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

import Control.Button1Handler;
import Control.Button3Handler;
import Control.Button4Handler;
import Control.Button5Handler;
import Control.Button6Handler;
import Control.Button7Handler;
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
	private Scene scene;
	private Stage stage;	
    private TableView<Order> table;
    private Button button1;	//������ ����������� �����������
	private Button button2; //������ ����������� ���������
	private Button button3; //��������
	private Button button4; //�������
	private Button button5; //������ ������� ���������
	private Button button6; //��������
	private Button button7; //�������� �����
	private GridPane gridPane;
    private int counter = 0;
    private String order;
    private VBox vbox2;
    private Button1Handler button1Handler;
    private Button1Handler button2Handler;
    private Button3Handler button3Handler;
    private Button4Handler button4Handler;
    private Button5Handler button5Handler;
    private Button6Handler button6Handler;
    private Button7Handler button7Handler;
    private VBox kati ;
    private ArrayList<String[]> changes;
    private ArrayList<Order> changes2;
    private HashMap<String, Order> changes3;
    private boolean flag = false; 
    private Stage dateStage;
    private Stage shippingStage;
    private Label tempLabel_1 = new Label("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    
	public PendingOrdersGUI(DataBaseHandler myDB, Stage stage)
	{
		changes3 = new HashMap<String, Order>();
		this.myDB = myDB;
//		myDB.initialize();
		dateStage = new Stage();
		shippingStage = new Stage();
		this.stage = stage;
	}
	
	public void initialize()
	{		
		createStage();
		createButtons();
		
		button1Handler = new Button1Handler(button1, dateStage);
		button2Handler = new Button1Handler(button2, dateStage);
		button3Handler = new Button3Handler(myDB);
		button4Handler = new Button4Handler(stage);
		button5Handler = new Button5Handler(myDB, shippingStage);
		button6Handler = new Button6Handler(myDB, stage);
		button7Handler = new Button7Handler(myDB, button6Handler);
		
		button1Handler.setChanges3(changes3);
		button2Handler.setChanges3(changes3);
		button3Handler.setChanges3(changes3);
		button4Handler.setChanges3(changes3);
		
		BorderPane border = new BorderPane();
		
		button1.setOnAction(button1Handler);
	    button2.setOnAction(button2Handler);
	    button3.setOnAction(button3Handler);
	    button4.setOnAction(button4Handler);
	    button5.setOnAction(button5Handler);
	    button6.setOnAction(button6Handler);
	    button7.setOnAction(button7Handler);;
		
		myDB.findAndParse();
		data = myDB.getData();
		
		
		
		createTable();
        createAndFillCells();
        
        button1Handler.setTable(table);
        button2Handler.setTable(table);
        button3Handler.setTable(table);
        button6Handler.setTable(table);
        
        scene = new Scene(border);
        setLastThingsOnTable();
        
        
        
        vbox2 = new VBox(15, button6, button1, button2, button5, button3, button4, tempLabel_1,  button7);

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
		
		
		button1 = new Button("Αλλαγή Χρόνου Δημιουργίας");
		button2 = new Button("Αλλαγή Χρόνου Εκτέλεσης");
		button3 = new Button("Εκτέλεση");
		button4 = new Button("Πίσω");
		button5 = new Button("Αλλαγή Αριθμού Αποστολής");
		button6 = new Button("Ανανέωση");
		button7 = new Button("Άδειασμα Βάσης");
		button1.setMaxWidth(Double.MAX_VALUE);
	    button2.setMaxWidth(Double.MAX_VALUE);
	    button3.setMaxWidth(Double.MAX_VALUE);
	    button4.setMaxWidth(Double.MAX_VALUE);
	    button5.setMaxWidth(Double.MAX_VALUE);
	    button6.setMaxWidth(Double.MAX_VALUE);
	    button7.setMaxWidth(Double.MAX_VALUE);
	    
//	    button1.setOnAction(button1Handler);
//	    button2.setOnAction(button1Handler);
//	    button3.setOnAction(button3Handler);
//	    button4.setOnAction(button4Handler);
	    
	    button1.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    button2.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    button3.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    button4.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    button5.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    button6.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    button7.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	}
	
	private void createStage()
	{
		//stage = new Stage();
        stage.setTitle("Λίστα Παραγγελιών σε Εκκρεμότητα");
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

        OrderCode = new TableColumn<Order, String>("ΚΩΔΙΚΟΣ ΠΑΡΑΓΓΕΛΙΑΣ");
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
        
        
        ProjectCode = new TableColumn<Order, String>("ΚΩΔΙΚΟΣ ΕΡΓΟΥ");
        ProjectCode.setCellFactory(TextFieldTableCell.forTableColumn());
        ProjectCode.setCellValueFactory(new PropertyValueFactory<Order, String>("ProjectCode"));
        ProjectCode.setEditable(false);
        
        
        CustomerCode = new TableColumn<Order, String>("ΚΩΔΙΚΟΣ ΠΕΛΑΤΗ");
        CustomerCode.setCellFactory(TextFieldTableCell.forTableColumn());
        CustomerCode.setCellValueFactory(new PropertyValueFactory<Order, String>("CustomerCode"));
        CustomerCode.setEditable(false);
        
        
        VehicleCode = new TableColumn<Order, String>("ΚΩΔΙΚΟΣ ΟΧΗΜΑΤΟΣ");
        VehicleCode.setCellFactory(TextFieldTableCell.forTableColumn());
        VehicleCode.setCellValueFactory(new PropertyValueFactory<Order, String>("VehicleCode"));
        VehicleCode.setEditable(false);
        
        
        DriverCode = new TableColumn<Order, String>("ΚΩΔΙΚΟΣ ΟΔΗΓΟΥ");
        DriverCode.setCellFactory(TextFieldTableCell.forTableColumn());
        DriverCode.setCellValueFactory(new PropertyValueFactory<Order, String>("DriverCode"));
        DriverCode.setEditable(false);
        
        
        DateCreation = new TableColumn<Order, String>("ΗΜΕΡΟΜΗΝΙΑ ΔΗΜΙΟΥΡΓΙΑΣ");
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
        
        TimeCreation = new TableColumn<Order, String>("ΩΡΑ ΔΗΜΙΟΥΡΓΙΑΣ");
        TimeCreation.setCellFactory(TextFieldTableCell.forTableColumn());
        TimeCreation.setCellValueFactory(new PropertyValueFactory<Order, String>("TimeCreation"));
        TimeCreation.setEditable(false);
        
        ExecutionDate = new TableColumn<Order, String>("ΗΜΕΡΟΜΗΝΙΑ ΕΚΤΕΛΕΣΗΣ");
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
    	
    	ExecutionTime = new TableColumn<Order, String>("ΩΡΑ ΕΚΤΕΛΕΣΗΣ");
    	ExecutionTime.setCellFactory(TextFieldTableCell.forTableColumn());
    	ExecutionTime.setCellValueFactory(new PropertyValueFactory<Order, String>("ExecutionTime"));
    	ExecutionTime.setEditable(false);
	}
	
	private void setLastThingsOnTable()
	{
		table.setItems(data);
        table.getColumns().addAll(OrderCode, RecipeCode, Quantity, ProjectCode, CustomerCode, VehicleCode, DriverCode, DateCreation, TimeCreation, ExecutionDate, ExecutionTime);
     
        table.getSelectionModel().setCellSelectionEnabled(false);
        
        ObservableList<Order> orderList = table.getSelectionModel().getSelectedItems();	    	    
	    orderList.addListener(new ListChangeListener()
	    		{
	    			@Override
	    			public void onChanged(Change c)
	    			{
	    				flag = true;
	    				button1Handler.setFlag(flag);
	    				button2Handler.setFlag(flag);
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