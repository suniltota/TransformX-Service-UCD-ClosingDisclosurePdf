package com.actualize.closingdisclosure.domainmodels;

import com.actualize.closingdisclosure.datalayer.Fees;

public class FeeCostsTableRow extends CostsTableRow {
	FeeCostsTableRow(Fees fee, boolean withTo, String label, String to) {
		this(fee, withTo, label, to, true);
	}
	
	FeeCostsTableRow(Fees fee, boolean withTo, String label, String to, boolean wrap) {
		super(fee, wrap);

		// Insert cost label
		String str = label;
		if (label == null || label.equals(""))
			str = StringFormatter.STRINGCLEAN.formatString(fee.getLabel());
		if (fee.isOptionalCostIndicator() && !fee.getLabel().toLowerCase().contains("optional"))
			str += " (optional)";
		add(Columns.CostLabel, str);
		

		// Insert to entity
		str = " ";
		//if (!fee.getPaidToType().equals("Lender") && withTo && (!(to == null || to.equals("")) || !fee.getPaymentToEntity().equals("")))
		if ( withTo && (!(to == null || to.equals("")) || !fee.getPaymentToEntity().equals("")) && ! "ServicesBorrowerDidNotShopFor".equalsIgnoreCase(fee.getIntegratedDisclosureSectionType()) ){
				str = "to  ";
		}
		//USB UCD-153 Fix
		else if(withTo && (!(to == null || to.equals("")) || !fee.getPaymentToEntity().equals("")) && "ServicesBorrowerDidNotShopFor".equalsIgnoreCase(fee.getIntegratedDisclosureSectionType()))
		{
			str = "to  ";
		}
			
		if (!(to == null || to.equals("")))
			str += to;
		else if (!fee.getPaymentToEntity().equals(""))
			str += StringFormatter.STRINGCLEAN.formatString(fee.getPaymentToEntity());
		add(Columns.ToEntity, str);

		// Populate amounts
		str = fee.getBuyerAtClosingAmount();
		if (!str.equals("") && StringFormatter.doubleValue(str) != 0)
			add(Columns.BuyerAtClosing, str);
		str = fee.getBuyerOutsideClosingAmount();
		if (!str.equals("") && StringFormatter.doubleValue(str) != 0)
			add(Columns.BuyerOutsideClosing, str);
		str = fee.getSellerAtClosingAmount();
		if (!str.equals("") && StringFormatter.doubleValue(str) != 0)
			add(Columns.SellerAtClosing, str);
		str = fee.getSellerOutsideClosingAmount();
		if (!str.equals("") && StringFormatter.doubleValue(str) != 0)
			add(Columns.SellerOutsideClosing, str);
		str = fee.getOtherAmount();
		String prefix = fee.getOtherEntity().equalsIgnoreCase("Lender") ? "(L)"
				: "";
		if (!str.equals("") && StringFormatter.doubleValue(str) != 0)
			addNoFormat(Columns.Other, prefix
					+ Columns.Other.formatter().formatString(str));
	}
}
