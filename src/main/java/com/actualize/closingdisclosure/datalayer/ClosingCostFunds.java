package com.actualize.closingdisclosure.datalayer;

/**
 * This class defines ClosingCostFunds element in MISMO xml  
 * @author sboragala
 *
 */
public class ClosingCostFunds extends Expenses {
	private String  totalAmount = "";

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	
}
