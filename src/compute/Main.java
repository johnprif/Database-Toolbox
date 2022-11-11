package compute;
	
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;

import java.sql.SQLException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;

public class Main extends Application 
{
	private DataBaseHandler myDB;
	private String path;
	private TakeThePath takeThePath;	
	private String programTitle = "SAITEC";
	private String button1Text = "Φόρτωση Βάσης Δεδομένων";
	private String button2Text = "Άνοιγμα παραγγελιών";	
	private Stage pendingStage;
	
	@Override
	public void start(Stage primaryStage) 
	{
		myDB = new DataBaseHandler();
		takeThePath = new TakeThePath(myDB);
		takeThePath.checkPathFile();
		checkIfAlreadyOpen(takeThePath.getPath());
//		path = takeThePath.getPath();
//		myDB.setPath(path);
//		myDB.initialize();
		pendingStage = new Stage();
		try {
			//Creating Buttons     
		    Button button1 = new Button(button1Text);
		    Button button2 = new Button(button2Text);
		    Button button3 = new Button("Έξοδος");
		    
		    
		    button1.setMaxWidth(Double.MAX_VALUE);
		    button2.setMaxWidth(Double.MAX_VALUE);
		    button3.setMaxWidth(Double.MAX_VALUE);
		    
//		    button1.setStyle("-fx-background-color: green; -fx-font-weight: bold; -fx-text-fill: white; -fx-border-color: yellow; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
//		    button2.setStyle("-fx-background-color: green; -fx-font-weight: bold; -fx-text-fill: white; -fx-border-color: yellow; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
		    button1.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
		    button2.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
		    button3.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
		    
		    
//		    TakeThePath takeThePath = new TakeThePath(button2);
		    button1.setOnAction(takeThePath);
		    
		    PendingOrdersFactory pendingOrdersFactory = new PendingOrdersFactory();
		    pendingOrdersFactory.setDB(myDB);
		    pendingOrdersFactory.setStage(pendingStage);
		    button2.setOnAction(pendingOrdersFactory);
		    
		    
		    pendingStage.initOwner(primaryStage);
		    pendingStage.initModality(Modality.WINDOW_MODAL);
		    
			//Creating a Grid Pane 
		    GridPane gridPane = new GridPane();

		    //Setting the Grid alignment 
		    gridPane.setAlignment(Pos.CENTER); 
    
		    //Arranging all the nodes in the grid 
		    gridPane.add(button2, 0, 0);
		    gridPane.add(button1, 0, 1);
		    gridPane.add(button3, 0, 2);
   
		    //Spaces between the buttons 
		    gridPane.setHgap(10);
		    gridPane.setVgap(10);
		    
		    //Setting the back ground color 
//		    gridPane.setStyle("-fx-background-color: orangered;");
		    //gridPane.setStyle("-fx-background-color: navy;");
		    gridPane.setStyle("-fx-background-color: dodgerblue;");
		    
		    //Creating a scene object 
			Scene scene = new Scene(gridPane,300,300);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
//			primaryStage.getIcons().add(new Image("file:\\C:\\Users\\Joanis Prifti\\eclipse-workspace\\First_Version\\SDOE.jpg"));
			primaryStage.getIcons().add(new Image("file:\\"+System.getProperty("user.dir")+"\\default.png"));
			//Setting title to the Stage 
			primaryStage.setTitle(programTitle);
			
			//Adding scene to the stage 
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			//Displaying the contents of the stage
			primaryStage.show();
			
			button3.setOnAction(new EventHandler<ActionEvent>() {
			    @Override public void handle(ActionEvent e) {
			    	primaryStage.close();
			    	try {
			    		if(!myDB.isClosed())
			    		{
			    			myDB.closeDB();			    			
			    		}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
			    	 System.exit(0);
			    }
			});
			
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		          public void handle(WindowEvent we) 
		          {
		              primaryStage.close();
		              try {
		            	  if(!myDB.isClosed())
				    		{
				    			myDB.closeDB();			    			
				    		}
					} catch (SQLException e) {
						e.printStackTrace();
					}
		              System.exit(0);
		          }
		      });        
		} catch(Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private void checkIfAlreadyOpen(String path)
	{
		String tempFile = path.substring(0,path.length()-3)+"ldb";
		File f = new File(tempFile);
		
		if(f.exists() && !f.isDirectory()) { 
			
			openAlreadyFile();
			System.exit(0);
		}
	}
	
	private void openAlreadyFile()
	{
		Alert alert = new Alert(Alert.AlertType.ERROR);
    	alert.setTitle("Error");
    	alert.setHeaderText("������� ���� ���������!");
    	alert.setContentText("�������� ������� ��� ���� ��������� � �� ��������� �� ����� ������������ ��� ���� ��������� ���� �� ��� �������� ������ �������� ������ ����������'");
    	alert.showAndWait();
	}
	
	public static void main(String[] args)
	{
		launch(args);	
	}
}

