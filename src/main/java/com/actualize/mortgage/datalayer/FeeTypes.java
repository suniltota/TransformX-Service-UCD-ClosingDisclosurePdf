package com.actualize.mortgage.datalayer;

public class FeeTypes {

	public static String origination[] = 
			{"Appraisal","DeskReviewFee","AppraisalFieldReviewFee","AssumptionFee","AutomatedUnderwritingFee",
			"AVMFee","CopyOrFaxFee","CourierFee","CreditReportFee","DocumentPreparationFee","ElectronicDocumentDeliveryFee",
			"EscrowWaiverFee","FilingFee","LoanLevelPriceAdjustment","LoanOriginationFee","LoanOriginatorCompensation",
			"ManualUnderwritingFee","Other","PreclosingVerificationControlFee","ProcessingFee","RateLockFee","ReinspectionFee",
			"SubordinationFee","Temporary","BuydownAdministrationFee","TemporaryBuydownPoints","VerificationOfAssetsFee",
			"VerificationOfEmploymentFee","VerificationOfIncomeFee","VerificationOfResidencyStatusFee",
			"VerificationOfTaxpayerIdentificationFee","VerificationOfTaxReturnFee","WireTransferFeeApplicationFee"};
	
	public static String transferTypes[] = 
		{"MortgageSurchargeCountyOrParish",
		"MortgageSurchargeMunicipal",
		"MortgageSurchargeState",
		"Other",
		"TransferTaxTotal"};
	
	public static String[] otherTypes = 
			{"AsbestosInspectionFee",					
			"CondominiumAssociationDues",				
			"CondominiumAssociationSpecialAssessment",
			"CooperativeAssociationDues",				
			"CooperativeAssociationSpecialAssessment",
			"CreditDisabilityInsurancePremium",		
			"CreditPropertyInsurancePremium",			
			"CreditUnemploymentInsurancePremium",		
			"DebtCancellationInsurancePremium",		
			"DisasterInspectionFee",					
			"DryWallInspectionFee",					
			"ElectricalInspectionFee",				
			"EnvironmentalInspectionFee",				
			"FoundationInspectionFee",				
			"HomeInspectionFee",						
			"HomeownersAssociationDues",				
			"HomeownersAssociationSpecialAssessment",	
			"HomeWarrantyFee",						
			"LeadInspectionFee",						
			"MoldInspectionFee",						
			"MunicipalLienCertificateFee",	
			"Other",
			"PestInspectionFee",	 					
			"PlumbingInspectionFee",					
			"RadonInspectionFee",						
			"RealEstateCommissionBuyersBroker",		
			"RealEstateCommissionSellersBroker",		
			"ReconveyanceFee",						
			"RoofInspectionFee",						
			"SepticInspectionFee",					
			"SmokeDetectorInspectionFee",				
			"TitleOwnersCoveragePremium",				
			"WaterTestingFee",						
			"WellInspectionFee"};
	
	public static String[] payoffs = {
		"CollectionsJudgementssAndLiens",
		"DeferredStudentLoan",
		"DelinquentTaxes",
		"FirstPositionMortgageLien",
		"Garnishments",
		"HELOC",
		"Installment",
		"Open30DayChargeAccount",
		"Other",
		"PersonalLoan",
		"Revolving",
		"SecondPositionLien",
		"Taxes",
		"TaxLien",
		"ThirdPositionMortgageLien",
		"UnsecuredHomeImprovementLoanInstallment",
		"UnsecuredHomeImprovementLoanRevolving"
	};
}
