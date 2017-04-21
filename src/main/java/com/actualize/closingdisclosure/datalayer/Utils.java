package com.actualize.closingdisclosure.datalayer;

public class Utils {
	public static boolean hasAdjustableInterestRate(InputData data) {
		ClosingMap closingMap = data.getClosingMap();
		return closingMap.getClosingMapValue("LOAN_DETAIL.InterestRateIncreaseIndicator").equalsIgnoreCase("true");
	}

	public static boolean hasAdjustablePayment(InputData data) {
		ClosingMap closingMap = data.getClosingMap();
		return closingMap.getClosingMapValue("LOAN_DETAIL.InterestOnlyIndicator").equalsIgnoreCase("true")
				|| closingMap.getClosingMapValue("PAYMENT_RULE.PaymentOptionIndicator").equalsIgnoreCase("true") 
				|| closingMap.getClosingMapValue("AMORTIZATION_RULE.AmortizationType").equalsIgnoreCase("Step") 
				|| closingMap.getClosingMapValue("AMORTIZATION_RULE.AmortizationType").equalsIgnoreCase("GPM") 
				|| closingMap.getClosingMapValue("AMORTIZATION_RULE.AmortizationType").equalsIgnoreCase("GraduatedARM") 
				|| closingMap.getClosingMapValue("LOAN_DETAIL.SeasonalPaymentFeatureIndicator").equalsIgnoreCase("true");
	}
	
}
