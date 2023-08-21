package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

public class DataBaseHandler 
{
	private Connection connection;
	private ResultSet pendingSet;
	private ObservableList<Order> data;
	private String OrderCode;
	private String RecipeCode;
	private String Quantity;
	private String MixerCapacity;
	private String BatchQuantity;
	private String NoOfBatches;
	private String ProjectCode;
	private String CustomerCode;
	private String VehicleCode;
	private String DriverCode;
	private String DateCreation;
	private String ExecutionDate;
	private String TimeCreation;
	private String DateLastEdit;
	private String ExecutionTime;
	double doubleQuantity=0;
	private String path;	
	private ArrayList<String> SiloID = new ArrayList<String>();
	private ArrayList<String> SiloQuantity = new ArrayList<String>();
	private ArrayList<String> SiloQuantityCopy = new ArrayList<String>();
	private HashMap<String, String> siloIDs;
	private ArrayList<String> siloIDsArrayList;
	private HashMap<String, ArrayList<String>> humiditySilosPerOrder;
	private HashMap<String, HashMap<String, String>> currentHumidityValues;
	private ArrayList<Double> waterPerCycle;

	private static DataBaseHandler instance = new DataBaseHandler();
	private String WaterAdjustSiloID;
	
	private DataBaseHandler()
	{
		data = FXCollections.observableArrayList();	
	}
	
	public static DataBaseHandler getInstance()
	{
	      return instance;
	}
	
	public void setPath(String path)
	{
		this.path = path;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public void clearOldData()
	{
		data.clear();
	}
	
	public void initialize()
	{
		try
		{
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");//Loading Driver
            connection = DriverManager.getConnection("jdbc:ucanaccess://"+path);//Establishing Connection
//          checkHashes();
        }catch(Exception e)
		{
        	errorWindow(e.getMessage());
        	System.exit(0);
        }

	}
		
	//PROBLEM! IT NO TAKES ALL THE # PROPERLY
	//TODO FIX
	private void checkHashes()
	{
		int i=0;
		try {
			Statement statement1 = connection.createStatement();
			statement1.executeQuery("SELECT OrderCode FROM BatchData WHERE OrderCode LIKE'##%'");
			i++;
			Statement statement2 = connection.createStatement();
			statement2.executeQuery("SELECT OrderCode FROM BatchIngredients WHERE OrderCode LIKE '##%'");
			i++;
			Statement statement3 = connection.createStatement();
			statement3.executeQuery("SELECT OrderCode FROM OrderIngredients WHERE OrderCode LIKE '##%'");
			i++;
			Statement statement4 = connection.createStatement();
			statement4.executeQuery("SELECT OrderCode FROM Orders WHERE OrderCode LIKE '##%'");

			connection.commit();
			System.out.println("It has not hashes - ok");
		} catch (SQLException e) {
			errorWindow(e.getMessage());
			System.out.println(e.getMessage());
			readOnlyWindow(e.getMessage(), i);
		}
	}

	private void readOnlyWindow(String message, int i)
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(message);
		alert.setHeaderText("Η βάση περιέχει καταχωρήσεις που περιέχουν πολλαπλές #\n"+i);
		alert.setContentText("Θα πρέπει να σβήσετε εκείνες τις καταχωρήσεις σε κάποιους απο τους παρακάτω πίνακα ώστε να μπορείτε να κάνετε τροποποιήσεις στην βάση\n- BatchData\n- BatchIngredients\n- OrderIngredients\n- Orders");

		alert.showAndWait();
		
		System.exit(0);
	}
	
	public void findAndParse()
	{
		findPendingOrders();
		parseEntries();
	}
	
	private void findPendingOrders()
	{
		try 
		{
			//Using SQL SELECT QUERY
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT OrderCode, RecipeCode, Quantity, MixerCapacity, BatchQuantity, NoOfBatches, ProjectCode, CustomerCode, VehicleCode, DriverCode, DateCreation, TimeCreation, DateLastEdit, ExecutionDate, ExecutionTime FROM Orders WHERE ExecutionState=0");
			
			//Creating Java ResultSet object
			pendingSet = preparedStatement.executeQuery();
		}catch(Exception e)
		{
			errorWindow(e.getMessage());
			System.out.println(e.getMessage());
			System.out.println("Error in searching");
		}
	}
	
