package com.actualize.mortgage.datalayer;

public class PropertyCostComponents {
	private String escrowedType = "";
	private String estimatedTaxesInsuranceAssessmentComponentType = "";
	private String estimatedTaxesInsuranceAssessmentComponentTypeOtherDescription = "";
	private String label="";
	public String getEscrowedType() {
		return escrowedType;
	}
	public void setEscrowedType(String escrowedType) {
		this.escrowedType = escrowedType;
	}
	public String getEstimatedTaxesInsuranceAssessmentComponentType() {
		return estimatedTaxesInsuranceAssessmentComponentType;
	}
	public void setEstimatedTaxesInsuranceAssessmentComponentType(String estimatedTaxesInsuranceAssessmentComponentType) {
		this.estimatedTaxesInsuranceAssessmentComponentType = estimatedTaxesInsuranceAssessmentComponentType;
	}
	public String getEstimatedTaxesInsuranceAssessmentComponentTypeOtherDescription() {
		return estimatedTaxesInsuranceAssessmentComponentTypeOtherDescription;
	}
	public void setEstimatedTaxesInsuranceAssessmentComponentTypeOtherDescription(String estimatedTaxesInsuranceAssessmentComponentTypeOtherDescription) {
		this.estimatedTaxesInsuranceAssessmentComponentTypeOtherDescription = estimatedTaxesInsuranceAssessmentComponentTypeOtherDescription;
	}
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

}
