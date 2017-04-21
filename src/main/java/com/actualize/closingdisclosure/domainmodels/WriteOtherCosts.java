package com.actualize.closingdisclosure.domainmodels;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.actualize.closingdisclosure.datalayer.ClosingMap;
import com.actualize.closingdisclosure.datalayer.Escrows;
import com.actualize.closingdisclosure.datalayer.FeeTypes;
import com.actualize.closingdisclosure.datalayer.Fees;
import com.actualize.closingdisclosure.datalayer.ID_Subsection;
import com.actualize.closingdisclosure.datalayer.InputData;
import com.actualize.closingdisclosure.datalayer.PageTwo;
import com.actualize.closingdisclosure.datalayer.Prepaids;
import com.actualize.closingdisclosure.pdfbuilder.Color;
import com.actualize.closingdisclosure.pdfbuilder.FormattedText;
import com.actualize.closingdisclosure.pdfbuilder.Grid;
import com.actualize.closingdisclosure.pdfbuilder.Page;
import com.actualize.closingdisclosure.pdfbuilder.Section;
import com.actualize.closingdisclosure.pdfbuilder.Text;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Dimension;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Position;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.Direction;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.HorizontalAlignment;

public class WriteOtherCosts implements Section {
	
	//write out all the row label columns 0, 1, 2
	Grid labelGrid = null;
	Grid borrowerGrid = null;
	Grid borrowerOverlayGrid = null;
	Grid sellerHeaderGrid = null;
	Grid sellerGrid = null;
	Grid otherOverlayGrid = null;
	Grid otherHeaderGrid = null;
	Grid otherGrid = null;
	Grid sellerOverlayGrid =null;
		
	// convenience constants
	float textWidth     = 238f/72f;
	float rowHeight 	= 10f/72f;
	float narrowRule 	= 1f/72f;
	float wideRule 		= 2f/72f;
	

	
	// the beginning point of each horizontal segment is set on the fly in the code
 	// store row numbers for each fee type-much less brittle than just re iterating fees
	//special cases for row numbers
	int aggregateAdjustmentRow = 0;
	
	boolean isPropertyTax(String type) {
		return	   type.equals("CityPropertyTax")
				|| type.equals("CountyPropertyTax")
				|| type.equals("DistrictPropertyTax")
			//	|| type.equals("StatePropertyTax")
				|| type.equals("TownPropertyTax");
	}
	
	private void initializeLabelGrid(Page page, InputData inputData) throws IOException {
		PageTwo pageTwo = inputData.getPageTwo();
		pageTwo.setCurrentSection("OtherCosts");
		inputData.setPageTwo(pageTwo);
		
		float heights[] = { rowHeight };
		float widths[] = { 12f/72f, pageTwo.getWidthLabel1(), pageTwo.getWidthLabel2()};
		
		List<Fees> feeList = inputData.getFeeList();
		List<Prepaids> prepaidsList = inputData.getPrepaidList();
		List<Escrows>  escrowList   = inputData.getEscrowList();
		List<ID_Subsection> idsList = inputData.getIdsList();
		//ClosingMap closingMap = inputData.getClosingMap();

		labelGrid = new Grid(pageTwo.getOtherCostsGridHeight(), heights, 3, widths);
		labelGrid.setLineBorder(2, Position.RIGHT, Color.DARK_GRAY, wideRule);
		if (inputData.isSellerOnly()) {
			labelGrid.setCellBorder(labelGrid.rows()-4, 2, Position.RIGHT, null, 0);
			labelGrid.setCellBorder(labelGrid.rows()-3, 2, Position.RIGHT, null, 0);
			labelGrid.setCellBorder(labelGrid.rows()-2, 2, Position.RIGHT, null, 0);
			labelGrid.setCellBorder(labelGrid.rows()-1, 2, Position.RIGHT, null, 0);
		}
		labelGrid.setCellBorder(0, 2, Position.RIGHT, null);
		labelGrid.getCell(0, 0)
			.setBackground(new Tab())
			.setForeground(	new FormattedText("   Other Costs", Text.SECTION_HEADER));
		
		labelGrid.setCellBorder(0, 1, Position.RIGHT, null);

		//TAXES
		int row = pageTwo.getStart(PageTwo.TAXES_GOVERNMENT);
		labelGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrowRule);
		//labelGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrowRule);
		labelGrid.setLineShade(row, Dimension.ROW, Color.LIGHT_GRAY);
		labelGrid.setLineMargin(row, Dimension.ROW, Direction.TOP, 2f);
		labelGrid.setCellText(row, 0, new FormattedText("E. Taxes and Other Government Fees", Text.ROW_HEADER));

		//8
		String str1 = "";
		String str2 = "";
		int lineNumber = 2;
		row = pageTwo.getStart(PageTwo.TAXES_GOVERNMENT) + 2;
		String text8_2_1 = "     ";
		String text8_2_2 = "    ";
		boolean foundTransferTax = false;
		
		for(Fees feeLocal:feeList){
			if (feeLocal.getIntegratedDisclosureSectionType().equals("TaxesAndOtherGovernmentFees")) {
				switch (feeLocal.getType()) {
				//8.2.1
				case "RecordingFeeForDeed":
					text8_2_1 = StringFormatter.DOLLARS.formatString(feeLocal.getTotalAmount());
					break;
					//8.2.1
				case "RecordingFeeForMortgage":
					text8_2_2 = StringFormatter.DOLLARS.formatString(feeLocal.getTotalAmount());
					break;
				case "RecordingFeeTotal":
					feeLocal.setPlacement(pageTwo.getCurrentSection(),2);
					break;
				}
				//8.3
				if (Arrays.asList(FeeTypes.transferTypes).contains(feeLocal.getType())) {
					feeLocal.setPlacement(pageTwo.getCurrentSection(),row);
					str1 = "Transfer Taxes";
					if (!foundTransferTax)
						foundTransferTax = true;
					else
						str1 = StringFormatter.CAMEL.formatString(feeLocal.getLabel());
					str2 = "to "+ feeLocal.getPaymentToEntity();
					row = LayoutPageTwo.writeLabel(page, labelGrid, str1, str2, lineNumber++, row, pageTwo);
				}
			}
		}//end fee for loop------------------------------------------------------------------------------------------------------
		str1 = "Recording Fees  Deed "+text8_2_1;
		str2 = "Mortgage "+ text8_2_2;
		LayoutPageTwo.writeLabel(page, labelGrid, str1, str2, 1, pageTwo.getStart(PageTwo.TAXES_GOVERNMENT) + 1, pageTwo);

