package Control;

import java.sql.SQLException;

import Model.DataBaseHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChangeShippingNumberHandler implements EventHandler<ActionEvent>
{
	private DataBaseHandler myDB;
	private Stage stage;
	
	private Button select;
	private Button cancel;
	
	private HBox hbox;
	private VBox vbox;
	
	public ChangeShippingNumberHandler(Stage stage)
	{
		this.stage = stage;
		this.myDB = DataBaseHandler.getInstance();
	}
	
	@Override
	public void handle(ActionEvent arg0)
	{
		createButtons();
		
		createBoxes();
	    
		
        Scene scene = new Scene(vbox, 400, 400);
        stage.setScene(scene);
        stage.setTitle("Αλλαγή Αριθμού Αποστολής");
        
        vbox.setAlignment(Pos.BASELINE_CENTER);
        vbox.setStyle("-fx-background-color: grey;");
        
        TextField textField = new TextField();
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label checkInlabel;
        Label currentShippingNumber;
		
        checkInlabel = new Label("Εισάγετε τον επιθυμητό αριθμό αποστολής:");
		checkInlabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
        gridPane.add(checkInlabel, 0, 0);
        GridPane.setHalignment(checkInlabel, HPos.CENTER);
        
        try {	        
	        currentShippingNumber = new Label("Τρέχον Αριθμός Αποστολής = "+myDB.getShippingInvoiceNumber());
	        currentShippingNumber.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
	        gridPane.add(currentShippingNumber, 0, 1);
	        GridPane.setHalignment(currentShippingNumber, HPos.CENTER);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        
        
        
        gridPane.add(textField, 0, 2);
        gridPane.add(hbox, 0, 3);
        gridPane.setAlignment(Pos.CENTER); 
        vbox.getChildren().add(gridPane);
        
        
        
        
        //stage.setHeight(150);
        //stage.setWidth(168);
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
	            	if(text.length()<8 && Long.parseLong(text)<9999999)
	            	{
	            		try {
							myDB.setShippingInvoiceNumber(text);
							rigthWindow(text);
							stage.close();
						} catch (NumberFormatException | SQLException e1) {
							e1.printStackTrace();
						}
	            	}else if(text.length()==7 && Long.parseLong(text)==9999999)
	            	{
	            		try {
							myDB.setShippingInvoiceNumber(text);
							maxNumberWindow(text);
							stage.close();
						} catch (SQLException e1) {
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
	
	
	private void warningWindowForFlag(String text)
	{
		Alert alert = new Alert(Alert.AlertType.ERROR);
    	alert.setTitle("Error");
    	alert.setHeaderText("Ο"+text+"' δεν αποτελεί έγκυρο Αριθμό Αποστολής!");
    	alert.setContentText("Παρακαλώ εισάγετε έναν έγυρο Αριθμό Αποστολής πρωτού συνεχίσετε\nΕπιτρέπονται μόνο αριθμοί μέχρι 7 ψηφίων οι οποίοι είναι μετεξύ 0 και 9999999");
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
}
