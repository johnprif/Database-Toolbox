package Model;

import javafx.beans.property.SimpleStringProperty;

public class Order 
{
	private SimpleStringProperty OrderCode; //
	private SimpleStringProperty RecipeCode; //
	private SimpleStringProperty Quantity; //
	private SimpleStringProperty MixerCapacity;
	private SimpleStringProperty BatchQuantity;
	private SimpleStringProperty NoOfBatches;
	private SimpleStringProperty ProjectCode; //
	private SimpleStringProperty CustomerCode; //
	private SimpleStringProperty VehicleCode; //
	private SimpleStringProperty DriverCode; //
//	private String MixTime;
//	private String Price;
	private SimpleStringProperty DateCreation; //
	private SimpleStringProperty TimeCreation;
	private SimpleStringProperty DateLastEdit;
//	private String DateScheduledExecution;
	private SimpleStringProperty ExecutionDate;
	private SimpleStringProperty ExecutionTime;
//	private String ExecutionState;
//	private String BatchesProduced;
//	private String UsedInRT;
//	private String ShippingInvoiceNumber;
//	
	public Order(String OrderCode, String RecipeCode, String Quantity, String ProjectCode, String CustomerCode, String VehicleCode, String DriverCode, String DateCreation, String ExecutionDate, String TimeCreation, String ExecutionTime, String MixerCapacity, String BatchQuantity, String NoOfBatches, String DateLastEdit)
	{
		this.OrderCode = new SimpleStringProperty(OrderCode);
		this.RecipeCode = new SimpleStringProperty(RecipeCode);
		this.Quantity = new SimpleStringProperty(Quantity);
		this.MixerCapacity = new SimpleStringProperty(MixerCapacity);
		this.BatchQuantity = new SimpleStringProperty(BatchQuantity);
		this.NoOfBatches = new SimpleStringProperty(NoOfBatches);
		this.ProjectCode = new SimpleStringProperty(ProjectCode);
		this.CustomerCode = new SimpleStringProperty(CustomerCode);
		this.VehicleCode = new SimpleStringProperty(VehicleCode);
		this.DriverCode = new SimpleStringProperty(DriverCode);
		this.DateCreation = new SimpleStringProperty(DateCreation);	
		this.ExecutionDate = new SimpleStringProperty(ExecutionDate);
		this.TimeCreation = new SimpleStringProperty(TimeCreation);
		this.DateLastEdit = new SimpleStringProperty(DateLastEdit);
		this.ExecutionTime = new SimpleStringProperty(ExecutionTime);
	}
	
	public String getOrderCode()
	{
		return OrderCode.get();
	}
	
	public void setOrderCode(String input) 
	{
		OrderCode.set(input);
    }
	
	public String getRecipeCode()
	{
		return RecipeCode.get();
	}
	
	public void setRecipeCode(String input) 
	{
		RecipeCode.set(input);
    }
	
	public String getQuantity()
	{
		return Quantity.get();
	}
	
	public void setQuantity(String input) 
	{
		Quantity.set(input);
    }
	
	public String getMixerCapacity()
	{
		return MixerCapacity.get();
	}
	
	public void setMixerCapacity(String input) 
	{
		MixerCapacity.set(input);
    }
	
	public String getBatchQuantity()
	{
		return BatchQuantity.get();
	}
	
	public void setBatchQuantity(String input)
	{
		BatchQuantity.set(input);
	}
	
	public String getNoOfBatches()
	{
		return NoOfBatches.get();
	}
	
	public void setNoOfBatches(String input)
	{
		NoOfBatches.set(input);
	}
	
	public String getProjectCode()
	{
		return ProjectCode.get();
	}
	
	public void setProjectCode(String input) 
	{
		ProjectCode.set(input);
    }
	
	public String getCustomerCode()
	{
		return CustomerCode.get();
	}
	
	public void setCustomerCode(String input) 
	{
		CustomerCode.set(input);
    }
	
	public String getVehicleCode()
	{
		return VehicleCode.get();
	}
	
	public void setVehicleCode(String input) 
	{
		VehicleCode.set(input);
    }
	
	public String getDriverCode()
	{
		return DriverCode.get();
	}
	
	public void setDriverCode(String input) 
	{
		DriverCode.set(input);
    }
	
	public String getDateCreation()
	{
		return DateCreation.get();
	}
	
	public void setTimeCreation(String input) 
	{
		TimeCreation.set(input);
    }
	
	public String getTimeCreation()
	{
		return TimeCreation.get();
	}
	
	public String getDateLastEdit()
	{
		return DateLastEdit.get();
	}
	
	public void setDateLastEdit(String input)
	{
		DateLastEdit.set(input);
	}
	
	public void setDateCreation(String input) 
	{
		DateCreation.set(input);
    }
	
	public String getExecutionDate()
	{
		return ExecutionDate.get();
	}
	
	public void setExecutionDate(String input) 
	{
		ExecutionDate.set(input);
    }
	
	public String getExecutionTime()
	{
		return ExecutionTime.get();
	}
	
	public void setExecutionTime(String input) 
	{
		ExecutionTime.set(input);
    }
}
