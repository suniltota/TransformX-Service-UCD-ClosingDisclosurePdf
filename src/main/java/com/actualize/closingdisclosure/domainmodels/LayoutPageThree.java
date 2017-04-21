package com.actualize.closingdisclosure.domainmodels;

import java.util.List;

import com.actualize.closingdisclosure.datalayer.CashToClose;
import com.actualize.closingdisclosure.datalayer.ClosingMap;
import com.actualize.closingdisclosure.datalayer.InputData;
import com.actualize.closingdisclosure.datalayer.Liabilities;

public class LayoutPageThree {
	
	static boolean standardView(InputData inputData) {
		ClosingMap closingMap = inputData.getClosingMap();
		if (closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_DETAIL.IntegratedDisclosureDocumentType").equalsIgnoreCase("Other")
				&& closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_DETAIL.IntegratedDisclosureDocumentTypeOtherDescription").equalsIgnoreCase("ClosingDisclosure:ModelForm"))
			return true;
		if (closingMap.getClosingMapValue("DOCUMENT_CLASS.DocumentType").equalsIgnoreCase("Other")
				&& closingMap.getClosingMapValue("DOCUMENT_CLASS.DocumentTypeOtherDescription").equalsIgnoreCase("ClosingDisclosure:ModelForm"))
			return true;
		if (closingMap.getClosingMapValue("DOCUMENT_CLASS.DocumentType").equalsIgnoreCase("Other")
				&& closingMap.getClosingMapValue("DOCUMENT_CLASS.DocumentTypeOtherDescription").equalsIgnoreCase("ClosingDisclosure:Standard"))
			return true;
		return false;
	}

	static boolean alternateView(InputData inputData) {
		List<Liabilities> liabilityList = inputData.getLiabilitiesList();
		List<CashToClose> cashList = inputData.getCashList();
		ClosingMap closingMap = inputData.getClosingMap();

		// Is "Standard Form" explicitly requested?
		if (standardView(inputData))
			return false;

		// Is "Alternate Form" explicitly requested?
		if (closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_DETAIL.IntegratedDisclosureDocumentType").equalsIgnoreCase("Other")
				&& closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_DETAIL.IntegratedDisclosureDocumentTypeOtherDescription").equalsIgnoreCase("ClosingDisclosure:AlternateForm")) {
			inputData.setAlternativeView(true);
			return true;
		}

		// Check payoffs and payment
		boolean isPayoffsAndPayments      = false;
		for(Liabilities liability:liabilityList) {
			if(liability.getIDSection().equals("PayoffsAndPayments")) {
				isPayoffsAndPayments = true;
				break;
			}
		}
		for (CashToClose cashToClose:cashList) {
			if (cashToClose.getItemType().equalsIgnoreCase("TotalPayoffsAndPayments")) {
				isPayoffsAndPayments = true;
				break;
			}
		}

		// Check if refi
		boolean isRefinanceTypeLoan       = false;
		if (closingMap.getClosingMapValue("TERMS_OF_LOAN.LoanPurposeType").equalsIgnoreCase("Refinance"))
			isRefinanceTypeLoan = true;

		// Check if home equity
		boolean isHomeEquityLoanIndicator = false;
		if (closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_DETAIL.IntegratedDisclosureHomeEquityLoanIndicator").equalsIgnoreCase("true")) {
			isHomeEquityLoanIndicator = true;
		}

		// Return alternate view
		if (isPayoffsAndPayments && (isRefinanceTypeLoan || isHomeEquityLoanIndicator)) {
			inputData.setAlternativeView(true);
			return true;
		}
		return false;
	}
}