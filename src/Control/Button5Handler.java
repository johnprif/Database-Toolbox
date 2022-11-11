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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Button5Handler implements EventHandler<ActionEvent>
{
	private DataBaseHandler myDB;
	private Stage stage;
	
	public Button5Handler(DataBaseHandler myDB, Stage stage)
	{
		this.stage = stage;
		this.myDB = myDB;
	}
	
	@Override
	public void handle(ActionEvent arg0)
	{
		Button button1 = new Button("�������");
		Button button2 = new Button("�������");
		button1.setMaxWidth(Double.MAX_VALUE);
	    button2.setMaxWidth(Double.MAX_VALUE);
	    button1.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    button2.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    
	    
		HBox hbox = new HBox(5, button1, button2);
		hbox.setAlignment(Pos.BASELINE_CENTER);
		VBox vbox = new VBox(20);
        vbox.setStyle("-fx-padding: 10;");
        Scene scene = new Scene(vbox, 400, 400);
        stage.setScene(scene);
        stage.setTitle("������ ������� ���������");
        vbox.setAlignment(Pos.BASELINE_CENTER);
        vbox.setStyle("-fx-background-color: dodgerblue;");
        TextField textField = new TextField();
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label checkInlabel;
        Label currentShippingNumber;
		try {
			checkInlabel = new Label("��������� ��� ��������� ������ ���������:");
			checkInlabel.setStyle("-fx-font-weight: bold; -fx-text-fill: yellow;");
	        gridPane.add(checkInlabel, 0, 0);
	        GridPane.setHalignment(checkInlabel, HPos.CENTER);
	        
	        currentShippingNumber = new Label("������ ������� ��������� = "+myDB.getShippingInvoiceNumber());
	        currentShippingNumber.setStyle("-fx-font-weight: bold; -fx-text-fill: yellow;");
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
        

		button2.setOnAction(new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent e) {
	            stage.close();
	        }
	    });
		

		button1.setOnAction(new EventHandler<ActionEvent>() {
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
	
	private void warningWindowForFlag(String text)
	{
		Alert alert = new Alert(Alert.AlertType.ERROR);
    	alert.setTitle("Error");
    	alert.setHeaderText("� '"+text+"' ��� �������� ������ ������ ���������!");
    	alert.setContentText("�������� �������� ���� ������ ������ ��������� ������ ����������\n������������ ���� ������� ����� 7 ����� ����������� ��� ����� ��� ���������� ��� 9999999'");
    	alert.showAndWait();
	}
	
	private void rigthWindow(String text)
	{
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
    	alert.setTitle("������� ���������");
    	alert.setHeaderText("�������� ���������!");
    	alert.setContentText("� ������� ��������� '" + text +"' ����������� ��������!");
    	alert.showAndWait();
	}
	
	private void maxNumberWindow(String text)
	{
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
    	alert.setTitle("������� ���������");
    	alert.setHeaderText("������� ����������\n�������� ���������!");
    	alert.setContentText("� ������� ��������� '" + text +"' ����������� ��������!\n� �������� ���������� �� ���������� �� ����� �� ������\n������ ���� ������������ ��� ������� ������ ��������� ��� �� ����������� ���� 1 ���� ������� ���� �� ����������� � ���� 1 ��������!");
    	alert.showAndWait();
	}
}
