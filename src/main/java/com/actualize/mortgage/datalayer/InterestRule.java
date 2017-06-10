package com.actualize.mortgage.datalayer;

public class InterestRule {
	private String adjustmentRuleType = "";
	private String perChangeMaximumIncreaseRatePercent = "";
    private String perChangeRateAdjustmentFrequencyMonthsCount = "";
    
	public String getPerChangeMaximumIncreaseRatePercent() {
		return perChangeMaximumIncreaseRatePercent;
	}
	public void setPerChangeMaximumIncreaseRatePercent(
			String perChangeMaximumIncreaseRatePercent) {
		this.perChangeMaximumIncreaseRatePercent = perChangeMaximumIncreaseRatePercent;
	}
	public String getPerChangeRateAdjustmentFrequencyMonthsCount() {
		return perChangeRateAdjustmentFrequencyMonthsCount;
	}
	public void setPerChangeRateAdjustmentFrequencyMonthsCount(
			String perChangeRateAdjustmentFrequencyMonthsCount) {
		this.perChangeRateAdjustmentFrequencyMonthsCount = perChangeRateAdjustmentFrequencyMonthsCount;
	}
	public String getAdjustmentRuleType() {
		return adjustmentRuleType;
	}
	public void setAdjustmentRuleType(String adjustmentRuleType) {
		this.adjustmentRuleType = adjustmentRuleType;
	}	
	
	
	
	
}
