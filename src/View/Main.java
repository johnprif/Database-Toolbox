package View;
	
import javafx.stage.WindowEvent;
import java.io.File;
import java.sql.SQLException;

import Control.PathHandler;
import Control.PendingOrdersFactory;
import Model.DataBaseHandler;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;

public class Main extends Application 
{
	private DataBaseHandler myDB;
	private String path;
	private PathHandler takeThePath;
	private String version = "-v3.0-";
	private String programTitle = "SAITEC"+version;
	
	private Button openOrdersButton;
	private Button loadDBButton;
	private Button exitButton;
	
	private GridPane gridPane;
	private Stage pendingStage;
	
	@Override
	public void start(Stage primaryStage) 
	{
		myDB = DataBaseHandler.getInstance();
		takeThePath = PathHandler.getInstance();
		takeThePath.checkPathFile();
		checkIfAlreadyOpen(takeThePath.getPath());
//		path = takeThePath.getPath();
//		myDB.setPath(path);
//		myDB.initialize();
		pendingStage = new Stage();
		try {		
			//Creating Buttons     
			createButtons();	    		    
		    
		    loadDBButton.setOnAction(takeThePath);
		    
		    PendingOrdersFactory pendingOrdersFactory = new PendingOrdersFactory();
		    pendingOrdersFactory.setDB();
		    pendingOrdersFactory.setStage(pendingStage);
		    
		    openOrdersButton.setOnAction(pendingOrdersFactory);
		    
		    
		    pendingStage.initOwner(primaryStage);
		    pendingStage.initModality(Modality.WINDOW_MODAL);
		    
			//Creating a Grid Pane 
		    createGridPane();
		    
		    
		    //Creating a scene object 
			Scene scene = new Scene(gridPane,300,300);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			//Setting title to the Stage 
			primaryStage.setTitle(programTitle);
			
			//Adding scene to the stage 
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			//Displaying the contents of the stage
			primaryStage.show();
			
			exitButton.setOnAction(new EventHandler<ActionEvent>() {
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
	
	private void createButtons()
	{
		openOrdersButton = new Button("Άνοιγμα παραγγελιών");
	    loadDBButton = new Button("Φόρτωση Βάσης Δεδομένων");
	    exitButton = new Button("Έξοδος");
	    
	    openOrdersButton.setMaxWidth(Double.MAX_VALUE);
	    loadDBButton.setMaxWidth(Double.MAX_VALUE);		    
	    exitButton.setMaxWidth(Double.MAX_VALUE);
	    
	    openOrdersButton.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    loadDBButton.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    exitButton.setStyle("-fx-font-weight: bold; -fx-text-fill: red; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
	    
	}
	
	private void createGridPane()
	{
		gridPane = new GridPane();

	    //Setting the Grid alignment 
	    gridPane.setAlignment(Pos.CENTER); 

	    //Arranging all the nodes in the grid 
	    gridPane.add(openOrdersButton, 0, 0);
	    gridPane.add(loadDBButton, 0, 1);
	    gridPane.add(exitButton, 0, 2);

	    //Spaces between the buttons 
	    gridPane.setHgap(10);
	    gridPane.setVgap(10);
	    
	    //Setting the back ground color 
//	    gridPane.setStyle("-fx-background-color: orangered;");
	    //gridPane.setStyle("-fx-background-color: navy;");
	    gridPane.setStyle("-fx-background-color: dodgerblue;");
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
    	alert.setHeaderText("Ανοιχτή βάση δεδομένων");
    	alert.setContentText("Παρακαλώ κλείστε την βάση δεδομένων ή το πρόγραμμα το οποίο χρησιμοποιεί την συγκεκριμένη βάση δεδομενών ώστε να μην προκύψει κάποιο πρόβλημα πρωτού συνεχίσετε");
    	alert.showAndWait();
	}
	
	public static void main(String[] args)
	{
		launch(args);	
	}
}

