package Control;

import java.sql.SQLException;

import Model.DataBaseHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ChangeShippingNumberHandler implements EventHandler<ActionEvent>
{
	private DataBaseHandler myDB;
	private Stage stage;
	private Scene scene;
	
	private Button select;
	private Button cancel;
	
	private HBox hbox;
	private VBox vbox;
	
	private TextField textField;
	
	public ChangeShippingNumberHandler()
	{
		this.myDB = DataBaseHandler.getInstance();
	}
	
	@Override
	public void handle(ActionEvent arg0)
	{
		stage = new Stage();
		createButtons();
		
		createBoxes();
	    		
        scene = new Scene(vbox, 400, 400);
        scene.getStylesheets().add("application.css");
        
        createStage();       
        
        vbox.setAlignment(Pos.BASELINE_CENTER);
        vbox.setStyle("-fx-background-color: grey;");
        
        textField = new TextField();
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label checkInlabel;
        Label currentShippingNumber;
		
        checkInlabel = new Label("Enter the desired shipping number:");
		checkInlabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
        gridPane.add(checkInlabel, 0, 0);
        GridPane.setHalignment(checkInlabel, HPos.CENTER);
        
        try {	        
	        currentShippingNumber = new Label("Current Shipment Number = "+myDB.getShippingInvoiceNumber());
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

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
		
		scene.setOnKeyPressed(new EventHandler<KeyEvent>()
		{
		    @Override
		    public void handle(KeyEvent ke) 
		    {
		        if (ke.getCode().equals(KeyCode.ENTER)) 
		        {
		        	getValueAction();
		        }
		    }
		});
	}
	
	private void createButtons()
	{
		select = new Button("Select");
		cancel = new Button("Cancel");
		
		cancel.setId("exitButton");
		
		select.setMaxWidth(Double.MAX_VALUE);
	    cancel.setMaxWidth(Double.MAX_VALUE); 
	    
	    select.setCursor(Cursor.HAND);	
	    cancel.setCursor(Cursor.HAND);	
	    
	    cancel.setOnAction(new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent e) {
	            stage.close();
	        }
	    });
	    
		select.setOnAction(new EventHandler<ActionEvent>() 
		{
	        @Override public void handle(ActionEvent e) 
	        {
	        	getValueAction();
	        }
	    });
	}
	
	private void getValueAction() 
	{
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
	
	private void createBoxes()
	{
		hbox = new HBox(5, select, cancel);
		hbox.setAlignment(Pos.BASELINE_CENTER);
		
		vbox = new VBox(20);
        vbox.setStyle("-fx-padding: 10;");
	}
	
	private void createStage()
	{
		stage.setScene(scene);
        stage.setTitle("Change Shipping Number");
        stage.setHeight(370);
        stage.setWidth(300);
        stage.setResizable(false);
	}
	
	private void warningWindowForFlag(String text)
	{
		Alert alert = new Alert(Alert.AlertType.ERROR);
    	alert.setTitle("Error");
    	alert.setHeaderText("'"+text+"' is not a valid Shipping Number!");
    	alert.setContentText("Please enter a valid Shipping Number before continuing. \nOnly numbers up to 7 digits between 0 and 9999999 are allowed.");
    	alert.showAndWait();
	}
	
	private void rigthWindow(String text)
	{
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
    	alert.setTitle("Successful Execution!");
    	alert.setHeaderText("'Successful Shipment Number update!");
    	alert.setContentText("The new Shipment Number -> '" + text +"'");
    	alert.showAndWait();
	}
	
	private void maxNumberWindow(String text)
	{
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
    	alert.setTitle("Notice");
    	alert.setHeaderText("Successfully updated Shipment Number! \nMaximum entry!");
    	alert.setContentText("The new Shipment Number -> '"+text+"'. \nThe current order will be updated with this shipping number. \nBut because you entered the maximum allowed Shipping Number it will not be incremented by 1 as always but the value 1 will be entered automatically in the next order!");
    	alert.showAndWait();
	}
}
