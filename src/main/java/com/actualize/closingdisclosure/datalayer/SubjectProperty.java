package com.actualize.closingdisclosure.datalayer;


public class SubjectProperty extends Address {

	private String PersonalPropertyAmount = "";
	private String PersonalPropertyIncludedIndicator = "";
	private String RealPropertyAmount = "";
	public String getPersonalPropertyAmount() {
		return PersonalPropertyAmount;
	}
	public void setPersonalPropertyAmount(String personalPropertyAmount) {
		PersonalPropertyAmount = personalPropertyAmount;
	}
	public String getPersonalPropertyIncludedIndicator() {
		return PersonalPropertyIncludedIndicator;
	}
	public void setPersonalPropertyIncludedIndicator(String personalPropertyIncludedIndicator) {
		PersonalPropertyIncludedIndicator = personalPropertyIncludedIndicator;
	}
	public String getRealPropertyAmount() {
		return RealPropertyAmount;
	}
	public void setRealPropertyAmount(String realPropertyAmount) {
		RealPropertyAmount = realPropertyAmount;
	}
}
