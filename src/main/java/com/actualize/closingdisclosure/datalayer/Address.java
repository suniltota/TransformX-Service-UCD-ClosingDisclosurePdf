package com.actualize.closingdisclosure.datalayer;

public class Address {
	private String addressLine 				= "";
	private String addressType 				= "";
	private String addressUnitDesignatorType = "";
	private String addressUnit 				= "";
	private String cityName 				= "";
	private String countryCode			 	= "";
	private String postalCode 				= "";
	private String stateCode 				= "";
	private String unparsedLegalDescription = "";
	
	public String getAddressLine() {
		return addressLine;
	}
	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}
	public String getAddressType() {
		return addressType;
	}
	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}
	public String getAddressUnitDesignatorType() {
		return addressUnitDesignatorType;
	}
	public void setAddressUnitDesignatorType(String addressUnitDesignatorType) {
		this.addressUnitDesignatorType = addressUnitDesignatorType;
	}
	public String getAddressUnit() {
		return addressUnit;
	}
	public void setAddressUnit(String addressUnit) {
		this.addressUnit = addressUnit;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		if (postalCode.length() > 5)
			this.postalCode = postalCode.substring(0, 5) + "-" + postalCode.substring(5);
		else
			this.postalCode = postalCode;
	}
	public String getStateCode() {
		return stateCode;
	}
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	
	
	public String getAddressFirstLine() {
		return addressLine.trim() + ",";
	}
	public String getAddressSecondLine() {
		return cityName + (!stateCode.equals("") || !postalCode.equals("") ? ", " : "") + stateCode + " " + postalCode;
	}
	public String getFullAddress() {
		return getAddressFirstLine() + " " +getAddressSecondLine();
	}
	public String getUnparsedLegalDescription() {
		return unparsedLegalDescription;
	}
	public void setUnparsedLegalDescription(String unparsedLegalDescription) {
		this.unparsedLegalDescription = unparsedLegalDescription;
	}
}
