package Control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import Model.DataBaseHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class PathHandler implements EventHandler<ActionEvent>
{
	private File myObj = new File("databaseLocation.txt");
	private String path;
	private DataBaseHandler myDB;

	//create an object of SingleObject
	private static PathHandler instance = new PathHandler();

	   //make the constructor private so that this class cannot be
	   //instantiated
	private PathHandler()
	{
		myDB.getInstance();
	}

	   //Get the only object available
	public static PathHandler getInstance()
	{
	      return instance;
	}
	
	@Override
	public void handle(ActionEvent arg0)
	{
		Stage stage = new Stage();
		FileChooser fileChooser = new FileChooser();
        stage.setWidth(800);
        stage.setHeight(800);
		fileChooser.setTitle("Επιλέξτε το αρχείο που περιέχει την βάση δεδομένων");
		fileChooser.getExtensionFilters().addAll(
			     new FileChooser.ExtensionFilter("Microsoft Access Data Base 2000-2007 Files", "*.mdb")
			    ,new FileChooser.ExtensionFilter("Microsoft Access Data Base 2008-2021 Files", "*.accdb")
			);
		File selectedFile = fileChooser.showOpenDialog(stage);
		
		if(selectedFile != null)
		{
			path = selectedFile.getAbsolutePath();
			setPath();
		}else
		{
		}		
	}
	
	private void setPath()
	{
		try {
		      FileWriter myWriter = new FileWriter("databaseLocation.txt");
		      myWriter.write(path);
		      myWriter.close();
		      System.out.println("Successfully wrote to the file.");
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}
	
	public String getPath()
	{
		readPath();
		return path;
	}
	
	public void checkPathFile()
	{
		//Check if file exists
		if (myObj.exists()) 
		{		
			//Check if file is empty
			if(myObj.length() == 0)
			{
				warningWindow();
				handle(null);
				
				myDB.setPath(getPath());
				myDB.clearOldData();
				myDB.initialize();			
			}else
			{
				//Read the path from the file
				readPath();
			}		
		} else //if not exists create a new one
		{
			try {
					myObj.createNewFile();
					warningWindow();
					handle(null);
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
		}
	}
	
	private void readPath()
	{
		try {
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) 
			{
		        path = myReader.nextLine();
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void warningWindow()
	{
		Alert alert = new Alert(Alert.AlertType.WARNING);
    	alert.setTitle("Προειδοποίηση");
    	alert.setHeaderText("Δεν υπάρχει καταχωρημένη βάση δεδομένων");
    	alert.setContentText("Επιλέξτε κάποια βάση δεδομένων πριν προχωρήσετε διότι δεν θα μπορείτε να συνεχίσετε");
    	alert.showAndWait();
	}
	
}
