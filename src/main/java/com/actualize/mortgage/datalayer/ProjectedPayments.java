package com.actualize.mortgage.datalayer;

public class ProjectedPayments {
	private int    paymentNumber = 0;
	private String frequencyType = "";
	private String calculationPeriodEndNumber = "";
	private String calculationPeriodStartNumber = "";
	private String calculationPeriodTermType = "";
	private String estimatedEscrowPaymentAmount = "";
	private String estimatedTotalMaximumPaymentAmount = "";
	private String estimatedTotalMinimumPaymentAmount = "";
	private String principalAndInterestMaximumPaymentAmount = "";
	private String principalAndInterestMinimumPaymentAmount = "";
	private String projectedPaymentMIPaymentAmount = "";
	private boolean interestOnlyIndicator = false;
	
	public String getFrequencyType() {
		return frequencyType;
	}
	public void setFrequencyType(String frequencyType) {
		this.frequencyType = frequencyType;
	}
	public String getCalculationPeriodEndNumber() {
		return calculationPeriodEndNumber;
	}
	public void setCalculationPeriodEndNumber(String calculationPeriodEndNumber) {
		this.calculationPeriodEndNumber = calculationPeriodEndNumber;
	}
	public String getCalculationPeriodStartNumber() {
		return calculationPeriodStartNumber;
	}
	public void setCalculationPeriodStartNumber(String calculationPeriodStartNumber) {
		this.calculationPeriodStartNumber = calculationPeriodStartNumber;
	}
	public String getCalculationPeriodTermType() {
		return calculationPeriodTermType;
	}
	public void setCalculationPeriodTermType(String calculationPeriodTermType) {
		this.calculationPeriodTermType = calculationPeriodTermType;
	}
	public String getEstimatedEscrowPaymentAmount() {
		return estimatedEscrowPaymentAmount;
	}
	public void setEstimatedEscrowPaymentAmount(String estimatedEscrowPaymentAmount) {
		this.estimatedEscrowPaymentAmount = estimatedEscrowPaymentAmount;
	}
	public String getEstimatedTotalMaximumPaymentAmount() {
		return estimatedTotalMaximumPaymentAmount;
	}
	public void setEstimatedTotalMaximumPaymentAmount(String estimatedTotalMaximumPaymentAmount) {
		this.estimatedTotalMaximumPaymentAmount = estimatedTotalMaximumPaymentAmount;
	}
	public String getPrincipalAndInterestMaximumPaymentAmount() {
		return principalAndInterestMaximumPaymentAmount;
	}
	public void setPrincipalAndInterestMaximumPaymentAmount(String principalAndInterestMaximumPaymentAmount) {
		this.principalAndInterestMaximumPaymentAmount = principalAndInterestMaximumPaymentAmount;
	}
	public int getPaymentNumber() {
		return paymentNumber;
	}
	public void setPaymentNumber(int paymentNumber) {
		this.paymentNumber = paymentNumber;
	}
	public String getProjectedPaymentMIPaymentAmount() {
		return projectedPaymentMIPaymentAmount;
	}
	public void setProjectedPaymentMIPaymentAmount(
			String projectedPaymentMIPaymentAmount) {
		this.projectedPaymentMIPaymentAmount = projectedPaymentMIPaymentAmount;
	}
	public String getPrincipalAndInterestMinimumPaymentAmount() {
		return principalAndInterestMinimumPaymentAmount;
	}
	public void setPrincipalAndInterestMinimumPaymentAmount(
			String principalAndInterestMinimumPaymentAmount) {
		this.principalAndInterestMinimumPaymentAmount = principalAndInterestMinimumPaymentAmount;
	}
	public String getEstimatedTotalMinimumPaymentAmount() {
		return estimatedTotalMinimumPaymentAmount;
	}
	public void setEstimatedTotalMinimumPaymentAmount(
			String estimatedTotalMinimumPaymentAmount) {
		this.estimatedTotalMinimumPaymentAmount = estimatedTotalMinimumPaymentAmount;
	}
	public boolean getInterestOnlyIndicator() {
		return interestOnlyIndicator;
	}
	public void setInterestOnlyIndicator(boolean interestOnlyIndicator) {
		this.interestOnlyIndicator = interestOnlyIndicator;
	}

}


