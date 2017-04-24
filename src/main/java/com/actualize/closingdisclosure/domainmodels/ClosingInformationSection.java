package com.actualize.closingdisclosure.domainmodels;

import java.io.IOException;
import java.util.List;

import com.actualize.closingdisclosure.datalayer.ClosingMap;
import com.actualize.closingdisclosure.datalayer.InputData;
import com.actualize.closingdisclosure.datalayer.PartyData;
import com.actualize.closingdisclosure.datalayer.PartyRoleTypes;
import com.actualize.closingdisclosure.datalayer.SubjectProperty;
import com.actualize.closingdisclosure.pdfbuilder.BoxedCharacter;
import com.actualize.closingdisclosure.pdfbuilder.Color;
import com.actualize.closingdisclosure.pdfbuilder.FormattedText;
import com.actualize.closingdisclosure.pdfbuilder.Grid;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Dimension;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Position;
import com.actualize.closingdisclosure.pdfbuilder.Page;
import com.actualize.closingdisclosure.pdfbuilder.Paragraph;
import com.actualize.closingdisclosure.pdfbuilder.Section;
import com.actualize.closingdisclosure.pdfbuilder.Text;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.Direction;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.VerticalAlignment;
import com.actualize.closingdisclosure.pdfbuilder.Typeface;

public class ClosingInformationSection implements Section {
	private final Text TITLE_INFO        = new Text(Color.BLACK, 10, Typeface.CALIBRI_OBLIQUE);
	private final Text HEADER_LARGE      = new Text(Color.BLACK, 20, Typeface.CALIBRI_BOLD);
	private final Text SECTION_LARGE     = new Text(Color.BLACK, 12, Typeface.CALIBRI);
	private final Text TABLE_TEXT        = new Text(Color.BLACK, 8, Typeface.CALIBRI);
	private final Text TABLE_TEXT_BOLD   = new Text(Color.BLACK, 8, Typeface.CALIBRI_BOLD);
	
	private Grid headerGrid, titleGrid, closingDataGrid, transactionDataGrid, loanDataGrid;

	private void initializeHeaderGrid() {
		float heights[] = { 24f/72f };
		float widths[] = { 3.1f, 4.4f };
		headerGrid = new Grid(1, heights, 2, widths);
		headerGrid.setCellText(0, 0, new FormattedText("Closing Disclosure", HEADER_LARGE));
		headerGrid.getCell(0, 1).setWrap(true).setForeground(
				new FormattedText("This form is a statement of final loan terms and closing costs.  Compare this document with your Loan Estimate.", TITLE_INFO));
		headerGrid.setLineBorder(0, Position.BOTTOM, Color.BLACK, 2f/72f);
	}

	private void initializeTitleGrid(InputData data) {
		boolean isSellerOnly = data.isSellerOnly();
		float heights[] = { 12f/72f };
		float widths[] = { 2.5f, 3.0f, 2.0f};
		titleGrid = new Grid(heights.length, heights, widths.length, widths);
		titleGrid.setCellText(0, 0, new FormattedText("Closing Information", SECTION_LARGE));
		titleGrid.setCellText(0, 1, new FormattedText("Transaction Information", SECTION_LARGE));
		if (!isSellerOnly)
			titleGrid.setCellText(0, 2, new FormattedText("Loan Information", SECTION_LARGE));
	}

