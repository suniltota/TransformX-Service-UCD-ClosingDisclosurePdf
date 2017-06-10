package com.actualize.mortgage.domainmodels;

import java.io.IOException;
import java.util.List;

import com.actualize.mortgage.datalayer.ClosingMap;
import com.actualize.mortgage.datalayer.Fees;
import com.actualize.mortgage.datalayer.ID_Subsection;
import com.actualize.mortgage.datalayer.InputData;
import com.actualize.mortgage.datalayer.PageTwo;
import com.actualize.mortgage.pdfbuilder.Color;
import com.actualize.mortgage.pdfbuilder.FormattedText;
import com.actualize.mortgage.pdfbuilder.Grid;
import com.actualize.mortgage.pdfbuilder.Page;
import com.actualize.mortgage.pdfbuilder.Section;
import com.actualize.mortgage.pdfbuilder.Text;
import com.actualize.mortgage.pdfbuilder.Grid.Dimension;
import com.actualize.mortgage.pdfbuilder.Grid.Position;
import com.actualize.mortgage.pdfbuilder.TextBox.HorizontalAlignment;
import com.actualize.mortgage.pdfbuilder.TextBox.VerticalAlignment;

public class WriteLoanCosts implements Section {

	//write out all the row label columns 0, 1, 2
	Grid headerGrid, labelGrid, borrowerGrid, borrowerOverlayGrid, sellerOverlayGrid, sellerGrid
	, otherOverlayGrid, otherHeaderGrid, otherGrid;

	float rowHeight  = 10f/72f;
	float narrow 	 = 1f/72f;
	float wide 		 = 2f/72f;

	// the beginning point of each horizontal segment is set on the fly in the code
	// store row numbers for each fee type-much less brittle than just re iterating fees

	private void initializeHeaderGrid(Object data) {
		PageTwo pageTwo = ((InputData) data).getPageTwo();
		float heights[] = { 2*rowHeight };
		float widths[]  = {pageTwo.getWidthPage()};
		headerGrid = new Grid(1, heights, 1, widths);
		headerGrid.setCellText(0, 0, new FormattedText("Closing Cost Details", Text.HEADER_MEDIUM));
		headerGrid.setLineBorder(0, Position.BOTTOM, Color.BLACK, 1f/72f);
	}