	private void parseEntries()
	{		
		try {
			findHumiditySilos();
			while(pendingSet.next())
			{
				addOrders();
			}
		} catch (SQLException e) {
			errorWindow(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void addOrders() throws SQLException
	{
		OrderCode =  pendingSet.getString("OrderCode");
	    RecipeCode = pendingSet.getString("RecipeCode");	    
	    Quantity = pendingSet.getString("Quantity");
	    doubleQuantity = (Double.parseDouble(Quantity))/100.0;
	    Quantity = doubleQuantity+"";	    
	    MixerCapacity = pendingSet.getString("MixerCapacity");
	    BatchQuantity = pendingSet.getString("BatchQuantity");
	    NoOfBatches = pendingSet.getString("NoOfBatches");
	    ProjectCode = pendingSet.getString("ProjectCode");
	    CustomerCode = pendingSet.getString("CustomerCode");
	    VehicleCode = pendingSet.getString("VehicleCode");
	    DriverCode = pendingSet.getString("DriverCode");
	    DateCreation = pendingSet.getString("DateCreation");
	    TimeCreation = pendingSet.getString("TimeCreation");
	    DateLastEdit = pendingSet.getString("DateLastEdit");
	    ExecutionDate = pendingSet.getString("ExecutionDate");
	    ExecutionTime = pendingSet.getString("ExecutionTime");	    
	    
	    Order order = new Order(OrderCode, RecipeCode, Quantity, ProjectCode, CustomerCode, VehicleCode, DriverCode, DateCreation, ExecutionDate, TimeCreation, ExecutionTime, MixerCapacity, BatchQuantity, NoOfBatches, DateLastEdit, "ΟΧΙ");
	    data.add(order);
	}
	
	public ObservableList<Order> getData()
	{
		return data;
	}
	
	public void updateDataBase(Order order) throws SQLException
	{
		cooking(order);
		Statement update = connection.createStatement();
		String sql1 = "UPDATE Orders SET DateCreation="+order.getDateCreation()+" , TimeCreation="+order.getTimeCreation()+" , DateLastEdit="+order.getExecutionDate()+" , ExecutionDate="+order.getExecutionDate()+" , ExecutionTime="+order.getExecutionTime()+" , ExecutionDuration="+computeDurationTime(Integer.parseInt(order.getNoOfBatches()))+" , ExecutionState=2 , BatchesProduced="+order.getNoOfBatches()+" , ShippingInvoiceNumber="+setShippingInvoiceNumber()+" where OrderCode="+"\""+order.getOrderCode()+"\"";
		update.executeUpdate(sql1);
	
		connection.commit();
		
		printOrder(order);
	}
	
	public void setCurrentHumidityValuesToDB(HashMap<String, HashMap<String, String>> currentHumidityValues)
	{
		this.currentHumidityValues = currentHumidityValues;	
	}

	public void updateDataBase2(Order order) throws SQLException
	{
		cooking2(order);
		Statement update = connection.createStatement();
		String sql1 = "UPDATE Orders SET DateCreation="+order.getDateCreation()+" , TimeCreation="+order.getTimeCreation()+" , DateLastEdit="+order.getExecutionDate()+" , ExecutionDate="+order.getExecutionDate()+" , ExecutionTime="+order.getExecutionTime()+" , ExecutionDuration="+computeDurationTime(Integer.parseInt(order.getNoOfBatches()))+" , ExecutionState=2 , BatchesProduced="+order.getNoOfBatches()+" , ShippingInvoiceNumber="+setShippingInvoiceNumber()+" where OrderCode="+"\""+order.getOrderCode()+"\"";
		update.executeUpdate(sql1);		
		connection.commit();		
		printOrder(order);

	}
	
	private void printOrder(Order order)
	{
		System.out.println("OrderCode = "+order.getOrderCode());
		System.out.println("RecipeCode = "+order.getRecipeCode());
		System.out.println("Quantity = "+order.getQuantity());
		System.out.println("MixerCapacity = "+order.getMixerCapacity());
		System.out.println("BatchQuantity = "+order.getBatchQuantity());
		System.out.println("NoOfBatches = "+order.getNoOfBatches());
		System.out.println("ProjectCode = "+order.getProjectCode());
		System.out.println("CustomerCode = "+order.getCustomerCode());
		System.out.println("VehicleCode = "+order.getVehicleCode());
		System.out.println("DriverCode = "+order.getDriverCode());
		System.out.println("DateCreation = "+order.getDateCreation());		
		System.out.println("TimeCreation = "+order.getTimeCreation());
		System.out.println("DateLastEdit = "+order.getDateLastEdit());
		System.out.println("ExecutionDate = "+order.getExecutionDate());
		System.out.println("ExecutionTime = "+order.getExecutionTime());
		System.out.println("Humidity = "+order.getHumidity());
	}
	
	private int computeDurationTime(int cycles)
	{
		int max;
		int min;
		int range;
		int randomMinutes;
		int minutes=0;
		
		max = (int) (300); //change minutes
		min = (int) (150);
		range = (max - min) + 1;
		
		for(int i=0; i<cycles; i++)
		{
			randomMinutes = (int) ((Math.random() * range) + min);
			minutes += randomMinutes;
		}
		
		return minutes;
	}
	
	private int setShippingInvoiceNumber() throws SQLException
	{
		int ShippingInvoiceNumber;
		int next;
		//Using SQL SELECT QUERY
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT NextOrderShippingInvoiceNumber FROM Parameters");
		
		//Creating Java ResultSet object
		ResultSet pendingSet2 = preparedStatement.executeQuery();
		pendingSet2.next();
		
	    ShippingInvoiceNumber = Integer.parseInt(pendingSet2.getString("NextOrderShippingInvoiceNumber"));  
		
		Statement update = connection.createStatement();
		
		if(ShippingInvoiceNumber == 9999999)
		{
			next = 1;
		}else
		{
			next = ShippingInvoiceNumber+1;
		}
		String sql1 = "UPDATE Parameters SET NextOrderShippingInvoiceNumber="+next;
		update.executeUpdate(sql1);
		connection.commit();
		
		return ShippingInvoiceNumber;
	}
	
	public void setShippingInvoiceNumber(String newNumber) throws SQLException
	{
		Statement update = connection.createStatement();
		String sql1 = "UPDATE Parameters SET NextOrderShippingInvoiceNumber="+newNumber;
		update.executeUpdate(sql1);
		connection.commit();
	}
	
	public void findHumiditySilos() throws SQLException
	{
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT SiloID, Description FROM Silos WHERE SiloScaleID=201 AND (AllowManualHumidity=True OR NOT HumidityScaleID=0)");
		ResultSet pendingSet2 = preparedStatement.executeQuery();
		
		siloIDs = new HashMap<String, String>();
		siloIDsArrayList = new ArrayList<String>();
		
		try {
			while(pendingSet2.next())
			{
				siloIDsArrayList.add(pendingSet2.getString("SiloID"));
				siloIDs.put(pendingSet2.getString("SiloID"), pendingSet2.getString("Description"));
				System.out.println("HERE IS THE HUMIDITY SILOS -> "+ pendingSet2.getString("SiloID"));
			}
		} catch (SQLException e) {
			errorWindow(e.getMessage());
			e.printStackTrace();
		}
		System.out.println("HERE IS THE HUMIDITY SILOS length-> "+ siloIDs.size());
	}
	
	public HashMap<String, String> getHumiditySilos()
	{
		return siloIDs;
	}
	
	public ArrayList<String> getHumidityIDs()
	{
		return siloIDsArrayList;
	}
	
	public String checkIfSilosContainHumidityPerOrder(String OrderCode) throws SQLException
	{
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT SiloID FROM OrderIngredients WHERE OrderCode='"+OrderCode+"'");
		ResultSet pendingSet4 = preparedStatement.executeQuery();
		
		try {
			while(pendingSet4.next())
			{
				if(siloIDs.get(pendingSet4.getString("SiloID")) != null)
				{
					return "ΝΑΙ";
				}
			}
		} catch (SQLException e) {
			errorWindow(e.getMessage());
			e.printStackTrace();
		}

		return "ΟΧΙ";
	}
	
	public void findHumiditySilosPerOrder(String OrderCode) throws SQLException
	{
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT SiloID FROM OrderIngredients WHERE OrderCode='"+OrderCode+"'");
		ResultSet pendingSet5 = preparedStatement.executeQuery();
		
		humiditySilosPerOrder = new HashMap<String, ArrayList<String>>();
		ArrayList<String> test2 = new ArrayList<String>();
		try {
			while(pendingSet5.next())
			{
				if(siloIDs.get(pendingSet5.getString("SiloID"))!=null)
				{
					test2.add(pendingSet5.getString("SiloID"));
				}				
			} 
			humiditySilosPerOrder.put(OrderCode, test2);
		}catch (SQLException e) {
			errorWindow(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getHumiditySilosPerOrder(String OrderCode)
	{
		try {
			findHumiditySilosPerOrder(OrderCode);
		} catch (SQLException e) {
			errorWindow(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return humiditySilosPerOrder.get(OrderCode);
		
	}
	
	public int getShippingInvoiceNumber() throws SQLException
	{
		int ShippingInvoiceNumber;
		//Using SQL SELECT QUERY
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT NextOrderShippingInvoiceNumber FROM Parameters");
		
		//Creating Java ResultSet object
		ResultSet pendingSet2 = preparedStatement.executeQuery();
		pendingSet2.next();
		
	    ShippingInvoiceNumber = Integer.parseInt(pendingSet2.getString("NextOrderShippingInvoiceNumber"));  
		
		return ShippingInvoiceNumber;
	}
	
	private void cooking(Order order) throws SQLException
	{	
		WaterAdjustSiloID = getWaterAdjustSiloID();
		
		parseOrderIngredients(order.getOrderCode());
		addEntriesToBatchIngredientsTable(order);
		addEntriesToBatchData(order);
		clearOldBatches();
	}
	
	private void cooking2(Order order) throws SQLException
	{	
		WaterAdjustSiloID = getWaterAdjustSiloID();
				
		parseOrderIngredients(order.getOrderCode());
		addEntriesToBatchIngredientsTable2(order);
		addEntriesToBatchData2(order);
		clearOldBatches();
	}

	
	private void parseOrderIngredients(String OrderCode) throws SQLException
	{
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT SiloID, Quantity FROM OrderIngredients WHERE OrderCode='"+OrderCode+"'");
		ResultSet pendingSet2 = preparedStatement.executeQuery();
		try {
			while(pendingSet2.next())
			{
				SiloID.add(pendingSet2.getString("SiloID"));
				SiloQuantity.add(pendingSet2.getString("Quantity"));
				SiloQuantityCopy.add(pendingSet2.getString("Quantity"));
			}
		} catch (SQLException e) {
			errorWindow(e.getMessage());
			e.printStackTrace();
		}
	}
	
	//TODO - DELETE OR SHOW POPUP FROM BATCHINGRIDIENT WHERE ORDERCODE ALREADY EXISTS 
	private void addEntriesToBatchIngredientsTable2(Order order) throws SQLException
	{
		int NoOfBatches = Integer.parseInt(order.getNoOfBatches());
		ArrayList<String[]> Quantitys = computeQuantitys2(order, NoOfBatches);
		
		String UsedHumidityTemp;
		
		int batchNumber;
		int siloID;
		int Quantity;
		int ActualQuantity;
		double UsedHumidity;

		for(int i=0; i<Quantitys.size(); i++)
		{
			batchNumber = Integer.parseInt(Quantitys.get(i)[0]);
			siloID = Integer.parseInt(Quantitys.get(i)[1]);
			Quantity = Integer.parseInt(Quantitys.get(i)[2]);
			ActualQuantity = Integer.parseInt(Quantitys.get(i)[3]);
			
			
			UsedHumidityTemp = currentHumidityValues.get(order.getOrderCode()).get(siloID+"");
			if(UsedHumidityTemp == null)
			{
				UsedHumidity = 0;
			}else
			{
				UsedHumidity = 10*Double.parseDouble(UsedHumidityTemp);
			}
			Statement statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO BatchIngredients " + "VALUES ( '"+order.getOrderCode()+"' , "+batchNumber+" , "+siloID+" , "+Quantity+" , "+Quantity+" , "+ActualQuantity+" , "+UsedHumidity+" )");	
		}	
		
		connection.commit();
	}
	
	//TODO - DELETE OR SHOW POPUP FROM BATCHINGRIDIENT WHERE ORDERCODE ALREADY EXISTS 
	private void addEntriesToBatchIngredientsTable(Order order) throws SQLException
	{
		int NoOfBatches = Integer.parseInt(order.getNoOfBatches());
		ArrayList<String[]> Quantitys = computeQuantitys(order, NoOfBatches);
		
		int batchNumber;
		int siloID;
		int Quantity;
		int ActualQuantity;

		for(int i=0; i<Quantitys.size(); i++)
		{
			batchNumber = Integer.parseInt(Quantitys.get(i)[0]);
			siloID = Integer.parseInt(Quantitys.get(i)[1]);
			Quantity = Integer.parseInt(Quantitys.get(i)[2]);
			ActualQuantity = Integer.parseInt(Quantitys.get(i)[3]);
			Statement statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO BatchIngredients " + "VALUES ( '"+order.getOrderCode()+"' , "+batchNumber+" , "+siloID+" , "+Quantity+" , "+Quantity+" , "+ActualQuantity+" , 0 )");		
		}
		connection.commit();
	}
	
	private void addEntriesToBatchData2(Order order) throws SQLException
	{	
		int noOfBatches = Integer.parseInt(order.getNoOfBatches());
		int max;
		int min;
		int range;
		int newHours;
		int newMinutes;
		int newSeconds;
		String newCoockedTime;
		String temp;
		String[] tempMixingStartTime;
		double percentageOfWater = 10*computeWaterAdjustement(order);
		newCoockedTime = order.getExecutionTime();

		for(int i=0; i<noOfBatches; i++)
		{
			Statement statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO BatchData " + "VALUES ( "+order.getOrderCode()+" , "+(i+1)+" , "+newCoockedTime+" , "+0+" , -"+waterPerCycle.get(0)+" , "+WaterAdjustSiloID+")");

			tempMixingStartTime = newCoockedTime.split("");
				
			if(tempMixingStartTime.length == 5) //4 54 18
			{
				max = (int) (5.0 + (Integer.parseInt(tempMixingStartTime[1]+tempMixingStartTime[2]))); //change minutes
				min = (int) (2.5 + (Integer.parseInt(tempMixingStartTime[1]+tempMixingStartTime[2])));
				range = (max - min) + 1;
				
				int randomMinutes = (int) ((Math.random() * range) + min);
				
				if(randomMinutes>59)
				{
					newHours = Integer.parseInt(tempMixingStartTime[0])+1;
					newMinutes = randomMinutes-60;
				}else
				{
					newHours = Integer.parseInt(tempMixingStartTime[0]);
					newMinutes = randomMinutes;
				}
				tempMixingStartTime[0] = newHours + ""; //Hours
				if(newMinutes < 10)
				{
					tempMixingStartTime[1] = 0 + ""; //firstMinute
					tempMixingStartTime[2] = newMinutes + ""; //secondMinute
				}else
				{
					String[] tempNewMinutes = (newMinutes+"").split("");
					tempMixingStartTime[1] = tempNewMinutes[0]; //firstMinute
					tempMixingStartTime[2] = tempNewMinutes[1]; //secondMinute
				}
				tempMixingStartTime[3] = ((int) ((Math.random() * 4) + 1)) + ""; //firstSecond
				tempMixingStartTime[4] = ((int) ((Math.random() * 8) + 1)) + ""; //secondSecong
				
				newCoockedTime = tempMixingStartTime[0]+tempMixingStartTime[1]+tempMixingStartTime[2]+tempMixingStartTime[3]+tempMixingStartTime[4];
			}else if(tempMixingStartTime.length == 6)						//12 54 89 == length == 6
			{
				max = (int) (5.0 + (Integer.parseInt(tempMixingStartTime[2]+tempMixingStartTime[3]))); //change minutes
				min = (int) (2.5 + (Integer.parseInt(tempMixingStartTime[2]+tempMixingStartTime[3])));
				range = (max - min) + 1;

				int randomMinutes = (int) (Math.random() * range) + min;
				int oldHours = Integer.parseInt(tempMixingStartTime[0]+tempMixingStartTime[1]);
				if(randomMinutes > 59)
				{
					if(oldHours == 23)
					{
						newHours = 0;
					}else
					{
						newHours = Integer.parseInt(tempMixingStartTime[0]+tempMixingStartTime[1])+1;	
					}
					newMinutes = randomMinutes-60;
				}else
				{
					newHours = Integer.parseInt(tempMixingStartTime[0]+tempMixingStartTime[1]);
					newMinutes = randomMinutes;
				}
				
				if(newHours < 10)
				{
					tempMixingStartTime[0] = 0 + ""; //firsHours
					tempMixingStartTime[1] = newHours + ""; //secondHours
				}else
				{
					String[] tempNewHour = (newHours+"").split("");
					tempMixingStartTime[0] = tempNewHour[0];
					tempMixingStartTime[1] = tempNewHour[1];
				}
				if(newMinutes < 10)
				{
					tempMixingStartTime[2] = 0 + ""; //firstMinute
					tempMixingStartTime[3] = newMinutes + ""; //secondMinute
				}else
				{
					String[] tempNewMinutes = (newMinutes+"").split("");
					tempMixingStartTime[2] = tempNewMinutes[0]; //firstMinute
					tempMixingStartTime[3] = tempNewMinutes[1]; //secondMinute
				}
				tempMixingStartTime[4] = ((int) ((Math.random() * 4) + 1)) + ""; //firstSecond
				tempMixingStartTime[5] = ((int) ((Math.random() * 8) + 1)) + ""; //secondSecong
					
				newCoockedTime = tempMixingStartTime[0]+tempMixingStartTime[1]+tempMixingStartTime[2]+tempMixingStartTime[3]+tempMixingStartTime[4]+tempMixingStartTime[5];
			}else if(tempMixingStartTime.length == 4)//00 52 06
			{
				max = (int) (5.0 + (Integer.parseInt(tempMixingStartTime[0]+tempMixingStartTime[1]))); //change minutes
				min = (int) (2.5 + (Integer.parseInt(tempMixingStartTime[0]+tempMixingStartTime[1])));
				range = (max - min) + 1;

				int randomMinutes = (int) (Math.random() * range) + min;

				if(randomMinutes > 59)
				{				
					newHours = 1;	
					newMinutes = randomMinutes-60;
				}else
				{
					newHours = 0;
					newMinutes = randomMinutes;
				}
				if(newMinutes < 10)
				{
					tempMixingStartTime[0] = 0 + ""; //firstMinute
					tempMixingStartTime[1] = newMinutes + ""; //secondMinute
				}else
				{
					String[] tempNewMinutes = (newMinutes+"").split("");
					tempMixingStartTime[0] = tempNewMinutes[0]; //firstMinute
					tempMixingStartTime[1] = tempNewMinutes[1]; //secondMinute
				}
				tempMixingStartTime[2] = ((int) ((Math.random() * 4) + 1)) + ""; //firstSecond
				tempMixingStartTime[3] = ((int) ((Math.random() * 8) + 1)) + ""; //secondSecong
				
				newCoockedTime = tempMixingStartTime[0]+tempMixingStartTime[1]+tempMixingStartTime[2]+tempMixingStartTime[3];	
			}else if(tempMixingStartTime.length == 3)//00 02 06
			{
				max = (int) (5.0 + (Integer.parseInt(tempMixingStartTime[0]))); //change minutes
				min = (int) (2.5 + (Integer.parseInt(tempMixingStartTime[0])));
				range = (max - min) + 1;

				int randomMinutes = (int) (Math.random() * range) + min;

				tempMixingStartTime[0] = randomMinutes + ""; //firstMinute + secondMinute

				tempMixingStartTime[1] = ((int) ((Math.random() * 4) + 1)) + ""; //firstSecond
				tempMixingStartTime[2] = ((int) ((Math.random() * 8) + 1)) + ""; //secondSecong
				
				newCoockedTime = tempMixingStartTime[0]+tempMixingStartTime[1]+tempMixingStartTime[2];	
			}else if(tempMixingStartTime.length == 2)//00 00 16
			{
				max = (int) (5.0 + 0); //change minutes
				min = (int) (2.5 + 0);
				range = (max - min) + 1;

				int randomMinutes = (int) (Math.random() * range) + min; //firstMinute and secondMinute
				
				tempMixingStartTime[0] = ((int) ((Math.random() * 4) + 1)) + ""; //firstSecond
				tempMixingStartTime[1] = ((int) ((Math.random() * 8) + 1)) + ""; //secondSecong
				
				newCoockedTime = randomMinutes+tempMixingStartTime[0]+tempMixingStartTime[1];	
			}else if(tempMixingStartTime.length == 1)//00 00 06
			{
				max = (int) (5.0 + 0); //change minutes
				min = (int) (2.5 + 0);
				range = (max - min) + 1;
				
				int randomMinutes = (int) (Math.random() * range) + min; //firstMinute and secondMinute
								
				int maxSec = (int) (5.0 + (Integer.parseInt(tempMixingStartTime[0]))); //change minutes
				int minSec = (int) (2.5 + (Integer.parseInt(tempMixingStartTime[0])));
				int rangeSec = (maxSec - minSec) + 1;
				
				int randomSeconds = (int) (Math.random() * rangeSec) + minSec;

				tempMixingStartTime[0] = randomSeconds + ""; //firstSecond
				
				newCoockedTime = randomMinutes+tempMixingStartTime[0]+tempMixingStartTime[1];	
			}else//00 00 00
			{
				max = (int) (5.0 + 0); //change minutes
				min = (int) (2.5 + 0);
				range = (max - min) + 1;
				
				int randomMinutes = (int) (Math.random() * range) + min; //firstMinute and secondMinute
				
				int maxSec = (int) (5.0 + 0); //change minutes
				int minSec = (int) (2.5 + 0);
				int rangeSec = (maxSec - minSec) + 1;
				
				int randomSeconds = (int) (Math.random() * rangeSec) + minSec;

				tempMixingStartTime[0] = randomSeconds + ""; //firstSecond
				
				newCoockedTime = randomMinutes+tempMixingStartTime[0];	
			}
		//	mixingStartTime = Integer.parseInt(newCoockedTime);
		}		
		connection.commit();
	}
	
	private double computeWaterAdjustement(Order order)
	{
		double percentageOfWater = 0.0;
		
		if(this.currentHumidityValues.get(order.getOrderCode())==null)
		{
			percentageOfWater = 0.0;
		}else
		{
			for(int i=0; i<siloIDsArrayList.size(); i++)
			{
				percentageOfWater += Double.parseDouble(currentHumidityValues.get(order.getOrderCode()).get(siloIDsArrayList.get(i)));
			}
		}	

		return percentageOfWater;
	}
	
	private void addEntriesToBatchData(Order order) throws SQLException
	{	
		int noOfBatches = Integer.parseInt(order.getNoOfBatches());
		int max;
		int min;
		int range;
		int newHours;
		int newMinutes;
		String newCoockedTime;
		String[] tempMixingStartTime;
		newCoockedTime = order.getExecutionTime();

		for(int i=0; i<noOfBatches; i++)
		{
			Statement statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO BatchData " + "VALUES ( "+order.getOrderCode()+" , "+(i+1)+" , "+newCoockedTime+" , "+0+" , "+0+" , "+WaterAdjustSiloID+")");

			tempMixingStartTime = newCoockedTime.split("");
			
			if(tempMixingStartTime.length == 5) //04 54 18
			{
				max = (int) (5.0 + (Integer.parseInt(tempMixingStartTime[1]+tempMixingStartTime[2]))); //change minutes
				min = (int) (2.5 + (Integer.parseInt(tempMixingStartTime[1]+tempMixingStartTime[2])));
				range = (max - min) + 1;
					
				int randomMinutes = (int) ((Math.random() * range) + min);
				
				if(randomMinutes>59)
				{
					newHours = Integer.parseInt(tempMixingStartTime[0])+1;
					newMinutes = randomMinutes-60;
				}else
				{
					newHours = Integer.parseInt(tempMixingStartTime[0]);
					newMinutes = randomMinutes;
				}
				tempMixingStartTime[0] = newHours + ""; //Hours
				if(newMinutes < 10)
				{
					tempMixingStartTime[1] = 0 + ""; //firstMinute
					tempMixingStartTime[2] = newMinutes + ""; //secondMinute
				}else
				{
					String[] tempNewMinutes = (newMinutes+"").split("");
					tempMixingStartTime[1] = tempNewMinutes[0]; //firstMinute
					tempMixingStartTime[2] = tempNewMinutes[1]; //secondMinute
				}
				tempMixingStartTime[3] = ((int) ((Math.random() * 4) + 1)) + ""; //firstSecond
				tempMixingStartTime[4] = ((int) ((Math.random() * 8) + 1)) + ""; //secondSecong
				
				newCoockedTime = tempMixingStartTime[0]+tempMixingStartTime[1]+tempMixingStartTime[2]+tempMixingStartTime[3]+tempMixingStartTime[4];
			}else if(tempMixingStartTime.length == 6)//15 45 21								//12 54 89 == length == 6
			{
				max = (int) (5.0 + (Integer.parseInt(tempMixingStartTime[2]+tempMixingStartTime[3]))); //change minutes
				min = (int) (2.5 + (Integer.parseInt(tempMixingStartTime[2]+tempMixingStartTime[3])));
				range = (max - min) + 1;

				int randomMinutes = (int) (Math.random() * range) + min;
				int oldHours = Integer.parseInt(tempMixingStartTime[0]+tempMixingStartTime[1]);
				if(randomMinutes > 59)
				{
					if(oldHours == 23)
					{
						newHours = 0;
					}else
					{
						newHours = Integer.parseInt(tempMixingStartTime[0]+tempMixingStartTime[1])+1;	
					}
					newMinutes = randomMinutes-60;
				}else
				{
					newHours = Integer.parseInt(tempMixingStartTime[0]+tempMixingStartTime[1]);
					newMinutes = randomMinutes;
				}
				
				if(newHours < 10)
				{
					tempMixingStartTime[0] = 0 + ""; //firsHours
					tempMixingStartTime[1] = newHours + ""; //secondHours
				}else
				{
					String[] tempNewHour = (newHours+"").split("");
					tempMixingStartTime[0] = tempNewHour[0];
					tempMixingStartTime[1] = tempNewHour[1];
				}
				if(newMinutes < 10)
				{
					tempMixingStartTime[2] = 0 + ""; //firstMinute
					tempMixingStartTime[3] = newMinutes + ""; //secondMinute
				}else
				{
					String[] tempNewMinutes = (newMinutes+"").split("");
					tempMixingStartTime[2] = tempNewMinutes[0]; //firstMinute
					tempMixingStartTime[3] = tempNewMinutes[1]; //secondMinute
				}
				tempMixingStartTime[4] = ((int) ((Math.random() * 4) + 1)) + ""; //firstSecond
				tempMixingStartTime[5] = ((int) ((Math.random() * 8) + 1)) + ""; //secondSecong
					
				newCoockedTime = tempMixingStartTime[0]+tempMixingStartTime[1]+tempMixingStartTime[2]+tempMixingStartTime[3]+tempMixingStartTime[4]+tempMixingStartTime[5];
			}else if(tempMixingStartTime.length == 4)//00 52 06
			{
				max = (int) (5.0 + (Integer.parseInt(tempMixingStartTime[0]+tempMixingStartTime[1]))); //change minutes
				min = (int) (2.5 + (Integer.parseInt(tempMixingStartTime[0]+tempMixingStartTime[1])));
				range = (max - min) + 1;

				int randomMinutes = (int) (Math.random() * range) + min;

				if(randomMinutes > 59)
				{				
					newHours = 1;	
					newMinutes = randomMinutes-60;
				}else
				{
					newHours = 0;
					newMinutes = randomMinutes;
				}
				if(newMinutes < 10)
				{
					tempMixingStartTime[0] = 0 + ""; //firstMinute
					tempMixingStartTime[1] = newMinutes + ""; //secondMinute
				}else
				{
					String[] tempNewMinutes = (newMinutes+"").split("");
					tempMixingStartTime[0] = tempNewMinutes[0]; //firstMinute
					tempMixingStartTime[1] = tempNewMinutes[1]; //secondMinute
				}
				tempMixingStartTime[2] = ((int) ((Math.random() * 4) + 1)) + ""; //firstSecond
				tempMixingStartTime[3] = ((int) ((Math.random() * 8) + 1)) + ""; //secondSecong
				
				newCoockedTime = tempMixingStartTime[0]+tempMixingStartTime[1]+tempMixingStartTime[2]+tempMixingStartTime[3];	
			}else if(tempMixingStartTime.length == 3)//00 02 06
			{
				max = (int) (5.0 + (Integer.parseInt(tempMixingStartTime[0]))); //change minutes
				min = (int) (2.5 + (Integer.parseInt(tempMixingStartTime[0])));
				range = (max - min) + 1;

				int randomMinutes = (int) (Math.random() * range) + min;

				tempMixingStartTime[0] = randomMinutes + ""; //firstMinute + secondMinute

				tempMixingStartTime[1] = ((int) ((Math.random() * 4) + 1)) + ""; //firstSecond
				tempMixingStartTime[2] = ((int) ((Math.random() * 8) + 1)) + ""; //secondSecong
				
				newCoockedTime = tempMixingStartTime[0]+tempMixingStartTime[1]+tempMixingStartTime[2];	
			}else if(tempMixingStartTime.length == 2)//00 00 16
			{
				max = (int) (5.0 + 0); //change minutes
				min = (int) (2.5 + 0);
				range = (max - min) + 1;

				int randomMinutes = (int) (Math.random() * range) + min; //firstMinute and secondMinute
				
				tempMixingStartTime[0] = ((int) ((Math.random() * 4) + 1)) + ""; //firstSecond
				tempMixingStartTime[1] = ((int) ((Math.random() * 8) + 1)) + ""; //secondSecong
				
				newCoockedTime = randomMinutes+tempMixingStartTime[0]+tempMixingStartTime[1];	
			}else if(tempMixingStartTime.length == 1)//00 00 06
			{
				max = (int) (5.0 + 0); //change minutes
				min = (int) (2.5 + 0);
				range = (max - min) + 1;
				
				int randomMinutes = (int) (Math.random() * range) + min; //firstMinute and secondMinute
								
				int maxSec = (int) (5.0 + (Integer.parseInt(tempMixingStartTime[0]))); //change minutes
				int minSec = (int) (2.5 + (Integer.parseInt(tempMixingStartTime[0])));
				int rangeSec = (maxSec - minSec) + 1;
				
				int randomSeconds = (int) (Math.random() * rangeSec) + minSec;

				tempMixingStartTime[0] = randomSeconds + ""; //firstSecond
				
				newCoockedTime = randomMinutes+tempMixingStartTime[0]+tempMixingStartTime[1];	
			}else//00 00 00
			{
				max = (int) (5.0 + 0); //change minutes
				min = (int) (2.5 + 0);
				range = (max - min) + 1;
				
				int randomMinutes = (int) (Math.random() * range) + min; //firstMinute and secondMinute
				
				int maxSec = (int) (5.0 + 0); //change minutes
				int minSec = (int) (2.5 + 0);
				int rangeSec = (maxSec - minSec) + 1;
				
				int randomSeconds = (int) (Math.random() * rangeSec) + minSec;

				tempMixingStartTime[0] = randomSeconds + ""; //firstSecond
				
				newCoockedTime = randomMinutes+tempMixingStartTime[0];	
			}
		}
				
		connection.commit();
	}
	
	private String getWaterAdjustSiloID() throws SQLException
	{	
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT SiloID FROM Silos WHERE SiloScaleID=203");
		ResultSet pendingSet2 = preparedStatement.executeQuery();
		
		pendingSet2.next();
		
	    WaterAdjustSiloID = pendingSet2.getString("SiloID");  
		
		
		return WaterAdjustSiloID;
	}
	
	private ArrayList<String[]> computeQuantitys(Order order, int NoOfBatches)
	{
		ArrayList<String[]> Quantitys = new ArrayList<String[]>();
		HashMap<String, Integer> oldNewQuantity = new HashMap<String, Integer>();
		int intSiloQuantity;
		int intBatchQuantity;
		int Quantity;
		int QuantityActual;

		int oldQuantity;
		int newQuantity;
		
		int max;
		int min;
		int range;

		for(int i=0; i<NoOfBatches; i++)
		{
			for(int j=0; j<SiloID.size(); j++)
			{
				intSiloQuantity = Integer.parseInt(SiloQuantity.get(j));
				intBatchQuantity = Integer.parseInt(order.getBatchQuantity());
				
				if(i>0)
				{
					Quantity = oldNewQuantity.get(SiloID.get(j));
				}else
				{
					Quantity = (int) (((intSiloQuantity * 1.0)/100)*intBatchQuantity);
				}
				
				max = (int) (1.04*Quantity);
				min = (int) (0.96*Quantity);
				range = (max - min) + 1;
				
				QuantityActual = (int) ((Math.random() * range) + min);
				
				String[] idQuaAcQua= {(i+1)+"" , SiloID.get(j), Quantity+"", QuantityActual+""};
				Quantitys.add(idQuaAcQua);
				
				oldQuantity =(int) (((intSiloQuantity * 1.0)/100)*intBatchQuantity);
				newQuantity = (Quantity - QuantityActual)+oldQuantity;
				oldNewQuantity.put(SiloID.get(j), newQuantity);
			}			
		}
		
		return Quantitys;
	}
	
	private ArrayList<String[]> computeQuantitys2(Order order, int NoOfBatches)
	{
		ArrayList<String[]> Quantitys = new ArrayList<String[]>();
		HashMap<String, Integer> oldNewQuantity = new HashMap<String, Integer>();
		waterPerCycle = new ArrayList<Double>();
		
		
		int tempAdWater;
		int tempAcWater;
		int tempMin;
		int tempMax;	
		int tempRange;
		
		
		int intSiloQuantity;
		int intBatchQuantity;
		int Quantity=0;
		int QuantityActual;

		int oldQuantity;
		int newQuantity;
		
		int max;
		int min;
		int range;
		
		String orderCode = order.getOrderCode();
		String humidityTemp;
		double humidity;
		double waterPerCycleTemp = 0.0;
		int waterPerCycleTempNEW = 0;
		int waterSiloIndexAtQuantitys = 0;
		
		for(int i=0; i<NoOfBatches; i++)
		{
			for(int j=0; j<SiloID.size(); j++)
			{
				intSiloQuantity = Integer.parseInt(SiloQuantity.get(j));
				intBatchQuantity = Integer.parseInt(order.getBatchQuantity());
				
				humidityTemp = currentHumidityValues.get(orderCode).get(SiloID.get(j));
		
				if(SiloID.get(j).equals(WaterAdjustSiloID))
				{
					waterSiloIndexAtQuantitys = j;
				}
				
				if(i>0)
				{
					if(humidityTemp!=null)
					{
						humidity = Double.parseDouble(humidityTemp)/100;
						Quantity = (int) (oldNewQuantity.get(SiloID.get(j)));
						
						if(Quantity*(1+humidity)-Quantity>95)
						{
							Quantity =(int) (Quantity+(Quantity*humidity)+(Quantity*humidity*humidity)+(Quantity*humidity*humidity*humidity));
						}else
						{
							Quantity = (int) (oldNewQuantity.get(SiloID.get(j))*((1+humidity)));	
						}
					}else
					{
						Quantity = oldNewQuantity.get(SiloID.get(j));
					}
				}else
				{
					if(humidityTemp != null)
					{
						humidity = Double.parseDouble(humidityTemp)/100;
						Quantity = (int) ((((intSiloQuantity * 1.0)/100)*intBatchQuantity));
						
						if(Quantity*(1+humidity)-Quantity>95)
						{
							Quantity =(int) (Quantity+(Quantity*humidity)+(Quantity*humidity*humidity)+(Quantity*humidity*humidity*humidity));
						}else
						{
							Quantity = (int) ((((intSiloQuantity * 1.0)/100)*intBatchQuantity)*((1+humidity)));	
						}
						waterPerCycleTempNEW = Quantity;
					}else
					{
						Quantity = (int) (((intSiloQuantity * 1.0)/100)*intBatchQuantity);
					}
				}
				
				max = (int) (1.04*Quantity);
				min = (int) (0.96*Quantity);


				range = (max - min) + 1;
					
				QuantityActual = (int) ((Math.random() * range) + min);
					
				String[] idQuaAcQua= {(i+1)+"" , SiloID.get(j), Quantity+"", QuantityActual+""};
				Quantitys.add(idQuaAcQua);
					
				oldQuantity =(int) (((intSiloQuantity * 1.0)/100)*intBatchQuantity);
				newQuantity = (Quantity - QuantityActual)+oldQuantity;
				oldNewQuantity.put(SiloID.get(j), newQuantity);
				
				if(humidityTemp != null)
				{
					humidity = Double.parseDouble(humidityTemp)/100;
					waterPerCycleTemp += waterPerCycleTempNEW*humidity;				
				}	
			}	
			
			tempAdWater = (int)(Double.parseDouble(Quantitys.get(waterSiloIndexAtQuantitys)[2])-waterPerCycleTemp);

			tempMax = (int) (1.02*tempAdWater);
			tempMin = (int) (0.98*tempAdWater);

			tempRange = (tempMax - tempMin) + 1;
										
			tempAcWater = (int) ((Math.random() * tempRange) + tempMin);

			String[] tempIdQuanAcQua = {(i+1)+"" , WaterAdjustSiloID, tempAdWater+"", tempAcWater+""};
			Quantitys.set((SiloID.size()*i)+waterSiloIndexAtQuantitys, tempIdQuanAcQua);
			
			waterSiloIndexAtQuantitys = 0;
			waterPerCycleTempNEW=0;
			waterPerCycle.add(waterPerCycleTemp);	
			waterPerCycleTemp = 0.0;
		}		
		return Quantitys;
	}
	
	private void clearOldBatches()
	{
		SiloID.clear();
		SiloQuantity.clear();
		SiloQuantityCopy.clear();
	}
	
	public void closeDB() throws SQLException
	{
		connection.commit();
		connection.close();
	}
	
	public boolean isClosed() throws SQLException
	{
		if(connection == null)
		{
			return true;
		}
		return false;
	}
	
	public void cleanDataBase() throws SQLException
	{
		Statement statement_1 = connection.createStatement();
		statement_1.executeUpdate("DELETE FROM BatchData");
		
		Statement statement_2 = connection.createStatement();
		statement_2.executeUpdate("DELETE FROM BatchIngredients");
		
		Statement statement_3 = connection.createStatement();
		statement_3.executeUpdate("DELETE FROM OrderIngredients");
		
		Statement statement_4 = connection.createStatement();
		statement_4.executeUpdate("DELETE FROM Orders");
		
		connection.commit();
	}
	
	public boolean checkOrderIfExists(String OrderCode) throws SQLException
	{
		PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT OrderCode FROM BatchData WHERE OrderCode='"+OrderCode+"'");
		ResultSet pendingSet1 = preparedStatement1.executeQuery();
		
		PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT OrderCode FROM BatchIngredients WHERE OrderCode='"+OrderCode+"'");
		ResultSet pendingSet2 = preparedStatement2.executeQuery();
		
		if(pendingSet1.next())
		{
			batchAlerts("BatchData", OrderCode);
			return true;
		}else if(pendingSet2.next())
		{
			batchAlerts("BatchIngredients", OrderCode);
			return true;
		}
		return false;
	}
	
	private void batchAlerts(String table, String OrderCode) throws SQLException
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(table);
		alert.setHeaderText("Πρόβλημα με την βάση δεδομένων!");
		alert.setContentText("Στον πίνακα= '"+table+"' εντοπίστηκε παραβίαση του κλειδιού '"+OrderCode+"'\nΘα πρέπει να σβήσετε τις καταχωρήσεις στους\n-BacthData\n-BatchIngridient\nμε OrderCode='"+OrderCode+"' πρωτού συνεχίσετε.\nΠατήστε 'ΟΚ' εάν επιθυμείτε να σβηστούν αυτόματα.\nΠατήστε 'Cancel' εάν επιθυμείτε να μην σβηστούν αυτόματα.");

		Optional<ButtonType> result = alert.showAndWait();	
		
		if (result.get() == ButtonType.OK)
		{
			cleanBadOrders(OrderCode);
			sucCleanBadOrders(OrderCode);
 		    // ... user chose OK
		} else {
		    // ... user chose CANCEL or closed the dialog
		}
	}
	
	private void cleanBadOrders(String OrderCode) throws SQLException
	{
		Statement statement_1 = connection.createStatement();
		statement_1.executeUpdate("DELETE FROM BatchData WHERE OrderCode='"+OrderCode+"'");
		
		Statement statement_2 = connection.createStatement();
		statement_2.executeUpdate("DELETE FROM BatchIngredients WHERE OrderCode='"+OrderCode+"'");
	}
	
	private void sucCleanBadOrders(String OrderCode)
	{
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
    	alert.setTitle("Ειδοποίηση");
    	alert.setHeaderText("Επιτυχής επιδιόρθωση!");
    	alert.setContentText("Η παραγγελία '"+OrderCode+"' ενημερώθηκε επιτυχώς.\nΤώρα μπορείτε να την εκτελέσετε επιτυχώς.");
    	alert.showAndWait();
	}
	
	private void errorWindow(String errorMessage)
	{
		Alert alert = new Alert(Alert.AlertType.ERROR);
    	alert.setTitle("Σφάλμα");
    	alert.setHeaderText(null);
    	alert.setContentText(errorMessage);
    	alert.showAndWait();
	}
}