	private void initializeClosingDataGrid(InputData inputData) {

		// Create grid with set widths and dynamic height
		final float heights[] = { Grid.DYNAMIC };
		final float widths[] = { 1.25f, 1.25f };
		closingDataGrid = new Grid(10, heights, widths.length, widths);
		closingDataGrid.setLineWrap(1, Dimension.COLUMN, true);
		
		// String mortgageTypes = "Conventional,FHA,LocalAgency,PublicAndIndianHousing,StateAgency,USDARuralDevelopment,VAConventional";
		ClosingMap 		closingMap 		= inputData.getClosingMap();
		SubjectProperty subjectProperty = inputData.getSubjectProperty();
		List<PartyData> partyList       = inputData.getPartyList();
		
		// Set grid margins and alignment
		for (int row = 0; row < closingDataGrid.rows(); row++) {
			closingDataGrid.setLineMargin(row, Dimension.ROW, Direction.TOP, 0.0f/72.0f);
			closingDataGrid.setLineVerticalAlignment(row, Dimension.ROW, VerticalAlignment.TOP);
		}

		//1.1 INTEGRATED_DISCLOSURE_DETAIL.IntegratedDisclosureIssuedDate
		int row = 0;
		closingDataGrid.setCellText(row, 0, new FormattedText("Date Issued", TABLE_TEXT_BOLD));
		closingDataGrid.setCellText(row, 1, new FormattedText(
				StringFormatter.DATE.formatString(closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_DETAIL.IntegratedDisclosureIssuedDate")),
				TABLE_TEXT));
		
		//1.2 CLOSING_INFORMATION_DETAIL.ClosingDate
		closingDataGrid.setCellText(++row, 0, new FormattedText("Closing Date", TABLE_TEXT_BOLD));
		closingDataGrid.setCellText(row, 1, new FormattedText(
				StringFormatter.DATE.formatString(closingMap.getClosingMapValue("CLOSING_INFORMATION_DETAIL.ClosingDate")),
				TABLE_TEXT));
		
		//1.3
		closingDataGrid.setCellText(++row, 0, new FormattedText("Disbursement Date", TABLE_TEXT_BOLD));
		closingDataGrid.setCellText(row, 1, new FormattedText(
				StringFormatter.DATE.formatString(closingMap.getClosingMapValue("CLOSING_INFORMATION_DETAIL.DisbursementDate")),
				TABLE_TEXT));
		
		//1.4
		closingDataGrid.setCellText(++row, 0, new FormattedText("Settlement Agent", TABLE_TEXT_BOLD));
		for (PartyData partyLocal:partyList)
			if (partyLocal.getRoleType().equals(PartyRoleTypes.SettlementAgent) && partyLocal.getIsLegalEntity()) {
				closingDataGrid.setCellText(row, 1, new FormattedText(partyLocal.getPartyName(), TABLE_TEXT));
				break;
			}
		
		//1.5
		closingDataGrid.setCellText(++row, 0, new FormattedText("File  #", TABLE_TEXT_BOLD));
		closingDataGrid.setCellText(row, 1, new FormattedText(
				closingMap.getClosingMapValue("CLOSING_INFORMATION_DETAIL.ClosingAgentOrderNumberIdentifier"), TABLE_TEXT));
		
		//1.6
		closingDataGrid.setCellText(++row, 0, new FormattedText("Property", TABLE_TEXT_BOLD));
		if (!subjectProperty.getAddressLine().equals("")){
			closingDataGrid.setCellText(row, 1, new FormattedText(subjectProperty.getAddressLine(), TABLE_TEXT));
		   if (closingMap.getClosingMapValue("TERMS_OF_LOAN.LoanPurposeType").equals("Refinance")) {
			  closingDataGrid.setLineWrap(row, Dimension.ROW, false);
		   }
		}
		//1.7
		if (!subjectProperty.getAddressLine().equals(""))
			row++;
		String str = "";
		if (!subjectProperty.getCityName().equals(""))
			str = str + subjectProperty.getCityName();
		if (!subjectProperty.getStateCode().equals(""))
			str = (str.equals("") ? "" : (str + ", ")) + subjectProperty.getStateCode();
		str = (str.equals("") ? "" : (str + " "))  + subjectProperty.getPostalCode();
		closingDataGrid.setCellText(row, 1, new FormattedText(str, TABLE_TEXT));
		closingDataGrid.getCell(row, 1).setMargin(Direction.TOP, -3f / 72f);
		if (closingMap.getClosingMapValue("TERMS_OF_LOAN.LoanPurposeType").equals("Refinance")) {
			closingDataGrid.setLineWrap(row, Dimension.ROW, false);
		}
		
		//1.7
		row++;
		if (closingMap.getClosingMapValue("TERMS_OF_LOAN.LoanPurposeType").equals("Purchase")) {
			if(!closingMap.getClosingMapValue("SALES_CONTRACT_DETAIL.SalesContractAmount").equals("")){
				closingDataGrid.setCellText(row, 0, new FormattedText("Sale Price", TABLE_TEXT_BOLD));
				closingDataGrid.setCellText(row, 1, new FormattedText(
						StringFormatter.TRUNCDOLLARS.formatString(closingMap.getClosingMapValue("SALES_CONTRACT_DETAIL.SalesContractAmount")),
						TABLE_TEXT));
			} else if(!closingMap.getClosingMapValue("SALES_CONTRACT_DETAIL.RealPropertyAmount").equals("")) {
				closingDataGrid.setCellText(row, 0, new FormattedText("Sale Price", TABLE_TEXT_BOLD));
				closingDataGrid.setCellText(row, 1, new FormattedText(
						StringFormatter.TRUNCDOLLARS.formatString(closingMap.getClosingMapValue("SALES_CONTRACT_DETAIL.RealPropertyAmount")),
						TABLE_TEXT));
			} else if (!closingMap.getClosingMapValue("PROPERTY_VALUATION_DETAIL.PropertyValuationAmount").equals("")) {
				closingDataGrid.setCellText(row, 0, new FormattedText("Appraised Prop. Value", TABLE_TEXT_BOLD));
				closingDataGrid.setCellText(row, 1, new FormattedText(
						StringFormatter.TRUNCDOLLARS.formatString(closingMap.getClosingMapValue("PROPERTY_VALUATION_DETAIL.PropertyValuationAmount")),
						TABLE_TEXT));
			}
		} else if (!closingMap.getClosingMapValue("PROPERTY_VALUATION_DETAIL.PropertyValuationAmount").equals("")) {
			closingDataGrid.setCellText(row, 0, new FormattedText("Appraised Prop. Value", TABLE_TEXT_BOLD));
			closingDataGrid.setCellText(row, 1, new FormattedText(
					StringFormatter.TRUNCDOLLARS.formatString(closingMap.getClosingMapValue("PROPERTY_VALUATION_DETAIL.PropertyValuationAmount")),
					TABLE_TEXT));
		} else if (!closingMap.getClosingMapValue("PROPERTY_DETAIL.PropertyEstimatedValueAmount").equals("")) {
			closingDataGrid.setCellText(row, 0, new FormattedText("Estimated Prop. Value", TABLE_TEXT_BOLD));
			closingDataGrid.setCellText(row, 1, new FormattedText(
					StringFormatter.TRUNCDOLLARS.formatString(closingMap.getClosingMapValue("PROPERTY_DETAIL.PropertyEstimatedValueAmount")),
					TABLE_TEXT));
		}
	}
	

	private void initializeTransactionDataGrid(InputData inputData) {
		
		boolean isSellerOnly = inputData.isSellerOnly();

		// Create grid with set widths and dynamic height
		float heights[] = { Grid.DYNAMIC };
		float widths[] = { 0.5f, 2.5f };
		transactionDataGrid = new Grid(10, heights, widths.length, widths);
		transactionDataGrid.setLineWrap(1, Dimension.COLUMN, true);
		
		// String mortgageTypes = "Conventional,FHA,LocalAgency,PublicAndIndianHousing,StateAgency,USDARuralDevelopment,VAConventional";
		ClosingMap 		closingMap 		= inputData.getClosingMap();
		List<PartyData> partyList       = inputData.getPartyList();
		
		// Set grid margins and alignment
		for (int row = 0; row < transactionDataGrid.rows(); row++) {
			transactionDataGrid.setLineMargin(row, Dimension.ROW, Direction.TOP, 0.0f/72.0f);
			transactionDataGrid.setLineVerticalAlignment(row, Dimension.ROW, VerticalAlignment.TOP);
		}

		//2.1
		transactionDataGrid.setCellText(0, 0, new FormattedText("Borrower", TABLE_TEXT_BOLD));
		
		//2.2
		if (!closingMap.getClosingMapValue("TERMS_OF_LOAN.LoanPurposeType").equals("Refinance")) {
			transactionDataGrid.setCellText(3, 0, new FormattedText("Seller", TABLE_TEXT_BOLD));
			if (!isSellerOnly)
				transactionDataGrid.setCellText(6, 0, new FormattedText("Lender", TABLE_TEXT_BOLD));
		} else {
			//2.3
			transactionDataGrid.setCellText(3, 0, new FormattedText("Lender", TABLE_TEXT_BOLD));
		}
		int borrowerCount = 0;
		int sellerCount = 0;
		String borrowerName = "";
		String borrowerAddress = "";
		String borrowerCityState = "";
		String sellerName = "";
		String sellerAddress = "";
		String sellerCityState = "";
		String lenderName = "";
		for (PartyData partyLocal:partyList) {
			switch (partyLocal.getRoleType()) {
			case PartyRoleTypes.Borrower:
				if (borrowerCount < 2) {
					String borrowerTmp1 = StringFormatter.STRINGCLEAN.formatString(partyLocal.getAddressLine());
					String borrowerTmp2 = StringFormatter.STRINGCLEAN.formatString(partyLocal.getAddressSecondLine());
					if (borrowerCount == 0) {
						borrowerCount = 1;
						borrowerName = StringFormatter.STRINGCLEAN.formatString(partyLocal.getPartyName());
						borrowerAddress = borrowerTmp1;
						borrowerCityState = borrowerTmp2;
					} else if (borrowerAddress.equals(borrowerTmp1) && borrowerCityState.equals(borrowerTmp2) || borrowerTmp1.equals("") && borrowerTmp2.equals("")) {
						++borrowerCount;
						borrowerName += " & "+ partyLocal.getPartyName();
					}
				}
				break;
			case PartyRoleTypes.Seller:
				if (sellerCount < 2) {
					String sellerTmp1 = StringFormatter.STRINGCLEAN.formatString(partyLocal.getAddressLine());
					String sellerTmp2 = StringFormatter.STRINGCLEAN.formatString(partyLocal.getAddressSecondLine());
					if (sellerCount == 0) {
						sellerCount = 1;
						sellerName = partyLocal.getPartyName();
						sellerAddress = sellerTmp1;
						sellerCityState = sellerTmp2;
					} else if (sellerAddress.equals(sellerTmp1) && sellerCityState.equals(sellerTmp2) || sellerTmp1.equals("") && sellerTmp2.equals("")) {
						++sellerCount;
						sellerName += " & "+ partyLocal.getPartyName();
					}
				}
				break;
			case PartyRoleTypes.Lender:
				if (partyLocal.getIsLegalEntity())
					lenderName = partyLocal.getPartyName();
			}
		}
		transactionDataGrid.setCellText(0, 1, new FormattedText(borrowerName, TABLE_TEXT));
		transactionDataGrid.setCellText(1, 1, new FormattedText(borrowerAddress, TABLE_TEXT));
		transactionDataGrid.setCellText(2, 1, new FormattedText(borrowerCityState, TABLE_TEXT));
		transactionDataGrid.setCellText(3, 1, new FormattedText(sellerName, TABLE_TEXT));
		transactionDataGrid.setCellText(4, 1, new FormattedText(sellerAddress, TABLE_TEXT));
		transactionDataGrid.setCellText(5, 1, new FormattedText(sellerCityState, TABLE_TEXT));
		if (!isSellerOnly) {
			if (closingMap.getClosingMapValue("TERMS_OF_LOAN.LoanPurposeType").equals("Refinance")) {
				transactionDataGrid.setCellText(3, 1, new FormattedText(lenderName, TABLE_TEXT));
			} else {
				transactionDataGrid.setCellText(6, 1, new FormattedText(lenderName,TABLE_TEXT));
			}
		}
	}

	private void initializeLoanDataGrid(InputData inputData) {
		
		// Create grid with set widths and dynamic height
		float heights[] = { Grid.DYNAMIC };
		float widths[] = { 0.75f, 1.25f,};
		loanDataGrid = new Grid(10, heights, widths.length, widths);
		
		// String mortgageTypes = "Conventional,FHA,LocalAgency,PublicAndIndianHousing,StateAgency,USDARuralDevelopment,VAConventional";
		ClosingMap 		closingMap 		= inputData.getClosingMap();
		
		// Set grid margins and alignment
		for (int row = 0; row < loanDataGrid.rows(); row++) {
			loanDataGrid.setLineMargin(row, Dimension.ROW, Direction.TOP, 0.0f/72.0f);
			loanDataGrid.setLineVerticalAlignment(row, Dimension.ROW, VerticalAlignment.TOP);
		}


		//3.1
		// TODO need direction of variable loan maturity disclosure
		loanDataGrid.setCellText(1, 0, new FormattedText("Loan Term", TABLE_TEXT_BOLD));
		if("true".equals(closingMap.getClosingMapValue("LOAN_DETAIL.ConstructionLoanIndicator")) && "ConstructionToPermanent".equals(closingMap.getClosingMapValue("CONSTRUCTION.ConstructionLoanType"))){
            int months = Integer.parseInt(closingMap.getClosingMapValue("CONSTRUCTION.ConstructionLoanTotalTermMonthsCount"));
            loanDataGrid.setCellText(1, 1, new FormattedText(convertMonthsToDisplayFormat(months), TABLE_TEXT));
        } else if(closingMap.getClosingMapValue("MATURITY_RULE.LoanMaturityPeriodType").equals("Year")){
			loanDataGrid.setCellText(1, 1, new FormattedText(
					closingMap.getClosingMapValue("MATURITY_RULE.LoanMaturityPeriodCount")+" years",TABLE_TEXT));
		} else if(closingMap.getClosingMapValue("MATURITY_RULE.LoanMaturityPeriodType").equals("Month") && !closingMap.getClosingMapValue("MATURITY_RULE.LoanMaturityPeriodCount").equals("")){
			int months = Integer.parseInt(closingMap.getClosingMapValue("MATURITY_RULE.LoanMaturityPeriodCount"));
			loanDataGrid.setCellText(1, 1, new FormattedText(convertMonthsToDisplayFormat(months), TABLE_TEXT));
		}
		
		//3.2
		loanDataGrid.setCellText(2, 0, new FormattedText("Purpose", TABLE_TEXT_BOLD));
		if (closingMap.getClosingMapValue("TERMS_OF_LOAN.LoanPurposeType").equals("Purchase")) {
			loanDataGrid.setCellText(2, 1, new FormattedText("Purchase",TABLE_TEXT));
		} else if (closingMap.getClosingMapValue("LOAN_DETAIL.ConstructionLoanIndicator").equals("true")){
			loanDataGrid.setCellText(2, 1, new FormattedText("Construction", TABLE_TEXT));
		} else if (closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_DETAIL.IntegratedDisclosureHomeEquityLoanIndicator").equals("true")){
			loanDataGrid.setCellText(2, 1, new FormattedText("Home Equity Loan", TABLE_TEXT));
		} else {
			loanDataGrid.setCellText(2, 1, new FormattedText("Refinance",TABLE_TEXT));
		}
		
		//3.3
		loanDataGrid.setCellText(3, 0, new FormattedText("Product", TABLE_TEXT_BOLD));
		loanDataGrid.setCellWrap(3, 1, true);
		loanDataGrid.setCellText(3, 1, new FormattedText(StringFormatter.STRINGCLEAN.formatString(
				closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_DETAIL.IntegratedDisclosureLoanProductDescription")), TABLE_TEXT));
		
		//3.4
		loanDataGrid.setCellText(5, 0, new FormattedText("Loan Type", TABLE_TEXT_BOLD));
		
		//3.5
		Paragraph text3_5_1 = (new Paragraph())
				.append(BoxedCharacter.CHECK_BOX_EMPTY)
				.append(new FormattedText("   Conventional   ", TABLE_TEXT))
				.append(BoxedCharacter.CHECK_BOX_EMPTY)
				.append(new FormattedText("   FHA  ", TABLE_TEXT));
		Paragraph text3_5_2 = (new Paragraph())
				.append(BoxedCharacter.CHECK_BOX_EMPTY)
				.append(new FormattedText("   VA   ", TABLE_TEXT))
				.append(BoxedCharacter.CHECK_BOX_EMPTY)
				.append(new FormattedText("   _____ ", TABLE_TEXT));
		if (closingMap.getClosingMapValue("TERMS_OF_LOAN.MortgageType").equals("Conventional")) {
			text3_5_1 = (new Paragraph())
					.append(BoxedCharacter.CHECK_BOX_NO)
					.append(new FormattedText("   Conventional   ", TABLE_TEXT))
					.append(BoxedCharacter.CHECK_BOX_EMPTY)
					.append(new FormattedText("   FHA  ", TABLE_TEXT));
		} else if(closingMap.getClosingMapValue("TERMS_OF_LOAN.MortgageType").equals("FHA")) {
			text3_5_1 = (new Paragraph())
					.append(BoxedCharacter.CHECK_BOX_EMPTY)
					.append(new FormattedText("   Conventional   ", TABLE_TEXT))
					.append(BoxedCharacter.CHECK_BOX_NO)
					.append(new FormattedText("   FHA  ", TABLE_TEXT));
		} else if(closingMap.getClosingMapValue("TERMS_OF_LOAN.MortgageType").equals("VA")) {
			text3_5_2 = (new Paragraph())
					.append(BoxedCharacter.CHECK_BOX_NO)
					.append(new FormattedText("   VA   ", TABLE_TEXT))
					.append(BoxedCharacter.CHECK_BOX_EMPTY)
					.append(new FormattedText("   _____   ", TABLE_TEXT));
		} else {
			String str = closingMap.getClosingMapValue("TERMS_OF_LOAN.MortgageTypeOtherDescription");
			if (str == "")
				str = "Other";
			text3_5_2 = (new Paragraph())
					.append(BoxedCharacter.CHECK_BOX_EMPTY)
					.append(new FormattedText("   VA   ", TABLE_TEXT))
					.append(BoxedCharacter.CHECK_BOX_NO)
					.append(new FormattedText("   ", TABLE_TEXT))
					.append(new FormattedText(str, TABLE_TEXT, true));
		}
		loanDataGrid.setCellText(5, 1, text3_5_1);
		loanDataGrid.setCellText(6, 1, text3_5_2);
		
		//3.6
		loanDataGrid.setCellText(7, 0, new FormattedText("Loan ID #", TABLE_TEXT_BOLD));
		loanDataGrid.setCellText(7, 1, new FormattedText(closingMap.getClosingMapValue("LOAN_IDENTIFIER.LenderLoan"), TABLE_TEXT));
		
		//3.7
		loanDataGrid.setCellText(8, 0, new FormattedText("MIC #", TABLE_TEXT_BOLD));
		if (closingMap.getClosingMapValue("LOAN_DETAIL.MIRequiredIndicator").equals("true")) {
			if (closingMap.getClosingMapValue("TERMS_OF_LOAN.MortgageType").equals("Conventional"))
				loanDataGrid.setCellText(8, 1, new FormattedText(closingMap.getClosingMapValue("MI_DATA_DETAIL.MICertificateIdentifier"), TABLE_TEXT));
			else
				loanDataGrid.setCellText(8, 1, new FormattedText(closingMap.getClosingMapValue("LOAN_IDENTIFIER.AgencyCase"), TABLE_TEXT));
		}
	}

	private String convertMonthsToDisplayFormat(Integer months){
	    int years = months / 12;
        int modMonths = months % 12;
        String maturity = "";
        if (months == 12)
            maturity = "1 year";
        else if (months < 24)
            maturity = Integer.toString(months) + " mo.";
        else if (modMonths == 0)
            maturity = Integer.toString(years) + " years";
        else
            maturity = Integer.toString(years) + " yr. " + Integer.toString(modMonths) + " mo.";
        return maturity;
	}

	public void draw(Page page, Object d) throws IOException {
		InputData data = (InputData)d;
		boolean isSellerOnly = data.isSellerOnly();

		// Draw header grid
		initializeHeaderGrid();
		float x = page.leftMargin;
		float y = page.height - page.topMargin - headerGrid.height(page);
		headerGrid.draw(page, x, y);

		// Draw title grid
		initializeTitleGrid(data);
		y = y - titleGrid.height(page) - 6f/72f;
		titleGrid.draw(page, x, y);

		// Draw closing data grid
		initializeClosingDataGrid(data);
		closingDataGrid.draw(page, x, y - closingDataGrid.height(page));

		// Draw transaction information grid
		initializeTransactionDataGrid(data);
		x += closingDataGrid.width(page);
		transactionDataGrid.draw(page, x, y - transactionDataGrid.height(page));

		// Loan information grid
		if (!isSellerOnly)
		{
			initializeLoanDataGrid(data);
			x += transactionDataGrid.width(page);
			loanDataGrid.draw(page, x, y - loanDataGrid.height(page));
		}
	}

	public float height(Page page) throws IOException {
		return headerGrid.height(page) + titleGrid.height(page) + closingDataGrid.height(page) + 4f/72f;
	}
}