		while (row < pageTwo.getStart(PageTwo.PREPAIDS) ){
			labelGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrowRule);
			labelGrid.setCellText(row++, 0, new FormattedText(String.format("%02d", lineNumber++), Text.TABLE_NUMBER));
		}

		//-----------------------------------------------------------------------------------------------------------
		//8.7
		row = pageTwo.getStart(PageTwo.PREPAIDS); 
		labelGrid.setLineShade(row, Dimension.ROW, Color.LIGHT_GRAY);
		labelGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrowRule);
		labelGrid.setLineMargin(row, Dimension.ROW, Direction.TOP, 2f);
	//	labelGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrowRule);
		labelGrid.setCellText(row++, 0, new FormattedText("F. Prepaids", Text.ROW_HEADER));

		//8.5
		str1 = "Homeowners Insurance Premium";
		str2 = "( mo.) to ";
		for (Prepaids prepaid:prepaidsList){
			if (prepaid.getIntegratedDisclosureSectionType().equals("Prepaids") 
					&& prepaid.getType().equals("HomeownersInsurancePremium")) {
				str2 = "("+prepaid.getPrepaidItemMonthsPaidCount() + " mo.) to " + prepaid.getPaymentToEntity();
				prepaid.setPlacement(pageTwo.getCurrentSection(), row);
				break;
			}
		}
		row = LayoutPageTwo.writeLabel(page, labelGrid, str1, str2, 1, row, pageTwo);
	
		//8.6
		str1 = "Mortgage Insurance Premium";
		str2 = "( mo.) to     ";
		for (Prepaids prepaid:prepaidsList){
			if (prepaid.getIntegratedDisclosureSectionType().equals("Prepaids") && prepaid.getType().equals("MortgageInsurancePremium")) {
				str2 = "(" + prepaid.getPrepaidItemMonthsPaidCount() + " mo.) to " + prepaid.getPaymentToEntity();
				prepaid.setPlacement(pageTwo.getCurrentSection(), row);
				break;
			}
		}
		row = LayoutPageTwo.writeLabel(page, labelGrid, str1, str2, 2, row, pageTwo);
		
		//8.7 Prepaid Interest
		str1 = "Prepaid Interest ";
		str2 = "($   per day from   to   ) ";
		for (Prepaids prepaid:prepaidsList) {
			if (prepaid.getType().equals("PrepaidInterest")) {
				str2 = "(" + StringFormatter.DOLLARS.formatString(prepaid.getPrepaidItemPerDiemAmount()) + " per day from " +
						StringFormatter.DATE.formatString(prepaid.getPrepaidItemPaidFromDate()) + " to " +
						StringFormatter.DATE.formatString(prepaid.getPrepaidItemPaidThroughDate()) + ")";
				prepaid.setPlacement(pageTwo.getCurrentSection(), row);
			}
		}
		row = LayoutPageTwo.writeLabel(page, labelGrid, str1, str2, 3, row, pageTwo);

		//8.7 Property Taxes  StatePropertyTax
		str1 = "Property Taxes ";
		str2 = "( mo.) to     ";
		lineNumber = 4;
		for (Prepaids prepaid:prepaidsList){
			if (isPropertyTax(prepaid.getType())){
				str2 = "(" + prepaid.getPrepaidItemMonthsPaidCount() + " mo.) to " + prepaid.getPaymentToEntity();
				if (row < pageTwo.getStart(PageTwo.ESCROWS)) {
					prepaid.setPlacement(pageTwo.getCurrentSection(),row);
				} else {
					prepaid.setPlacement(pageTwo.getCurrentSection()+"_Addendum", row++);
				}
				row = LayoutPageTwo.writeLabel(page, labelGrid, str1, str2, lineNumber++, row, pageTwo);
			}
		}
		if (lineNumber == 4)
			row = LayoutPageTwo.writeLabel(page, labelGrid, str1, str2, lineNumber++, row, pageTwo);

		str1 = "State Property Tax ";
		str2 = "( mo.) to     ";
		lineNumber = 5;
		for (Prepaids prepaid:prepaidsList){
			if (prepaid.getType().equalsIgnoreCase("StatePropertyTax")){
				str2 = "(" + prepaid.getPrepaidItemMonthsPaidCount() + " mo.) to " + prepaid.getPaymentToEntity();
				if (row < pageTwo.getStart(PageTwo.ESCROWS)) {
					prepaid.setPlacement(pageTwo.getCurrentSection(),row);
				} else {
					prepaid.setPlacement(pageTwo.getCurrentSection()+"_Addendum", row++);
				}
				row = LayoutPageTwo.writeLabel(page, labelGrid, str1, str2, lineNumber++, row, pageTwo);
			}
		}
		
		str1 = "Extra Charges ";
		str2 = "( mo.) to     ";
		lineNumber = 6;
		for (Prepaids prepaid:prepaidsList){
			if (prepaid.getType().equalsIgnoreCase("Other")){
				str2 = "(" + prepaid.getPrepaidItemMonthsPaidCount() + " mo.) to " + prepaid.getPaymentToEntity();
				if (row < pageTwo.getStart(PageTwo.ESCROWS)) {
					prepaid.setPlacement(pageTwo.getCurrentSection(),row);
				} else {
					prepaid.setPlacement(pageTwo.getCurrentSection()+"_Addendum", row++);
				}
				row = LayoutPageTwo.writeLabel(page, labelGrid, str1, str2, lineNumber++, row, pageTwo);
			}
		}
		//8.13
		while (row < pageTwo.getStart(PageTwo.ESCROWS) ){
			labelGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrowRule);
			labelGrid.setCellText(row++, 0, new FormattedText(String.format("%02d", lineNumber++), Text.TABLE_NUMBER));
		}
		
		row = pageTwo.getStart(PageTwo.ESCROWS);
		labelGrid.setLineShade(row, Dimension.ROW, Color.LIGHT_GRAY);
		labelGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrowRule);
		labelGrid.setLineMargin(row, Dimension.ROW, Direction.TOP, 2f);
	//	labelGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrowRule);
		labelGrid.setCellText(row++, 0, new FormattedText("G. Initial Escrow Payment At Closing", Text.ROW_HEADER));
		for (Escrows escrowLocal:escrowList) {
			escrowLocal.setPaidOutsideOfClosingIndicator(false);
		}
		
		str1 = "Homeowners Insurance ";
		str2 = "(  per month for  mo.)";
		for (Escrows escrowLocal:escrowList) {
			if (escrowLocal.getType().equals("HomeownersInsurance")) {
				str2 = "("+ StringFormatter.DOLLARS.formatString(escrowLocal.getMonthlyPaymentAmount())+ " per month for "
						+escrowLocal.getCollectedNumberOfMonthsCount() +" mo.)";
				escrowLocal.setPlacement(pageTwo.getCurrentSection(), row);
			}
		}
		row = LayoutPageTwo.writeLabel(page, labelGrid, str1, str2, 1, row, pageTwo);
		
		str1 = "Mortgage Insurance ";
		str2 = "(  per month for  mo.)";
		for (Escrows escrowLocal:escrowList) {
			if (escrowLocal.getType().equals("MortgageInsurance")) {
					str2 = "(" + StringFormatter.DOLLARS.formatString(escrowLocal.getMonthlyPaymentAmount()) + " per month for " + escrowLocal.getCollectedNumberOfMonthsCount() + " mo.)";
					escrowLocal.setPlacement(pageTwo.getCurrentSection(), row);
			}
		}
		row = LayoutPageTwo.writeLabel(page, labelGrid, str1, str2, 2, row, pageTwo);

		str1 = "Property Taxes ";
		str2 = "(  per month for  mo.)";
		lineNumber = 3;
		for (Escrows escrowLocal:escrowList) {
			if (isPropertyTax(escrowLocal.getType())) {
				str2 = "(" + StringFormatter.DOLLARS.formatString(escrowLocal.getMonthlyPaymentAmount()) + " per month for " + escrowLocal.getCollectedNumberOfMonthsCount() + " mo.)";
				escrowLocal.setPlacement(pageTwo.getCurrentSection(), row);
				row = LayoutPageTwo.writeLabel(page, labelGrid, str1, str2, lineNumber++, row, pageTwo);
			}
		}
		if (lineNumber == 3)
			row = LayoutPageTwo.writeLabel(page, labelGrid, str1, str2, lineNumber++, row, pageTwo);

		str1 = "Other";
		str2 = "(  per month for  mo.)";
		
		for (Escrows escrowLocal:escrowList) {
			if (!escrowLocal.getType().equals("HomeownersInsurance") && !escrowLocal.getType().equals("MortgageInsurance") && !isPropertyTax(escrowLocal.getType())) {
				str1 = escrowLocal.getLabel();
				str2 = "(" + StringFormatter.DOLLARS.formatString(escrowLocal.getMonthlyPaymentAmount()) + " per month for " + escrowLocal.getCollectedNumberOfMonthsCount() + " mo.)";
				escrowLocal.setPlacement(pageTwo.getCurrentSection(), row);
				row = LayoutPageTwo.writeLabel(page, labelGrid, str1, str2, lineNumber++, row, pageTwo);
			}
		}
		//System.out.println(row);
		row = LayoutPageTwo.writeLabel(page, labelGrid, "Aggregate Adjustment","", lineNumber++, row, pageTwo);
		
		
		while ( row < pageTwo.getStart(PageTwo.OTHER_FEES) ){
			labelGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrowRule);
			labelGrid.setCellText(row++, 0, new FormattedText(String.format("%02d", lineNumber++), Text.TABLE_NUMBER));
		}

		//8.15
		/*labelGrid.setLineBorder(pageTwo.getStart(PageTwo.OTHER_FEES)-1, Position.BOTTOM, Color.LIGHT_GRAY, narrowRule);
		labelGrid.setCellText(pageTwo.getStart(PageTwo.OTHER_FEES)-1, 0, new FormattedText(String.format("%02d", lineNumber++), Text.TABLE_NUMBER));
		labelGrid.setCellText(pageTwo.getStart(PageTwo.OTHER_FEES)-1, 1, new FormattedText("Aggregate Adjustment", Text.TABLE_TEXT));*/
		aggregateAdjustmentRow = pageTwo.getStart(PageTwo.OTHER_FEES)-1;
		
		//---------------------------------------------------------------------------------------------------
		String borrowerTag = " (Borrower-Paid)";
		if (inputData.isSellerOnly())
			borrowerTag = "";

		row = pageTwo.getStart(PageTwo.OTHER_FEES);
		labelGrid.setLineShade(row, Dimension.ROW, Color.LIGHT_GRAY);
		labelGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrowRule);
		labelGrid.setLineMargin(row, Dimension.ROW, Direction.TOP, 2f);
		//labelGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrowRule);
		labelGrid.setCellText(row++, 0, new FormattedText("H. Other", Text.ROW_HEADER));
		lineNumber = 1;
		for(Fees feeLocal:feeList){
			if(feeLocal.getIntegratedDisclosureSectionType().equals("OtherCosts")
					&& Arrays.asList(FeeTypes.otherTypes).contains(feeLocal.getType())){
				str2 = "";
				if(!feeLocal.getPaymentToEntity().equals("")){
					str2 = "to "+feeLocal.getPaymentToEntity();
				}
				str1 = feeLocal.getLabel();
				if(feeLocal.isOptionalCostIndicator()){
					str1+= " (Optional) ";
				}
				if (row < pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)){
					feeLocal.setPlacement(pageTwo.getCurrentSection(),row);
					//row += writeLabel(page, str1, str2, lineNumber++, row);
					row = LayoutPageTwo.writeLabel(page, labelGrid, str1, str2, lineNumber++, row, pageTwo);
				} else {
					feeLocal.setPlacement(pageTwo.getCurrentSection()+"_Addendum", row);
				}
			}
		}
		while ( row < pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES) ){
			labelGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrowRule);
			labelGrid.setCellText(row++, 0, new FormattedText(String.format("%02d", lineNumber++), Text.TABLE_NUMBER));
		}
		
		// Section I
   		row = pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES);
		if (!inputData.isSellerOnly()) {
			labelGrid.setLineShade(row, Dimension.ROW, Color.LIGHT_GRAY);
			labelGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrowRule);
			labelGrid.setLineMargin(row, Dimension.ROW, Direction.TOP, 2f);
		//	labelGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrowRule);
			labelGrid.setCellText(row++, 0, new FormattedText("I. TOTAL OTHER COSTS" + borrowerTag, Text.ROW_HEADER));
		//	labelGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrowRule);
			//8.19
			for(ID_Subsection idsLocal:idsList){
				if(idsLocal.getIntegratedDisclosureSubsectionType().equals("OtherCostsSubtotal")){
					idsLocal.setPlacement(pageTwo.getCurrentSection(),row);
					break;
				}
			}
			labelGrid.setCellText(row++, 0, new FormattedText("Other Costs Subtotals (E + F + G + H)",Text.TABLE_TEXT));
		}

		// Section J
		labelGrid.setLineShade(row, Dimension.ROW, Color.LIGHT_GRAY);
		labelGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrowRule);
		labelGrid.setLineMargin(row, Dimension.ROW, Direction.TOP, 2f);
	//	labelGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrowRule);
		
		labelGrid.setCellText(row++, 0, new FormattedText("J. TOTAL CLOSING COSTS" + borrowerTag, Text.ROW_HEADER));
		for(ID_Subsection idsLocal:idsList){
			if(idsLocal.getIntegratedDisclosureSubsectionType().equals("ClosingCostsSubtotal")){
				idsLocal.setPlacement(pageTwo.getCurrentSection(),row);
			}
		}
		if (!inputData.isSellerOnly()) {
		//	labelGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrowRule);
			labelGrid.setCellText(row++, 0, new FormattedText("Closing Cost Subtotals (D+I)", Text.TABLE_TEXT));
		//	labelGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrowRule);
			String str = "Lender Credits";
			for(ID_Subsection idsLocal:idsList){
				if (idsLocal.getIntegratedDisclosureSubsectionType().equals("LenderCredits")){
					//System.out.println("credits:"+row);
					idsLocal.setPlacement(pageTwo.getCurrentSection(),row);
					if(!idsLocal.getLenderTolerance().isEmpty()){
						str += " (Includes "+StringFormatter.DOLLARS.formatString(idsLocal.getLenderTolerance())
								+" credit for increase in Closing Costs above legal Limit)";
					}
					break;
				}
			}
			labelGrid.setLineWrap(row, Dimension.ROW, true);
			labelGrid.setCellText(row, 0, new FormattedText(str, Text.TABLE_TEXT));
			float lcHeight = labelGrid.getSize(page, Dimension.ROW, row);
			if (borrowerGrid != null)
				borrowerGrid.setLineHeight(row, lcHeight);
			if (borrowerOverlayGrid != null)
				borrowerOverlayGrid.setLineHeight(row, lcHeight);
			if (sellerGrid != null)
				sellerGrid.setLineHeight(row, lcHeight);
			if (otherOverlayGrid != null)
				otherOverlayGrid.setLineHeight(row, lcHeight);
			if (otherGrid != null)
				otherGrid.setLineHeight(row, lcHeight);
		}
	}

	private void initializeBorrowerGrid(InputData inputData){
		PageTwo pageTwo = inputData.getPageTwo();
		ClosingMap closingMap		= inputData.getClosingMap();
		List<Fees> feeList 			= inputData.getFeeList();
		List<ID_Subsection> idsList = inputData.getIdsList();
		List<Escrows> escrowList 	= inputData.getEscrowList();
		List<Prepaids> prepaidsList = inputData.getPrepaidList();
		
		float heights[] = { rowHeight };
		float widths[]  = {pageTwo.getWidthBuyer1(), pageTwo.getWidthBuyer2()};
		borrowerGrid = new Grid(pageTwo.getOtherCostsGridHeight(), heights, 2, widths);

		borrowerGrid.setLineHorizontalAlignment(0, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		borrowerGrid.setLineHorizontalAlignment(1, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		borrowerGrid.setLineBorder(1, Position.RIGHT, Color.DARK_GRAY, wideRule);
		//borrowerGrid.setCellBorder(0, 1, Position.RIGHT, null);
		
		
		borrowerGrid.getCell(1, 0)
		.setHorizontalAlignment(HorizontalAlignment.CENTER)
		.setForeground( new FormattedText("At Closing", Text.TABLE_TEXT));
		borrowerGrid.getCell(1, 1)
		.setHorizontalAlignment(HorizontalAlignment.CENTER)
		.setForeground( new FormattedText("Before Closing", Text.TABLE_TEXT));
		borrowerGrid.setLineShade(1, Dimension.ROW, Color.LIGHT_GRAY);
		//borrowerGrid.setLineBorder(1, Position.BOTTOM, Color.BLACK, narrow);
		borrowerGrid.setCellBorder(1, 0, Position.LEFT, Color.DARK_GRAY, wideRule);
		//lines
		//borrowerGrid.setLineBorder(2, Position.TOP, Color.BLACK, narrowRule);
	//	borrowerGrid.setLineBorder(1, Position.BOTTOM, Color.BLACK, narrowRule);
		borrowerGrid.setLineShade(2, Dimension.ROW, Color.LIGHT_GRAY);
		int row = 2;
		while (row < pageTwo.getStart(PageTwo.PREPAIDS) ){
			if (!pageTwo.getDoubleLines(row))borrowerGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrowRule);
			borrowerGrid.setCellBorder(row++, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
		}
		borrowerGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrowRule);
	//	borrowerGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrowRule);
		borrowerGrid.setLineShade(row++, Dimension.ROW, Color.LIGHT_GRAY);

		while (row < pageTwo.getStart(PageTwo.ESCROWS) ){
			if (!pageTwo.getDoubleLines(row))borrowerGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrowRule);
			borrowerGrid.setCellBorder(row++, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
		}
		borrowerGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrowRule);
	//	borrowerGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrowRule);
		borrowerGrid.setLineShade(row++, Dimension.ROW, Color.LIGHT_GRAY);

		while ( row < pageTwo.getStart(PageTwo.OTHER_FEES) ){
			if (!pageTwo.getDoubleLines(row))borrowerGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrowRule);
			borrowerGrid.setCellBorder(row++, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
		}
		borrowerGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrowRule);
	//	borrowerGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrowRule);
		borrowerGrid.setLineShade(row++, Dimension.ROW, Color.LIGHT_GRAY);
		while ( row < pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES) ){
			if (!pageTwo.getDoubleLines(row))borrowerGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrowRule);
			borrowerGrid.setCellBorder(row++, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
		}
		borrowerGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrowRule);
	//	borrowerGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrowRule);
		borrowerGrid.setLineShade(row++, Dimension.ROW, Color.LIGHT_GRAY);
		borrowerGrid.setCellBorder(row, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
		row++;
		borrowerGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrowRule);
	//	borrowerGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrowRule);
		borrowerGrid.setLineShade(row++, Dimension.ROW, Color.LIGHT_GRAY);
		//bottom lines
		borrowerGrid.setLineBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+2, Position.TOP, Color.BLACK, narrowRule);
		borrowerGrid.setLineBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+2, Position.BOTTOM, Color.BLACK, narrowRule);
		borrowerGrid.setLineBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+3, Position.BOTTOM, Color.BLACK, narrowRule);
		borrowerGrid.setCellBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+3, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
		borrowerGrid.setLineBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+4, Position.BOTTOM, Color.BLACK, narrowRule);
		borrowerGrid.setCellBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+4, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
		
		//lay in data
		//special cases
		//aggregate adjustment
		if (aggregateAdjustmentRow > 0 
				&& !closingMap.getClosingMapValue("ESCROW_DETAIL.EscrowAggregateAccountingAdjustmentAmount").equals("")){
			borrowerGrid.setCellText(aggregateAdjustmentRow, 0, new FormattedText( StringFormatter.DOLLARS.formatString(
					closingMap.getClosingMapValue("ESCROW_DETAIL.EscrowAggregateAccountingAdjustmentAmount"))
					,Text.TABLE_TEXT));
		}
		//remaining values
		row = 0;
		for(Fees feeLocal:feeList){
			if(feeLocal.getPaymentPaidByType().equals("Buyer")){
				if(feeLocal.getPlacement(pageTwo.getCurrentSection()) > 0){
					if (feeLocal.isPaidOutsideOfClosingIndicator()){
						borrowerGrid.setCellText(feeLocal.getPlacement(pageTwo.getCurrentSection()), 1, new FormattedText(
								StringFormatter.DOLLARS.formatString(feeLocal.getPaymentAmount()),Text.TABLE_TEXT));
					} else {
						borrowerGrid.setCellText(feeLocal.getPlacement(pageTwo.getCurrentSection()), 0, new FormattedText(
								StringFormatter.DOLLARS.formatString(feeLocal.getPaymentAmount()),Text.TABLE_TEXT));
					}
				}
			}
		} //end of fee list for loop
			
		for(Escrows escrowLocal:escrowList){
			if(escrowLocal.getPaymentPaidByType().equals("Buyer")){
				if(escrowLocal.getPlacement(pageTwo.getCurrentSection()) > 0){
					if (escrowLocal.isPaidOutsideOfClosingIndicator()){
						borrowerGrid.setCellText(escrowLocal.getPlacement(pageTwo.getCurrentSection()), 1, new FormattedText(
								StringFormatter.DOLLARS.formatString(escrowLocal.getPaymentAmount()),Text.TABLE_TEXT));
					} else {
						borrowerGrid.setCellText(escrowLocal.getPlacement(pageTwo.getCurrentSection()), 0, new FormattedText(
								StringFormatter.DOLLARS.formatString(escrowLocal.getPaymentAmount()),Text.TABLE_TEXT));
					}
				}
			}
		}// end of escrow loop
		
		for(Prepaids prepaidLocal:prepaidsList){
			if(prepaidLocal.getPaymentPaidByType().equals("Buyer")){
				if(prepaidLocal.getPlacement(pageTwo.getCurrentSection()) > 0){
					if (prepaidLocal.isPaidOutsideOfClosingIndicator()){
						borrowerGrid.setCellText(prepaidLocal.getPlacement(pageTwo.getCurrentSection()), 1, new FormattedText(
								StringFormatter.DOLLARS.formatString(prepaidLocal.getPaymentAmount()),Text.TABLE_TEXT));
					} else {
						borrowerGrid.setCellText(prepaidLocal.getPlacement(pageTwo.getCurrentSection()), 0, new FormattedText(
								StringFormatter.DOLLARS.formatString(prepaidLocal.getPaymentAmount()),Text.TABLE_TEXT));
					}
				}
			}
		}// end of prepaids loop
		
		for(ID_Subsection idsLocal:idsList){
			if(idsLocal.getPaymentPaidByType().equals("Buyer")){
				if(idsLocal.getPlacement(pageTwo.getCurrentSection()) > 0){
					if (idsLocal.isPaidOutsideOfClosingIndicator()){
						borrowerGrid.setCellText(idsLocal.getPlacement(pageTwo.getCurrentSection()), 1, new FormattedText(
								StringFormatter.DOLLARS.formatString(idsLocal.getPaymentAmount()),Text.TABLE_TEXT));
					} else {
						borrowerGrid.setCellText(idsLocal.getPlacement(pageTwo.getCurrentSection()), 0, new FormattedText(
								StringFormatter.DOLLARS.formatString(idsLocal.getPaymentAmount()),Text.TABLE_TEXT));
					}
				}
			}
		}// end of id section loop
		
	}
	
	private void initializeBorrowerOverlayGrid(InputData inputData) {
		PageTwo pageTwo = inputData.getPageTwo();
		float heights[] = { rowHeight };
		float width = pageTwo.getWidthBuyer1() + pageTwo.getWidthBuyer2();
		float widths[] = { width };
		ClosingMap closingMap = inputData.getClosingMap();
		
		
		borrowerOverlayGrid = new Grid(pageTwo.getOtherCostsGridHeight(), heights, 1, widths);
		
		borrowerOverlayGrid.setLineHorizontalAlignment(0, Dimension.ROW, HorizontalAlignment.CENTER);
		borrowerOverlayGrid.setLineShade(0, Dimension.ROW, Color.LIGHT_GRAY);
		borrowerOverlayGrid.setCellBorder(0, 0, Position.LEFT, Color.DARK_GRAY, wideRule);
		borrowerOverlayGrid.setCellBorder(0, 0, Position.RIGHT, Color.DARK_GRAY, wideRule);
		borrowerOverlayGrid.setCellText(0, 0, new FormattedText("Borrower-Paid", Text.TABLE_HEADER_LARGE));
		borrowerOverlayGrid.setLineBorder(0, Position.TOP, Color.BLACK, wideRule);
		borrowerOverlayGrid.setLineBorder(0, Position.BOTTOM, Color.DARK_GRAY, narrowRule);

		
		borrowerOverlayGrid.getCell(1, 0)
		.setHorizontalAlignment(HorizontalAlignment.CENTER)
		.setForeground( new FormattedText( StringFormatter.DOLLARS.formatString(
				closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.TaxesAndOtherGovernmentFees")),
				Text.TABLE_TEXT));
		borrowerOverlayGrid.getCell(pageTwo.getStart(PageTwo.PREPAIDS), 0)
		.setHorizontalAlignment(HorizontalAlignment.CENTER)
		.setForeground( new FormattedText( StringFormatter.DOLLARS.formatString(
				closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.Prepaids")),
				Text.TABLE_TEXT));
		borrowerOverlayGrid.getCell(pageTwo.getStart(PageTwo.ESCROWS), 0)
		.setHorizontalAlignment(HorizontalAlignment.CENTER)
		.setForeground( new FormattedText( StringFormatter.DOLLARS.formatString(
				closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.InitialEscrowPaymentAtClosing")),
				Text.TABLE_TEXT));
		borrowerOverlayGrid.getCell(pageTwo.getStart(PageTwo.OTHER_FEES), 0)
		.setHorizontalAlignment(HorizontalAlignment.CENTER)
		.setForeground( new FormattedText( StringFormatter.DOLLARS.formatString(
				closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.OtherCosts")),
				Text.TABLE_TEXT));
		borrowerOverlayGrid.getCell(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES), 0)
		.setHorizontalAlignment(HorizontalAlignment.CENTER)
		.setForeground( new FormattedText( StringFormatter.DOLLARS.formatString(
				closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.TotalOtherCosts")),
				Text.TABLE_TEXT));
		borrowerOverlayGrid.getCell(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+2, 0)
		.setHorizontalAlignment(HorizontalAlignment.CENTER)
		.setForeground( new FormattedText( StringFormatter.DOLLARS.formatString(
				closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.TotalClosingCosts")),
				Text.TABLE_TEXT));;
	}
	
	private void initializeSellerOverlayGrid(InputData inputData) {
		PageTwo pageTwo = inputData.getPageTwo();
		float heights[] = { rowHeight };
		float widths[]  = {pageTwo.getWidthSeller1()+pageTwo.getWidthSeller2()};
		sellerOverlayGrid = new Grid(pageTwo.getOtherCostsGridHeight(), heights, 1, widths);
		sellerOverlayGrid.setLineHorizontalAlignment(0, Dimension.ROW, HorizontalAlignment.CENTER);
		sellerOverlayGrid.setLineShade(0, Dimension.ROW, Color.LIGHT_GRAY);
		sellerOverlayGrid.setCellBorder(0, 0, Position.LEFT, Color.DARK_GRAY, wideRule);
		sellerOverlayGrid.setCellBorder(0, 0, Position.RIGHT, Color.DARK_GRAY, wideRule);
		sellerOverlayGrid.setCellText(0, 0, new FormattedText("Seller-Paid", Text.TABLE_HEADER_LARGE));
		sellerOverlayGrid.setLineBorder(0, Position.TOP, Color.BLACK, narrowRule);
		sellerOverlayGrid.setLineBorder(0, Position.BOTTOM, Color.DARK_GRAY, narrowRule);
	}
	
	
	private void initializeSellerGrid(InputData inputData){
		PageTwo pageTwo = inputData.getPageTwo();
		List<Fees> feeList = inputData.getFeeList();
		List<ID_Subsection> idsList = inputData.getIdsList();
		List<Escrows> escrowList 	= inputData.getEscrowList();
		List<Prepaids> prepaidsList = inputData.getPrepaidList();
		
		float heights[] = { rowHeight };
		float widths[]  = {pageTwo.getWidthSeller1(), pageTwo.getWidthSeller2()};
		sellerGrid = new Grid(pageTwo.getOtherCostsGridHeight(), heights, 2, widths);
		

		sellerGrid.setLineHorizontalAlignment(0, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		sellerGrid.setLineHorizontalAlignment(1, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		sellerGrid.setLineBorder(1, Position.RIGHT, Color.DARK_GRAY, wideRule);
		if (inputData.isSellerOnly()) {
			sellerGrid.setCellBorder(sellerGrid.rows()-4, 1, Position.RIGHT, null, 0);
			sellerGrid.setCellBorder(sellerGrid.rows()-3, 1, Position.RIGHT, null, 0);
			sellerGrid.setCellBorder(sellerGrid.rows()-2, 1, Position.RIGHT, null, 0);
			sellerGrid.setCellBorder(sellerGrid.rows()-1, 1, Position.RIGHT, null, 0);
		}
		
		sellerGrid.getCell(1, 0)
		.setHorizontalAlignment(HorizontalAlignment.CENTER)
		.setForeground( new FormattedText("At Closing", Text.TABLE_TEXT));
		sellerGrid.setCellBorder(1, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
		sellerGrid.getCell(1, 1)
		.setHorizontalAlignment(HorizontalAlignment.CENTER)
		.setForeground( new FormattedText("Before Closing", Text.TABLE_TEXT));
		sellerGrid.setLineShade(1, Dimension.ROW, Color.LIGHT_GRAY);
		//sellerGrid.setCellBorder(0, 1, Position.RIGHT, null);

		sellerGrid.setCellBorder(2, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
		//sellerGrid.setLineBorder(2, Position.TOP, Color.BLACK, narrowRule);
	//	sellerGrid.setLineBorder(1, Position.BOTTOM, Color.BLACK, narrowRule);
		int row = 2;
		while (row < pageTwo.getStart(PageTwo.PREPAIDS) ){
			if (!pageTwo.getDoubleLines(row))sellerGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrowRule);
			sellerGrid.setCellBorder(row++, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
		}
		sellerGrid.setCellBorder(row, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
		sellerGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrowRule);
	//	sellerGrid.setLineBorder(row++, Position.BOTTOM, Color.BLACK, narrowRule);

		while (row < pageTwo.getStart(PageTwo.ESCROWS) ){
			if (!pageTwo.getDoubleLines(row))sellerGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrowRule);
			sellerGrid.setCellBorder(row++, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
		}
		sellerGrid.setCellBorder(row, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
		sellerGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrowRule);
	//	sellerGrid.setLineBorder(row++, Position.BOTTOM, Color.BLACK, narrowRule);

		while ( row < pageTwo.getStart(PageTwo.OTHER_FEES)){
			if (!pageTwo.getDoubleLines(row))sellerGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrowRule);
			sellerGrid.setCellBorder(row++, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
		}
		sellerGrid.setCellBorder(row, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
		sellerGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrowRule);
	//	sellerGrid.setLineBorder(row++, Position.BOTTOM, Color.BLACK, narrowRule);
		while ( row < pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES) ){
			if (!pageTwo.getDoubleLines(row))sellerGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrowRule);
			sellerGrid.setCellBorder(row++, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
		}
		sellerGrid.setCellBorder(row, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
		sellerGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrowRule);
	//	sellerGrid.setLineBorder(row++, Position.BOTTOM, Color.BLACK, narrowRule);
		// lay in data
		row = 0;
		for(Fees feeLocal:feeList){
			if(feeLocal.getPaymentPaidByType().equals("Seller")){
				if(feeLocal.getPlacement(pageTwo.getCurrentSection()) > 0){
					if (feeLocal.isPaidOutsideOfClosingIndicator()){
						sellerGrid.setCellText(feeLocal.getPlacement(pageTwo.getCurrentSection()), 1, new FormattedText(
								StringFormatter.DOLLARS.formatString(feeLocal.getPaymentAmount()),Text.TABLE_TEXT));
					} else {
						sellerGrid.setCellText(feeLocal.getPlacement(pageTwo.getCurrentSection()), 0, new FormattedText(
								StringFormatter.DOLLARS.formatString(feeLocal.getPaymentAmount()),Text.TABLE_TEXT));
					}
				}
			}
		} //end of fee list for loop
			
		for(Escrows escrowLocal:escrowList){
			if(escrowLocal.getPaymentPaidByType().equals("Seller")){
				if(escrowLocal.getPlacement(pageTwo.getCurrentSection()) > 0){
					if (escrowLocal.isPaidOutsideOfClosingIndicator()){
						sellerGrid.setCellText(escrowLocal.getPlacement(pageTwo.getCurrentSection()), 1, new FormattedText(
								StringFormatter.DOLLARS.formatString(escrowLocal.getPaymentAmount()),Text.TABLE_TEXT));
					} else {
						sellerGrid.setCellText(escrowLocal.getPlacement(pageTwo.getCurrentSection()), 0, new FormattedText(
								StringFormatter.DOLLARS.formatString(escrowLocal.getPaymentAmount()),Text.TABLE_TEXT));
					}
				}
			}
		}// end of escrow loop
		
		for(Prepaids prepaidLocal:prepaidsList){
			if(prepaidLocal.getPaymentPaidByType().equals("Seller")){
				if(prepaidLocal.getPlacement(pageTwo.getCurrentSection()) > 0){
					if (prepaidLocal.isPaidOutsideOfClosingIndicator()){
						sellerGrid.setCellText(prepaidLocal.getPlacement(pageTwo.getCurrentSection()), 1, new FormattedText(
								StringFormatter.DOLLARS.formatString(prepaidLocal.getPaymentAmount()),Text.TABLE_TEXT));
					} else {
						sellerGrid.setCellText(prepaidLocal.getPlacement(pageTwo.getCurrentSection()), 0, new FormattedText(
								StringFormatter.DOLLARS.formatString(prepaidLocal.getPaymentAmount()),Text.TABLE_TEXT));
					}
				}
			}
		}// end of prepaids loop
		
		for(ID_Subsection idsLocal:idsList){
			if(idsLocal.getPaymentPaidByType().equals("Seller")){
				if(idsLocal.getPlacement(pageTwo.getCurrentSection()) > 0){
					if (idsLocal.isPaidOutsideOfClosingIndicator()){
						sellerGrid.setCellText(idsLocal.getPlacement(pageTwo.getCurrentSection()), 1, new FormattedText(
								StringFormatter.DOLLARS.formatString(idsLocal.getPaymentAmount()),Text.TABLE_TEXT));
					} else {
						sellerGrid.setCellText(idsLocal.getPlacement(pageTwo.getCurrentSection()), 0, new FormattedText(
								StringFormatter.DOLLARS.formatString(idsLocal.getPaymentAmount()),Text.TABLE_TEXT));
					}
				}
			}
		}// end of id section loop
		if (!inputData.isSellerOnly()) {
			sellerGrid.setCellBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES), 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
			sellerGrid.setCellBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+1, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
			sellerGrid.setCellBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+2, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
			sellerGrid.setCellBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+3, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
			sellerGrid.setCellBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+4, 0, Position.RIGHT, Color.DARK_GRAY, narrowRule);
			
			sellerGrid.setCellBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+2, 1, Position.RIGHT, Color.DARK_GRAY, wideRule);
			
			sellerGrid.setLineBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+2, Position.TOP, Color.BLACK, narrowRule);
			sellerGrid.setLineBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+2, Position.BOTTOM, Color.BLACK, narrowRule);
			sellerGrid.setLineBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+3, Position.BOTTOM, Color.BLACK, narrowRule);
			sellerGrid.setLineBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+4, Position.BOTTOM, Color.BLACK, narrowRule);
		} else {
			for (ID_Subsection idsLocal:idsList) {
				if (idsLocal.getIntegratedDisclosureSubsectionType().equals("TotalClosingCostsSellerOnly")) {
					if (idsLocal.isPaidOutsideOfClosingIndicator()) {
						sellerGrid.setCellText(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES), 1, new FormattedText(
								StringFormatter.DOLLARS.formatString(idsLocal.getPaymentAmount()),Text.TABLE_TEXT));
					} else {
						sellerGrid.setCellText(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES), 0, new FormattedText(
								StringFormatter.DOLLARS.formatString(idsLocal.getPaymentAmount()),Text.TABLE_TEXT));
					}
				}
			}// end of id section loop
		}
	}
	
		private void initializeOtherGrid(InputData inputData){
			PageTwo pageTwo = inputData.getPageTwo();
			List<Fees> feeList = inputData.getFeeList();
			List<ID_Subsection> idsList = inputData.getIdsList();

			List<Escrows> escrowList 	= inputData.getEscrowList();
			List<Prepaids> prepaidsList = inputData.getPrepaidList();
			
			float heights[] = { rowHeight };
			float widths[]  = {pageTwo.getWidthOther()};
			
			otherGrid = new Grid(pageTwo.getOtherCostsGridHeight(), heights, 1, widths);
			otherGrid.setLineHorizontalAlignment(0, Dimension.COLUMN, HorizontalAlignment.RIGHT);
			otherGrid.setLineBorder(1, Position.TOP, Color.BLACK, narrowRule);
		//	otherGrid.setLineBorder(1, Position.BOTTOM, Color.BLACK, narrowRule);
			otherGrid.setCellShade(0, 0, null);
			
			int row = 2;
			while (row < pageTwo.getStart(PageTwo.PREPAIDS) ){
				if (!pageTwo.getDoubleLines(row))otherGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrowRule);
				row++;
			}
			otherGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrowRule);
		//	otherGrid.setLineBorder(row++, Position.BOTTOM, Color.BLACK, narrowRule);

			while (row < pageTwo.getStart(PageTwo.ESCROWS) ){
				if (!pageTwo.getDoubleLines(row))otherGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrowRule);
				row++;
			}
			otherGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrowRule);
		//	otherGrid.setLineBorder(row++, Position.BOTTOM, Color.BLACK, narrowRule);

			while ( row < pageTwo.getStart(PageTwo.OTHER_FEES) ){
				if (!pageTwo.getDoubleLines(row))otherGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrowRule);
				row++;
			}
			otherGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrowRule);
		//	otherGrid.setLineBorder(row++, Position.BOTTOM, Color.BLACK, narrowRule);

			while ( row < pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES) ){
				if (!pageTwo.getDoubleLines(row))otherGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrowRule);
				row++;
			}
			otherGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrowRule);
		//	otherGrid.setLineBorder(row++, Position.BOTTOM, Color.BLACK, narrowRule);
			
			// lay in data
			row = 0;
			String prefix ="";
			for(Fees feeLocal:feeList){
				if(!feeLocal.getPaymentPaidByType().equals("Buyer")
						&& !feeLocal.getPaymentPaidByType().equals("Seller") ){
					if(feeLocal.getPlacement(pageTwo.getCurrentSection()) > 0){
						if (feeLocal.getPaymentPaidByType().equals("Lender")){
							prefix = "(L)";
						} else {
							prefix = "";
						}
						otherGrid.setCellText(feeLocal.getPlacement(pageTwo.getCurrentSection()), 0, new FormattedText(
								prefix + StringFormatter.DOLLARS.formatString(feeLocal.getPaymentAmount()), Text.TABLE_TEXT));
					}
				}
			} //end of fee list for loop

			for(Escrows escrowLocal:escrowList){
				if(!escrowLocal.getPaymentPaidByType().equals("Buyer")
						&& !escrowLocal.getPaymentPaidByType().equals("Seller") ){
					if(escrowLocal.getPlacement(pageTwo.getCurrentSection()) > 0){
						if (escrowLocal.getPaymentPaidByType().equals("Lender")){
							prefix = "(L)";
						} else {
							prefix = "";
						}
						otherGrid.setCellText(escrowLocal.getPlacement(pageTwo.getCurrentSection()), 0, new FormattedText(
								prefix + StringFormatter.DOLLARS.formatString(escrowLocal.getPaymentAmount()), Text.TABLE_TEXT));
					}
				}
			}// end of escrow loop

			for(Prepaids prepaidLocal:prepaidsList){
				if(!prepaidLocal.getPaymentPaidByType().equals("Buyer")
						&& !prepaidLocal.getPaymentPaidByType().equals("Seller") ){
					if(prepaidLocal.getPlacement(pageTwo.getCurrentSection()) > 0){
						if (prepaidLocal.getPaymentPaidByType().equals("Lender")){
							prefix = "(L)";
						} else {
							prefix = "";
						}
						otherGrid.setCellText(prepaidLocal.getPlacement(pageTwo.getCurrentSection()), 0, new FormattedText(
								prefix + StringFormatter.DOLLARS.formatString(prepaidLocal.getPaymentAmount()), Text.TABLE_TEXT));
					}
				}
			}// end of prepaids loop-----------------

			for(ID_Subsection idsLocal:idsList){
				if(!idsLocal.getPaymentPaidByType().equals("Seller")
						&& !idsLocal.getPaymentPaidByType().equals("Buyer")){
					if(idsLocal.getPlacement(pageTwo.getCurrentSection()) > 0){
						if (idsLocal.getPaymentPaidByType().equals("Lender")){
							prefix = "(L)";
						} else {
							prefix = "";
						}
						otherGrid.setCellText(idsLocal.getPlacement(pageTwo.getCurrentSection()), 0, new FormattedText( 
								prefix + StringFormatter.DOLLARS.formatString(idsLocal.getPaymentAmount()), Text.TABLE_TEXT));
					}
				}
			}
			otherGrid.setLineBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+2, Position.TOP, Color.BLACK, narrowRule);
			otherGrid.setLineBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+2, Position.BOTTOM, Color.BLACK, narrowRule);
			otherGrid.setLineBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+3, Position.BOTTOM, Color.BLACK, narrowRule);
			otherGrid.setLineBorder(pageTwo.getStart(PageTwo.TOTAL_OTHER_FEES)+4, Position.BOTTOM, Color.BLACK, narrowRule);
		}
	

	public void draw(Page page, Object data) throws IOException {
	
		ClosingMap closingMap = ((InputData) data).getClosingMap();
		PageTwo pageTwo = ((InputData) data).getPageTwo();
		float rowLocation = page.bottomMargin + rowHeight;
		float columnLocation = page.leftMargin;
		boolean noSeller = false; //((InputData) data).isBorrowerOnly();
		boolean noBuyer  = ((InputData) data).isSellerOnly();
		
		if (closingMap.getClosingMapValue("TERMS_OF_LOAN.LoanPurposeType").equalsIgnoreCase("Refinance")
				&& !closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_DETAIL.IntegratedDisclosureHomeEquityLoanIndicator").equalsIgnoreCase("true")){
			noSeller = true;
		}
		if (noSeller){
			initializeLabelGrid((Page) page,(InputData) data);
			initializeBorrowerGrid((InputData) data);
			initializeBorrowerOverlayGrid((InputData) data);
			if(pageTwo.isExpandedFees()){
				rowLocation = page.height - page.topMargin - labelGrid.height(page);
			}
			labelGrid.draw(  page, columnLocation, rowLocation );
			columnLocation = page.leftMargin+ labelGrid.width(page);
			borrowerGrid.draw(  page, columnLocation, rowLocation );
			borrowerOverlayGrid.draw( page, columnLocation, rowLocation );
			columnLocation += borrowerGrid.width(page);
			initializeOtherGrid((InputData) data);
			otherGrid.draw( page, columnLocation, rowLocation );
		} else if (noBuyer) {
			initializeLabelGrid((Page) page,(InputData) data);			
			
			pageTwo.setWidthLabel1(PageTwo.defaultWidthLabel1 + pageTwo.getWidthBuyer1() + pageTwo.getWidthOther()/2);
			pageTwo.setWidthLabel2(PageTwo.defaultWidthLabel2 + pageTwo.getWidthBuyer2() + pageTwo.getWidthOther()/2);
			if(pageTwo.isExpandedFees()){
				rowLocation = page.height - page.topMargin - labelGrid.height(page);
						}
			
			initializeBorrowerGrid((InputData) data);
			initializeBorrowerOverlayGrid((InputData) data);
			initializeSellerOverlayGrid((InputData) data);
			initializeSellerGrid((InputData) data);			
			labelGrid.draw(  page, columnLocation, rowLocation );
			columnLocation = page.leftMargin+ labelGrid.width(page);
			sellerGrid.draw( page, columnLocation, rowLocation );
			sellerOverlayGrid.draw( page, columnLocation, rowLocation );
			columnLocation += sellerGrid.width(page);
			
		} else {
			initializeLabelGrid((Page) page,(InputData) data);
			initializeBorrowerGrid((InputData) data);
			initializeBorrowerOverlayGrid((InputData) data);
			initializeSellerOverlayGrid((InputData) data);
			initializeSellerGrid((InputData) data);
			if(pageTwo.isExpandedFees()){
				rowLocation = page.height - page.topMargin - labelGrid.height(page);
			}

			labelGrid.draw(  page, columnLocation, rowLocation );
			columnLocation = page.leftMargin+ labelGrid.width(page);
			borrowerGrid.draw(  page, columnLocation, rowLocation );
			borrowerOverlayGrid.draw( page, columnLocation, rowLocation );	
			columnLocation = columnLocation+borrowerGrid.width(page);
			sellerGrid.draw( page, columnLocation, rowLocation );
			sellerOverlayGrid.draw( page, columnLocation, rowLocation );
			columnLocation += sellerGrid.width(page);
			initializeOtherGrid((InputData) data);
			otherGrid.draw( page, columnLocation, rowLocation );
		}
		
		
	}


}
