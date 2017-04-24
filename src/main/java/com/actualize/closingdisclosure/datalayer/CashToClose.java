package com.actualize.closingdisclosure.datalayer;

/**
 * This class defines CashToClose element in MISMO xml 
 * @author sboragala
 *
 */
public class CashToClose {

	private boolean isAmountChangedIndicator = false;
	private String  itemChangeDescription = "";
	private String  itemEstimatedAmount = "";
	private String  itemFinalAmount = "";
	private String  itemType = "";
	private String  itemPaymentType = "";
	
	public boolean isAmountChangedIndicator() {
		return isAmountChangedIndicator;
	}
	public void setAmountChangedIndicator(boolean isAmountChangedIndicator) {
		this.isAmountChangedIndicator = isAmountChangedIndicator;
	}
	public String getItemChangeDescription() {
		return itemChangeDescription;
	}
	public void setItemChangeDescription(String itemChangeDescription) {
		this.itemChangeDescription = itemChangeDescription;
	}
	public String getItemEstimatedAmount() {
		return itemEstimatedAmount;
	}
	public void setItemEstimatedAmount(String itemEstimatedAmount) {
		this.itemEstimatedAmount = itemEstimatedAmount;
	}
	public String getItemFinalAmount() {
		return itemFinalAmount;
	}
	public void setItemFinalAmount(String itemFinalAmount) {
		this.itemFinalAmount = itemFinalAmount;
	}
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public String getItemPaymentType() {
		return itemPaymentType;
	}
	public void setItemPaymentType(String itemPaymentType) {
		this.itemPaymentType = itemPaymentType;
	}
	

	
	
}
