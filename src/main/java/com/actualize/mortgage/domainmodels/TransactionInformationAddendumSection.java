package com.actualize.mortgage.domainmodels;

import java.io.IOException;
import java.util.Iterator;

import com.actualize.mortgage.datalayer.InputData;
import com.actualize.mortgage.datalayer.PartyData;
import com.actualize.mortgage.datalayer.PartyRoleTypes;
import com.actualize.mortgage.pdfbuilder.Color;
import com.actualize.mortgage.pdfbuilder.FormattedText;
import com.actualize.mortgage.pdfbuilder.Grid;
import com.actualize.mortgage.pdfbuilder.Page;
import com.actualize.mortgage.pdfbuilder.Section;
import com.actualize.mortgage.pdfbuilder.Text;
import com.actualize.mortgage.pdfbuilder.Typeface;
import com.actualize.mortgage.pdfbuilder.Grid.Position;
import com.actualize.mortgage.pdfbuilder.TextBox.Direction;
import com.actualize.mortgage.pdfbuilder.TextBox.VerticalAlignment;

public class TransactionInformationAddendumSection implements Section {
	public static final Text TAB               = new Text(Color.WHITE, 11, Typeface.CALIBRI_BOLD);

	private Grid titleGrid, borrowerGrid, sellerGrid;
	private InputData data;
	public float rowHeight = 12f/72f;
	private static final float leftIndent  = 2f/72f;

	public static Boolean IsSectionRequired(InputData data) {
		return PartyRelationshipHelper.borrowerAddendumCount(data) > 0 || PartyRelationshipHelper.sellerAddendumCount(data) > 0;
	}
	
	private void initializeTitleGrid() {
		float heights[] = { rowHeight };
		float widths[] = { 7.5f };
		titleGrid = new Grid(1, heights, 1, widths);
		titleGrid.getCell(0, 0)
			.setBackground(new Tab(3f))
			.setMargin(Direction.LEFT, leftIndent)
			.setForeground(new FormattedText("Transaction Information Addendum", TAB));
		titleGrid.setLineBorder(0, Position.BOTTOM, Color.BLACK);
	}
	
