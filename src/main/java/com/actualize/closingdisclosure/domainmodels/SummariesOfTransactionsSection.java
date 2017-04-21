package com.actualize.closingdisclosure.domainmodels;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.actualize.closingdisclosure.datalayer.Adjustments;
import com.actualize.closingdisclosure.datalayer.ClosingCostFunds;
import com.actualize.closingdisclosure.datalayer.ClosingMap;
import com.actualize.closingdisclosure.datalayer.ID_Subsection;
import com.actualize.closingdisclosure.datalayer.InputData;
import com.actualize.closingdisclosure.datalayer.Liabilities;
import com.actualize.closingdisclosure.datalayer.Prorations;
import com.actualize.closingdisclosure.pdfbuilder.BoxedCharacter;
import com.actualize.closingdisclosure.pdfbuilder.Color;
import com.actualize.closingdisclosure.pdfbuilder.FormattedText;
import com.actualize.closingdisclosure.pdfbuilder.Grid;
import com.actualize.closingdisclosure.pdfbuilder.Page;
import com.actualize.closingdisclosure.pdfbuilder.Paragraph;
import com.actualize.closingdisclosure.pdfbuilder.Section;
import com.actualize.closingdisclosure.pdfbuilder.Spacer;
import com.actualize.closingdisclosure.pdfbuilder.Text;
import com.actualize.closingdisclosure.pdfbuilder.Typeface;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Dimension;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Position;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.Direction;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.HorizontalAlignment;

public class SummariesOfTransactionsSection implements Section {
	public static final Text TAB               = new Text(Color.WHITE, 11, Typeface.CALIBRI_BOLD);

	private float location;
	
	private static final int c1n =  0; // Column 1 number
	private static final int c1l =  1; // Column 1 label
	private static final int c1f =  2; // Column 1 from date
	private static final int c1o =  3; // Column 1 "to"
	private static final int c1t =  4; // Column 1 to date	
	private static final int c1v =  5; // Column 1 value
	private static final int cmd =  6; // Middle column
	private int c2n =  7; // Column 2 number
	private int c2l =  8; // Column 2 label
	private int c2f =  9; // Column 2 from date
	private int c2o = 10; // Column 2 "to"
	private int c2t = 11; // Column 2 to date	
	private int c2v = 12; // Column 2 value
	
	private static final float numwidth  = 12f/72f;
	private static final float labwidth  = 2.5f - numwidth;
	private static final float valwidth  = 3.5f - labwidth - numwidth;
	private static final float da1width  = 0.42f;
	private static final float towidth   = 0.15f;
	private static final float da2width  = 0.90f;
	private static final float lineHeight = 11.5f/72f;

	private static final float leftIndent  = 2f/72f;
	
	private static final float heights[] = { lineHeight };
	private float widths[]  = {
			numwidth, labwidth-da1width-towidth-da2width-2f/72f, da1width, towidth, da2width, valwidth+2f/72f, 0.5f,
			numwidth, labwidth-da1width-towidth-da2width-2f/72f, da1width, towidth, da2width, valwidth+2f/72f};

	FormattedText blankdate = new FormattedText("", Text.TABLE_TEXT);

	Grid grid;
	Grid titleGrid;

