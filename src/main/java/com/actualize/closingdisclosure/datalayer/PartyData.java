package com.actualize.closingdisclosure.datalayer;


//party model	
public class PartyData extends Address  {
	
	private String partyName 				= "";
	private String roleType 				= "";
    private String licenseIdentifier        = "";
    private String licenseIssuingStateCode  = "";
    private String roleLabel                = "";
    private String realEstateAgentType      = "";
    private String emailAddress             = "";
    private String phoneNumber              = "";
    private Boolean isLegalEntity           = false;
    private String licenseURI               = "";
    
	public String getPartyName() {
		return partyName;
	}
	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}
	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public String getLicenseIdentifier() {
		return licenseIdentifier;
	}
	public void setLicenseIdentifier(String licenseIdentifier) {
		this.licenseIdentifier = licenseIdentifier;
	}
	public String getLicenseIssuingStateCode() {
		return licenseIssuingStateCode;
	}
	public void setLicenseIssuingStateCode(String licenseIssuingStateCode) {
		this.licenseIssuingStateCode = licenseIssuingStateCode;
	}
	public String getRoleLabel() {
		return roleLabel;
	}
	public void setRoleLabel(String roleLabel) {
		this.roleLabel = roleLabel;
	}
	public String getRealEstateAgentType() {
		return realEstateAgentType;
	}
	public void setRealEstateAgentType(String realEstateAgentType) {
		this.realEstateAgentType = realEstateAgentType;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public Boolean getIsLegalEntity() {
		return isLegalEntity;
	}
	public void setIsLegalEntity(Boolean isLegalEntity) {
		this.isLegalEntity = isLegalEntity;
	}
	public String getLicenseURI() {
		return licenseURI;
	}
	public void setLicenseURI(String licenseURI) {
		this.licenseURI = licenseURI;
	}
}
