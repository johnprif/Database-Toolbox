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
			stage.close();
		}else if(order.getHumidity().equals("ΟΧΙ"))
		{
			noHumidityWindow();
		}else
		{
			prepareCurrentHumidityValues();
			siloIDsPerOrder = new ArrayList<String>(myDB.getHumiditySilosPerOrder(order.getOrderCode()));
//			System.out.println("THE SIZE OF siloIDs = "+siloIDs.size());
			makeComboSilos();
			
			createButtons();
			
			createBoxes();
			
			Scene scene = new Scene(vbox, 400, 400);
		    stage.setScene(scene);
		    stage.setTitle("Αλλαγή Υγρασίας -> "+order.getOrderCode());
		       
		    vbox.setAlignment(Pos.BASELINE_CENTER);
		    vbox.setStyle("-fx-background-color: dodgerblue;");
		        
		    GridPane gridPane = new GridPane();
		    gridPane.setHgap(10);
		    gridPane.setVgap(10);
		    
		    Label checkInlabel;
		    Label currentHumidity;
				
		    String checkInlabelText = "Εισάγετε την επιθυμητή υγρασία για το σιλό: ";
			checkInlabel = new Label(checkInlabelText);
				
//			makeComboSilos();
				
			String currentHumidityString;
			comboTest.setOnAction(new EventHandler<ActionEvent>() {
			       @Override public void handle(ActionEvent e) {		
			       checkInlabel.setText(checkInlabelText+myDB.getHumiditySilos().get(comboTest.getSelectionModel().getSelectedItem()));
//			       checkInlabel.setText(checkInlabelText+currentHumidityValues.get(order.getOrderCode()).get(tempString));
//			       currentHumidityString = 
//			       System.out.println(currentHumidityValues.get(order.getOrderCode()).get(comboTest.getSelectionModel().getSelectedItem()));
			       textField.setText(currentHumidityValues.get(order.getOrderCode()).get(comboTest.getSelectionModel().getSelectedItem()));
//			       textField.setPromptText(myDB.getHumiditySilos().get(comboTest.getSelectionModel().getSelectedItem()));
//			       textField.setPromptText(currentHumidityValues.get(order.getOrderCode()).get(innerHashMap.get(myDB.getHumiditySilos().get(comboTest.getSelectionModel().getSelectedItem()))));
			       }
			   });
				
				
				
			checkInlabel.setStyle("-fx-font-weight: bold; -fx-text-fill: yellow;");
		    gridPane.add(checkInlabel, 0, 0);
		    GridPane.setHalignment(checkInlabel, HPos.CENTER);
		        
		    Label textField1 = new Label("Hello");
		    HBox hbox1 = new HBox(5, comboTest, textField, select);			
			gridPane.add(hbox1, 0, 2);
//=============================================================================================				 
//		    gridPane.add(textField, 0, 2);
		    gridPane.add(hbox, 0, 3);
		    gridPane.setAlignment(Pos.CENTER); 
		    vbox.getChildren().add(gridPane);
		        
		    stage.setHeight(370);
		    stage.setWidth(350);
		    stage.setResizable(false);
		    stage.show();
		        

		    cancel.setOnAction(new EventHandler<ActionEvent>() {
			       @Override public void handle(ActionEvent e) {
			    	   if(selectClicks==0)
			    	   {
			    		   currentHumidityValues.remove(order.getOrderCode());
			    	   }
			           stage.close();
			       }
			   });

		    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		        @Override
		        public void handle(WindowEvent e) {
		        	System.out.println("BYE BYE");
		        	if(selectClicks==0)
			    	   {
			    		   currentHumidityValues.remove(order.getOrderCode());
			    	   }
//		        	Platform.exit();
//		        	System.exit(0);
		        	stage.close();
		        }
		      });
		    
		    select.setOnAction(new EventHandler<ActionEvent>() {
			       @Override public void handle(ActionEvent e) {
			          String text = textField.getText();
			            
			           if(comboTest.getSelectionModel().isEmpty())
			           {
			        	   warningWindowForComboBox();
			           }else
			           {
			        	   if(text == null || text.trim().isEmpty())
				            {
				            	warningWindowForFlag(text);
				            }else
				            {				            	
				            	double humidity=0.0;
				            	try {
				            		humidity = DecimalFormat.getNumberInstance().parse(text).doubleValue();
								} catch (ParseException e2) {
									// TODO Auto-generated catch block
									e2.printStackTrace();
								}
				            	if(humidity<=10)
				            	{
//				            		innerHashMap = new HashMap<String, String>();
				            		selectClicks++;
				            		try {		
				            			tempString = (String) comboTest.getSelectionModel().getSelectedItem();
				            			innerHashMap.put(tempString, humidity+"");
				            			currentHumidityValues.put(order.getOrderCode(), innerHashMap);
//				            			System.out.println(currentHumidityValues.get(order.getOrderCode()).get(tempString));
										//order.setHumidity(""+humidity+"");									
										table.getItems().set(table.getSelectionModel().getSelectedIndex(), order);
										changes3.put(order.getOrderCode(), order);
										
										rigthWindow(text);
										//stage.close();
									} catch (NumberFormatException e1) {
										e1.printStackTrace();
									}
				            	}else
				            	{
				            		warningWindowForFlag(text);
				            	}
				            //	stage.close();
				            }
			            }
			            
			            
			        }
			    });
		}
		
		
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
	
	private void makeComboSilos()
	{
		comboTest = new ComboBox();
		for(int i=0; i<siloIDsPerOrder.size(); i++)
		{
			comboTest.getItems().add(siloIDsPerOrder.get(i));
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
	
	private void createButtons()
	{
		select = new Button("Επιλογή");
		back = new Button("Επιστροφή");
		cancel = new Button("Έξοδος");
		
		select.setMaxWidth(Double.MAX_VALUE);
		back.setMaxWidth(Double.MAX_VALUE);
	    cancel.setMaxWidth(Double.MAX_VALUE);
	    
	    select.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    back.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    cancel.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	}
	
	private void createBoxes()
	{
//		hbox = new HBox(5, select, cancel);
//		hbox = new HBox(5, back, cancel);
		hbox = new HBox(5, cancel);
		hbox.setAlignment(Pos.BASELINE_CENTER);
		
		vbox = new VBox(20);
        vbox.setStyle("-fx-padding: 10;");
	}
	
	public void setFlag(boolean flag)
	{
		this.flag = flag;
	}
	
	private void warningWindowForFlag(String text)
	{
		Alert alert = new Alert(Alert.AlertType.ERROR);
    	alert.setTitle("Error");
    	alert.setHeaderText("Η τιμή '"+text+"' δεν αποτελεί έγκυρη υγρασία!");
    	alert.setContentText("Παρακαλώ εισάγετε μια έγκυρη τιμή πρωτού συνεχίσετε\nΕπιτρέπονται μόνο αριθμοί μέχρι 7 ψηφίων οι οποίοι είναι μετεξύ 0 και 10");
    	alert.showAndWait();
	}
	
	private void rigthWindow(String text)
	{
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
    	alert.setTitle("Υγρασία");
    	alert.setHeaderText("Επιτυχής Ενημέρωση!");
    	alert.setContentText("Η τιμή της υγρασίας '" + text +"' ενημερώθηκε επιτυχώς!");
    	alert.showAndWait();
	}
	
	private void warningWindowForFlag()
	{
		Alert alert = new Alert(Alert.AlertType.WARNING);
    	alert.setTitle("Warning");
    	alert.setHeaderText("Καμία επιλογή");
    	alert.setContentText("Δεν έχει επιλεγεί καμία απο τις παραγγελίες\nΠαρακαλώ επιλέξτε κάποια πρωτού συνεχίσετε");
    	alert.showAndWait();
	}

	private void noHumidityWindow()
	{
		Alert alert = new Alert(Alert.AlertType.WARNING);
    	alert.setTitle("Υγρασία");
    	alert.setHeaderText("Αδύνατη αλλαγή υγρασίας!");
    	alert.setContentText("Δεν γίνεται να αλλάξετε την υγρασία διότι δεν υπάρχει κάποιο σιλό που περιέχει αισθητήρα ούτε επιτρέπεται η χειροκίνητη ρύθμιση");
    	alert.showAndWait();
	}

	private void warningWindowForComboBox()
	{
		Alert alert = new Alert(Alert.AlertType.ERROR);
    	alert.setTitle("Error");
    	alert.setHeaderText("Αδύνατη αλλαγή υγρασίας!");
    	alert.setContentText("Παρακαλώ επιλέξτε κάποιο σιλό πρωτού συνεχίσετε!");
    	alert.showAndWait();
	}
}
