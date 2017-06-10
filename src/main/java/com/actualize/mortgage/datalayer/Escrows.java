package com.actualize.mortgage.datalayer;


public class Escrows extends Expenses {
	private String  monthlyPaymentAmount = "";
	private String  collectedNumberOfMonthsCount = "";
	private String  buyerAtClosingAmount = "";
	private String  buyerOutsideClosingAmount = "";
	private String  sellerAtClosingAmount = "";
	private String  sellerOutsideClosingAmount = "";
	private String  otherAmount = "";
	private String  otherEntity = "";

	public String getMonthlyPaymentAmount() {
		return monthlyPaymentAmount;
	}

	public void setMonthlyPaymentAmount(String monthlyPaymentAmount) {
		this.monthlyPaymentAmount = monthlyPaymentAmount;
	}

	public String getCollectedNumberOfMonthsCount() {
		return collectedNumberOfMonthsCount;
	}

	public void setCollectedNumberOfMonthsCount(
			String collectedNumberOfMonthsCount) {
		this.collectedNumberOfMonthsCount = collectedNumberOfMonthsCount;
	}
	public String getBuyerAtClosingAmount() {
		return buyerAtClosingAmount;
	}
	public void setBuyerAtClosingAmount(String buyerAtClosingAmount) {
		this.buyerAtClosingAmount = buyerAtClosingAmount;
	}
	public String getBuyerOutsideClosingAmount() {
		return buyerOutsideClosingAmount;
	}
	public void setBuyerOutsideClosingAmount(String buyerOutsideClosingAmount) {
		this.buyerOutsideClosingAmount = buyerOutsideClosingAmount;
	}
	public String getSellerAtClosingAmount() {
		return sellerAtClosingAmount;
	}
	public void setSellerAtClosingAmount(String sellerAtClosingAmount) {
		this.sellerAtClosingAmount = sellerAtClosingAmount;
	}
	public String getSellerOutsideClosingAmount() {
		return sellerOutsideClosingAmount;
	}
	public void setSellerOutsideClosingAmount(String sellerOutsideClosingAmount) {
		this.sellerOutsideClosingAmount = sellerOutsideClosingAmount;
	}
	public String getOtherAmount() {
		return otherAmount;
	}
	public void setOtherAmount(String otherAmount) {
		this.otherAmount = otherAmount;
	}
	public String getOtherEntity() {
		return otherEntity;
	}
	public void setOtherEntity(String otherEntity) {
		this.otherEntity = otherEntity;
	}
}