	SummariesOfTransactionsSection(float location, InputData data) {
		this.location = location;

		if (data.isSellerOnly()){
			// if seller only view move seller section to left side
			c2n = c1n; // Column 2 number
			c2l = c1l; // Column 2 label
			c2f = c1f; // Column 2 from date
			c2o = c1o; // Column 2 "to"
			c2t = c1t; // Column 2 to date	
			c2v = c1v; // Column 2 value
			grid = new Grid(44, heights, 6, widths);
		} else {
			grid = new Grid(44, heights, widths.length, widths);
		}

		// Set initial shading and bordering
		for (int row = 2; row < grid.rows(); row++)
			grid.setLineBorder(row, Position.TOP, Color.MEDIUM_GRAY);
		grid.setLineBorder(40, Position.TOP, Color.BLACK);
		grid.setLineShade(40, Dimension.ROW, Color.MEDIUM_GRAY);
		grid.setLineBorder(41, Position.TOP, Color.BLACK);
		grid.setLineBorder(43, Position.TOP, Color.BLACK, 1f/72f);
		grid.setLineBorder(43, Position.BOTTOM, Color.BLACK);

		// Set full line across top as well as number alignment
		grid.setLineBorder(0, Position.TOP, Color.BLACK);
		grid.setLineHorizontalAlignment(c1v, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		grid.setLineHorizontalAlignment(c2v, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		grid.setLineHorizontalAlignment(c1o, Dimension.COLUMN, HorizontalAlignment.LEFT);
	}

	private void initializeTitleGrid(InputData data) {
		float heights[] = { Grid.DYNAMIC };
		float widths[] = { Grid.DYNAMIC, 5.5f };
		titleGrid = new Grid(1, heights, 2, widths);
		titleGrid.getCell(0, 0)
			.setBackground(new Tab(2.15f))
			.setMargin(Direction.LEFT, leftIndent)
			.setForeground(new FormattedText("Summaries of Transactions", TAB));
		if (!data.isSellerOnly()){
			titleGrid.setCellText(0, 1, new FormattedText("  Use this table to see a summary of your transaction.", Text.SECTION_INFO));
		}
	}

	private void insertText(Page page, InputData data) throws IOException {
		String adjustmentTypes ="FuelCosts,RelocationFunds,Repairs,SellersEscrowAssumption,SellersMortgageInsuranceAssumption,SweatEquity.TenantSecurityDeposit,TradeEquity,Other";
		String cityTaxFees   = "CityPropertyTax,DistrictPropertyTax,TownPropertyTax";
		String countyTaxFees = "BoroughPropertyTax,CountyPropertyTax";
		String assesmentFees = "CondominiumAssociationSpecialAssessment,CooperativeAssociationSpecialAssessment,HomeownersAssociationSpecialAssessment";
		String[] adjustmentFees	 = {"CondominiumAssociationDues","CooperativeAssociationDues","EarthquakeInsurancePremium","FloodInsurancePremium",
								"GroundRent","HailInsurancePremium","HazardInsurancePremium","HomeownersAssociationDues",
								"HomeownersInsurancePremium","InterestOnLoanAssumption","MortgageInsurancePremium","PastDuePropertyTax",
								"RentFromSubjectProperty","StatePropertyTax","Utilities","VolcanoInsurancePremium","WindAndStormInsurancePremium","Other"};
		String paidAlready   =  "ProceedsOfSubordinateLiens,SatisfactionOfSubordinateLien,";
		String liabilityFromSeller = "DelinquentTaxes,HELOC,TaxLien,Taxes,ThirdPositionMortgage,Other";
//		String sellersFees   =  "CollectionsJudgmentsAndLiens,DeferredStudentLoan,Garnishments,Installment,Open30DayChargeAccount,PersonalLoan"
//								+"Revolving,UnsecuredHomeImprovementLoanInstallment";
//		String sellersAdjustments = "RepairCompletionEscrowHoldback,SatisfactionOfSubordinateLien,TenantSecurityDeposit,TradeEquity"
//								  + "UnpaidUtilityEscrowHoldback,Other";
		
		ClosingMap          	closingMap  		= data.getClosingMap();
		List<ID_Subsection> 	idsList 			= data.getIdsList();
		List<Adjustments>   	adjustmentList  	= data.getAdjustmentList();
		List<Prorations>		prorationsList 		= data.getProrationsList();
		List<ClosingCostFunds>  closingFunds		= data.getClosingCostFunds();
		List<Liabilities>		liabilityList		= data.getLiabilitiesList();
		FormattedText from;
		FormattedText to;
		FormattedText amt;
		String str;
		int row;
		int currentPrintNumber;

		str = closingMap.getClosingMapValue("TERMS_OF_LOAN.LienPriorityType");
		boolean firstLien = str.equals("") || str.equalsIgnoreCase("FirstLien");
		
		if (!data.isSellerOnly()) {

			// Start column 1
			row = 0;
			grid.setCellText(row, c1n, new FormattedText( "BORROWER'S TRANSACTION", Text.TABLE_HEADER));
			
			//K.T1
			grid.setCellText(++row, c1n, new FormattedText("K. Due from Borrower at Closing", Text.TABLE_HEADER));
			grid.setLineBorder(row, Position.TOP, Color.BLACK);
			grid.setLineShade(row, Dimension.ROW, Color.MEDIUM_GRAY);
			str = closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.DueFromBorrowerAtClosing");
			if (!str.equals(""))
				grid.setCellText(row, c1v, new FormattedText(StringFormatter.DOLLARS.formatString(str), Text.TABLE_HEADER));
			currentPrintNumber = 0;
			
			//K.01
			grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c1l, new FormattedText("Sale Price of Property", Text.TABLE_TEXT));
			if (firstLien) {
				str = closingMap.getClosingMapValue("SALES_CONTRACT_DETAIL.RealPropertyAmount");
				if (!str.equals(""))
					grid.setCellText(row, c1v, new FormattedText(StringFormatter.DOLLARS.formatString(str), Text.TABLE_TEXT));
				else {
					str = closingMap.getClosingMapValue("SALES_CONTRACT_DETAIL.SalesContractAmount");
					if (!str.equals(""))
						grid.setCellText(row, c1v, new FormattedText(StringFormatter.DOLLARS.formatString(str), Text.TABLE_TEXT));
				}
			}
			//K.02
			grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c1l, new FormattedText("Sale Price of Any Personal Property Included in Sale", Text.TABLE_TEXT));
			str = closingMap.getClosingMapValue("SALES_CONTRACT_DETAIL.PersonalPropertyAmount");
			if (!str.equals(""))
				grid.setCellText(row, c1v, new FormattedText(StringFormatter.DOLLARS.formatString(str), Text.TABLE_TEXT));

			//K.03
			ID_Subsection payment = null;
			ID_Subsection lenderCredit = null;
			grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c1l, new FormattedText("Closing Costs Paid at Closing (J)", Text.TABLE_TEXT));
			for (ID_Subsection idsLocal : idsList) {
				if (idsLocal.getIntegratedDisclosureSubsectionType().equals("LenderCredits"))
					lenderCredit = idsLocal;
				else if (idsLocal.getIntegratedDisclosureSectionType().equals("TotalClosingCosts")
						&& idsLocal.getIntegratedDisclosureSubsectionType().equals("ClosingCostsSubtotal")
						&& idsLocal.getPaymentPaidByType().equals("Buyer")
						&& !idsLocal.isPaidOutsideOfClosingIndicator())
					payment = idsLocal;
			}
			if (payment != null) {
				double netPayment = 0;
				try {
					netPayment = Double.valueOf(payment.getPaymentAmount());
					if (lenderCredit != null && !lenderCredit.getPaymentAmount().equals(""))
						netPayment += Double.valueOf(lenderCredit.getPaymentAmount());
				} catch (Exception e) {
					e.printStackTrace();
				}
				grid.setCellText(row, c1v, new FormattedText(StringFormatter.DOLLARS.formatString(String.valueOf(netPayment)), Text.TABLE_TEXT));
			}

			
			//K.04
			for (Liabilities liabilityLocal:liabilityList) {
				if (!liabilityLocal.getType().equals("") && liabilityLocal.getIDSection().equalsIgnoreCase("DueFromBorrowerAtClosing")) {
					grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
					grid.setCellText(row, c1l, new FormattedText(liabilityLocal.getLabel()+" "+liabilityLocal.getFullName(), Text.TABLE_TEXT));
					//grid.setCellText(row, cmd, new FormattedText(liabilityLocal.getFullName(), Text.TABLE_TEXT));
					grid.setCellText(row, c1v, new FormattedText(StringFormatter.DOLLARS.formatString(liabilityLocal.getPayoffAmount()), Text.TABLE_TEXT));
				}
			}
			
			// Fill to K.04
			while (currentPrintNumber < 4)
				grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));

			//K.T2
			grid.setCellText(++row, c1n, new FormattedText("Adjustments", Text.TABLE_HEADER));
			
			//K.05 through K.07
			for (Adjustments adjustmentLocal : adjustmentList) {
				if (adjustmentLocal.getIntegratedDisclosureSectionType().equals("DueFromBorrowerAtClosing")
						&& adjustmentLocal.getIntegratedDisclosureSubsectionType().equals("Adjustments")
						&& adjustmentTypes.contains(adjustmentLocal.getType()))
					if (currentPrintNumber < 8) {
						grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
						grid.setCellText(row, c1l, new FormattedText(adjustmentLocal.getLabel(), Text.TABLE_TEXT));
						grid.setCellText(row, c1v, new FormattedText(StringFormatter.DOLLARS.formatString(adjustmentLocal.getPaymentAmount()), Text.TABLE_TEXT));
					} else {
						//ManageAddendums.K.write( adjustmentLocal.getType(), StringFormatter.DOLLARS.formatString(adjustmentLocal.getPaymentAmount()));
					}
			}
			while (currentPrintNumber < 7)
				grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			
			//K.T3
			grid.setCellText(++row, c1n, new FormattedText("Adjustments for Items Paid by Seller in Advance", Text.TABLE_HEADER));
			
			//K.08
			from = blankdate;
			to = blankdate;
			amt = new FormattedText("", Text.TABLE_TEXT);
			for (Prorations prorationLocal : prorationsList)
				if (prorationLocal.getIntegratedDisclosureSectionType().equals("DueFromBorrowerAtClosing")
						&& prorationLocal.getIntegratedDisclosureSubsectionType().equals("AdjustmentsForItemsPaidBySellerInAdvance"))
					if (cityTaxFees.contains(prorationLocal.getType())) {
						if (!prorationLocal.getProrationItemPaidFromDate().equals(""))
							from = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidFromDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getProrationItemPaidThroughDate().equals(""))
							to = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidThroughDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getPaymentAmount().equals(""))
							amt = new FormattedText(StringFormatter.DOLLARS.formatString(prorationLocal.getPaymentAmount()), Text.TABLE_TEXT);
					}
			grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c1l, new FormattedText("City/Town Taxes", Text.TABLE_TEXT));
			grid.setCellText(row, c1f, from);
			grid.setCellText(row, c1o, new FormattedText(" to", Text.TABLE_TEXT));
			grid.setCellText(row, c1t, to);
			grid.setCellText(row, c1v, amt);
			
			//K.09
			from = blankdate;
			to = blankdate;
			amt = new FormattedText("", Text.TABLE_TEXT);
			for (Prorations prorationLocal : prorationsList)
				if (prorationLocal.getIntegratedDisclosureSectionType().equals("DueFromBorrowerAtClosing")
						&& prorationLocal.getIntegratedDisclosureSubsectionType().equals("AdjustmentsForItemsPaidBySellerInAdvance"))
					if (countyTaxFees.contains(prorationLocal.getType())) {
						if (!prorationLocal.getProrationItemPaidFromDate().equals(""))
							from = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidFromDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getProrationItemPaidThroughDate().equals(""))
							to = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidThroughDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getPaymentAmount().equals(""))
							amt = new FormattedText(StringFormatter.DOLLARS.formatString(prorationLocal.getPaymentAmount()), Text.TABLE_TEXT);
					}
			grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c1l, new FormattedText("County Taxes", Text.TABLE_TEXT));
			grid.setCellText(row, c1f, from);
			grid.setCellText(row, c1o, new FormattedText(" to", Text.TABLE_TEXT));
			grid.setCellText(row, c1t, to);
			grid.setCellText(row, c1v, amt);
			
			//K.10
			from = blankdate;
			to = blankdate;
			amt = new FormattedText("", Text.TABLE_TEXT);
			for (Prorations prorationLocal : prorationsList) {
				if (prorationLocal.getIntegratedDisclosureSectionType().equals("DueFromBorrowerAtClosing")
						&& prorationLocal.getIntegratedDisclosureSubsectionType().equals("AdjustmentsForItemsPaidBySellerInAdvance"))
					if (assesmentFees.contains(prorationLocal.getType())) {
						if (!prorationLocal.getProrationItemPaidFromDate().equals(""))
							from = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidFromDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getProrationItemPaidThroughDate().equals(""))
							to = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidThroughDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getPaymentAmount().equals(""))
							amt = new FormattedText(StringFormatter.DOLLARS.formatString(prorationLocal.getPaymentAmount()), Text.TABLE_TEXT);
					}
			}
			grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c1l, new FormattedText("Assessments", Text.TABLE_TEXT));
			grid.setCellText(row, c1f, from);
			grid.setCellText(row, c1o, new FormattedText(" to", Text.TABLE_TEXT));
			grid.setCellText(row, c1t, to);
			grid.setCellText(row, c1v, amt);
			
			//K.11 to K.15
			for (Prorations prorationLocal : prorationsList) {
				if (prorationLocal.getIntegratedDisclosureSectionType().equals("DueFromBorrowerAtClosing")
						&& prorationLocal.getIntegratedDisclosureSubsectionType().equals("AdjustmentsForItemsPaidBySellerInAdvance"))
					if (Arrays.asList(adjustmentFees).contains(prorationLocal.getType())) {
						from = blankdate;
						to = blankdate;
						amt = new FormattedText("", Text.TABLE_TEXT);
						str = prorationLocal.getLabel();
						if ("".equalsIgnoreCase(str))
							str = StringFormatter.CAMEL.formatString(prorationLocal.getType());
						if (!prorationLocal.getProrationItemPaidFromDate().equals(""))
							from = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidFromDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getProrationItemPaidThroughDate().equals(""))
							to = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidThroughDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getPaymentAmount().equals(""))
							amt = new FormattedText(StringFormatter.DOLLARS.formatString(prorationLocal.getPaymentAmount()), Text.TABLE_TEXT);
						if (currentPrintNumber < 15) {
							grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
							grid.setCellText(row, c1l, new FormattedText(str, Text.TABLE_TEXT));
							grid.setCellText(row, c1f, from);
							grid.setCellText(row, c1o, new FormattedText(" to", Text.TABLE_TEXT));
							grid.setCellText(row, c1t, to);
							grid.setCellText(row, c1v, amt);
						} else {
							//ManageAddendums.K.write( prorationLocal.getType(), StringFormatter.DOLLARS.formatString(prorationLocal.getPaymentAmount()));
						}
				}
			}
			while (currentPrintNumber < 15)
				grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			
			//L.T1-------------------------------------------------------------------------------------------------------------------------
			grid.setCellText(++row, c1n, new FormattedText(
					"L. Paid Already by or on Behalf of Borrower at Closing",
					Text.TABLE_HEADER));
			grid.setLineBorder(row, Position.TOP, Color.BLACK);
			grid.setLineShade(row, Dimension.ROW, Color.MEDIUM_GRAY);
			str = closingMap
					.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.PaidAlreadyByOrOnBehalfOfBorrowerAtClosing");
			if (!str.equals(""))
				grid.setCellText(row, c1v, new FormattedText(
						StringFormatter.DOLLARS.formatString(str),
						Text.TABLE_HEADER));
			currentPrintNumber = 0;

			//L.01
			grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c1l, new FormattedText("Deposit", Text.TABLE_TEXT));
			for (ClosingCostFunds fundsLocal : closingFunds)
				if (fundsLocal.getIntegratedDisclosureSectionType().equals("PaidAlreadyByOrOnBehalfOfBorrowerAtClosing")
						&& fundsLocal.getType().equals("DepositOnSalesContract")) {
					grid.setCellText(row, c1v, new FormattedText(StringFormatter.DOLLARS.formatString(fundsLocal.getTotalAmount()), Text.TABLE_TEXT));
					break;
				}
			
			//L.02
			grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c1l, new FormattedText("Loan Amount", Text.TABLE_TEXT));
			str = closingMap.getClosingMapValue("TERMS_OF_LOAN.NoteAmount");
			if (!str.equals(""))
				grid.setCellText(row, c1v, new FormattedText(StringFormatter.DOLLARS.formatString(str), Text.TABLE_TEXT));
			
			//L.03
			grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c1l, new FormattedText("Existing Loan(s) Assumed or Taken Subject to", Text.TABLE_TEXT));
			str = closingMap.getClosingMapValue("TERMS_OF_LOAN.AssumedLoanAmount");
			if (!str.equals(""))
				grid.setCellText(row, c1v, new FormattedText(StringFormatter.DOLLARS.formatString(str), Text.TABLE_TEXT));
			
			//L.04 (maybe) "Subordinated Lien" adjustments
			for (Adjustments adjustmentLocal : adjustmentList) {
				if (adjustmentLocal.getIntegratedDisclosureSectionType().equals("PaidAlreadyByOrOnBehalfOfBorrowerAtClosing") && !adjustmentLocal.getType().equals("SellerCredit")) {
					if (!adjustmentLocal.getIntegratedDisclosureSubsectionType().equals("OtherCredits") && !adjustmentLocal.getIntegratedDisclosureSubsectionType().equals("Adjustments")) {
						grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
						Paragraph text12_4 = new Paragraph();
						if (paidAlready.contains(adjustmentLocal.getType()) && paidAlready.contains(adjustmentLocal.getTypeOtherDescription()))
							text12_4.append(new FormattedText("Second Loan (Principal Balance ", Text.TABLE_TEXT))
								.append(new FormattedText(
										StringFormatter.DOLLARS.formatString(closingMap.getClosingMapValue("LOAN_DETAIL.TotalSubordinateFinancingAmount")),
										Text.TABLE_TEXT))
								.append(new FormattedText(")", Text.TABLE_TEXT));
						else
							text12_4.append(new FormattedText(adjustmentLocal.getLabel(), Text.TABLE_TEXT));
						grid.setCellText(row, c1l, text12_4);
						grid.setCellText(row, c1v, new FormattedText(StringFormatter.DOLLARS.formatString(adjustmentLocal.getPaymentAmount()), Text.TABLE_TEXT));
						break;
					}
				}
			}
			
			//L.04 (if none)
			if (currentPrintNumber < 4)
				grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			
			//L.05 (or L.06) "Seller Credit" adjustments
			grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c1l, new FormattedText("Seller Credit", Text.TABLE_TEXT));
			for (Adjustments adjustmentLocal : adjustmentList) {
				if (adjustmentLocal.getType().equals("SellerCredit") && adjustmentLocal.getIntegratedDisclosureSectionType().equalsIgnoreCase("PaidAlreadyByOrOnBehalfOfBorrowerAtClosing")) {
					grid.setCellText(row, c1v, new FormattedText(StringFormatter.DOLLARS .formatString(adjustmentLocal.getPaymentAmount()), Text.TABLE_TEXT));
					break;
				}
			}
			
			//L.T2: Other Credits
			grid.setCellText(++row, c1n, new FormattedText("Other Credits", Text.TABLE_HEADER));
			
			//L.06 (or L.07)
			for (Adjustments adjustmentLocal : adjustmentList) {
				if (adjustmentLocal.getIntegratedDisclosureSubsectionType().equals("OtherCredits")) {
					Paragraph text12_6 = new Paragraph().append(new FormattedText(StringFormatter.CAMEL.formatString(adjustmentLocal.getLabel()), Text.TABLE_TEXT));
					if (!adjustmentLocal.getPaymentPaidByType().equals(""))
						text12_6.append(new FormattedText(" from ", Text.TABLE_TEXT))
								.append(new FormattedText(adjustmentLocal.getPaymentPaidByType(), Text.TABLE_TEXT));
					else if (!adjustmentLocal.getPaymentToEntity().equals(""))
						text12_6.append(new FormattedText(" from ", Text.TABLE_TEXT))
								.append(new FormattedText(adjustmentLocal.getPaymentToEntity(), Text.TABLE_TEXT));
					if (currentPrintNumber < 7) {
						grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
						grid.getCell(row, c1l).setForeground(text12_6);
						//grid.setCellText(row, c1l, text12_6);
						grid.setCellText(row, c1v, new FormattedText(StringFormatter.DOLLARS.formatString(adjustmentLocal.getPaymentAmount()), Text.TABLE_TEXT));
					} else {
						//ManageAddendums.L.write( adjustmentLocal.getType(), StringFormatter.DOLLARS.formatString(adjustmentLocal.getPaymentAmount()));
					}
				}
			}
			
			//Fill to L.07
			while (currentPrintNumber < 7)
				grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			
			//L.T3: Adjustments
			grid.setCellText(++row, c1n, new FormattedText("Adjustments", Text.TABLE_HEADER));
			
			//L.08 through L.11
			for (Adjustments adjustmentLocal : adjustmentList) {
				if (adjustmentLocal.getIntegratedDisclosureSectionType().equals("PaidAlreadyByOrOnBehalfOfBorrowerAtClosing")
						&& adjustmentLocal.getIntegratedDisclosureSubsectionType().equals("Adjustments")
						&& adjustmentTypes.contains(adjustmentLocal.getType()))
					
					if (currentPrintNumber < 11) {
						grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
						grid.setCellText(row, c1l, new FormattedText(adjustmentLocal.getLabel(), Text.TABLE_TEXT));
						//if (adjustmentLocal.getPaymentToEntity().length()>12)
						if (!adjustmentLocal.getPaymentPaidByType().equals(""))
							grid.getCell(row, c1f).setForeground( new Paragraph().append(new Spacer(30f/72f, 0)).append(new FormattedText("from " + adjustmentLocal.getPaymentPaidByType(), Text.TABLE_TEXT)));
						if (!adjustmentLocal.getPaymentToEntity().equals(""))
							grid.getCell(row, c1f).setForeground( new Paragraph().append(new Spacer(30f/72f, 0)).append(new FormattedText("from " + adjustmentLocal.getPaymentPaidByType(), Text.TABLE_TEXT)));
						grid.setCellText(row, c1v, new FormattedText(StringFormatter.DOLLARS.formatString(adjustmentLocal.getPaymentAmount()), Text.TABLE_TEXT));
					} else {
						//ManageAddendums.K.write( adjustmentLocal.getType(), StringFormatter.DOLLARS.formatString(adjustmentLocal.getPaymentAmount()));
					}
			}
			while (currentPrintNumber < 11)
				grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
	
			//L.T4: Adjustments for Items Unpaid by Seller
			grid.setCellText(++row, c1n, new FormattedText("Adjustments for Items Unpaid by Seller", Text.TABLE_HEADER));
			
			//L.12
			from = blankdate;
			to = blankdate;
			amt = new FormattedText("", Text.TABLE_TEXT);
			for (Prorations prorationLocal : prorationsList)
				if (prorationLocal.getIntegratedDisclosureSectionType().equals("PaidAlreadyByOrOnBehalfOfBorrowerAtClosing")
						&& prorationLocal.getIntegratedDisclosureSubsectionType().equals("AdjustmentsForItemsUnpaidBySeller"))
					if (cityTaxFees.contains(prorationLocal.getType())) {
						if (!prorationLocal.getProrationItemPaidFromDate().equals(""))
							from = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidFromDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getProrationItemPaidThroughDate().equals(""))
							to = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidThroughDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getPaymentAmount().equals(""))
							amt = new FormattedText(StringFormatter.DOLLARS.formatString(prorationLocal.getPaymentAmount()), Text.TABLE_TEXT);
					}
			grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c1l, new FormattedText("City/Town Taxes", Text.TABLE_TEXT));
			grid.setCellText(row, c1f, from);
			grid.setCellText(row, c1o, new FormattedText(" to", Text.TABLE_TEXT));
			grid.setCellText(row, c1t, to);
			grid.setCellText(row, c1v, amt);

			//L.13
			from = blankdate;
			to = blankdate;
			amt = new FormattedText("", Text.TABLE_TEXT);
			for (Prorations prorationLocal : prorationsList)
				if (prorationLocal.getIntegratedDisclosureSectionType().equals("PaidAlreadyByOrOnBehalfOfBorrowerAtClosing")
						&& prorationLocal.getIntegratedDisclosureSubsectionType().equals("AdjustmentsForItemsUnpaidBySeller"))
					if (countyTaxFees.contains(prorationLocal.getType())) {
						if (!prorationLocal.getProrationItemPaidFromDate().equals(""))
							from = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidFromDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getProrationItemPaidThroughDate().equals(""))
							to = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidThroughDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getPaymentAmount().equals(""))
							amt = new FormattedText(StringFormatter.DOLLARS.formatString(prorationLocal.getPaymentAmount()), Text.TABLE_TEXT);
					}
			grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c1l, new FormattedText("County Taxes", Text.TABLE_TEXT));
			grid.setCellText(row, c1f, from);
			grid.setCellText(row, c1o, new FormattedText(" to", Text.TABLE_TEXT));
			grid.setCellText(row, c1t, to);
			grid.setCellText(row, c1v, amt);
			
			//L.14
			from = blankdate;
			to = blankdate;
			amt = new FormattedText("", Text.TABLE_TEXT);
			for (Prorations prorationLocal : prorationsList)
				if (prorationLocal.getIntegratedDisclosureSectionType().equals("PaidAlreadyByOrOnBehalfOfBorrowerAtClosing")
						&& prorationLocal.getIntegratedDisclosureSubsectionType().equals("AdjustmentsForItemsUnpaidBySeller"))
					if (assesmentFees.contains(prorationLocal.getType())) {
						if (!prorationLocal.getProrationItemPaidFromDate().equals(""))
							from = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidFromDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getProrationItemPaidThroughDate().equals(""))
							to = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidThroughDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getPaymentAmount().equals(""))
							amt = new FormattedText(StringFormatter.DOLLARS.formatString(prorationLocal.getPaymentAmount()), Text.TABLE_TEXT);
						break;
					}
			grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c1l, new FormattedText("Assessments", Text.TABLE_TEXT));
			grid.setCellText(row, c1f, from);
			grid.setCellText(row, c1o, new FormattedText(" to", Text.TABLE_TEXT));
			grid.setCellText(row, c1t, to);
			grid.setCellText(row, c1v, amt);
			
			//L.15 through L.17
			from = blankdate;
			to = blankdate;
			amt = new FormattedText("", Text.TABLE_TEXT);
			for (Prorations prorationLocal : prorationsList)
				if (prorationLocal.getIntegratedDisclosureSectionType().equals("PaidAlreadyByOrOnBehalfOfBorrowerAtClosing")
						&& prorationLocal.getIntegratedDisclosureSubsectionType().equals("AdjustmentsForItemsUnpaidBySeller"))
						if (Arrays.asList(adjustmentFees).contains(prorationLocal.getType())) {
						from = blankdate;
						to = blankdate;
						amt = new FormattedText("", Text.TABLE_TEXT);
						str = prorationLocal.getLabel();
						if ("".equalsIgnoreCase(str))
							str = StringFormatter.CAMEL.formatString(prorationLocal.getType());
						if (!prorationLocal.getProrationItemPaidFromDate().equals(""))
							from = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidFromDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getProrationItemPaidThroughDate().equals(""))
							to = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidThroughDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getPaymentAmount().equals(""))
							amt = new FormattedText(StringFormatter.DOLLARS.formatString(prorationLocal.getPaymentAmount()), Text.TABLE_TEXT);
						if (currentPrintNumber < 17) {
							grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
							grid.setCellText(row, c1l, new FormattedText(str, Text.TABLE_TEXT));
							grid.setCellText(row, c1f, from);
							grid.setCellText(row, c1o, new FormattedText(" to", Text.TABLE_TEXT));
							grid.setCellText(row, c1t, to);
							grid.setCellText(row, c1v, amt);
						}
					}
			while (currentPrintNumber < 17)
				grid.setCellText(++row, c1n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			
			// Write Borrower "Calculation"
			grid.setCellText(++row, c1n, new FormattedText("CALCULATION", Text.TABLE_HEADER));
			grid.setCellText(++row, c1n, new FormattedText("Total Due from Borrower at Closing (K)", Text.TABLE_TEXT));
			str = closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.DueFromBorrowerAtClosing");
			if (!str.equals(""))
				grid.setCellText(row, c1v, new FormattedText(StringFormatter.DOLLARS.formatString(str), Text.TABLE_TEXT));
			grid.setCellText(++row, c1n, new FormattedText("Total Paid Already by or on Behalf of Borrower at Closing (L)", Text.TABLE_TEXT));
			str = closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.PaidAlreadyByOrOnBehalfOfBorrowerAtClosing");
			if (!str.isEmpty())
				grid.setCellText(row, c1v, new FormattedText(StringFormatter.DOLLARS.formatString(StringFormatter.NEGATE.formatString(str)), Text.TABLE_TEXT));
			str = closingMap.getClosingMapValue("CLOSING_INFORMATION_DETAIL.CashFromBorrowerAtClosingAmount");
			if (!str.isEmpty()) {
				Paragraph text = (new Paragraph())
						.append(new FormattedText("Cash to Close ", Text.TABLE_HEADER))
						.append(BoxedCharacter.CHECK_BOX_NO)
						.append(new FormattedText(" From ", Text.TABLE_HEADER))
						.append(BoxedCharacter.CHECK_BOX_EMPTY)
						.append(new FormattedText(" To Borrower", Text.TABLE_HEADER));
				grid.setCellText(row + 1, c1n, text);
				grid.setCellText(row + 1, c1v, new FormattedText(StringFormatter.ABSDOLLARS.formatString(str), Text.TABLE_HEADER));
			} else {
				str = closingMap.getClosingMapValue("CLOSING_INFORMATION_DETAIL.CashToBorrowerAtClosingAmount");
				if (!str.isEmpty()) {
					Paragraph text = (new Paragraph())
							.append(new FormattedText("Cash to Close ", Text.TABLE_HEADER))
							.append(BoxedCharacter.CHECK_BOX_EMPTY)
							.append(new FormattedText(" From ", Text.TABLE_HEADER))
							.append(BoxedCharacter.CHECK_BOX_NO)
							.append(new FormattedText(" To Borrower", Text.TABLE_HEADER));
					grid.setCellText(row + 1, c1l, text);
					grid.setCellText(row + 1, c1v, new FormattedText(StringFormatter.ABSDOLLARS.formatString(str), Text.TABLE_HEADER));
				}
			}
		}
		
		//----------------------------------------------------------------------------------------------------------------------
		//borrower split view (or a standard view refi) suppresses sections M and N
		if (!data.isBorrowerOnly() && !closingMap.getClosingMapValue("TERMS_OF_LOAN.LoanPurposeType").equalsIgnoreCase("Refinance")) {


			// Start column 2
			row = 0;
			grid.setCellText(row, c2n, new FormattedText("SELLER'S TRANSACTION", Text.TABLE_HEADER));

			//M.T1-----------------------------------------------------------------------------------------------------------------
			grid.setCellText(++row, c2n, new FormattedText("M. Due to Seller at Closing", Text.TABLE_HEADER));
			grid.setLineShade(row, Dimension.ROW, Color.MEDIUM_GRAY);
			str = closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.DueToSellerAtClosing");
			if (!str.equals("")) 
				grid.setCellText(row, c2v, new FormattedText(StringFormatter.DOLLARS.formatString(str), Text.TABLE_HEADER));
		
			currentPrintNumber = 0;

			//M.01
			grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c2l, new FormattedText("Sale Price of Property", Text.TABLE_TEXT));
			if (firstLien) {
				str = closingMap.getClosingMapValue("SALES_CONTRACT_DETAIL.RealPropertyAmount");
				if (!str.equals("") && !closingMap.getClosingMapValue("SALES_CONTRACT_DETAIL.PersonalPropertyAmount").equals(""))
					grid.setCellText(row, c2v, new FormattedText(StringFormatter.DOLLARS.formatString(str), Text.TABLE_TEXT));
				else {
					str = closingMap.getClosingMapValue("SALES_CONTRACT_DETAIL.SalesContractAmount");
					if (!str.equals(""))
						grid.setCellText(row, c2v, new FormattedText(StringFormatter.DOLLARS.formatString(str), Text.TABLE_TEXT));
				}
			}
			//M.02
			grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c2l, new FormattedText("Sale Price of Any Personal Property Included in Sale", Text.TABLE_TEXT));
			str = closingMap.getClosingMapValue("SALES_CONTRACT_DETAIL.PersonalPropertyAmount");
			if (!str.equals(""))
				grid.setCellText(row, c2v, new FormattedText(StringFormatter.DOLLARS.formatString(str), Text.TABLE_TEXT));

			//M.04 through M.08
			for(Adjustments adjustmentLocal:adjustmentList){
				if(adjustmentLocal.getIntegratedDisclosureSectionType().equals("DueToSellerAtClosing")) {
					if(currentPrintNumber < 8){
						grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
						grid.setCellText(row, c2l, new FormattedText(StringFormatter.CAMEL.formatString(adjustmentLocal.getLabel()), Text.TABLE_TEXT) );
						grid.setCellText(row, c2v, new FormattedText(StringFormatter.DOLLARS.formatString(adjustmentLocal.getPaymentAmount()), Text.TABLE_TEXT));
					} else{
						//ManageAddendums.M.write( adjustmentLocal.getType(), StringFormatter.DOLLARS.formatString(adjustmentLocal.getPaymentAmount()));
					}
				}
			}
			while (currentPrintNumber < 8)
				grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));

			//M.T2
			grid.setCellText(++row, c2n, new FormattedText("Adjustments for Items Paid by Seller in Advance", Text.TABLE_HEADER));;

			//M.09
			from = blankdate;
			to   = blankdate;
			amt  = new FormattedText("", Text.TABLE_TEXT);
			for (Prorations prorationLocal : prorationsList)
				if (prorationLocal.getIntegratedDisclosureSectionType().equals("DueToSellerAtClosing")
						&& prorationLocal.getIntegratedDisclosureSubsectionType().equals("AdjustmentsForItemsPaidBySellerInAdvance"))
					if (cityTaxFees.contains(prorationLocal.getType())) {
						if (!prorationLocal.getProrationItemPaidFromDate().equals(""))
							from = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidFromDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getProrationItemPaidThroughDate().equals(""))
							to = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidThroughDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getPaymentAmount().equals(""))
							amt = new FormattedText(StringFormatter.DOLLARS.formatString(prorationLocal.getPaymentAmount()), Text.TABLE_TEXT);
					}
			grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c2l, new FormattedText("City/Town Taxes", Text.TABLE_TEXT));
			grid.setCellText(row, c2f, from);
			grid.setCellText(row, c2o, new FormattedText(" to", Text.TABLE_TEXT));
			grid.setCellText(row, c2t, to);
			grid.setCellText(row, c2v, amt);

			//M.10
			from = blankdate;
			to   = blankdate;
			amt  = new FormattedText("", Text.TABLE_TEXT);
			for (Prorations prorationLocal : prorationsList)
				if (prorationLocal.getIntegratedDisclosureSectionType().equals("DueToSellerAtClosing")
						&& prorationLocal.getIntegratedDisclosureSubsectionType().equals("AdjustmentsForItemsPaidBySellerInAdvance"))
					if (countyTaxFees.contains(prorationLocal.getType())) {
						if (!prorationLocal.getProrationItemPaidFromDate().equals(""))
							from = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidFromDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getProrationItemPaidThroughDate().equals(""))
							to = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidThroughDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getPaymentAmount().equals(""))
							amt = new FormattedText(StringFormatter.DOLLARS.formatString(prorationLocal.getPaymentAmount()), Text.TABLE_TEXT);
					}
			grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c2l, new FormattedText("County Taxes", Text.TABLE_TEXT));
			grid.setCellText(row, c2f, from);
			grid.setCellText(row, c2o, new FormattedText(" to", Text.TABLE_TEXT));
			grid.setCellText(row, c2t, to);
			grid.setCellText(row, c2v, amt);

			//M.11
			from = blankdate;
			to   = blankdate;
			amt  = new FormattedText("", Text.TABLE_TEXT);
			for (Prorations prorationLocal : prorationsList)
				if (prorationLocal.getIntegratedDisclosureSectionType().equals("DueToSellerAtClosing")
						&& prorationLocal.getIntegratedDisclosureSubsectionType().equals("AdjustmentsForItemsPaidBySellerInAdvance"))
					if (assesmentFees.contains(prorationLocal.getType())) {
						if (!prorationLocal.getProrationItemPaidFromDate().equals(""))
							from = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidFromDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getProrationItemPaidThroughDate().equals(""))
							to = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidThroughDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getPaymentAmount().equals(""))
							amt = new FormattedText(StringFormatter.DOLLARS.formatString(prorationLocal.getPaymentAmount()), Text.TABLE_TEXT);
					}
			grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c2l, new FormattedText("Assessments", Text.TABLE_TEXT));
			grid.setCellText(row, c2f, from);
			grid.setCellText(row, c2o, new FormattedText(" to", Text.TABLE_TEXT));
			grid.setCellText(row, c2t, to);
			grid.setCellText(row, c2v, amt);

			//M.12
			for (Prorations prorationLocal : prorationsList) {
				if (prorationLocal.getIntegratedDisclosureSectionType().equals("DueToSellerAtClosing")
						&& prorationLocal.getIntegratedDisclosureSubsectionType().equals("AdjustmentsForItemsPaidBySellerInAdvance"))
					if (Arrays.asList(adjustmentFees).contains(prorationLocal.getType())) {
						from = blankdate;
						to   = blankdate;
						amt  = new FormattedText("", Text.TABLE_TEXT);
						if (!prorationLocal.getProrationItemPaidFromDate().equals(""))
							from = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidFromDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getProrationItemPaidThroughDate().equals(""))
							to = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidThroughDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getPaymentAmount().equals(""))
							amt = new FormattedText(StringFormatter.DOLLARS.formatString(prorationLocal.getPaymentAmount()), Text.TABLE_TEXT);
						if (currentPrintNumber < 16){
							grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
							grid.setCellText(row, c2l, new FormattedText(prorationLocal.getLabel(), Text.TABLE_TEXT));
							grid.setCellText(row, c2f, from);
							grid.setCellText(row, c2o, new FormattedText(" to", Text.TABLE_TEXT));
							grid.setCellText(row, c2t, to);
							grid.setCellText(row, c2v, amt);
						//} else {
							//ManageAddendums.M.write( prorationLocal.getType(), StringFormatter.DOLLARS.formatString(prorationLocal.getPaymentAmount()));
						}
					}
			}

			//M.12 or M.13 through M.16
			while (currentPrintNumber < 16)
				grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));

			//N.T1
			grid.setCellText(++row, c2n, new FormattedText("N. Due from Seller at Closing", Text.TABLE_HEADER));
			grid.setLineShade(row, Dimension.ROW, Color.MEDIUM_GRAY);
			str = closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.DueFromSellerAtClosing");
			if (!"".equals(str))
				grid.setCellText(row, c2v, new FormattedText(StringFormatter.DOLLARS.formatString(str), Text.TABLE_HEADER));
			currentPrintNumber = 0;

			//N.01
			grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c2l, new FormattedText("Excess Deposit", Text.TABLE_TEXT));
			for (ClosingCostFunds fundsLocal : closingFunds)
				if (fundsLocal.getIntegratedDisclosureSectionType().equals("DueFromSellerAtClosing")
						&& fundsLocal.getType().equals("ExcessDeposit")) {
					grid.setCellText(row, c2v, new FormattedText(StringFormatter.DOLLARS.formatString(fundsLocal.getTotalAmount()), Text.TABLE_TEXT));
					break;
				}

			//N.02
			grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c2l, new FormattedText("Closing Costs Paid at Closing (J)", Text.TABLE_TEXT));
			str = "";
			if (data.isSellerOnly()) {
				for (ID_Subsection idsLocal:idsList)
					if (idsLocal.getIntegratedDisclosureSubsectionType().equals("TotalClosingCostsSellerOnly"))
						if (!idsLocal.isPaidOutsideOfClosingIndicator())
							str = idsLocal.getPaymentAmount();
			} else {
				for (ID_Subsection idsLocal:idsList)
					if (idsLocal.getIntegratedDisclosureSectionType().equals("TotalClosingCosts"))
						if (idsLocal.getIntegratedDisclosureSubsectionType().equals("ClosingCostsSubtotal")
								&& idsLocal.getPaymentPaidByType().equals("Seller") && !idsLocal.isPaidOutsideOfClosingIndicator())
							str = idsLocal.getPaymentAmount();
			}
			if (str != null)
				grid.setCellText(row, c2v, new FormattedText(StringFormatter.DOLLARS.formatString(str), Text.TABLE_TEXT));

			//N.03
			grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c2l, new FormattedText("Existing Loan(s) Assumed or Taken Subject to", Text.TABLE_TEXT));
			str = closingMap.getClosingMapValue("TERMS_OF_LOAN.AssumedLoanAmount");
			if (!str.equals(""))
				grid.setCellText(row, c2v, new FormattedText(StringFormatter.DOLLARS.formatString(str), Text.TABLE_TEXT));

			//N.04
			grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c2l, new FormattedText("Payoff of First Mortgage Loan", Text.TABLE_TEXT));
			for (Liabilities liabilityLocal:liabilityList) {
				if (liabilityLocal.getType().equals("FirstPositionMortgageLien")) {
					grid.setCellText(row, c2v, new FormattedText(StringFormatter.DOLLARS.formatString(liabilityLocal.getPayoffAmount()), Text.TABLE_TEXT));
				}
			}

			//N.05
			grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c2l, new FormattedText("Payoff of Second Mortgage Loan", Text.TABLE_TEXT));
			for (Liabilities liabilityLocal:liabilityList) {
				if (liabilityLocal.getType().equals("SecondPositionMortgageLien")) {
					grid.setCellText(row, c2v, new FormattedText(StringFormatter.DOLLARS.formatString(liabilityLocal.getPayoffAmount()), Text.TABLE_TEXT));
				}
			}

			//N.06 through N.07
			for (Liabilities liabilityLocal:liabilityList) {
				if (liabilityLocal.getIDSection().equalsIgnoreCase("DueFromSellerAtClosing") && liabilityFromSeller.contains(liabilityLocal.getType())) {
					grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
					grid.setCellText(row, c2l, new FormattedText(liabilityLocal.getLabel(), Text.TABLE_TEXT));
					grid.setCellText(row, c2v, new FormattedText(StringFormatter.DOLLARS.formatString(liabilityLocal.getPayoffAmount()), Text.TABLE_TEXT));
				}
			}
			while (currentPrintNumber < 7)
				grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));

			//N.08
			grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c2l, new FormattedText("Seller Credit", Text.TABLE_TEXT));
			for (Adjustments adjustmentLocal:adjustmentList) {
				if (adjustmentLocal.getIntegratedDisclosureSectionType().equals("DueFromSellerAtClosing")
						&& adjustmentLocal.getType().equals("SellerCredit")
						&& !adjustmentLocal.isPaidOutsideOfClosingIndicator())
					grid.setCellText(row, c2v, new FormattedText(StringFormatter.DOLLARS.formatString(adjustmentLocal.getPaymentAmount()), Text.TABLE_TEXT));
			}

			//N.09
			for (Adjustments adjustmentLocal:adjustmentList) {
				if (adjustmentLocal.getIntegratedDisclosureSectionType().equals("DueFromSellerAtClosing")
						&& !adjustmentLocal.isPaidOutsideOfClosingIndicator()
						&& !adjustmentLocal.getType().equals("SellerCredit")) {
					grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
					grid.setCellText(row, c2l, new FormattedText(adjustmentLocal.getLabel(), Text.TABLE_TEXT));
					grid.setCellText(row, c2v, new FormattedText(StringFormatter.DOLLARS.formatString(adjustmentLocal.getPaymentAmount()), Text.TABLE_TEXT));
				}
			}

			//N.10 through N.13
			while (currentPrintNumber < 13)
				grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));

			//N.T2
			grid.setCellText(++row, c2n, new FormattedText("Adjustments for Items Unpaid by Seller", Text.TABLE_HEADER));

			//N.14
			from = blankdate;
			to   = blankdate;
			amt  = new FormattedText("", Text.TABLE_TEXT);
			for (Prorations prorationLocal : prorationsList)
				if (prorationLocal.getIntegratedDisclosureSectionType().equals("DueFromSellerAtClosing")
						&& prorationLocal.getIntegratedDisclosureSubsectionType().equals("AdjustmentsForItemsUnpaidBySeller"))
					if (cityTaxFees.contains(prorationLocal.getType())) {
						if (!prorationLocal.getProrationItemPaidFromDate().equals(""))
							from = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidFromDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getProrationItemPaidThroughDate().equals(""))
							to = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidThroughDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getPaymentAmount().equals(""))
							amt = new FormattedText(StringFormatter.DOLLARS.formatString(prorationLocal.getPaymentAmount()), Text.TABLE_TEXT);
					}
			grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c2l, new FormattedText("City/Town Taxes", Text.TABLE_TEXT));
			grid.setCellText(row, c2f, from);
			grid.setCellText(row, c2o, new FormattedText(" to", Text.TABLE_TEXT));
			grid.setCellText(row, c2t, to);
			grid.setCellText(row, c2v, amt);

			//N.15
			from = blankdate;
			to   = blankdate;
			amt  = new FormattedText("", Text.TABLE_TEXT);
			for (Prorations prorationLocal : prorationsList)
				if (prorationLocal.getIntegratedDisclosureSectionType().equals("DueFromSellerAtClosing")
						&& prorationLocal.getIntegratedDisclosureSubsectionType().equals("AdjustmentsForItemsUnpaidBySeller"))
					if (countyTaxFees.contains(prorationLocal.getType())) {
						if (!prorationLocal.getProrationItemPaidFromDate().equals(""))
							from = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidFromDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getProrationItemPaidThroughDate().equals(""))
							to = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidThroughDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getPaymentAmount().equals(""))
							amt = new FormattedText(StringFormatter.DOLLARS.formatString(prorationLocal.getPaymentAmount()), Text.TABLE_TEXT);
					}
			grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c2l, new FormattedText("County Taxes", Text.TABLE_TEXT));
			grid.setCellText(row, c2f, from);
			grid.setCellText(row, c2o, new FormattedText(" to", Text.TABLE_TEXT));
			grid.setCellText(row, c2t, to);
			grid.setCellText(row, c2v, amt);

			//N.16
			from = blankdate;
			to   = blankdate;
			amt  = new FormattedText("", Text.TABLE_TEXT);
			for (Prorations prorationLocal : prorationsList)
				if (prorationLocal.getIntegratedDisclosureSectionType().equals("DueFromSellerAtClosing")
						&& prorationLocal.getIntegratedDisclosureSubsectionType().equals("AdjustmentsForItemsUnpaidBySeller"))
					if (assesmentFees.contains(prorationLocal.getType())) {
						if (!prorationLocal.getProrationItemPaidFromDate().equals(""))
							from = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidFromDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getProrationItemPaidThroughDate().equals(""))
							to = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidThroughDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getPaymentAmount().equals(""))
							amt = new FormattedText(StringFormatter.DOLLARS.formatString(prorationLocal.getPaymentAmount()), Text.TABLE_TEXT);
					}
			grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
			grid.setCellText(row, c2l, new FormattedText("Assessments", Text.TABLE_TEXT));
			grid.setCellText(row, c2f, from);
			grid.setCellText(row, c2o, new FormattedText(" to", Text.TABLE_TEXT));
			grid.setCellText(row, c2t, to);
			grid.setCellText(row, c2v, amt);

			//N.17
			from = blankdate;
			to   = blankdate;
			amt  = new FormattedText("", Text.TABLE_TEXT);
			for (Prorations prorationLocal : prorationsList)
				if (prorationLocal.getIntegratedDisclosureSectionType().equals("DueFromSellerAtClosing")
						&& prorationLocal.getIntegratedDisclosureSubsectionType().equals("AdjustmentsForItemsUnpaidBySeller"))
					if (Arrays.asList(adjustmentFees).contains(prorationLocal.getType())) {		
						from = blankdate;
						to   = blankdate;
						amt  = new FormattedText("", Text.TABLE_TEXT);
						if (!prorationLocal.getProrationItemPaidFromDate().equals(""))
							from = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidFromDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getProrationItemPaidThroughDate().equals(""))
							to = new FormattedText(StringFormatter.SHORTDATE.formatString(prorationLocal.getProrationItemPaidThroughDate()), Text.TABLE_TEXT);
						if (!prorationLocal.getPaymentAmount().equals(""))
							amt = new FormattedText(StringFormatter.DOLLARS.formatString(prorationLocal.getPaymentAmount()), Text.TABLE_TEXT);
						if (currentPrintNumber < 19){
							grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));
							grid.setCellText(row, c2l, new FormattedText(prorationLocal.getLabel(), Text.TABLE_TEXT));
							grid.setCellText(row, c2f, from);
							grid.setCellText(row, c2o, new FormattedText(" to", Text.TABLE_TEXT));
							grid.setCellText(row, c2t, to);
							grid.setCellText(row, c2v, amt);
						//} else {
							//ManageAddendums.N.write( prorationLocal.getType(), StringFormatter.DOLLARS.formatString(prorationLocal.getPaymentAmount()));
						}
					}

			//N.17 or N.18 through N.19
			while (currentPrintNumber < 19)
				grid.setCellText(++row, c2n, new FormattedText(String.format("%02d", ++currentPrintNumber), Text.TABLE_NUMBER));

			// Write Seller "Calculation"
			grid.setCellText(++row, c2n, new FormattedText("CALCULATION", Text.TABLE_HEADER));
			grid.setCellText(++row, c2n, new FormattedText("Total Due to Seller at Closing (M)", Text.TABLE_TEXT));
			str = closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.DueToSellerAtClosing");
			if (!str.equals(""))
				grid.setCellText(row, c2v, new FormattedText(StringFormatter.DOLLARS.formatString(str), Text.TABLE_TEXT));
			grid.setCellText(++row, c2n, new FormattedText("Total Due from Seller at Closing (N)", Text.TABLE_TEXT));
			str = closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.DueFromSellerAtClosing");
			if (str != null)
				grid.setCellText(row, c2v, new FormattedText(StringFormatter.DOLLARS.formatString(StringFormatter.NEGATE.formatString(str)), Text.TABLE_TEXT));
			str = closingMap.getClosingMapValue("CLOSING_INFORMATION_DETAIL.CashFromSellerAtClosingAmount");
			if (!str.equals("")) {
				Paragraph text = (new Paragraph())
						.append(new FormattedText("Cash to Close ", Text.TABLE_HEADER))
						.append(BoxedCharacter.CHECK_BOX_NO)
						.append(new FormattedText(" From ", Text.TABLE_HEADER))
						.append(BoxedCharacter.CHECK_BOX_EMPTY)
						.append(new FormattedText(" To Seller", Text.TABLE_HEADER));
				grid.setCellText(row+1, c2n, text);
				grid.setCellText(row+1, c2v, new FormattedText(StringFormatter.ABSDOLLARS.formatString(str), Text.TABLE_HEADER));
			} else {
				str = closingMap.getClosingMapValue("CLOSING_INFORMATION_DETAIL.CashToSellerAtClosingAmount");
				if (!str.equals("")){
					Paragraph text = (new Paragraph())
							.append(new FormattedText("Cash to Close ", Text.TABLE_HEADER))
							.append(BoxedCharacter.CHECK_BOX_EMPTY)
							.append(new FormattedText(" From ", Text.TABLE_HEADER))
							.append(BoxedCharacter.CHECK_BOX_NO)
							.append(new FormattedText(" To Seller", Text.TABLE_HEADER));
					grid.setCellText(row+1, c2n, text);
					grid.setCellText(row+1, c2v, new FormattedText(StringFormatter.ABSDOLLARS.formatString(str), Text.TABLE_HEADER));
				}
			}
		}
		if(!data.isBorrowerOnly() && ! data.isSellerOnly()){
			// Clear out middle column
			for (row = 1; row < grid.rows(); row++)
				grid.setCellBorder(row, cmd, Position.TOP, null);
			grid.setCellBorder(43, cmd, Position.BOTTOM, null);
			grid.setLineShade(cmd, Dimension.COLUMN, null);
		}
	}

	public void draw(Page page, Object d) throws IOException {
		InputData data = (InputData)d;
		initializeTitleGrid(data);
		insertText(page, data);
		grid.draw(page, page.leftMargin, location);
		titleGrid.draw(page, page.leftMargin, location + grid.height(page));
	}
}