	private Grid initializeBorrowerGrid(int borrowerCount) {
		float heights[] = { rowHeight };
		float widths[] = {0.2f, 3.5f, 0.3f, 2.5f, 1.0f };
		int gridHeight = 3*borrowerCount + 1;
		Grid grid = new Grid(gridHeight, heights, 5, widths);	
		
		grid.getCell(0,0).setForeground(new FormattedText("Additional Borrowers", Text.TABLE_HEADER));
		
		Iterator<PartyData> iterParty = data.getPartyList().iterator();

		// TODO : ensure unit included in address - add to party data - also in contact information section.
		
		// Write left column
		int currentCount = 0;
		int pageOneCount = 0;
		int lineNumber = 1;
		int currentPrintNumber = 1;
		String borrowerAddress = "";
		String borrowerCityState = "";
		while (iterParty.hasNext()) {
			PartyData thisContact = iterParty.next();
			if (thisContact == null)
				continue;
			if (thisContact.getRoleType().equalsIgnoreCase(PartyRoleTypes.Borrower)) {
				String borrowerTmp1 = StringFormatter.STRINGCLEAN.formatString(thisContact.getAddressLine());
				String borrowerTmp2 = StringFormatter.STRINGCLEAN.formatString(thisContact.getAddressSecondLine());
				if (currentCount == 0) {
					currentCount = 1;
					pageOneCount = 1;
					borrowerAddress = borrowerTmp1;
					borrowerCityState = borrowerTmp2;
				} else if (pageOneCount > 1 || ((!borrowerAddress.equals(borrowerTmp1) || !borrowerCityState.equals(borrowerTmp2))
						 && (!borrowerTmp1.equals("") || !borrowerTmp2.equals("")))) {
					currentCount++;
					String ContactLine1 = (thisContact != null) ? thisContact.getPartyName() : "";

					grid.setCellText(currentPrintNumber*3-2, 0, new FormattedText(String.format("%02d", lineNumber++), Text.TABLE_NUMBER));
					grid.setCellText(currentPrintNumber*3-2, 1, new FormattedText(ContactLine1, Text.TABLE_TEXT));

					grid.setCellBorder(currentPrintNumber*3-2, 0, Position.BOTTOM, Color.MEDIUM_GRAY);
					grid.setCellBorder(currentPrintNumber*3-2, 1, Position.BOTTOM, Color.MEDIUM_GRAY);
					
					// Don't print borrower signature column if seller only
					if (!data.isSellerOnly()) {
						grid.setCellBorder(currentPrintNumber*3-2, 3, Position.BOTTOM, Color.MEDIUM_GRAY);
						grid.getCell(currentPrintNumber*3-1, 3)
							.setVerticalAlignment(VerticalAlignment.TOP)
							.setForeground(new FormattedText("Signature",Text.TABLE_TEXT));
						grid.setCellBorder(currentPrintNumber*3-2, 4, Position.BOTTOM, Color.MEDIUM_GRAY);
						grid.getCell(currentPrintNumber*3-1, 4)
							.setVerticalAlignment(VerticalAlignment.TOP)
							.setForeground(new FormattedText("Date",Text.TABLE_TEXT));
					}

					String ContactLine2 = "";
					String ContactLine3 = "";
					if (thisContact.getAddressType().equalsIgnoreCase("Mailing")) {
						ContactLine2 =  thisContact.getAddressLine();
						if (!thisContact.getAddressUnitDesignatorType().isEmpty()){
							ContactLine2 += " " + thisContact.getAddressUnitDesignatorType();
						}
						if (!thisContact.getAddressUnit().isEmpty()){
							ContactLine2 += " " + thisContact.getAddressUnit();
						}
						ContactLine3 = thisContact.getAddressSecondLine();
						grid.getCell(currentPrintNumber*3-1,1).setForeground(new FormattedText(ContactLine2, Text.TABLE_TEXT));
						grid.getCell(currentPrintNumber*3,1).setForeground(new FormattedText(ContactLine3, Text.TABLE_TEXT));
					}
					grid.setCellBorder(currentPrintNumber*3-1, 0, Position.BOTTOM, Color.MEDIUM_GRAY);
					grid.setCellBorder(currentPrintNumber*3-1, 1, Position.BOTTOM, Color.MEDIUM_GRAY);
					grid.setCellBorder(currentPrintNumber*3, 0, Position.BOTTOM, Color.MEDIUM_GRAY);
					grid.setCellBorder(currentPrintNumber*3, 1, Position.BOTTOM, Color.MEDIUM_GRAY);
					currentPrintNumber++;
				} else
					++pageOneCount;
			}
		}
		return grid;
	}
	private Grid initializeSellerGrid(int sellerCount) {
		float heights[] = { rowHeight };
		float widths[] = {0.2f, 3.5f, 0.3f, 0.2f, 3.3f };
		int gridHeight = 3*sellerCount + 1;;
		Grid grid = new Grid(gridHeight, heights, 5, widths);	
		
		grid.getCell(0,0).setForeground(new FormattedText("Additional Sellers", Text.TABLE_HEADER));
		
		Iterator<PartyData> iterParty = data.getPartyList().iterator();

		// TODO : ensure unit included in address - add to party data - also in contact information section.
		int currentCount = 0;
		int pageOneCount = 0;
		int currentPrintNumber = 1;
		int lineNumber = 1;
		int column = 0;
		String sellerAddress = "";
		String sellerCityState = "";
		while (iterParty.hasNext()) {
			PartyData thisContact = iterParty.next();
			if (thisContact == null)
				continue;
			if (thisContact.getRoleType().equalsIgnoreCase(PartyRoleTypes.Seller)) {
				String sellerTmp1 = StringFormatter.STRINGCLEAN.formatString(thisContact.getAddressLine());
				String sellerTmp2 = StringFormatter.STRINGCLEAN.formatString(thisContact.getAddressSecondLine());
				if (currentCount == 0) {
					currentCount = 1;
					pageOneCount = 1;
					sellerAddress = sellerTmp1;
					sellerCityState = sellerTmp2;
				} else if (pageOneCount > 1 || ((!sellerAddress.equals(sellerTmp1) || !sellerCityState.equals(sellerTmp2))
						&& (!sellerTmp1.equals("") || !sellerTmp2.equals("")))) {
					currentCount++;
					String ContactLine1 = thisContact.getPartyName();
					grid.setCellText(currentPrintNumber*3-2, column, new FormattedText(String.format("%02d", lineNumber++), Text.TABLE_NUMBER));
					grid.setCellBorder(currentPrintNumber*3-2, column, Position.BOTTOM, Color.MEDIUM_GRAY);
					grid.setCellText(currentPrintNumber*3-2, column+1, new FormattedText(ContactLine1, Text.TABLE_TEXT));
					grid.setCellBorder(currentPrintNumber*3-2, column+1, Position.BOTTOM, Color.MEDIUM_GRAY);
					String ContactLine2 = "";
					String ContactLine3 = "";
					if(thisContact.getAddressType().equalsIgnoreCase("Mailing")) {
						ContactLine2 =  thisContact.getAddressLine();
						if (!thisContact.getAddressUnitDesignatorType().isEmpty()){
							ContactLine2 += " " + thisContact.getAddressUnitDesignatorType();
						}
						if (!thisContact.getAddressUnit().isEmpty()){
							ContactLine2 += " " + thisContact.getAddressUnit();
						}
						ContactLine3 = thisContact.getAddressSecondLine();
						grid.getCell(currentPrintNumber*3-1,column+1).setForeground(new FormattedText(ContactLine2, Text.TABLE_TEXT));
						grid.getCell(currentPrintNumber*3,column+1).setForeground(new FormattedText(ContactLine3, Text.TABLE_TEXT));
					}
					grid.setCellBorder(currentPrintNumber*3-1, column, Position.BOTTOM, Color.MEDIUM_GRAY);
					grid.setCellBorder(currentPrintNumber*3-1, column+1, Position.BOTTOM, Color.MEDIUM_GRAY);
					grid.setCellBorder(currentPrintNumber*3, column, Position.BOTTOM, Color.MEDIUM_GRAY);
					grid.setCellBorder(currentPrintNumber*3, column+1, Position.BOTTOM, Color.MEDIUM_GRAY);
					currentPrintNumber++;
				} else
					++pageOneCount;
			}
		}
		return grid;
	}

