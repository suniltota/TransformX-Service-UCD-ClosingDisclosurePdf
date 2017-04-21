package com.actualize.closingdisclosure.datalayer;


public class Prorations extends Expenses {
	private String integratedDisclosureSubsectionType 	= "";
	private String prorationItemPaidFromDate 			= "";
	private String prorationItemPaidThroughDate			= "";

	public String getIntegratedDisclosureSubsectionType() {
		return integratedDisclosureSubsectionType;
	}

	public void setIntegratedDisclosureSubsectionType(String integratedDisclosureSubsectionType) {
		this.integratedDisclosureSubsectionType = integratedDisclosureSubsectionType;
	}

	public String getProrationItemPaidFromDate() {
		return prorationItemPaidFromDate;
	}

	public void setProrationItemPaidFromDate(String prorationItemPaidFromDate) {
		this.prorationItemPaidFromDate = prorationItemPaidFromDate;
	}

	public String getProrationItemPaidThroughDate() {
		return prorationItemPaidThroughDate;
	}

	public void setProrationItemPaidThroughDate(String prorationItemPaidThroughDate) {
		this.prorationItemPaidThroughDate = prorationItemPaidThroughDate;
	}

}
