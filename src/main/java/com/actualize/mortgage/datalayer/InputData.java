package com.actualize.mortgage.datalayer;

import java.util.List;


public class InputData {
	
	private String      			transactionId 			= null;
	private SubjectProperty     	subjectProperty 		= null;
	private PageTwo					pageTwo					= null;
	private ClosingMap 				closingMap 				= null;
	private boolean 				borrowerOnly			= false;
	private boolean 				sellerOnly				= false;
	private boolean 				docSets					= false;
	private boolean 				dealSets				= false;
	
	private List<PartyData> 		partyList 				= null;
	private List<Fees>   	    	feeList 				= null;
	private List<ID_Subsection> 	idsList					= null;
	private List<Prepaids>      	prepaidList     		= null;
	private List<Escrows>			escrowList				= null;
	private List<Adjustments>		adjustmentList      	= null;
	private List<Prorations>		prorationsList			= null;
	private List<CashToClose>		cashList				= null;
	private List<ProjectedPayments> paymentsList    		= null;
	private List<ClosingCostFunds> 	closingCostFunds 		= null;
	private List<Liabilities>		liabilitiesList         = null;
	private List<PropertyCostComponents> propertyCostList 	= null;
	private List<RelationshipData>  relationshipList        = null;
	private List<InterestRule>      interestRuleList        = null;
	private List<PIadjustments>     piAdjustmentsList 		= null;
	
	private boolean alternativeView   = false;
	private boolean isDocsDirect 	  = false;
	private boolean pages2A2B		  = false;
	private float   alternativeC2Crow = 0;
	
	public boolean isAlternativeView() {
		return alternativeView;
	}
	public void setAlternativeView(boolean alternativeView) {
		this.alternativeView = alternativeView;
	}
	public float getAlternativeC2Crow() {
		return alternativeC2Crow;
	}
	public void setAlternativeC2Crow(float aC2C) {
		alternativeC2Crow = aC2C;
	}

	public ClosingMap getClosingMap() {
		return closingMap;
	}
	public void setClosingMap(ClosingMap closingMap) {
		this.closingMap = closingMap;
	}
	public List<PartyData> getPartyList() {
		return partyList;
	}
	public void setPartyList(List <PartyData> partyData) {
		this.partyList = partyData;
	}
	public List<Fees> getFeeList() {
		return feeList;
	}
	public void setFeeList(List<Fees> feeList) {
		this.feeList = feeList;
	}
	public List<ID_Subsection> getIdsList() {
		return idsList;
	}
	public void setIdsList(List<ID_Subsection> idsList) {
		this.idsList = idsList;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public List<Prepaids> getPrepaidList() {
		return prepaidList;
	}
	public void setPrepaidList(List<Prepaids> prepaidList) {
		this.prepaidList = prepaidList;
	}
	public List<Escrows> getEscrowList() {
		return escrowList;
	}
	public void setEscrowList(List<Escrows> escrowList) {
		this.escrowList = escrowList;
	}
	public SubjectProperty getSubjectProperty() {
		return subjectProperty;
	}
	public void setSubjectProperty(SubjectProperty subjectProperty) {
		this.subjectProperty = subjectProperty;
	}
	public List<Adjustments> getAdjustmentList() {
		return adjustmentList;
	}
	public void setAdjustmentList(List<Adjustments> adjustmentList) {
		this.adjustmentList = adjustmentList;
	}
	public List<ClosingCostFunds> getClosingCostFunds() {
		return closingCostFunds;
	}
	public void setClosingCostFunds(List<ClosingCostFunds> closingCostFunds) {
		this.closingCostFunds = closingCostFunds;
	}
	public List<Prorations> getProrationsList() {
		return prorationsList;
	}
	public void setProrationsList(List<Prorations> prorationsList) {
		this.prorationsList = prorationsList;
	}
	public List<CashToClose> getCashList() {
		return cashList;
	}
	public void setCashList(List<CashToClose> cashList) {
		this.cashList = cashList;
	}
	public List<PropertyCostComponents> getPropertyCostList() {
		return propertyCostList;
	}
	public void setPropertyCostList(List<PropertyCostComponents> propertyCostList) {
		this.propertyCostList = propertyCostList;
	}
	public List<ProjectedPayments> getPaymentsList() {
		return paymentsList;
	}
	public void setPaymentsList(List<ProjectedPayments> paymentsList) {
		this.paymentsList = paymentsList;
	}
	public List<Liabilities> getLiabilitiesList() {
		return liabilitiesList;
	}
	public void setLiabilitiesList(List<Liabilities> liabilitiesList) {
		this.liabilitiesList = liabilitiesList;
	}
	public List<RelationshipData> getRelationshipList() {
		return relationshipList;
	}
	public void setRelationshipList(List <RelationshipData> relationshipData) {
		this.relationshipList = relationshipData;
	}
	public List<InterestRule> getInterestRuleList() {
		return interestRuleList;
	}
	public void setInterestRuleList(List<InterestRule> interestRuleList) {
		this.interestRuleList = interestRuleList;
	}
	public PageTwo getPageTwo() {
		return pageTwo;
	}
	public void setPageTwo(PageTwo pageTwo) {
		this.pageTwo = pageTwo;
	}
	public List<PIadjustments> getPiAdjustmentsList() {
		return piAdjustmentsList;
	}
	public void setPiAdjustmentsList(List<PIadjustments> piAdjustmentsList) {
		this.piAdjustmentsList = piAdjustmentsList;
	}
	public boolean isBorrowerOnly() {
		if ("Other".equals(closingMap.getClosingMapValue("DOCUMENT_CLASS.DocumentType")) &&
				"ClosingDisclosure:BorrowerOnly".equals(closingMap.getClosingMapValue("DOCUMENT_CLASS.DocumentTypeOtherDescription")))
			return true;
		return borrowerOnly;
	}
	public void setBorrowerOnly(boolean borrowerOnly) {
		this.borrowerOnly = borrowerOnly;
	}
	public boolean isSellerOnly() {
		if ("Other".equals(closingMap.getClosingMapValue("DOCUMENT_CLASS.DocumentType")) &&
				"ClosingDisclosure:SellerOnly".equals(closingMap.getClosingMapValue("DOCUMENT_CLASS.DocumentTypeOtherDescription")))
			return true;
		return sellerOnly;
	}
	public void setSellerOnly(boolean sellerOnly) {
		this.sellerOnly = sellerOnly;
	}
	public boolean isDocSets() {
		return docSets;
	}
	public void setDocSets(boolean docSets) {
		this.docSets = docSets;
	}
	public boolean isDealSets() {
		return dealSets;
	}
	public void setDealSets(boolean dealSets) {
		this.dealSets = dealSets;
	}
	public boolean isDocsDirect() {
		return isDocsDirect;
	}
	public void setDocsDirect(boolean isDocsDirect) {
		this.isDocsDirect = isDocsDirect;
	}
	public boolean isPages2A2B() {
		return pages2A2B;
	}
	public void setPages2A2B(boolean pages2a2b) {
		pages2A2B = pages2a2b;
	}

}