	private void initializeLabelGrid(Page page, InputData inputData) throws IOException {
		PageTwo pageTwo = inputData.getPageTwo();
		pageTwo.setCurrentSection("LoanCosts");
		inputData.setPageTwo(pageTwo);
		boolean isSellerOnly = inputData.isSellerOnly();
		
		float heights[] = { rowHeight };
		float widths[] = { 12f/72f, pageTwo.getWidthLabel1(), pageTwo.getWidthLabel2()};

		List<Fees> feeList = inputData.getFeeList();
		List<ID_Subsection> idsList = inputData.getIdsList();

		labelGrid = new Grid(pageTwo.getLoanCostsGridHeight(), heights, 3, widths);
		labelGrid.setLineBorder(2, Position.RIGHT, Color.DARK_GRAY, wide);
		if (isSellerOnly) {
			labelGrid.setCellBorder(labelGrid.rows()-2, 2, Position.RIGHT, null, 0);
			labelGrid.setCellBorder(labelGrid.rows()-1, 2, Position.RIGHT, null, 0);
		}
		labelGrid.getCell(1, 0)
			.setBackground(new Tab())
			.setForeground(	new FormattedText("Loan Costs", Text.SECTION_HEADER));

		//A.01 7.2.1 DATA------------------------------------------------------------------------------------------------------------------------------------------
		//create local row numbers for each section since we will need the beginning points later
		int row0 = pageTwo.getStart(PageTwo.ORIGINATION_CHARGES)+1;
		int row1 = pageTwo.getStart(PageTwo.ORIGINATION_CHARGES)+2;
		int line1 = 2;
		int row2 = pageTwo.getStart(PageTwo.DID_NOT_SHOP_FOR)+1;
		int line2 = 1;
		int row3 = pageTwo.getStart(PageTwo.DID_SHOP_FOR)+1;
		int line3 = 1;
		String str1 = "";
		String str2 = "";
		boolean LoanDiscountPoints = false;
		for (Fees feeLocal : feeList) {
			// 7.2 
		//	System.out.println("Original Charge For: "+feeLocal.getIntegratedDisclosureSectionType());
			if (feeLocal.getIntegratedDisclosureSectionType().equals("OriginationCharges")){
				if (feeLocal.getType().equals("LoanDiscountPoints")) {
					feeLocal.setPlacement(pageTwo.getCurrentSection(), row0);
					str1 =  StringFormatter.PERCENT.formatString(feeLocal.getTotalPercent())+" of Loan Amount (Points)";
					str2 = feeLocal.getPaymentToEntity();
					if (!str2.isEmpty()){
						str2 = " to " + str2;
					}
					LoanDiscountPoints = true;
					System.out.println("Original Charge 1: "+feeLocal.getPaymentToEntity() +" str1: " +str1 +" str2: " +str2);
					row1 = LayoutPageTwo.writeLabel(page, labelGrid, str1, str2, 1, row0, pageTwo);
				} else {
					// 7.3
					if (row1 < pageTwo.getStart(PageTwo.DID_NOT_SHOP_FOR)){
						feeLocal.setPlacement(pageTwo.getCurrentSection(),row1);
						if (!feeLocal.getPaidToType().equals("Lender")) {
							str2 = " to " + feeLocal.getPaymentToEntity();
						} else {
							str2 = "";
						}
						System.out.println("Original Charge 2: "+feeLocal.getLabel());
						row1 = LayoutPageTwo.writeLabel(page, labelGrid, feeLocal.getLabel(), str2, line1++, row1, pageTwo);
					} 
				}
			} else if (feeLocal.getIntegratedDisclosureSectionType().equals("ServicesBorrowerDidNotShopFor")){
				//7.5
				if (row2 < pageTwo.getStart(PageTwo.DID_SHOP_FOR)){
					feeLocal.setPlacement(pageTwo.getCurrentSection(),row2);
					if (!feeLocal.getPaidToType().equals("Lender")) {
						str2 = " to " + feeLocal.getPaymentToEntity();				
					} else {
						str2 = "";
					}
					row2 = LayoutPageTwo.writeLabel(page, labelGrid, feeLocal.getLabel(), str2, line2++, row2, pageTwo);
				}
			} else if (feeLocal.getIntegratedDisclosureSectionType().equals("ServicesBorrowerDidShopFor")){
				//7.6
				if (row3 < pageTwo.getStart(PageTwo.TOTAL_LOAN_COSTS)){
					feeLocal.setPlacement(pageTwo.getCurrentSection(),row3);
					if (!feeLocal.getPaidToType().equals("Lender")) {
						str2 = " to " + feeLocal.getPaymentToEntity();
					} else {
							str2 = "";
					}
					row3 = LayoutPageTwo.writeLabel(page, labelGrid, feeLocal.getLabel(), str2, line3++, row3, pageTwo);
				}
			}
		}//end fee for loop-------------------------------------------------------------------------------------------------------------------------------
		//check to output empty loan discount line if needed
		if(!LoanDiscountPoints){
			str1 =  "% of Loan Amount (Points)";
			str2 = "";
			LayoutPageTwo.writeLabel(page, labelGrid, str1, str2, 1, row0, pageTwo);
		}
		
		String borrowerTag = " (Borrower-Paid)";
		if (isSellerOnly)
			borrowerTag = "";
		
		// Section A
		int row = pageTwo.getStart(PageTwo.ORIGINATION_CHARGES);
		labelGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrow);
		//labelGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrow);
		labelGrid.setLineShade(row, Dimension.ROW, Color.LIGHT_GRAY);	
		labelGrid.setCellText(row, 0, new FormattedText("A. Origination Charges", Text.ROW_HEADER));
		while (row1 < pageTwo.getStart(PageTwo.DID_NOT_SHOP_FOR)){
			labelGrid.setLineBorder(row1, Position.BOTTOM, Color.LIGHT_GRAY, narrow);
			labelGrid.setCellText(row1++, 0, new FormattedText(String.format("%02d", line1++), Text.TABLE_NUMBER));
		}
		
