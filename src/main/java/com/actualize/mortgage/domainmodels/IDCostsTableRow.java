package com.actualize.mortgage.domainmodels;

import com.actualize.mortgage.datalayer.ID_Subsection;

public class IDCostsTableRow extends CostsTableRow {
	IDCostsTableRow(ID_Subsection subsection, String label) {
		super(subsection);
		add(Columns.Number, label== null || label.equals("") ? subsection.getLabel() : label);
		appendPayment(subsection);
	}
	
	void appendPayment(ID_Subsection subsection) {
		String str = subsection.getPaymentAmount();
		if (!"".equals(str) && !subsection.isPaidOutsideOfClosingIndicator() && subsection.getPaymentPaidByType().equalsIgnoreCase("Buyer"))
			add(Columns.BuyerAtClosing, str);
		if (!"".equals(str) && subsection.isPaidOutsideOfClosingIndicator() && subsection.getPaymentPaidByType().equalsIgnoreCase("Buyer"))
			add(Columns.BuyerOutsideClosing, str);
		if (!"".equals(str) && !subsection.isPaidOutsideOfClosingIndicator() && subsection.getPaymentPaidByType().equalsIgnoreCase("Seller"))
			add(Columns.SellerAtClosing, str);
		if (!"".equals(str) && subsection.isPaidOutsideOfClosingIndicator() && subsection.getPaymentPaidByType().equalsIgnoreCase("Seller"))
			add(Columns.SellerOutsideClosing, str);
		String prefix = subsection.getPaymentToEntity().equalsIgnoreCase("Lender") ? "(L)" : "";
		if (!"".equals(str) && !subsection.getPaymentPaidByType().equalsIgnoreCase("Buyer") && !subsection.getPaymentPaidByType().equalsIgnoreCase("Seller"))
			addNoFormat(Columns.Other, prefix + Columns.Other.formatter().formatString(str));
	}
}