	public void draw(Page page, Object d) throws IOException {
		data = (InputData)d;
		float thisLocation = page.height - page.topMargin - rowHeight;
		initializeTitleGrid();
		titleGrid.draw(page, page.leftMargin, page.height - page.topMargin - rowHeight);
		
		// Add additional borrowers
		if (PartyRelationshipHelper.borrowerAddendumCount(data) > 0) {
		    //add the receipt confirmation section but not for DD
		    if (!data.getClosingMap().getClosingMapValue("ABOUT_VERSION.AboutVersionIdentifier").equalsIgnoreCase("DDOFileNumber")){
		        thisLocation = thisLocation - titleGrid.height(page);
		        ReceiptConfirmationSection.drawTitleGrid(page, thisLocation - 0.85f);
		        thisLocation = thisLocation-0.3f;
		    }
			borrowerGrid = initializeBorrowerGrid(PartyRelationshipHelper.borrowerAddendumCount(data));
			thisLocation = thisLocation - titleGrid.height(page) - borrowerGrid.height(page);
			borrowerGrid.draw(page, page.leftMargin, thisLocation);
		}
		
		// Add additional sellers
		if (PartyRelationshipHelper.sellerAddendumCount(data) > 0) {
			sellerGrid = initializeSellerGrid(PartyRelationshipHelper.sellerAddendumCount(data));
			thisLocation = thisLocation - sellerGrid.height(page) - 0.2f;
			sellerGrid.draw(page, page.leftMargin, thisLocation);
		}
	}
}
