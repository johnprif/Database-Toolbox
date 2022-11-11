package compute;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

public class DataBaseHandler 
{
	private Connection connection;
	private ResultSet pendingSet;
	private ResultSet hashesPendingSet;
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
	private String sql;
	private String TimeCreation;
	private String DateLastEdit;
	private String ExecutionTime;
	double doubleQuantity=0;
	private String path;
	
	private ArrayList<String> SiloID = new ArrayList<String>();
	private ArrayList<String> SiloQuantity = new ArrayList<String>();
	private ArrayList<String> SiloQuantityCopy = new ArrayList<String>();
	
	public DataBaseHandler()
	{
		data = FXCollections.observableArrayList();	
	}
	
	public void setPath(String path)
	{
		this.path = path;
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
            checkHashes();
        }catch(Exception e)
		{
        	alertWindow();
        	System.exit(0);
        }

	}
	
	public void checkHashes()
	{
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate("delete from BatchData where OrderCode='##' or OrderCode='###' or OrderCode='####' or OrderCode='#####' or OrderCode='######' or OrderCode='#######' or OrderCode='########' or OrderCode='#########' or OrderCode='##########' or OrderCode='###########' or OrderCode='############' or OrderCode='#############' or OrderCode='##############' or OrderCode='###############' or OrderCode='################'");
			connection.commit();
			System.out.println("Deleted hashes ok");
		} catch (SQLException e) {
			System.out.println("Error in searching hashes");
			readOnlyWindow();
		}
	}

	private void readOnlyWindow()
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Σφάλμα ανάγνωσης");
		alert.setHeaderText("Η βάση περιέχει καταχώρηση του τύπου: ########");
		alert.setContentText("Θα πρέπει να σβήσετε εκείνη την καταχώρηση ώστε να μπορείτε να κάνετε τροποποιήσεις στην βάση");

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
//			PreparedStatement preparedStatement = connection.prepareStatement("select * from Orders where ExecutionState=0 or ExecutionState=1");
			PreparedStatement preparedStatement = connection.prepareStatement("select * from Orders where ExecutionState=0");
			
			//Creating Java ResultSet object
			pendingSet = preparedStatement.executeQuery();

		}catch(Exception e)
		{
			System.out.println("Error in searching");
		}
	}
	
	private void parseEntries()
	{
		try {
			while(pendingSet.next())
			{
				addOrders();
			}
		} catch (SQLException e) {
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
	    
	    Order order = new Order(OrderCode, RecipeCode, Quantity, ProjectCode, CustomerCode, VehicleCode, DriverCode, DateCreation, ExecutionDate, TimeCreation, ExecutionTime, MixerCapacity, BatchQuantity, NoOfBatches, DateLastEdit);
	    data.add(order);
	    System.out.println("Order: ", OrderCode, RecipeCode, Quantity, ProjectCode, CustomerCode, VehicleCode, DriverCode, DateCreation, ExecutionDate, TimeCreation, ExecutionTime, MixerCapacity, BatchQuantity, NoOfBatches, DateLastEdit);
	}
	
	public ObservableList<Order> getData()
	{
		return data;
	}
	
	public void updateDataBase(Order order) throws SQLException
	{
		cooking(order);
//		System.out.println("EDW EIMAI");
		Statement update = connection.createStatement();
		String sql1 = "update Orders set DateCreation="+order.getDateCreation()+" , TimeCreation="+order.getTimeCreation()+" , DateLastEdit="+order.getExecutionDate()+" , ExecutionDate="+order.getExecutionDate()+" , ExecutionTime="+order.getExecutionTime()+" , ExecutionDuration="+computeDurationTime(Integer.parseInt(order.getNoOfBatches()))+" , ExecutionState=2 , BatchesProduced="+order.getNoOfBatches()+" , ShippingInvoiceNumber="+setShippingInvoiceNumber()+" where OrderCode="+"\""+order.getOrderCode()+"\"";
		byte[] bytes = sql1.getBytes(StandardCharsets.UTF_8);
		String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);
		
		//update.executeUpdate(utf8EncodedString);
//		System.out.println("EDW EIMAI");
		update.executeUpdate(sql1);
		
		
		
		connection.commit();
		
		printOrder(order);
		
//		cooking(order);
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
		PreparedStatement preparedStatement = connection.prepareStatement("select NextOrderShippingInvoiceNumber from Parameters");
		
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
		String sql1 = "update Parameters set NextOrderShippingInvoiceNumber="+next;
		
//		byte[] bytes = sql1.getBytes(StandardCharsets.UTF_8);
//		String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);
//		update.executeUpdate(utf8EncodedString);
		update.executeUpdate(sql1);
		connection.commit();
		
		return ShippingInvoiceNumber;
	}
	
	public void setShippingInvoiceNumber(String newNumber) throws SQLException
	{
		Statement update = connection.createStatement();
		String sql1 = "update Parameters set NextOrderShippingInvoiceNumber="+newNumber;
		byte[] bytes = sql1.getBytes(StandardCharsets.UTF_8);
		String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);
		update.executeUpdate(utf8EncodedString);
		connection.commit();
	}
	
	public int getShippingInvoiceNumber() throws SQLException
	{
		int ShippingInvoiceNumber;
		//Using SQL SELECT QUERY
		PreparedStatement preparedStatement = connection.prepareStatement("select NextOrderShippingInvoiceNumber from Parameters");
		
		//Creating Java ResultSet object
		ResultSet pendingSet2 = preparedStatement.executeQuery();
		pendingSet2.next();
		
	    ShippingInvoiceNumber = Integer.parseInt(pendingSet2.getString("NextOrderShippingInvoiceNumber"));  
		
		return ShippingInvoiceNumber;
	}
	
	private void cooking(Order order) throws SQLException
	{	
		parseOrderIngredients(order.getOrderCode());
		//System.out.println("EDW EIMAI");
		addEntriesToBatchIngredientsTable(order);
		addEntriesToBatchData(order);
		clearOldBatches();
	}
	
	private void parseOrderIngredients(String OrderCode) throws SQLException
	{
		//Using SQL SELECT QUERY
		PreparedStatement preparedStatement = connection.prepareStatement("select * from OrderIngredients where OrderCode='"+OrderCode+"'");
//		System.out.println("EDW EIMAI");
		ResultSet pendingSet2 = preparedStatement.executeQuery();
		try {
			while(pendingSet2.next())
			{
				SiloID.add(pendingSet2.getString("SiloID"));
				SiloQuantity.add(pendingSet2.getString("Quantity"));
				SiloQuantityCopy.add(pendingSet2.getString("Quantity"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void addEntriesToBatchIngredientsTable(Order order) throws SQLException
	{
		int NoOfBatches = Integer.parseInt(order.getNoOfBatches());
		ArrayList<String[]> Quantitys = computeQuantitys(order, NoOfBatches);
		
		int batchNumber;
		int siloID;
		int Quantity;
		int ActualQuantity;
//		System.out.println("EDW EIMAI");
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
	
	private void addEntriesToBatchData(Order order) throws SQLException
	{
		int noOfBatches = Integer.parseInt(order.getNoOfBatches());
		int mixingStartTime;
		int max;
		int min;
		int range;
		int newHours;
		int newMinutes;
		int newSeconds;
		String newCoockedTime;
		String temp;
		String[] tempMixingStartTime;
		
	//	mixingStartTime = Integer.parseInt(order.getExecutionTime());
	//	newCoockedTime = mixingStartTime + "";
		newCoockedTime = order.getExecutionTime();
		for(int i=0; i<noOfBatches; i++)
		{
			
			Statement statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO BatchData " + "VALUES ( "+order.getOrderCode()+" , "+(i+1)+" , "+newCoockedTime+" , "+0+" , "+0+" , "+getWaterAdjustSiloID()+")");

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
//					newMinutes = (int) Integer.parseInt(tempMixingStartTime[1]+tempMixingStartTime[2])-60;
					newMinutes = randomMinutes-60;
				}else
				{
					newHours = Integer.parseInt(tempMixingStartTime[0]);
//					newMinutes = (int) Integer.parseInt(tempMixingStartTime[1]+tempMixingStartTime[2]);
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
//					tempMixingStartTime[1] = (newMinutes / 10) + ""; //firstMinute
//					tempMixingStartTime[2] = (newMinutes % 10) + ""; //secondMinute
					tempMixingStartTime[1] = tempNewMinutes[0]; //firstMinute
					tempMixingStartTime[2] = tempNewMinutes[1]; //secondMinute
				}
				tempMixingStartTime[3] = ((int) ((Math.random() * 4) + 1)) + ""; //firstSecond
				tempMixingStartTime[4] = ((int) ((Math.random() * 8) + 1)) + ""; //secondSecong
				
				newCoockedTime = tempMixingStartTime[0]+tempMixingStartTime[1]+tempMixingStartTime[2]+tempMixingStartTime[3]+tempMixingStartTime[4];
			}else								//12 54 89 == length == 6
			{
				System.out.println("tempMixingStartTime == "+ tempMixingStartTime.length);
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
//					tempMixingStartTime[0] = (newHours / 10) + ""; //firsHours
//					tempMixingStartTime[1] = (newHours % 10) + ""; //secondHours
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
//					tempMixingStartTime[2] = (int)(newMinutes / 10) + ""; //firstMinute
//					tempMixingStartTime[3] = (int)(newMinutes % 10) + ""; //secondMinute
					tempMixingStartTime[2] = tempNewMinutes[0]; //firstMinute
					tempMixingStartTime[3] = tempNewMinutes[1]; //secondMinute
				}
				tempMixingStartTime[4] = ((int) ((Math.random() * 4) + 1)) + ""; //firstSecond
				tempMixingStartTime[5] = ((int) ((Math.random() * 8) + 1)) + ""; //secondSecong
					
				newCoockedTime = tempMixingStartTime[0]+tempMixingStartTime[1]+tempMixingStartTime[2]+tempMixingStartTime[3]+tempMixingStartTime[4]+tempMixingStartTime[5];
			}
		//	mixingStartTime = Integer.parseInt(newCoockedTime);
		}
		connection.commit();
	}
	
	private int getWaterAdjustSiloID() throws SQLException
	{
		int WaterAdjustSiloID;
		
		PreparedStatement preparedStatement = connection.prepareStatement("select * from Silos where SiloScaleID=203");
		ResultSet pendingSet2 = preparedStatement.executeQuery();
		
		pendingSet2.next();
		
	    WaterAdjustSiloID = Integer.parseInt(pendingSet2.getString("SiloID"));  
		
		
		return WaterAdjustSiloID;
	}
	
	private ArrayList<String[]> computeQuantitys(Order order, int NoOfBatches)
	{
//		System.out.println("EDW EIMAI");
		ArrayList<String[]> Quantitys = new ArrayList<String[]>();
		//ArrayList<Integer> oldNewQuantity = new ArrayList<Integer>();
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
				
				if(i == NoOfBatches-1)
				{
					max = (int) (1.005*Quantity);
					min = (int) (0.995*Quantity);
					range = (max - min) + 1;
				}else
				{
					max = (int) (1.04*Quantity);
					min = (int) (0.96*Quantity);
					range = (max - min) + 1;
				}
				
				QuantityActual = (int) ((Math.random() * range) + min);
				
				String[] idQuaAcQua= {(i+1)+"" , SiloID.get(j), Quantity+"", QuantityActual+""};
				Quantitys.add(idQuaAcQua);
				
			//	intOldSiloQuantity = Integer.parseInt(SiloQuantityCopy.get(j));
				oldQuantity =(int) (((intSiloQuantity * 1.0)/100)*intBatchQuantity);
				newQuantity = (Quantity - QuantityActual)+oldQuantity;
				oldNewQuantity.put(SiloID.get(j), newQuantity);
			}			
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
	
	private void alertWindow()
	{
		Alert alert = new Alert(Alert.AlertType.ERROR);
    	alert.setTitle("Error in connection");
    	alert.setHeaderText("Πρόβλημα με την βάση δεδομένων");
    	alert.setContentText("Πιθανόν να βρίσκετε σε διαφορετικό κατάλογο ή να έχει διαγραφεί");
    	alert.showAndWait();
	}
}

