package com.actualize.mortgage.datalayer;

public class PIadjustments {
	private String adjustmentRuleType				= null;
	private String firstChangeMonthsCount			= null;
	private String perChangeMaximumPaymentAmount	= null;
	private String perChangeMinimumPaymentAmount	= null;
	private String perChangeAdjustmentFrequencyMonthsCount = null;
	public String getAdjustmentRuleType() {
		return adjustmentRuleType;
	}
	public void setAdjustmentRuleType(String adjustmentRuleType) {
		this.adjustmentRuleType = adjustmentRuleType;
	}
	public String getFirstChangeMonthsCount() {
		return firstChangeMonthsCount;
	}
	public void setFirstChangeMonthsCount(String firstChangeMonthsCount) {
		this.firstChangeMonthsCount = firstChangeMonthsCount;
	}
	public String getPerChangeMaximumPaymentAmount() {
		return perChangeMaximumPaymentAmount;
	}
	public void setPerChangeMaximumPaymentAmount(
			String perChangeMaximumPaymentAmount) {
		this.perChangeMaximumPaymentAmount = perChangeMaximumPaymentAmount;
	}
	public String getPerChangeMinimumPaymentAmount() {
		return perChangeMinimumPaymentAmount;
	}
	public void setPerChangeMinimumPaymentAmount(
			String perChangeMinimumPaymentAmount) {
		this.perChangeMinimumPaymentAmount = perChangeMinimumPaymentAmount;
	}
	public String getPerChangeAdjustmentFrequencyMonthsCount() {
		return perChangeAdjustmentFrequencyMonthsCount;
	}
	public void setPerChangeAdjustmentFrequencyMonthsCount(
			String perChangeAdjustmentFrequencyMonthsCount) {
		this.perChangeAdjustmentFrequencyMonthsCount = perChangeAdjustmentFrequencyMonthsCount;
	}
}
