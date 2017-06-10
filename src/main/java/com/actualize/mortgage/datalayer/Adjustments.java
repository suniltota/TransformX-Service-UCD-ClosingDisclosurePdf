package com.actualize.mortgage.datalayer;
/**
 * This class defines Adjustments element in MISMO xml 
 * @author sboragala
 *
 */

public class Adjustments extends Expenses {

	private String typeOtherDescription = "";
	private String integratedDisclosureSubsectionType = "";

	public String getTypeOtherDescription() {
		return typeOtherDescription;
	}

	public void setTypeOtherDescription(String typeOtherDescription) {
		this.typeOtherDescription = typeOtherDescription;
	}

	public String getIntegratedDisclosureSubsectionType() {
		return integratedDisclosureSubsectionType;
	}

	public void setIntegratedDisclosureSubsectionType(String integratedDisclosureSubsectionType) {
		this.integratedDisclosureSubsectionType = integratedDisclosureSubsectionType;
	}
}
