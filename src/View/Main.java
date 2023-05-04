package View;
	
import javafx.stage.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Control.PathHandler;
import Control.PendingOrdersFactory;
import Model.DataBaseHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;

public class Main extends Application 
{
	private DataBaseHandler myDB;
	private String path;
	private PathHandler pathHandler;
	private String version = "->v4.9";
	private String programTitle = "Database Toolbox"+version;
	private String pathToImportant = "ImportantFiles";
	
	private Button openOrdersButton;
	private Button loadDBButton;
	private Button exitButton;
	
	private GridPane gridPane;
	private Stage pendingStage;
	
	private FileInputStream inputstreamMain;
	private Image imageMain;
	private ImageView imageViewMain;
	
	private Image imageStage;
	private ImageView imageViewStage;
	
	private Image imageOpenOrdersButton;
    private ImageView viewOpenOrdersButton;
    
	private Image imageLoadDBButton;
    private ImageView viewLoadDBButton;
    
	private Image imageExitButton;
    private ImageView viewExitButton;
	
	@Override
	public void start(Stage primaryStage) 
	{
		myDB = DataBaseHandler.getInstance();
		pathHandler = PathHandler.getInstance();
		pathHandler.checkPathFile();
		checkIfAlreadyOpen(pathHandler.getPath());
		
		createIcons();
		
		pendingStage = new Stage();
		if(imageStage != null)
		{
			pendingStage.getIcons().add(imageStage);
		}
			
		//Creating Buttons     
		createButtons();	    		    
		    
		loadDBButton.setOnAction(pathHandler);
		    
		PendingOrdersFactory pendingOrdersFactory = new PendingOrdersFactory();
	    pendingOrdersFactory.setDB();
	    pendingOrdersFactory.setStage(pendingStage);
	    
	    openOrdersButton.setOnAction(pendingOrdersFactory);
	    
	    
	    pendingStage.initOwner(primaryStage);
	    pendingStage.initModality(Modality.WINDOW_MODAL);
	    
	    
//		    createLogoForMain();
		//Creating a Grid Pane 
	    createGridPane();
	    
	    //Creating a scene object 
		Scene scene = new Scene(gridPane,300,300);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
//			createLogoForStage();
		
		
		if(imageStage != null)
		{
			primaryStage.getIcons().add(imageStage);
		}
		
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
		
	}
	
	private static void checkIfImportantDirectoryExists()
	{
		JFrame frame = new JFrame("Swing Tester");
		try {
	         File file = new File("pathToImportant");
	         file.createNewFile();
	         if(!file.exists())
	         {
	        	 JOptionPane.showMessageDialog(frame, "Ο φάκελος 'ImportantFiles' δεν βρίσκεται στον τρέχον κατάλογο ή έχει μεταβληθεί το περιεχόμενό του. \nΠαρακαλώ τοποθετήστε τον στον τρέχον κατάλογο!",
		                 "Σφάλμα Βιβλιοθηκών-Γραφικών", JOptionPane.ERROR_MESSAGE);
	        	 file.delete();
	        	 System.exit(0);
	         } 
	         file.delete();
	         System.out.println(file.exists());
	      } catch(Exception e) {
	         e.printStackTrace();
	      }
	}
	
	private void createButtons()
	{
		openOrdersButton = new Button("Άνοιγμα παραγγελιών");
	    loadDBButton = new Button("Φόρτωση Βάσης Δεδομένων");
	    exitButton = new Button("Έξοδος");
	    
	    exitButton.setId("exitButton");
	    
	    openOrdersButton.setMaxWidth(Double.MAX_VALUE);
	    loadDBButton.setMaxWidth(Double.MAX_VALUE);		    
	    exitButton.setMaxWidth(Double.MAX_VALUE);
	    
	    openOrdersButton.setGraphic(viewOpenOrdersButton);
	    loadDBButton.setGraphic(viewLoadDBButton);
	    exitButton.setGraphic(viewExitButton);
	    
	    openOrdersButton.setCursor(Cursor.HAND);
	    loadDBButton.setCursor(Cursor.HAND);
	    exitButton.setCursor(Cursor.HAND);	    
	}
	
	private void createGridPane()
	{
		gridPane = new GridPane();

	  //Spaces between the buttons 
	    gridPane.setHgap(10);
	    gridPane.setVgap(10);	   
	    	        
	    gridPane.add(openOrdersButton, 0, 1);
	    gridPane.add(loadDBButton, 0, 2);
	    gridPane.add(exitButton, 0, 3);
	}
	
	private void createIcons()
	{
		createLogoForStage();
		createOpenOrdersButtonIcon();
		createLoadDBButtonIcon();
		createExitButtonIcon();
	}
	
	private void createLogoForStage()
	{
		URL url = getClass().getResource("/Icons/StageLogo.png");	
		imageStage = new Image(url.toString()); 
		imageViewStage = new ImageView(imageStage);		
	}	
	
	private void createOpenOrdersButtonIcon()
	{		
		URL url = getClass().getResource("/Icons/OpenButton.png");		
		imageOpenOrdersButton = new Image(url.toString()); 
		viewOpenOrdersButton = new ImageView(imageOpenOrdersButton);
		viewOpenOrdersButton.setFitHeight(20); 
		viewOpenOrdersButton.setFitWidth(18);		
	}
	
	private void createLoadDBButtonIcon()
	{
		URL url = getClass().getResource("/Icons/LoadButton.png");				
		imageLoadDBButton = new Image(url.toString()); 
		viewLoadDBButton = new ImageView(imageLoadDBButton);
		viewLoadDBButton.setFitHeight(20); 
		viewLoadDBButton.setFitWidth(18);
	}
	
	private void createExitButtonIcon()
	{
		URL url = getClass().getResource("/Icons/ExitButton.png");			
		imageExitButton = new Image(url.toString()); 
		viewExitButton = new ImageView(imageExitButton);
		viewExitButton.setFitHeight(20); 
		viewExitButton.setFitWidth(20);
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
    	alert.setTitle("Σφάλμα");
    	alert.setHeaderText("Ανοιχτή βάση δεδομένων");
    	alert.setContentText("Παρακαλώ κλείστε την βάση δεδομένων ή \nτο πρόγραμμα το οποίο χρησιμοποιεί την \nσυγκεκριμένη βάση.");
    	alert.showAndWait();
	}
	
	public static void main(String[] args)
	{
//		checkIfImportantDirectoryExists();
		launch(args);	
	}
}

