package View;
	
import javafx.stage.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;

public class Main extends Application 
{
	private DataBaseHandler myDB;
	private String path;
	private PathHandler pathHandler;
	private String version = " -> v3.1";
	private String programTitle = "SAITEC"+version;
	private String pathToImportant = "ImportantFiles";
	
	private Button openOrdersButton;
	private Button loadDBButton;
	private Button exitButton;
	
	private GridPane gridPane;
	private Stage pendingStage;
	
	private FileInputStream inputstreamMain;
	private Image imageMain;
	private ImageView imageViewMain;
	
	private FileInputStream inputstreamStage;
	private Image imageStage;
	private ImageView imageViewStage;
	
	private FileInputStream inputstreamOpenOrdesButton;
	private Image imageOpenOrdersButton;
    private ImageView viewOpenOrdersButton;
    
    private FileInputStream inputstreamLoadDBButton;
	private Image imageLoadDBButton;
    private ImageView viewLoadDBButton;
    
    private FileInputStream inputstreamExitButton;
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
	    
	    if(viewOpenOrdersButton!=null)
	    {
	    	openOrdersButton.setGraphic(viewOpenOrdersButton);
	    }
	    
	    if(viewLoadDBButton!=null)
	    {
	    	loadDBButton.setGraphic(viewLoadDBButton);
	    }
	    
	    if(viewExitButton!=null)
	    {
	    	exitButton.setGraphic(viewExitButton);
	    }
	    
	}
	
	private void createGridPane()
	{
		gridPane = new GridPane();

	    //Setting the Grid alignment 
	    gridPane.setAlignment(Pos.CENTER); 
	  //Spaces between the buttons 
	    gridPane.setHgap(10);
	    gridPane.setVgap(10);	    
	    //Setting the back ground color 
	    gridPane.setStyle("-fx-background-color: grey;");

	    if(imageViewMain !=null)
	    {
	    	gridPane.add(imageViewMain, 0, 0);	
	    }
	    	    
//	    gridPane.add(imageView, 0, 0);	    
	    gridPane.add(openOrdersButton, 0, 1);
	    gridPane.add(loadDBButton, 0, 2);
	    gridPane.add(exitButton, 0, 3);

	    
	}
	
	private void createIcons()
	{
		createLogoForMain();
		createLogoForStage();
		createOpenOrdersButtonIcon();
		createLoadDBButtonIcon();
		createExitButtonIcon();
	}
	
	private void createLogoForMain()
	{
		String saitecLogo = pathToImportant+"/Icons/SaitecLogo.png";
		
		File f = new File(saitecLogo);
		
		if(f.exists() && !f.isDirectory())
		{
			try {
				inputstreamMain = new FileInputStream(saitecLogo);
			} catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			imageMain = new Image(inputstreamMain); 
			imageViewMain = new ImageView(imageMain);
			//setting the fit height and width of the image view 
		    imageViewMain.setFitHeight(50); 
		    imageViewMain.setFitWidth(172);
		}else
		{
			imageMain=null;
			imageViewMain = null;
		}
		
	}
	
	private void createLogoForStage()
	{
		String stageLogo = pathToImportant+"/Icons/StageLogo.png";
		
		File f = new File(stageLogo);
		
		if(f.exists() && !f.isDirectory())
		{
			try {
				inputstreamStage = new FileInputStream(stageLogo);
			} catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			imageStage = new Image(inputstreamStage); 
			imageViewStage = new ImageView(imageStage);
			//setting the fit height and width of the image view 
		}else
		{
			imageStage = null;
			imageViewStage = null;
		}
		
	}
	
	
	
	private void createOpenOrdersButtonIcon()
	{
		String openImage = pathToImportant+"/Icons/OpenButton.png";
		File f = new File(openImage);
		
		if(f.exists() && !f.isDirectory())
		{
			try {
				inputstreamOpenOrdesButton = new FileInputStream(openImage);						
			}catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			imageOpenOrdersButton = new Image(inputstreamOpenOrdesButton); 
			viewOpenOrdersButton = new ImageView(imageOpenOrdersButton);
			viewOpenOrdersButton.setFitHeight(20); 
			viewOpenOrdersButton.setFitWidth(18);
		}else
		{
			imageOpenOrdersButton = null;
			viewOpenOrdersButton = null;
		}		
	}
	
	private void createLoadDBButtonIcon()
	{
		String loadImage = pathToImportant+"/Icons/LoadButton.png";
		File f = new File(loadImage);
		if(f.exists() && !f.isDirectory())
		{
			try {
				inputstreamLoadDBButton = new FileInputStream(loadImage);						
			}catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			imageLoadDBButton = new Image(inputstreamLoadDBButton); 
			viewLoadDBButton = new ImageView(imageLoadDBButton);
			viewLoadDBButton.setFitHeight(20); 
			viewLoadDBButton.setFitWidth(18);
		}else
		{
			imageLoadDBButton = null;
			viewLoadDBButton = null;
		}	
	}
	
	private void createExitButtonIcon()
	{
		String exitImage = pathToImportant+"/Icons/ExitButton.png";
		File f = new File(exitImage);
		if(f.exists() && !f.isDirectory())
		{
			try {
				inputstreamExitButton = new FileInputStream(exitImage);						
			}catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			imageExitButton = new Image(inputstreamExitButton); 
			viewExitButton = new ImageView(imageExitButton);
			viewExitButton.setFitHeight(20); 
			viewExitButton.setFitWidth(20);
		}else
		{
			imageLoadDBButton = null;
			viewLoadDBButton = null;
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
    	alert.setHeaderText("Ανοιχτή βάση δεδομένων");
    	alert.setContentText("Παρακαλώ κλείστε την βάση δεδομένων ή το πρόγραμμα το οποίο χρησιμοποιεί την συγκεκριμένη βάση δεδομενών ώστε να μην προκύψει κάποιο πρόβλημα πρωτού συνεχίσετε");
    	alert.showAndWait();
	}
	
	public static void main(String[] args)
	{
		launch(args);	
	}
}

