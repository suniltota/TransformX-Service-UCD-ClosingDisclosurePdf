package com.actualize.closingdisclosure.domainmodels;

import java.util.List;

import com.actualize.closingdisclosure.datalayer.InputData;
import com.actualize.closingdisclosure.datalayer.PartyData;
import com.actualize.closingdisclosure.datalayer.PartyRoleTypes;
import com.actualize.closingdisclosure.datalayer.RelationshipData;

public class PartyRelationshipHelper {
		
	public static PartyData getRelatedParty(PartyData originalParty, List<PartyData> listParties, List<RelationshipData> listRelationships) {
		
		String thisRole, otherRole = null;
		thisRole = originalParty.getRoleLabel();
		PartyData relatedParty = null;
		
		for (RelationshipData relationship : listRelationships) {
			if (relationship.getFrom().equalsIgnoreCase(thisRole)) {
				otherRole = relationship.getTo();
			} else if (relationship.getTo().equalsIgnoreCase(thisRole)) {
				otherRole = relationship.getFrom();
			}
		}
		
		for (PartyData otherParty : listParties) {
			if (otherParty.getRoleLabel().equalsIgnoreCase(otherRole)) {
				relatedParty = otherParty;
			}
		}
		
		return relatedParty;
	}
	
	public static Boolean IsRealEstateAgentTypeInvolved(String realEstateAgentType, List<PartyData> listParties) {
		Boolean foundAgentType = false;
		for (PartyData party : listParties) {
			if (party.getRealEstateAgentType().equalsIgnoreCase(realEstateAgentType) & foundAgentType == false) {
				foundAgentType = true;
			}
		}
		return foundAgentType;
	}
	
	public static Boolean IsAddendumReqd(InputData inputData) {
		return (sellerAddendumCount(inputData) > 0 || borrowerAddendumCount(inputData) > 0);
	}
	
	public static int borrowerAddendumCount(InputData inputData) {
		int borrowerCount = 0;
		int pageOneCount = 0;
		String borrowerAddress = "";
		String borrowerCityState = "";
		for (PartyData thisParty : inputData.getPartyList()) {
		   if (thisParty.getRoleType().equalsIgnoreCase(PartyRoleTypes.Borrower)) {
				String borrowerTmp1 = StringFormatter.STRINGCLEAN.formatString(thisParty.getAddressLine());
				String borrowerTmp2 = StringFormatter.STRINGCLEAN.formatString(thisParty.getAddressSecondLine());
				if (borrowerCount == 0) {
					borrowerCount = 1;
					pageOneCount = 1;
					borrowerAddress = borrowerTmp1;
					borrowerCityState = borrowerTmp2;
				} else if ((!borrowerAddress.equals(borrowerTmp1) || !borrowerCityState.equals(borrowerTmp2))
						&& (!borrowerTmp1.equals("") || !borrowerTmp2.equals(""))) {
					++borrowerCount;
				} else {
					++pageOneCount;
					if (pageOneCount > 2)
						++borrowerCount;
				}
		   }
		}
		return borrowerCount - 1;
	}
	
	public static int sellerAddendumCount(InputData inputData) {	
		int sellerCount = 0; 
		int pageOneCount = 0;
		String sellerAddress = "";
		String sellerCityState = "";
		for (PartyData thisParty : inputData.getPartyList()) {
		   if (thisParty.getRoleType().equalsIgnoreCase(PartyRoleTypes.Seller)) {
				String sellerTmp1 = StringFormatter.STRINGCLEAN.formatString(thisParty.getAddressLine());
				String sellerTmp2 = StringFormatter.STRINGCLEAN.formatString(thisParty.getAddressSecondLine());
				if (sellerCount == 0) {
					sellerCount = 1;
					pageOneCount = 1;
					sellerAddress = sellerTmp1;
					sellerCityState = sellerTmp2;
				} else if ((!sellerAddress.equals(sellerTmp1) || !sellerCityState.equals(sellerTmp2))
						&& (!sellerTmp1.equals("") || !sellerTmp2.equals(""))) {
					++sellerCount;
				} else {
					++pageOneCount;
					if (pageOneCount > 2)
						++sellerCount;
				}
		   }
		}
		if (inputData.getClosingMap().getClosingMapValue("TERMS_OF_LOAN.LoanPurposeType").equalsIgnoreCase("Purchase")) {
			return sellerCount - 1;
		} else {
			return 0;
		}
	}
}
