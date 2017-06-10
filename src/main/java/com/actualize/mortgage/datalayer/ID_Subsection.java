package com.actualize.mortgage.datalayer;


/*
 * This work around is only because the sub section type in integrated disclosure is in the parent container
 */
public class ID_Subsection extends Expenses {

	private String integratedDisclosureSectionType		= "";
	private String integratedDisclosureSubsectionType	= "";
	private String lenderTolerance						= "";
		
	public String getIntegratedDisclosureSubsectionType() {
		return integratedDisclosureSubsectionType;
	}

	public void setIntegratedDisclosureSubsectionType(String integratedDisclosureSubsectionType) {
		this.integratedDisclosureSubsectionType = integratedDisclosureSubsectionType;
	}

	public String getIntegratedDisclosureSectionType() {
		return integratedDisclosureSectionType;
	}

	public void setIntegratedDisclosureSectionType(String integratedDisclosureSectionType) {
		this.integratedDisclosureSectionType = integratedDisclosureSectionType;
	}

	public String getLenderTolerance() {
		return lenderTolerance;
	}

	public void setLenderTolerance(String lenderTolerance) {
		this.lenderTolerance = lenderTolerance;
	}
}
