package Control;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap; // import the HashMap class
import java.util.Optional;
import Model.DataBaseHandler;
import Model.Order;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class ExecuteHandler  implements EventHandler<ActionEvent>
{
	private ArrayList<String[]> changes;
	private DataBaseHandler myDB;
	private ArrayList<Order> changes2;
	private HashMap<String, Order> changes3;
	private HashMap<String, HashMap<String, String>> currentHumidityValues;
	private TableView<Order> table;
	private Order order;
	
	public ExecuteHandler()
	{
		this.myDB = DataBaseHandler.getInstance();
	}
	
	@Override
	public void handle(ActionEvent arg0)
	{	
		order = table.getSelectionModel().getSelectedItem();
		
		if(order == null)
		{
			uncompleteWindow();
		}else //not null
		{
			try {
				if(!myDB.checkOrderIfExists(order.getOrderCode()))
				{
//					if(selectedItem.getDateCreation() != null && selectedItem.getExecutionDate() != null && selectedItem.getTimeCreation() != null && selectedItem.getExecutionTime() != null)
					if(order.getExecutionDate() != null && order.getExecutionTime() != null)
					{
						try {
//							myDB.updateDataBase(changes3.get(selectedItem.getOrderCode()));
//							myDB.updateDataBase(selectedItem);
							if(currentHumidityValues.size()==0)
							{
								myDB.updateDataBase(order);
							}else
							{
								myDB.updateDataBase2(order, currentHumidityValues);
							}
//							table.getItems().remove(selectedItem);   
							completeWindow();
							table.getItems().remove(order); 
							changes3.clear();					
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else
					{
						try {
//							emptyDate(order);
							emptyDate2(order, currentHumidityValues);	
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} 
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public void setChanges3(HashMap<String, Order> changes3)
	{
		this.changes3 = changes3;
	}
	
	public void setCurrentHumidityValues(HashMap<String, HashMap<String, String>> currentHumidityValues)
	{
		this.currentHumidityValues = currentHumidityValues;
	}
	
	public void setTable(TableView<Order> table)
	{
		this.table = table;
	}
	
	private void completeWindow()
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Επιτυχής Εκτέλεση!");
		alert.setHeaderText(null);
		alert.setContentText("Ενημερώθηκε επιτυχώς 1 παραγγελία");

		alert.showAndWait();
	}
	
	private void uncompleteWindow()
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Καμία αλλαγή!");
		alert.setContentText("Δεν έχει επιλεχθεί ή τροποποιηθεί καμία παραγγελία ώστε να γίνει η εκτέλεση");

		alert.showAndWait();
	}
	
	private void emptyDate(Order order) throws SQLException
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Παραγγελία προς εκτέλεση -> " +order.getOrderCode());
		alert.setHeaderText("Κενή Ημερομηνία!");
		alert.setContentText("Η 'Ημερομηνία Εκτέλεσης' είναι κενή!\nΠατήστε το 'ΟΚ' για να συμπληρωθούς αυτόματα με την τωρινή ημερομηνία\nΠατήστε το 'Cancel' για ακύρωση");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK)
		{
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
			LocalDateTime now = LocalDateTime.now();  
			String[] temp = dtf.format(now).split(" ");
			String[] tempDate = temp[0].split("/");
			String[] tempTime = temp[1].split(":");
			
			if(checkDate(tempDate, order, 1, tempTime[0], tempTime[1]))
			{
				order.setExecutionDate(tempDate[0]+tempDate[1]+tempDate[2]);
				order.setExecutionTime(tempTime[0]+tempTime[1]+tempTime[2]);
				myDB.updateDataBase(order);
//				table.getItems().remove(order);  
				table.getItems().set(table.getSelectionModel().getSelectedIndex(), order);
				changes3.put(order.getOrderCode(), order);
				completeWindow();
				table.getItems().remove(order); 
				changes3.clear();			
			}else
			{
				
			}
 		    // ... user chose OK
		} else {
		    // ... user chose CANCEL or closed the dialog
		}
	}
	
	private void emptyDate2(Order order, HashMap<String, HashMap<String, String>> currentHumidityValues) throws SQLException
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Παραγγελία προς εκτέλεση -> " +order.getOrderCode());
		alert.setHeaderText("Κενή Ημερομηνία!");
		alert.setContentText("Η 'Ημερομηνία Εκτέλεσης' είναι κενή!\nΠατήστε το 'ΟΚ' για να συμπληρωθούς αυτόματα με την τωρινή ημερομηνία\nΠατήστε το 'Cancel' για ακύρωση");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK)
		{
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
			LocalDateTime now = LocalDateTime.now();  
			String[] temp = dtf.format(now).split(" ");
			String[] tempDate = temp[0].split("/");
			String[] tempTime = temp[1].split(":");
			
			if(checkDate(tempDate, order, 1, tempTime[0], tempTime[1]))
			{
				order.setExecutionDate(tempDate[0]+tempDate[1]+tempDate[2]);
				order.setExecutionTime(tempTime[0]+tempTime[1]+tempTime[2]);
				if(currentHumidityValues.size()==0)
				{
					myDB.updateDataBase(order);
				}else
				{
					myDB.updateDataBase2(order, currentHumidityValues);
				}
//				table.getItems().remove(order);  
				table.getItems().set(table.getSelectionModel().getSelectedIndex(), order);
				changes3.put(order.getOrderCode(), order);
				completeWindow();
				table.getItems().remove(order); 
				changes3.clear();	
			}else
			{
				
			}
 		    // ... user chose OK
		} else {
		    // ... user chose CANCEL or closed the dialog
		}
	}
	
		private boolean checkDate(String[] medianDate, Order order, int mode, String hours, String minutes)
		{
			if(order.getDateCreation() == null)
			{
				return true;
			}

			String[] stringDateCreation = order.getDateCreation().split("");
			int intYear = Integer.parseInt(stringDateCreation[0]+stringDateCreation[1]+stringDateCreation[2]+stringDateCreation[3]);
			int intMonth = Integer.parseInt(stringDateCreation[4]+stringDateCreation[5]);
			int intDay = Integer.parseInt(stringDateCreation[6]+stringDateCreation[7]);
			int[] dateCreation = {intYear, intMonth, intDay};
			
			String[] stringTimeCreation = order.getTimeCreation().split("");
			int intHours;
			int intMinutes;
			int intSeconds;
			
			if(stringTimeCreation.length == 5)//1..9
			{
				intHours = Integer.parseInt(stringTimeCreation[0]);
				intMinutes = Integer.parseInt(stringTimeCreation[1]+stringTimeCreation[2]);
				intSeconds = Integer.parseInt(stringTimeCreation[3]+stringTimeCreation[4]);
			}else
			{
				intHours = Integer.parseInt(stringTimeCreation[0]+stringTimeCreation[1]);
				intMinutes = Integer.parseInt(stringTimeCreation[2]+stringTimeCreation[3]);
				intSeconds = Integer.parseInt(stringTimeCreation[4]+stringTimeCreation[5]);
			}
			
			for(int i=0; i<medianDate.length; i++)
			{
				if(Integer.parseInt(medianDate[i]) > dateCreation[i]) //check year, after month, after day, after time
				{
					return true;
				}else if(Integer.parseInt(medianDate[i]) == dateCreation[i])
				{
					if(i == medianDate.length-1)
					{
						if(order.getTimeCreation() == null)
						{
							return true;
						}else
						{
							if((hours.equals("null")) && (minutes.equals("null")))
							{
								DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
        						LocalDateTime now = LocalDateTime.now();  
        						String[] temp_1 = dtf.format(now).split(" ");
        						String[] tempDate = temp_1[0].split("/");
        						String[] tempTime = temp_1[1].split(":");
        						hours = tempTime[0];
        						minutes = tempTime[1];
							}else if(!(hours.equals("null")) && !(minutes.equals("null")))
							{
								hours = hours;
								minutes = minutes;
//								if(order.getExecutionTime()!=null)
//								{
//									String[] stringExecutionTime = order.getExecutionTime().split("");
//									if(stringExecutionTime.length == 5)
//									{
//										hours = stringExecutionTime[0];
//										minutes = stringExecutionTime[1]+stringExecutionTime[2];
//									}else
//									{
//										hours = stringExecutionTime[0] + stringExecutionTime[1];
//										minutes = stringExecutionTime[2]+stringExecutionTime[3];
//									}
//								}
							
							}else
							{
								//nothing
								
							}
							if(Integer.parseInt(hours) > intHours)
							{
								return true;
							}else if(Integer.parseInt(hours) == intHours && Integer.parseInt(minutes) > intMinutes)
							{
								return true;
							}else
							{
								dateWindow(1);
								return false;
							}
						}
					}
					continue;
				}else 
				{
					dateWindow(1);
					return false;
				}
			}
		return false;
	}
	
		
		
	private void dateWindow(int mode)
	{
		Alert alert = new Alert(Alert.AlertType.WARNING);
    	alert.setTitle("Warning");
    	alert.setHeaderText("����� ����������");
    	if(mode == 0)
    	{
    		alert.setContentText("� ���������� ������� ��� ������� �� ����� ��� ���� ��� ��� ���������� ���������!");
    	}else
    	{
    		alert.setContentText("� ���������� ��������� ��� ������� �� ����� ��� ���� ��� ��� ���������� �������!\n�������� ��������� ����!");
    	}
    	alert.showAndWait();
	}
}
