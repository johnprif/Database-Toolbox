package Control;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Model.DataBaseHandler;
import Model.Order;
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

public class ChangeHumidityHandler implements EventHandler<ActionEvent>
{
	private Stage stage;
	private DataBaseHandler myDB;
	
	private Button select;
	private Button cancel;
	
	private HBox hbox;
	private VBox vbox;
	
	private Order order;
	private TableView<Order> table;
	
	private ArrayList<String> siloIDsPerOrder;
	private boolean flag = false;
	
	private HashMap<String, Order> changes3;
	
    
    private ComboBox comboTest;
	
	public ChangeHumidityHandler(Stage stage)
	{
		this.stage = stage;
		this.myDB = DataBaseHandler.getInstance();
	}
	
	@Override
	public void handle(ActionEvent arg0) 
	{
		System.out.println("ChangeHumidityHandler");
		order = table.getSelectionModel().getSelectedItem();	
		
		if(order.getHumidity().equals("ΟΧΙ"))
		{
			noHumidityWindow();
		}else if(flag == false)
		{
			warningWindowForFlag();
			stage.close();
		}else
		{
			siloIDsPerOrder = new ArrayList<String>(myDB.getHumiditySilosPerOrder(order.getOrderCode()));
//			System.out.println("THE SIZE OF siloIDs = "+siloIDs.size());
			createButtons();
			
			createBoxes();
			
			 Scene scene = new Scene(vbox, 400, 400);
		        stage.setScene(scene);
		        stage.setTitle("Αλλαγή Υγρασίας -> "+order.getOrderCode());
		        
		        vbox.setAlignment(Pos.BASELINE_CENTER);
		        vbox.setStyle("-fx-background-color: dodgerblue;");
		        
		        TextField textField = new TextField();
		        textField.setPromptText("0.0");
		        GridPane gridPane = new GridPane();
		        gridPane.setHgap(10);
		        gridPane.setVgap(10);

		        Label checkInlabel;
		        Label currentHumidity;
//				String currentHumidityLabel = "Τρέχουσα Υγρασία";
		        
		        checkInlabel = new Label("Εισάγετε την επιθυμητή υγρασία");
				checkInlabel.setStyle("-fx-font-weight: bold; -fx-text-fill: yellow;");
		        gridPane.add(checkInlabel, 0, 0);
		        GridPane.setHalignment(checkInlabel, HPos.CENTER);

				if(siloIDsPerOrder.size()==0)
				{
					//nothing?
				} else
				{
					if(siloIDsPerOrder.size()==1)
					{
//						currentHumidityLabel = currentHumidityLabel + " για το σιλό "+siloIDsPerOrder.get(0)+" = "+ myDB.getHumiditySilos().get(siloIDsPerOrder.get(0));
					}else
					{
//						currentHumidityLabel += " για τα σιλό: ";
						for(int i=0; i<siloIDsPerOrder.size(); i++)
						{
//							currentHumidityLabel += siloIDsPerOrder.get(i)+" ,";
						}
					}
				}
//				currentHumidity = new Label(currentHumidityLabel);
//				currentHumidity.setStyle("-fx-font-weight: bold; -fx-text-fill: yellow;");
//				gridPane.add(currentHumidity, 0, 1);
//				GridPane.setHalignment(currentHumidity, HPos.CENTER);
			
//=========================================================================================				
//				comboTest.getItems().add("0");
//				comboTest.getItems().add("1");
//				comboTest.getItems().add("2");
//				comboTest.getItems().add("3");
//				comboTest.getItems().add("4");
//				comboTest.getItems().add("5");
//				comboTest.getItems().add("6");
//				comboTest.getItems().add("7");
//				comboTest.getItems().add("8");
//				comboTest.getItems().add("9");
				
				makeComboSilos();
				
				comboTest.setOnAction(new EventHandler<ActionEvent>() {
			        @Override public void handle(ActionEvent e) {
			            System.out.println(comboTest.getSelectionModel().getSelectedItem());
			        }
			    });
				
				Label textField1 = new Label("Hello");
				HBox hbox1 = new HBox(5, comboTest, textField, select);			
				gridPane.add(hbox1, 0, 2);
//=============================================================================================				 
//		        gridPane.add(textField, 0, 2);
		        gridPane.add(hbox, 0, 3);
		        gridPane.setAlignment(Pos.CENTER); 
		        vbox.getChildren().add(gridPane);
		        
		        stage.setHeight(370);
		        stage.setWidth(330);
		        stage.setResizable(false);
		        stage.show();
		        

				cancel.setOnAction(new EventHandler<ActionEvent>() {
			        @Override public void handle(ActionEvent e) {
			            stage.close();
			        }
			    });
				

				select.setOnAction(new EventHandler<ActionEvent>() {
			        @Override public void handle(ActionEvent e) {
			            String text = textField.getText();

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
			            		try {
									order.setHumidity(""+humidity+"");									
									table.getItems().set(table.getSelectionModel().getSelectedIndex(), order);
									changes3.put(order.getOrderCode(), order);
									
									rigthWindow(text);
									stage.close();
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
			    });
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
		cancel = new Button("Ακύρωση");
		
		select.setMaxWidth(Double.MAX_VALUE);
	    cancel.setMaxWidth(Double.MAX_VALUE);
	    
	    select.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    cancel.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	}
	
	private void createBoxes()
	{
//		hbox = new HBox(5, select, cancel);
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
	
}
