package com.actualize.mortgage.datalayer;


public class SubjectProperty extends Address {

	private String PersonalPropertyAmount = "";
	private String PersonalPropertyIncludedIndicator = "";
	private String RealPropertyAmount = "";
	private String unparsedLegalDescription = "";
	
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
	/**
	 * @return the unparsedLegalDescription
	 */
	public String getUnparsedLegalDescription() {
		return unparsedLegalDescription;
	}
	/**
	 * @param unparsedLegalDescription the unparsedLegalDescription to set
	 */
	public void setUnparsedLegalDescription(String unparsedLegalDescription) {
		this.unparsedLegalDescription = unparsedLegalDescription;
	}
}
