package Control;

import java.sql.SQLException;
import java.util.ArrayList;
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
	
	private ArrayList<String> siloIDs;
	private boolean flag = false;
	
	public ChangeHumidityHandler(Stage stage)
	{
		this.stage = stage;
		this.myDB = DataBaseHandler.getInstance();
	}
	
	@Override
	public void handle(ActionEvent arg0) 
	{
		System.out.println("ChangeHumidityHandler");
		
		if(flag == false)
		{
			warningWindowForFlag();
			stage.close();
		}else
		{
			
			
			order = table.getSelectionModel().getSelectedItem();			
			
			try {
				siloIDs = new ArrayList<String>(myDB.getHumiditySilos());
			} catch (SQLException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			
			createButtons();
			
			createBoxes();
			
			 Scene scene = new Scene(vbox, 400, 400);
		        stage.setScene(scene);
		        stage.setTitle("Αλλαγή Υγρασίας -> "+order.getOrderCode());
		        
		        vbox.setAlignment(Pos.BASELINE_CENTER);
		        vbox.setStyle("-fx-background-color: dodgerblue;");
		        
		        TextField textField = new TextField();
		        GridPane gridPane = new GridPane();
		        gridPane.setHgap(10);
		        gridPane.setVgap(10);

		        Label checkInlabel;
		        Label currentHumidity;
				String currentHumidityLabel = "Τρέχουσα Υγρασία";
		        
		        checkInlabel = new Label("Εισάγετε την επιθυμητή υγρασία");
				checkInlabel.setStyle("-fx-font-weight: bold; -fx-text-fill: yellow;");
		        gridPane.add(checkInlabel, 0, 0);
		        GridPane.setHalignment(checkInlabel, HPos.CENTER);
		        
		        if(siloIDs.size()==0)
				{
					//nothing?
				}else if(siloIDs.size()==1)
				{
					currentHumidityLabel = currentHumidityLabel + " για το σιλό "+siloIDs.get(0)+" = 0.0";
				}else
				{
					currentHumidityLabel += " για τα σιλό ";
					for(int i=0; i<siloIDs.size(); i++)
					{
						currentHumidityLabel += siloIDs.get(i);
					}
					
				}
				currentHumidity = new Label(currentHumidityLabel);
				currentHumidity.setStyle("-fx-font-weight: bold; -fx-text-fill: yellow;");
				gridPane.add(currentHumidity, 0, 1);
				GridPane.setHalignment(currentHumidity, HPos.CENTER);
			
		        gridPane.add(textField, 0, 2);
		        gridPane.add(hbox, 0, 3);
		        gridPane.setAlignment(Pos.CENTER); 
		        vbox.getChildren().add(gridPane);
		        
		        stage.setHeight(370);
		        stage.setWidth(300);
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

			            if(text.length() == 1 && text.equals("0"))
			            {
			            	warningWindowForFlag(text);
			            }else if(text == null || text.trim().isEmpty())
			            {
			            	warningWindowForFlag(text);
			            }else if(text.matches("[0-9]*"))
			            {
			            	if(text.length()<8 && Long.parseLong(text)<=10)
			            	{
			            		try {
									myDB.setShippingInvoiceNumber(text);
									rigthWindow(text);
									stage.close();
								} catch (NumberFormatException | SQLException e1) {
									e1.printStackTrace();
								}
			            	}else
			            	{
			            		warningWindowForFlag(text);
			            	}
			            //	stage.close();
			            }else
			            {
			            	warningWindowForFlag(text);
			            }
			        }
			    });
		}
		
		
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
		hbox = new HBox(5, select, cancel);
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
    	alert.setTitle("Αριθμός Αποστολής");
    	alert.setHeaderText("Επιτυχής Ενημέρωση!");
    	alert.setContentText("Ο Αριθμός Αποστολής '" + text +"' ενημερώθηκε επιτυχώς!");
    	alert.showAndWait();
	}
	
	private void maxNumberWindow(String text)
	{
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
    	alert.setTitle("Αριθμός Αποστολής");
    	alert.setHeaderText("Επιτυχής Ενημέρωση!\nΜέγιστη καταχώρηση!");
    	alert.setContentText("Ο Αριθμός Αποστολής '" + text +"' ενημερώθηκε επιτυχώς!\nΗ τρέχουσα παραγγελία θα ενημερωθεί με αυτόν τον αριθμό αποστολής.\nΕπειδή όμως καταχωρήσατε τον μέγιστο επιτρεπτό Αριθμό Αποστολής δεν θα προσαυξηθεί κατά 1  όπως πάντα αλλά θα καταχωρηθεί η τιμή 1 αυτόματα στην επόμενη παραγγελία!");
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

}