		/*row = pageTwo.getStart(PageTwo.DID_NOT_SHOP_FOR);
		labelGrid.setLineShade(row, Dimension.ROW, Color.LIGHT_GRAY);
		labelGrid.setCellText(row, 0, new FormattedText("B. Services Borrower Did Not Shop For", Text.ROW_HEADER));
		labelGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrow);
		//labelGrid.setLineBorder(row++, Position.BOTTOM, Color.BLACK, narrow);
		while (row2 < pageTwo.getStart(PageTwo.DID_SHOP_FOR)){
			labelGrid.setLineBorder(row2, Position.BOTTOM, Color.LIGHT_GRAY, narrow);
			labelGrid.setCellText(row2++, 0, new FormattedText(String.format("%02d", line2++), Text.TABLE_NUMBER));
		}*/

		// Section B
		row = pageTwo.getStart(PageTwo.DID_NOT_SHOP_FOR);
		labelGrid.setLineShade(row, Dimension.ROW, Color.LIGHT_GRAY);
		labelGrid.setCellText(row, 0, new FormattedText("B. Services Borrower Did Not Shop For", Text.ROW_HEADER));
		labelGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrow);
		//labelGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrow);
		while (row3 < pageTwo.getStart(PageTwo.TOTAL_LOAN_COSTS)){
			labelGrid.setLineBorder(row3, Position.BOTTOM, Color.LIGHT_GRAY, narrow);
			labelGrid.setCellText(row3++, 0, new FormattedText(String.format("%02d", line3++), Text.TABLE_NUMBER));
			labelGrid.getCell(row3, 0).setVerticalAlignment(VerticalAlignment.TOP);
		}

		// Section C
		row = pageTwo.getStart(PageTwo.DID_SHOP_FOR);
		labelGrid.setLineShade(row, Dimension.ROW, Color.LIGHT_GRAY);
		labelGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrow);
		//labelGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrow);
		labelGrid.setCellText(row, 0, new FormattedText("C. Services Borrower Did Shop For", Text.ROW_HEADER));
		
		// Section D
		if (!isSellerOnly) {
			row = pageTwo.getStart(PageTwo.TOTAL_LOAN_COSTS);
			labelGrid.setLineShade(row, Dimension.ROW, Color.LIGHT_GRAY);
			labelGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrow);
			//labelGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrow);
			labelGrid.setCellText(row++, 0, new FormattedText("D. TOTAL LOAN COSTS" + borrowerTag, Text.ROW_HEADER));
			labelGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrow);
			labelGrid.setCellText(row, 0, new FormattedText("Loan Costs Subtotals (A + B + C)", Text.TABLE_TEXT));
			for(ID_Subsection idsLocal:idsList){
				if(idsLocal.getIntegratedDisclosureSubsectionType().equals("LoanCostsSubtotal")){
					idsLocal.setPlacement(pageTwo.getCurrentSection(), row);
				}
			}
		}
	}

	private void initializeBorrowerGrid(InputData inputData){
		PageTwo pageTwo = inputData.getPageTwo();
		List<Fees> feeList = inputData.getFeeList();
		List<ID_Subsection> idsList = inputData.getIdsList();
		float heights[] = { rowHeight };
		float widths[]  = {pageTwo.getWidthBuyer1(), pageTwo.getWidthBuyer2()};
		borrowerGrid = new Grid(pageTwo.getLoanCostsGridHeight(), heights, 2, widths);

		borrowerGrid.setLineHorizontalAlignment(0, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		borrowerGrid.setLineHorizontalAlignment(1, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		borrowerGrid.setLineBorder(1, Position.RIGHT, Color.DARK_GRAY, wide);
		borrowerGrid.setCellShade(1, 0, null);

		//Header 1
		borrowerGrid.getCell(1, 0)
		.setHorizontalAlignment(HorizontalAlignment.CENTER)
		.setForeground( new FormattedText("At Closing", Text.TABLE_TEXT));
		borrowerGrid.getCell(1, 1)
		.setHorizontalAlignment(HorizontalAlignment.CENTER)
		.setForeground( new FormattedText("Before Closing", Text.TABLE_TEXT));
		borrowerGrid.setLineShade(1, Dimension.ROW, Color.LIGHT_GRAY);
		//borrowerGrid.setLineBorder(1, Position.BOTTOM, Color.BLACK, narrow);
		borrowerGrid.setCellBorder(1, 0, Position.LEFT, Color.DARK_GRAY, wide);
		//lines
		//borrowerGrid.setLineBorder(2, Position.BOTTOM, Color.BLACK, narrow);
		borrowerGrid.setLineShade(2, Dimension.ROW, Color.LIGHT_GRAY);
		int row = pageTwo.getStart(PageTwo.ORIGINATION_CHARGES) + 1;
		while (row < pageTwo.getStart(PageTwo.DID_NOT_SHOP_FOR) ){
			if (!pageTwo.getDoubleLines(row))borrowerGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrow);
			borrowerGrid.setCellBorder(row++, 0, Position.RIGHT, Color.DARK_GRAY, narrow);
		}
		borrowerGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrow);
		//borrowerGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrow);
		borrowerGrid.setLineShade(row++, Dimension.ROW, Color.LIGHT_GRAY);

		while (row < pageTwo.getStart(PageTwo.DID_SHOP_FOR) ){
			if (!pageTwo.getDoubleLines(row))borrowerGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrow);
			borrowerGrid.setCellBorder(row++, 0, Position.RIGHT, Color.DARK_GRAY, narrow);
		}
		borrowerGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrow);
		//borrowerGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrow);
		borrowerGrid.setLineShade(row++, Dimension.ROW, Color.LIGHT_GRAY);

		while ( row < pageTwo.getStart(PageTwo.TOTAL_LOAN_COSTS) ){
			if (!pageTwo.getDoubleLines(row))borrowerGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrow);
			borrowerGrid.setCellBorder(row++, 0, Position.RIGHT, Color.DARK_GRAY, narrow);
		}
		borrowerGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrow);
		//borrowerGrid.setLineBorder(row, Position.BOTTOM, Color.BLACK, narrow);
		borrowerGrid.setLineShade(row, Dimension.ROW, Color.LIGHT_GRAY);
		// lay in data
		for (Fees feeLocal : feeList) {
			//only look at borrower(Buyer) paid
			if (feeLocal.getPaymentPaidByType().equals("Buyer")){
				if (feeLocal.getPlacement(pageTwo.getCurrentSection()) > 0) {
					if (!feeLocal.isPaidOutsideOfClosingIndicator()) {
						borrowerGrid.setCellText(feeLocal.getPlacement(pageTwo.getCurrentSection()), 0, new FormattedText( 
								StringFormatter.DOLLARS.formatString(feeLocal.getPaymentAmount()), Text.TABLE_TEXT));
					} else {
						borrowerGrid.setCellText(feeLocal.getPlacement(pageTwo.getCurrentSection()), 1, new FormattedText( 
								StringFormatter.DOLLARS.formatString(feeLocal.getPaymentAmount()), Text.TABLE_TEXT));
					}
				}	
			}
		}//end fee for loop-------------------------------------------------------------------------------------------------------------------------------
		for(ID_Subsection idsLocal:idsList){
			if(idsLocal.getPaymentPaidByType().equals("Buyer")){
				if (idsLocal.getPlacement(pageTwo.getCurrentSection()) > 0){
					if(idsLocal.isPaidOutsideOfClosingIndicator()){
						borrowerGrid.setCellText(idsLocal.getPlacement(pageTwo.getCurrentSection()), 1, new FormattedText( 
								StringFormatter.DOLLARS.formatString(idsLocal.getPaymentAmount()), Text.TABLE_TEXT));
					} else {
						borrowerGrid.setCellText(idsLocal.getPlacement(pageTwo.getCurrentSection()), 0, new FormattedText( 
								StringFormatter.DOLLARS.formatString(idsLocal.getPaymentAmount()), Text.TABLE_TEXT));
					}
				}
			}
		}
		borrowerGrid.setCellBorder(pageTwo.getLoanCostsGridHeight()-1, 0, Position.RIGHT, Color.DARK_GRAY, narrow);
		borrowerGrid.setLineBorder(pageTwo.getLoanCostsGridHeight()-1, Position.BOTTOM, Color.BLACK, narrow);
	}

	private void initializeOverlayGrid(InputData inputData) {
		PageTwo pageTwo = inputData.getPageTwo();
		float heights[] = { rowHeight };
		float width = pageTwo.getWidthBuyer1() + pageTwo.getWidthBuyer2();
		float widths[] = { width };

		ClosingMap closingMap = inputData.getClosingMap();

		borrowerOverlayGrid = new Grid(pageTwo.getLoanCostsGridHeight(), heights, 1, widths);	

		borrowerOverlayGrid.setLineHorizontalAlignment(0, Dimension.ROW, HorizontalAlignment.CENTER);
		borrowerOverlayGrid.setLineShade(0, Dimension.ROW, Color.LIGHT_GRAY);
		borrowerOverlayGrid.setCellBorder(0, 0, Position.LEFT, Color.DARK_GRAY, wide);
		borrowerOverlayGrid.setCellBorder(0, 0, Position.RIGHT, Color.DARK_GRAY, wide);
		borrowerOverlayGrid.setCellText(0, 0, new FormattedText("Borrower-Paid", Text.TABLE_HEADER_LARGE));
		borrowerOverlayGrid.setLineBorder(0, Position.TOP, Color.BLACK, wide);
		borrowerOverlayGrid.setLineBorder(0, Position.BOTTOM, Color.DARK_GRAY, narrow);

		// A.T1 
		borrowerOverlayGrid.getCell(pageTwo.getStart(PageTwo.ORIGINATION_CHARGES), 0)
		.setHorizontalAlignment(HorizontalAlignment.CENTER)
		.setForeground( new FormattedText( StringFormatter.DOLLARS.formatString(
				closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.OriginationCharges")),
				Text.TABLE_TEXT));

		borrowerOverlayGrid.getCell(pageTwo.getStart(PageTwo.DID_NOT_SHOP_FOR), 0)
		.setHorizontalAlignment(HorizontalAlignment.CENTER)
		.setForeground( new FormattedText(StringFormatter.DOLLARS.formatString(
				closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.ServicesBorrowerDidNotShopFor")),
				Text.TABLE_TEXT));

		borrowerOverlayGrid.getCell(pageTwo.getStart(PageTwo.DID_SHOP_FOR), 0)
		.setHorizontalAlignment(HorizontalAlignment.CENTER)
		.setForeground( new FormattedText( StringFormatter.DOLLARS.formatString(
				closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.ServicesBorrowerDidShopFor")),
				Text.TABLE_TEXT));

		borrowerOverlayGrid.getCell(pageTwo.getStart(PageTwo.TOTAL_LOAN_COSTS), 0)
		.setHorizontalAlignment(HorizontalAlignment.CENTER)
		.setForeground( new FormattedText( StringFormatter.DOLLARS.formatString(
				closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.TotalLoanCosts")),
				Text.TABLE_TEXT));
	}

	private void initializeSellerOverlayGrid(InputData inputData) {
		PageTwo pageTwo = inputData.getPageTwo();
		float heights[] = { rowHeight };
		float widths[]  = {pageTwo.getWidthSeller1()+pageTwo.getWidthSeller2()};
		sellerOverlayGrid = new Grid(pageTwo.getLoanCostsGridHeight(), heights, 1, widths);
		sellerOverlayGrid.setLineHorizontalAlignment(0, Dimension.ROW, HorizontalAlignment.CENTER);
		sellerOverlayGrid.setLineShade(0, Dimension.ROW, Color.LIGHT_GRAY);
		sellerOverlayGrid.setCellBorder(0, 0, Position.LEFT, Color.DARK_GRAY, wide);
		sellerOverlayGrid.setCellBorder(0, 0, Position.RIGHT, Color.DARK_GRAY, wide);
		sellerOverlayGrid.setCellText(0, 0, new FormattedText("Seller-Paid", Text.TABLE_HEADER_LARGE));
		sellerOverlayGrid.setLineBorder(0, Position.TOP, Color.BLACK, narrow);
		sellerOverlayGrid.setLineBorder(0, Position.BOTTOM, Color.DARK_GRAY, narrow);
	}

	private void initializeSellerGrid(InputData inputData){
		PageTwo pageTwo = inputData.getPageTwo();
		List<Fees> feeList = inputData.getFeeList();
		List<ID_Subsection> idsList = inputData.getIdsList();
		float heights[] = { rowHeight };
		float widths[]  = {pageTwo.getWidthSeller1(), pageTwo.getWidthSeller2()};
		sellerGrid = new Grid(pageTwo.getLoanCostsGridHeight(), heights, 2, widths);

		sellerGrid.setLineHorizontalAlignment(0, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		sellerGrid.setLineHorizontalAlignment(1, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		sellerGrid.setLineBorder(1, Position.RIGHT, Color.DARK_GRAY, wide);
		if (inputData.isSellerOnly()) {
			sellerGrid.setCellBorder(labelGrid.rows()-2, 1, Position.RIGHT, null, 0);
			sellerGrid.setCellBorder(labelGrid.rows()-1, 1, Position.RIGHT, null, 0);
		}

		//Header 1
		sellerGrid.getCell(1, 0)
		.setHorizontalAlignment(HorizontalAlignment.CENTER)
		.setForeground( new FormattedText("At Closing", Text.TABLE_TEXT));
		sellerGrid.setCellBorder(1, 0, Position.RIGHT, Color.DARK_GRAY, narrow);
		sellerGrid.getCell(1, 1)
		.setHorizontalAlignment(HorizontalAlignment.CENTER)
		.setForeground( new FormattedText("Before Closing", Text.TABLE_TEXT));
		sellerGrid.setLineShade(1, Dimension.ROW, Color.LIGHT_GRAY);
		//sellerGrid.setLineBorder(1, Position.BOTTOM, Color.BLACK, narrow);

		sellerGrid.setCellBorder(2, 0, Position.RIGHT, Color.DARK_GRAY, narrow);
		//sellerGrid.setLineBorder(2, Position.BOTTOM, Color.BLACK, narrow);
		int row = pageTwo.getStart(PageTwo.ORIGINATION_CHARGES);
		while (row < pageTwo.getStart(PageTwo.DID_NOT_SHOP_FOR) ){
			if (!pageTwo.getDoubleLines(row))sellerGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrow);
			sellerGrid.setCellBorder(row++, 0, Position.RIGHT, Color.DARK_GRAY, narrow);
		}
		sellerGrid.setCellBorder(row, 0, Position.RIGHT, Color.DARK_GRAY, narrow);
		sellerGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrow);
		//sellerGrid.setLineBorder(row++, Position.BOTTOM, Color.BLACK, narrow);

		while (row < pageTwo.getStart(PageTwo.DID_SHOP_FOR) ){
			if (!pageTwo.getDoubleLines(row))sellerGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrow);
			sellerGrid.setCellBorder(row++, 0, Position.RIGHT, Color.DARK_GRAY, narrow);
		}
		sellerGrid.setCellBorder(row, 0, Position.RIGHT, Color.DARK_GRAY, narrow);
		sellerGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrow);
		//sellerGrid.setLineBorder(row++, Position.BOTTOM, Color.BLACK, narrow);

		while (row < pageTwo.getStart(PageTwo.TOTAL_LOAN_COSTS) ){
			if (!pageTwo.getDoubleLines(row))sellerGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrow);
			sellerGrid.setCellBorder(row++, 0, Position.RIGHT, Color.DARK_GRAY, narrow);
		}
		if (!inputData.isSellerOnly()) {
			sellerGrid.setCellBorder(row, 0, Position.RIGHT, Color.DARK_GRAY, narrow);
			sellerGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrow);
			//sellerGrid.setLineBorder(row++, Position.BOTTOM, Color.BLACK, narrow);
		}

		// lay in data
		for (Fees feeLocal : feeList) {
			if (feeLocal.getPaymentPaidByType().equals("Seller")){
				// 7.2
				if (feeLocal.getPlacement(pageTwo.getCurrentSection()) > 0){	
					if (!feeLocal.isPaidOutsideOfClosingIndicator()) {
						sellerGrid.setCellText(feeLocal.getPlacement(pageTwo.getCurrentSection()), 0, new FormattedText( 
								StringFormatter.DOLLARS.formatString(feeLocal.getPaymentAmount()), Text.TABLE_TEXT));
					} else {
						sellerGrid.setCellText(feeLocal.getPlacement(pageTwo.getCurrentSection()), 1, new FormattedText( 
								StringFormatter.DOLLARS.formatString(feeLocal.getPaymentAmount()), Text.TABLE_TEXT));
					}
				} 
			}
		}//end fee for loop-------------------------------------------------------------------------------------------------------------------------------

		for(ID_Subsection idsLocal:idsList){
			if(idsLocal.getPaymentPaidByType().equals("Seller")){
				if (idsLocal.getPlacement(pageTwo.getCurrentSection()) > 0){
					if(idsLocal.isPaidOutsideOfClosingIndicator()){
						sellerGrid.setCellText(idsLocal.getPlacement(pageTwo.getCurrentSection()), 1, new FormattedText( 
								StringFormatter.DOLLARS.formatString(idsLocal.getPaymentAmount()), Text.TABLE_TEXT));
					} else {
						sellerGrid.setCellText(idsLocal.getPlacement(pageTwo.getCurrentSection()), 0, new FormattedText( 
								StringFormatter.DOLLARS.formatString(idsLocal.getPaymentAmount()), Text.TABLE_TEXT));
					}
				}
			}
		}
		
		if (!inputData.isSellerOnly()) {
			sellerGrid.setCellBorder(pageTwo.getLoanCostsGridHeight()-2, 0, Position.RIGHT, Color.DARK_GRAY, narrow);
			sellerGrid.setCellBorder(pageTwo.getLoanCostsGridHeight()-1, 0, Position.RIGHT, Color.DARK_GRAY, narrow);
			sellerGrid.setCellBorder(pageTwo.getLoanCostsGridHeight()-1, 1, Position.RIGHT, Color.DARK_GRAY, wide);
			sellerGrid.setLineBorder(pageTwo.getLoanCostsGridHeight()-1, Position.BOTTOM, Color.BLACK, narrow);
		}
	}

	private void initializeOtherGrid(InputData inputData){
		PageTwo pageTwo = inputData.getPageTwo();
		List<Fees> feeList = inputData.getFeeList();
		List<ID_Subsection> idsList = inputData.getIdsList();
		float heights[] = { rowHeight };
		float widths[]  = { pageTwo.getWidthOther() };
		otherGrid = new Grid(pageTwo.getLoanCostsGridHeight(), heights, 1, widths);
		otherGrid.setLineHorizontalAlignment(0, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		//Header 0
		otherGrid.setLineHorizontalAlignment(0, Dimension.ROW, HorizontalAlignment.CENTER);
		otherGrid.setLineShade(0, Dimension.ROW, Color.LIGHT_GRAY);
		otherGrid.setCellBorder(0, 0, Position.LEFT, Color.DARK_GRAY, wide);
		otherGrid.setCellText(0, 0, new FormattedText("Paid By", Text.TABLE_HEADER_LARGE));
		otherGrid.setLineBorder(0, Position.TOP, Color.BLACK, wide);
		otherGrid.setLineBorder(0, Position.BOTTOM, Color.DARK_GRAY, narrow);
		//Header 1
		otherGrid.getCell(1, 0)
		.setHorizontalAlignment(HorizontalAlignment.CENTER)
		.setForeground( new FormattedText("Others", Text.TABLE_HEADER_LARGE));

		otherGrid.setLineShade(1, Dimension.ROW, Color.LIGHT_GRAY);
		otherGrid.setLineBorder(1, Position.BOTTOM, Color.BLACK, narrow);
		otherGrid.setLineBorder(2, Position.BOTTOM, Color.BLACK, narrow);

		int row = pageTwo.getStart(PageTwo.ORIGINATION_CHARGES);
		while (row < pageTwo.getStart(PageTwo.DID_NOT_SHOP_FOR) ){
			if (!pageTwo.getDoubleLines(row))otherGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrow);
			row++;
		}
		otherGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrow);
		otherGrid.setLineBorder(row++, Position.BOTTOM, Color.BLACK, narrow);

		while (row < pageTwo.getStart(PageTwo.DID_SHOP_FOR) ){
			if (!pageTwo.getDoubleLines(row))otherGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrow);
			row++;
		}
		otherGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrow);
		otherGrid.setLineBorder(row++, Position.BOTTOM, Color.BLACK, narrow);

		while ( row < pageTwo.getStart(PageTwo.TOTAL_LOAN_COSTS) ){
			if (!pageTwo.getDoubleLines(row))otherGrid.setLineBorder(row, Position.BOTTOM, Color.LIGHT_GRAY, narrow);
			row++;
		}
		otherGrid.setLineBorder(row, Position.TOP, Color.BLACK, narrow);
		otherGrid.setLineBorder(row++, Position.BOTTOM, Color.BLACK, narrow);
		// lay in data

		String prefix = "";			
		for (Fees feeLocal : feeList) {
			//only look at borrower(Buyer) paid
			if (!feeLocal.getPaymentPaidByType().equals("Seller")
					&& !feeLocal.getPaymentPaidByType().equals("Buyer")){
				if (feeLocal.getPlacement(pageTwo.getCurrentSection()) > 0){
					if (feeLocal.getPaymentPaidByType().equals("Lender")){
						prefix = "(L)";
					} else {
						prefix = "";
					}
					otherGrid.setCellText(feeLocal.getPlacement(pageTwo.getCurrentSection()), 0, new FormattedText( 
							prefix + StringFormatter.DOLLARS.formatString(feeLocal.getPaymentAmount()), Text.TABLE_TEXT));
				} 
			}
		}//end fee for loop-------------------------------------------------------------------------------------------------------------------------------

		for(ID_Subsection idsLocal:idsList){
			if(!idsLocal.getPaymentPaidByType().equals("Seller")
					&& !idsLocal.getPaymentPaidByType().equals("Buyer")){
				if(idsLocal.getPlacement(pageTwo.getCurrentSection()) > 0){
					if (idsLocal.getPaymentPaidByType().equals("Lender")){
						prefix = "(L)";
					} else {
						prefix = "";
					}
					otherGrid.setCellText(idsLocal.getPlacement(pageTwo.getCurrentSection()) , 0, new FormattedText( 
							prefix + StringFormatter.DOLLARS.formatString(idsLocal.getPaymentAmount()), Text.TABLE_TEXT));
				}
			}
		}
		otherGrid.setLineBorder(pageTwo.getLoanCostsGridHeight()-1, Position.BOTTOM, Color.BLACK, narrow);
	}

	public void draw(Page page, Object data) throws IOException {
		PageTwo pageTwo = ((InputData) data).getPageTwo();
		ClosingMap closingMap = ((InputData) data).getClosingMap();
		initializeHeaderGrid(data);
		boolean noSeller = false; //((InputData) data).isBorrowerOnly();
		boolean noBuyer  = ((InputData) data).isSellerOnly();
		
		float rowLocation = page.height - page.topMargin - headerGrid.height(page) +rowHeight;
		float columnLocation = page.leftMargin;
		headerGrid.draw(page, columnLocation , rowLocation);
		
		if (closingMap.getClosingMapValue("TERMS_OF_LOAN.LoanPurposeType").equalsIgnoreCase("Refinance")
				&& !closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_DETAIL.IntegratedDisclosureHomeEquityLoanIndicator").equalsIgnoreCase("true")){
			noSeller = true;
		}
		
		if (noSeller) {
			System.out.println("noSeller: ");
			pageTwo.setWidthLabel1(pageTwo.getWidthLabel1()+pageTwo.getWidthSeller1());
			pageTwo.setWidthLabel2(pageTwo.getWidthLabel2()+pageTwo.getWidthSeller2());
			((InputData) data).setPageTwo(pageTwo);
			initializeLabelGrid(page, (InputData) data);
			initializeBorrowerGrid((InputData) data);
			initializeOverlayGrid((InputData) data);
			rowLocation -= labelGrid.height(page);
			labelGrid.draw(  page, columnLocation, rowLocation );
			columnLocation = page.leftMargin+ labelGrid.width(page);
			borrowerGrid.draw(  page, columnLocation, rowLocation );
			borrowerOverlayGrid.draw( page, columnLocation, rowLocation );
			columnLocation += borrowerGrid.width(page);
			initializeOtherGrid((InputData) data);
			otherGrid.draw( page, columnLocation, rowLocation );		
		} else if (noBuyer) {
			System.out.println("noBuyer: ");
			pageTwo.setWidthLabel1(PageTwo.defaultWidthLabel1 + pageTwo.getWidthBuyer1() + pageTwo.getWidthOther()/2);
			pageTwo.setWidthLabel2(PageTwo.defaultWidthLabel2 + pageTwo.getWidthBuyer2() + pageTwo.getWidthOther()/2);
			initializeLabelGrid(page, (InputData) data);
			initializeBorrowerGrid((InputData) data);
			initializeOverlayGrid((InputData) data);
			initializeSellerOverlayGrid((InputData) data);
			initializeSellerGrid((InputData) data);
			rowLocation -= labelGrid.height(page);
			labelGrid.draw(  page, columnLocation, rowLocation );
			columnLocation = page.leftMargin+ labelGrid.width(page);
			sellerGrid.draw( page, columnLocation, rowLocation );
			sellerOverlayGrid.draw( page, columnLocation, rowLocation );
			columnLocation += sellerGrid.width(page);
		} else {
			System.out.println("Else");
			initializeLabelGrid(page, (InputData) data);
			initializeBorrowerGrid((InputData) data);
			initializeOverlayGrid((InputData) data);
			initializeSellerOverlayGrid((InputData) data);
			initializeSellerGrid((InputData) data);
			rowLocation -= labelGrid.height(page);
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
