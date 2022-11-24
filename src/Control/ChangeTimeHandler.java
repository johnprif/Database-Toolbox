package Control;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import Model.Order;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChangeTimeHandler  implements EventHandler<ActionEvent>
{
	private DatePicker checkInDatePicker;
	private Label checkInlabel;
	private Stage stage;
	private Button button;
	private ArrayList<String[]> changes;
	private ArrayList<Order> changes2;
	private Order order;
	private boolean flag = false;
	private TableView<Order> table;
	private String newValue;
	private HashMap<String, Order> changes3;
	private ComboBox hours;
	private ComboBox minutes;
	Random rand;
	
	
	public ChangeTimeHandler(Button button, Stage stage)
	{
		rand = new Random();
		//stage = new Stage();
		this.stage = stage;
		this.button = button;
	}
	
	@Override
	public void handle(ActionEvent arg0)
	{	
		
		if(flag == false)
		{
			warningWindowForFlag();
			stage.close();
		}else
		{
			order = table.getSelectionModel().getSelectedItem();

			Button button1 = new Button("Επιλογή");
			Button button2 = new Button("Ακύρωση");
			button1.setMaxWidth(Double.MAX_VALUE);
		    button2.setMaxWidth(Double.MAX_VALUE);
		    button1.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
		    button2.setStyle("-fx-font-weight: bold; -fx-text-fill: darkslategrey; -fx-border-radius: 5; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
		    
		    createAndFillTimeBox();
		    
			HBox hbox = new HBox(5, button1, button2);
			hbox.setAlignment(Pos.BASELINE_CENTER);
			VBox vbox = new VBox(20);
	        vbox.setStyle("-fx-padding: 10;");
	        Scene scene = new Scene(vbox, 400, 400);
	        stage.setScene(scene);
	        stage.setTitle("Αλλαγή Ημερμηνίας -> "+order.getOrderCode());
	        vbox.setAlignment(Pos.BASELINE_CENTER);
	        vbox.setStyle("-fx-background-color: grey;");
	        checkInDatePicker = new DatePicker();
	        checkInDatePicker.setEditable(false);
	        GridPane gridPane = new GridPane();
	        gridPane.setHgap(10);
	        gridPane.setVgap(10);

	        if(button.getText().equals("Αλλαγή Χρόνου Δημιουργίας"))
	        {
	        	checkInlabel = new Label("Αλλαγή Χρόνου Δημιουργίας");
	        }else
	        {
	        	checkInlabel = new Label("Αλλαγή Χρόνου Εκτέλεσης");
	        }
	        
	        checkInlabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
	        gridPane.add(checkInlabel, 0, 0);

	        
	        GridPane.setHalignment(checkInlabel, HPos.CENTER);
	        
	        HBox timeVbox = new HBox(5, hours, minutes);
	        timeVbox.setAlignment(Pos.BASELINE_CENTER);
	        
	        gridPane.add(checkInDatePicker, 0, 1);
	        gridPane.add(timeVbox, 0, 2);
//	        gridPane.add(minutes, 0, 3);
	        gridPane.add(hbox, 0, 3);
//	        gridPane.add(hbox, 0, 2);
	        gridPane.setAlignment(Pos.CENTER); 
	        vbox.getChildren().add(gridPane);
	        //stage.setHeight(150);
	        //stage.setWidth(168);
	        stage.setHeight(400);
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
		        	if(checkInDatePicker.getValue() != null)
		        	{		        		
		        		String preDate = checkInDatePicker.getValue()+"";
		        		String[] temp = preDate.split("-");
		        		//temp[0] == year
		        		//temp[1] == month
		        		//temp[2] == day
		        		String afterDate = temp[0]+temp[1]+temp[2];
		        		
		        		String stringHours = hours.getValue()+"";
		        		String stringMinutes = minutes.getValue()+"";
		        		
//		        		Order order = table.getSelectionModel().getSelectedItem();
		        		
		        		if(button.getText().equals("Αλλαγή Χρόνου Δημιουργίας"))
		        		{
		        			if(checkDate(temp, order, 0, stringHours, stringMinutes))
		        			{
		        				order.setDateCreation(afterDate);
		        				order.setDateLastEdit(order.getExecutionDate());
		        				if(hours.getSelectionModel().isEmpty() && minutes.getSelectionModel().isEmpty())
		        				{
		        					order.setTimeCreation(order.getTimeCreation());
		        					stage.close();
		        				}else if((hours.getSelectionModel().isEmpty() && !(minutes.getSelectionModel().isEmpty())) || (!(hours.getSelectionModel().isEmpty()) &&(minutes.getSelectionModel().isEmpty())))
		        				{
		        					emptyOnlyOneDate();
		        				}else
		        				{
		        					if(checkDate(temp, order, 0, stringHours, stringMinutes))
		        					{
		        						order.setTimeCreation(stringHours+stringMinutes+(rand.nextInt(50)+10));
			        					stage.close();
		        					}
		        					
		        				}
		        				table.getItems().set(table.getSelectionModel().getSelectedIndex(), order);
		        				changes3.put(order.getOrderCode(), order);
		        			}
		        		}else if(button.getText().equals("Αλλαγή Χρόνου Εκτέλεσης"))
		        		{
		        			if(checkDate(temp, order, 1, stringHours, stringMinutes))
		        			{
		        				order.setDateLastEdit(afterDate);
		        				order.setExecutionDate(afterDate);
		        				if(hours.getSelectionModel().isEmpty() && minutes.getSelectionModel().isEmpty())        				
		        				{
		        					if(order.getExecutionTime() == null)
		        					{
		        						DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		        						LocalDateTime now = LocalDateTime.now();  
		        						String[] temp_1 = dtf.format(now).split(" ");
		        						String[] tempDate = temp_1[0].split("/");
		        						String[] tempTime = temp_1[1].split(":");
		        						order.setExecutionTime(tempTime[0]+tempTime[1]+tempTime[2]);
		        					}else
		        					{
		        						order.setExecutionTime(order.getExecutionTime());
		        					}
		        					stage.close();
		        				}else if((hours.getSelectionModel().isEmpty() && !(minutes.getSelectionModel().isEmpty())) || (!(hours.getSelectionModel().isEmpty()) &&(minutes.getSelectionModel().isEmpty())))
		        				{
		        					emptyOnlyOneDate();
		        				}else
		        				{
		        					if(checkDate(temp, order, 1, stringHours, stringMinutes))
		        					{
		        						order.setExecutionTime(stringHours+stringMinutes+(rand.nextInt(50)+10));
			        					stage.close();
		        					}
		        					
		        				}
		        				table.getItems().set(table.getSelectionModel().getSelectedIndex(), order);
		        				changes3.put(order.getOrderCode(), order);
		        			}
		        		}	        			
			        	//stage.close();
		        	}else
		        	{
		        		warningWindowForDate();
		        	}	
		        }
		    });
			
		}
	}
	
	private void createAndFillTimeBox()
	{
		hours = new ComboBox();
		minutes = new ComboBox();
		
		hours.getItems().add("0");
		hours.getItems().add("1");
		hours.getItems().add("2");
		hours.getItems().add("3");
		hours.getItems().add("4");
		hours.getItems().add("5");
		hours.getItems().add("6");
		hours.getItems().add("7");
		hours.getItems().add("8");
		hours.getItems().add("9");
		hours.getItems().add("10");
		hours.getItems().add("11");
		hours.getItems().add("12");
		hours.getItems().add("13");
		hours.getItems().add("14");
		hours.getItems().add("15");
		hours.getItems().add("16");
		hours.getItems().add("17");
		hours.getItems().add("18");
		hours.getItems().add("19");
		hours.getItems().add("20");
		hours.getItems().add("21");
		hours.getItems().add("22");
		hours.getItems().add("23");
		
		
		minutes.getItems().add("00");
		minutes.getItems().add("01");
		minutes.getItems().add("02");
		minutes.getItems().add("03");
		minutes.getItems().add("04");
		minutes.getItems().add("05");
		minutes.getItems().add("06");
		minutes.getItems().add("07");
		minutes.getItems().add("08");
		minutes.getItems().add("09");
		minutes.getItems().add("10");
		minutes.getItems().add("11");
		minutes.getItems().add("12");
		minutes.getItems().add("13");
		minutes.getItems().add("14");
		minutes.getItems().add("15");
		minutes.getItems().add("16");
		minutes.getItems().add("17");
		minutes.getItems().add("18");
		minutes.getItems().add("19");
		minutes.getItems().add("20");
		minutes.getItems().add("21");
		minutes.getItems().add("22");
		minutes.getItems().add("23");
		minutes.getItems().add("24");
		minutes.getItems().add("25");
		minutes.getItems().add("26");
		minutes.getItems().add("27");
		minutes.getItems().add("28");
		minutes.getItems().add("29");
		minutes.getItems().add("30");
		minutes.getItems().add("31");
		minutes.getItems().add("32");
		minutes.getItems().add("33");
		minutes.getItems().add("34");
		minutes.getItems().add("35");
		minutes.getItems().add("36");
		minutes.getItems().add("37");
		minutes.getItems().add("38");
		minutes.getItems().add("39");
		minutes.getItems().add("40");
		minutes.getItems().add("41");
		minutes.getItems().add("42");
		minutes.getItems().add("43");
		minutes.getItems().add("44");
		minutes.getItems().add("45");
		minutes.getItems().add("46");
		minutes.getItems().add("47");
		minutes.getItems().add("48");
		minutes.getItems().add("49");
		minutes.getItems().add("50");
		minutes.getItems().add("51");
		minutes.getItems().add("52");
		minutes.getItems().add("53");
		minutes.getItems().add("54");
		minutes.getItems().add("55");
		minutes.getItems().add("56");
		minutes.getItems().add("57");
		minutes.getItems().add("58");
		minutes.getItems().add("59");
		
		hours.setPromptText("ΩΡΑ");
		minutes.setPromptText("ΛΕΠΤΑ");
	}
	
	private boolean checkDate(String[] medianDate, Order order, int mode, String hours, String minutes)
	{	
		if(mode == 0) //DateCreation
		{
			if(order.getExecutionDate() == null)
			{
				return true;
			}
				
			String[] stringExecutionDate = order.getExecutionDate().split("");
			int intYear = Integer.parseInt(stringExecutionDate[0]+stringExecutionDate[1]+stringExecutionDate[2]+stringExecutionDate[3]);
			int intMonth = Integer.parseInt(stringExecutionDate[4]+stringExecutionDate[5]);
			int intDay = Integer.parseInt(stringExecutionDate[6]+stringExecutionDate[7]);
			int[] executionDate = {intYear, intMonth, intDay};
			
			String[] stringExecutionTime = order.getExecutionTime().split("");
			int intHours;
			int intMinutes;
			int intSeconds;
			
			if(stringExecutionTime.length == 5)//07 15 54
			{
				intHours = Integer.parseInt(stringExecutionTime[0]);
				intMinutes = Integer.parseInt(stringExecutionTime[1]+stringExecutionTime[2]);
				intSeconds = Integer.parseInt(stringExecutionTime[3]+stringExecutionTime[4]);
			}else if(stringExecutionTime.length == 5)//15 45 21
			{
				intHours = Integer.parseInt(stringExecutionTime[0]+stringExecutionTime[1]);
				intMinutes = Integer.parseInt(stringExecutionTime[2]+stringExecutionTime[3]);
				intSeconds = Integer.parseInt(stringExecutionTime[4]+stringExecutionTime[5]);
			}else if(stringExecutionTime.length == 4)//00 52 06
			{
				intHours = 0;
				intMinutes = Integer.parseInt(stringExecutionTime[0]+stringExecutionTime[1]);
				intSeconds = Integer.parseInt(stringExecutionTime[2]+stringExecutionTime[3]);
			}else if(stringExecutionTime.length == 3)//00 04 34
			{
				intHours = 0;
				intMinutes = Integer.parseInt(stringExecutionTime[0]);
				intSeconds = Integer.parseInt(stringExecutionTime[1]+stringExecutionTime[2]);
			}else if(stringExecutionTime.length == 2)//00 00 34
			{
				intHours = 0;
				intMinutes = 0;
				intSeconds = Integer.parseInt(stringExecutionTime[0]+stringExecutionTime[1]);
			}else if(stringExecutionTime.length == 1)//00 00 04
			{
				intHours = 0;
				intMinutes = 0;
				intSeconds = Integer.parseInt(stringExecutionTime[0]);
			}else//00 00 34
			{
				intHours = 0;
				intMinutes = 0;
				intSeconds = 0;
			}
			
			
			for(int i=0; i<medianDate.length; i++)
			{
				if(Integer.parseInt(medianDate[i]) < executionDate[i])
				{
					return true;
				}else if(Integer.parseInt(medianDate[i]) == executionDate[i])
				{
					if(i == medianDate.length-1)
					{
						if(order.getExecutionTime() == null)
						{
							return true;
						}else
						{
							if((hours.equals("null")) && (minutes.equals("null")))
							{
								System.out.println("Hello I AM Here");
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
							if(Integer.parseInt(hours) < intHours)
							{
								return true;
							}else if(Integer.parseInt(hours) == intHours && Integer.parseInt(minutes) < intMinutes)
							{
								return true;
							}else
							{
								dateWindow(0);
								return false;
							}
						}
					}
					continue;
				}else 
				{
					dateWindow(0);
					return false;
				}
			}
			
		}else //mode == 1  //ExecutionDate
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
		}
		return false;
	}
	
	private boolean checkTime(String[] medianTime, Order order, int mode)
	{
		return true;
	}
	
	private void dateWindow(int mode)
	{
		Alert alert = new Alert(Alert.AlertType.WARNING);
    	alert.setTitle("Warning");
    	alert.setHeaderText("Λάθος ημερομηνία");
    	if(mode == 0)
    	{
    		alert.setContentText("Η ημερομηνία έναρξης δεν γίνεται να είναι πιο μετά από την ημερομηνία εκτέλεσης!");
    	}else
    	{
    		alert.setContentText("Η ημερομηνία εκτέλεσης δεν γίνεται να είναι πιο πριν από την ημερομηνία έναρξης!\nΠαρακαλώ δοκιμάστε ξάνα!");
    	}
    	alert.showAndWait();
	}
	
	public void setTable(TableView<Order> table)
	{
		this.table = table;
	}

	public void setChanges3(HashMap<String, Order> changes3)
	{
		this.changes3 = changes3;
	}
	
	public void setOrder(Order order)
	{
		this.order = order;
	}
	
	public void setFlag(boolean flag)
	{
		this.flag = flag;
	}
	
	private void checkTheChoise()
	{
		if(flag == false)
		{
			warningWindowForFlag();
			stage.close();
		}
	}
	
	private void warningWindowForDate()
	{
		Alert alert = new Alert(Alert.AlertType.WARNING);
    	alert.setTitle("Warning");
    	alert.setHeaderText("Κενό πεδίο");
    	alert.setContentText("Δεν μπορείτε να καταχωρίσετε κενή ημερομηνία!\nΓια να συνεχίσετε πρέπει να επιλέξετε κάποια απο τις διαθέσιμες ημερομηνίες και να πατήσετε το πλήκτρο 'Επιλογή' αλλιώς πιέστε το πλήκτρο 'Ακύρωση'");
    	alert.showAndWait();
	}
	
	private void warningWindowForFlag()
	{
		Alert alert = new Alert(Alert.AlertType.WARNING);
    	alert.setTitle("Warning");
    	alert.setHeaderText("Καμία επιλογή");
    	alert.setContentText("Δεν έχει επιλεγεί καμία απο τις παραγγελίες\nΠαρακαλώ επιλέξτε κάποια πρωτού συνεχίσετε");
    	alert.showAndWait();
	}
	
	public String getNewValue()
	{
		return newValue;
	}

	private void emptyOnlyOneDate()
	{
		Alert alert = new Alert(Alert.AlertType.WARNING);
    	alert.setTitle("Warning");
    	alert.setHeaderText("Κενή ώρα ή λεπτά");
    	alert.setContentText("Έχετε επιλέξει μόνο ώρα ή λεπτά!\nΠαρακαλώ συμπληρώστε και τα δύο αλλιώς αφήστε τα κενά ώστε να διατηρηθεί η τρέχουσα ώρα");
    	alert.showAndWait();
	}
}
