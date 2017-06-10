package com.actualize.mortgage.domainmodels;

import java.io.IOException;

import com.actualize.mortgage.datalayer.CashToClose;
import com.actualize.mortgage.datalayer.InputData;
import com.actualize.mortgage.pdfbuilder.BoxedCharacter;
import com.actualize.mortgage.pdfbuilder.Bullet;
import com.actualize.mortgage.pdfbuilder.Color;
import com.actualize.mortgage.pdfbuilder.FormattedText;
import com.actualize.mortgage.pdfbuilder.Grid;
import com.actualize.mortgage.pdfbuilder.Page;
import com.actualize.mortgage.pdfbuilder.Paragraph;
import com.actualize.mortgage.pdfbuilder.Region;
import com.actualize.mortgage.pdfbuilder.Section;
import com.actualize.mortgage.pdfbuilder.Text;
import com.actualize.mortgage.pdfbuilder.Typeface;
import com.actualize.mortgage.pdfbuilder.Grid.Dimension;
import com.actualize.mortgage.pdfbuilder.Grid.Position;
import com.actualize.mortgage.pdfbuilder.TextBox.Direction;
import com.actualize.mortgage.pdfbuilder.TextBox.HorizontalAlignment;
import com.actualize.mortgage.pdfbuilder.TextBox.VerticalAlignment;

public class CashToCloseSection implements Section {
	public static final Text TAB               = new Text(Color.WHITE, 11, Typeface.CALIBRI_BOLD);
	public static final Text TABLE_TEXT        = new Text(Color.BLACK, 8.5f, Typeface.CALIBRI);
	public static final Text TABLE_TEXT_BOLD   = new Text(Color.BLACK, 8.5f, Typeface.CALIBRI_BOLD);

	private Grid titleGrid, dataGrid;
	private float location;

	private final int columnLabels = 0;
	private final int columnEstimated = 1;
	private final int columnFinal = 2;
	private final int columnChanged = 3;
	private final int columnBullet = 4;
	private final int columnChangeDesc = 5;
	
	private final int rowDONOTDISPLAY = -1; 
	private final int rowHeader = 0;
	private final int rowFirst = 1;
	private final int rowSecond = 2;
	private final int rowThird = 3;
	private final int rowFourth = 4;
	private final int rowFifth = 5;
	private final int rowSixth = 6;
	private final int rowSeventh = 7;
	private final int rowEighth = 8;
	private final int rowNinth = 9;

	private static final float leftIndent  = 2f/72f;

	private final String regexSkeleton = "(?<=*)|(?=*)";
	
	float rowHeight = 12f/72f;
	
	float topMargin = 0.0f;
	
	public CashToCloseSection() {
	}
	
	public CashToCloseSection(float topMargin) {
		this.topMargin = topMargin;
	}
	
	private void initializeTitleGrid(InputData inputData) {
		float heights[] = { Grid.DYNAMIC };
		float widths[] = { Grid.DYNAMIC, 5.5f };
		titleGrid = new Grid(1, heights, 2, widths);
		titleGrid.getCell(0, 0)
			.setBackground(new Tab(2.15f))
			.setMargin(Direction.LEFT, leftIndent)
			.setForeground(new FormattedText("Calculating Cash to Close", TAB));
		
		String verbiage = "  Use this table to see what has changed from your Loan Estimate.";
		titleGrid.setCellText(0, 1, new FormattedText(verbiage, Text.SECTION_INFO));
	}
	
	private void initializeDataGrid(InputData inputData) {
		// Create grid with set widths and dynamic height
		float heights[] = { rowHeight, Grid.DYNAMIC };
		float widths[] = { 2.0f, 1.0f, 1.0f, .25f, .15f, 3.10f };
		int rowcount = getRowCount(inputData);
		
		dataGrid = new Grid(rowcount, heights, widths.length, widths);
		
		// Set grid shading and borders
		for (int columns = 1; columns < dataGrid.columns()-2; columns++)
			dataGrid.setLineBorder(columns, Position.LEFT, Color.MEDIUM_GRAY, 2f/72f);
		dataGrid.setLineShade(rowHeader, Dimension.ROW, Color.LIGHT_GRAY);
		dataGrid.setLineBorder(rowHeader, Position.TOP, Color.BLACK);
		
		// Set grid margins and alignment
		for (int row = 1; row < dataGrid.rows(); row++) {
			dataGrid.setLineMargin(row, Dimension.ROW, Direction.TOP, 0);
			dataGrid.setLineVerticalAlignment(row, Dimension.ROW, VerticalAlignment.TOP);
			dataGrid.setCellWrap(row, columnChangeDesc, true);
		}
		dataGrid.setLineMargin(columnBullet, Dimension.COLUMN, Direction.TOP, 1f/72f);
		
		dataGrid.setLineHorizontalAlignment(columnEstimated, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		dataGrid.setLineMargin(columnEstimated, Dimension.COLUMN, Direction.RIGHT, 5.0f/72.0f);
		
		dataGrid.setLineHorizontalAlignment(columnFinal, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		dataGrid.setLineMargin(columnFinal, Dimension.COLUMN, Direction.RIGHT, 5.0f/72.0f);
		
		dataGrid.setLineMargin(columnChanged, Dimension.COLUMN, Direction.LEFT, 5.0f/72.0f);
		
		dataGrid.getCell(rowHeader,columnEstimated).setHorizontalAlignment(HorizontalAlignment.CENTER);
		dataGrid.getCell(rowHeader,columnFinal).setHorizontalAlignment(HorizontalAlignment.CENTER);

		// Write static labels
		dataGrid.setCellText(rowHeader, columnEstimated, new FormattedText("Loan Estimate", Text.TABLE_HEADER));
		dataGrid.setCellText(rowHeader, columnFinal, new FormattedText("Final", Text.TABLE_HEADER));
		dataGrid.setCellText(rowHeader, columnChanged, new FormattedText("Did this change?", Text.TABLE_HEADER));

		// Write dynamic values
		int lastRow = 0;
		for (CashToClose cashToClose : inputData.getCashList()) {
			Region labelRow = new Region();
			int rowNumber = rowDONOTDISPLAY;
			boolean abs = false;
			
			switch (cashToClose.getItemType()) {
			case "LoanAmount":
				labelRow = labelRow.append(new FormattedText("Loan Amount", TABLE_TEXT));
				rowNumber = (!inputData.isAlternativeView()) ? rowDONOTDISPLAY : rowFirst;
				break;
			case "TotalClosingCosts":
				labelRow = labelRow.append(new FormattedText("Total Closing Costs (J)", TABLE_TEXT));
				rowNumber = (!inputData.isAlternativeView()) ? rowFirst : rowSecond;
				break;
			case "ClosingCostsPaidBeforeClosing":
				labelRow = labelRow.append(new FormattedText("Closing Costs Paid Before Closing", TABLE_TEXT));
				rowNumber = (!inputData.isAlternativeView()) ? rowSecond : rowThird;
				break;
			case "ClosingCostsFinanced":
				labelRow = labelRow.append(new FormattedText("Closing Costs Financed", TABLE_TEXT)).append(new FormattedText("(Paid from your Loan Amount)", TABLE_TEXT));
				rowNumber = (!inputData.isAlternativeView()) ? rowThird : rowDONOTDISPLAY;
				break;
			case "DownPayment":
				labelRow = labelRow.append(new FormattedText("Down Payment/Funds from Borrower", TABLE_TEXT));
				rowNumber = (!inputData.isAlternativeView()) ? rowFourth : rowDONOTDISPLAY;
				break;
			case "TotalPayoffsAndPayments":
				labelRow = labelRow.append(new FormattedText("Total Payoffs and Payments (K)", TABLE_TEXT));
				rowNumber = (!inputData.isAlternativeView()) ? rowDONOTDISPLAY : rowFourth;
				break;
			case "Deposit":
				labelRow = labelRow.append(new FormattedText("Deposit", TABLE_TEXT));
				rowNumber = (!inputData.isAlternativeView()) ? rowFifth : rowDONOTDISPLAY;
				break;
			case "FundsForBorrower":
				labelRow = labelRow.append(new FormattedText("Funds for Borrower", TABLE_TEXT));
				rowNumber = (!inputData.isAlternativeView()) ? rowSixth : rowDONOTDISPLAY;
				break;
			case "SellerCredits":
				labelRow = labelRow.append(new FormattedText("Seller Credits", TABLE_TEXT));
				rowNumber = (!inputData.isAlternativeView()) ? rowSeventh : rowDONOTDISPLAY;
				break;
			case "AdjustmentsAndOtherCredits":
				labelRow = labelRow.append(new FormattedText("Adjustments and Other Credits", TABLE_TEXT));
				rowNumber = (!inputData.isAlternativeView()) ? rowEighth : rowDONOTDISPLAY;
				break;
			case "CashToCloseTotal":
				labelRow = labelRow.append(new FormattedText("Cash to Close", Text.TABLE_TEXT_BOLD));
				rowNumber = (!inputData.isAlternativeView()) ? rowNinth : rowFifth;
				abs = inputData.isAlternativeView();
				lastRow = rowNumber;
				break;
			}
			
			if (rowNumber != rowDONOTDISPLAY)
				WriteDataTableRow(cashToClose, rowNumber, labelRow, abs);
		}
		if(inputData.isAlternativeView()){
			Paragraph estimate = new Paragraph();
			Paragraph actual =   new Paragraph();
			Paragraph amount =   new Paragraph();
			for (CashToClose cashToClose : inputData.getCashList()) {
				if ("CashToCloseTotal".equals(cashToClose.getItemType())) {
					if (!cashToClose.getItemEstimatedAmount().isEmpty()) {
						if(cashToClose.getItemPaymentType().equalsIgnoreCase("FromBorrower")){
							estimate.append(BoxedCharacter.CHECK_BOX_NO)
							.append(new FormattedText(" From  ",Text.TABLE_TEXT_BOLD))
							.append(BoxedCharacter.CHECK_BOX_EMPTY)
							.append(new FormattedText(" To",Text.TABLE_TEXT_BOLD));
						} else if(cashToClose.getItemPaymentType().equalsIgnoreCase("ToBorrower")){
							estimate.append(BoxedCharacter.CHECK_BOX_EMPTY)
							.append(new FormattedText(" From  ",Text.TABLE_TEXT_BOLD))
							.append(BoxedCharacter.CHECK_BOX_NO)
							.append(new FormattedText(" To",Text.TABLE_TEXT_BOLD));
						}
					} else if(!cashToClose.getItemFinalAmount().isEmpty()) {
						if (cashToClose.getItemPaymentType().equalsIgnoreCase("FromBorrower")) {
							actual.append(BoxedCharacter.CHECK_BOX_NO)
							.append(new FormattedText(" From  ",Text.TABLE_TEXT_BOLD))
							.append(BoxedCharacter.CHECK_BOX_EMPTY)
							.append(new FormattedText(" To",Text.TABLE_TEXT_BOLD));
						} else if(cashToClose.getItemPaymentType().equalsIgnoreCase("ToBorrower")){
							actual.append(BoxedCharacter.CHECK_BOX_EMPTY)
							.append(new FormattedText(" From  ",Text.TABLE_TEXT_BOLD))
							.append(BoxedCharacter.CHECK_BOX_NO)
							.append(new FormattedText(" To",Text.TABLE_TEXT_BOLD));
						}
					}
				} else if (cashToClose.getItemType().equalsIgnoreCase("ClosingCostsFinanced")){
					amount.append(new FormattedText("Closing Costs Financed (Paid from your Loan Amount) ",Text.TABLE_TEXT))
						.append(new FormattedText(
							StringFormatter.DOLLARS.formatString(cashToClose.getItemFinalAmount()),Text.TABLE_TEXT));
				}
			}
			dataGrid.setLineBorder(lastRow, Position.TOP, Color.BLACK, 1f/72f);
			dataGrid.setCellText(lastRow+1, columnEstimated, estimate);
			dataGrid.setCellText(lastRow+1, columnFinal,     actual);
			dataGrid.setCellText(lastRow+2, columnEstimated, new FormattedText("Borrower         ",Text.TABLE_TEXT_BOLD));
			dataGrid.setCellText(lastRow+2, columnFinal,     new FormattedText("Borrower         ",Text.TABLE_TEXT_BOLD));
			dataGrid.setCellText(lastRow+2, columnChanged,   amount);
			dataGrid.setLineBorder(lastRow+2, Position.BOTTOM, Color.BLACK, 1f/72f);
 		} else {
 			dataGrid.setLineBorder(lastRow, Position.TOP, Color.BLACK, 1f/72f);
 			dataGrid.setLineBorder(lastRow, Position.BOTTOM, Color.BLACK, 1f/72f);
 		}
	}
	
	public void WriteDataTableRow(CashToClose cashToClose, int rowNumber, Region rowLabel, boolean abs) {
		dataGrid.setCellText(rowNumber, columnLabels, rowLabel);
		WriteNumericAmount(cashToClose, rowNumber, columnEstimated, cashToClose.getItemEstimatedAmount(), abs);
		WriteNumericAmount(cashToClose, rowNumber, columnFinal, cashToClose.getItemFinalAmount(), abs);
		WriteAmountChangeText(cashToClose, rowNumber);
		dataGrid.setLineBorder(rowNumber, Position.TOP, Color.MEDIUM_GRAY);
	}
	
	public void WriteNumericAmount(CashToClose cashToClose, int rowNumber, int columnNumber, String amount, boolean abs) {
		FormattedText amountText = null;
		Text textType = TABLE_TEXT;
		if ("CashToCloseTotal".equals(cashToClose.getItemType()))
			textType = TABLE_TEXT;
		if (!amount.isEmpty()) {
			if (abs)
				amountText = new FormattedText(StringFormatter.ABSDOLLARS.formatString(amount), textType);
			else
				amountText = new FormattedText(StringFormatter.DOLLARS.formatString(amount), textType);
		} else if (!"CashToCloseTotal".equals(cashToClose.getItemType()))
			amountText = new FormattedText(StringFormatter.DOLLARS.formatString("0"), textType);
		if (amountText != null)
			dataGrid.setCellText(rowNumber, columnNumber, amountText);
	}
	
	public void WriteAmountChangeText(CashToClose cashToClose, int rowNumber) {
		if (cashToClose.getItemType().equalsIgnoreCase("CashToCloseTotal") == false) {
			if (cashToClose.isAmountChangedIndicator() == true) {
				Paragraph text = (new Paragraph()).append(new FormattedText("YES  ", Text.TABLE_TEXT_BOLD));
				dataGrid.setCellText(rowNumber, columnChanged, text);
				AppendBulletText(StringFormatter.STRINGCLEAN.formatString(cashToClose.getItemChangeDescription()), rowNumber);
			} else {
				dataGrid.setCellText(rowNumber, columnChanged, new FormattedText("NO", Text.TABLE_TEXT_BOLD));
			}	
		}
	}
	
	public void AppendBulletText(String description, int rowNumber) {
		String[] bullets = description.split(buildBullets());
		Region descRegion = new Region();
		Region bulletRegion = new Region();
		for (String bullet : bullets) {
			String[] array = bullet.split(buildDescriptionFormatRegexString());
			Paragraph text = new Paragraph();
			
			// Alternate standard and bold fonts based on inclusive split on key bold phrases
			Text style = Text.TABLE_TEXT;
			for (String element : array) {
				text.append(new FormattedText(element, style));
				style = (style == Text.TABLE_TEXT) ? Text.TABLE_TEXT_BOLD : Text.TABLE_TEXT;
			}
			
			descRegion.append(text);
			bulletRegion.append(Bullet.BULLET);
		}
		dataGrid.setCellText(rowNumber, columnBullet, bulletRegion);
		dataGrid.setCellText(rowNumber, columnChangeDesc, descRegion);
	}
	
	public String buildBullets() {
		return "(?=Increase)";
	}
	
	public String buildDescriptionFormatRegexString() {
		String regexBoldSections =
				regexSkeleton.replace("*", "before Closing") + "|" +
				regexSkeleton.replace("*", "decreased") + "|" +
				regexSkeleton.replace("*", "exceeds legal limits") + "|" +
				regexSkeleton.replace("*", "increased") + "|" +
				regexSkeleton.replace("*", "Lender Credits") + "|" +
				regexSkeleton.replace("*", "Payoffs and Payments \\(K\\)") + "|" +
				regexSkeleton.replace("*", "Sections K and L") + "|" +
				regexSkeleton.replace("*", "Section L") + "|" +
				regexSkeleton.replace("*", "Total Loan Costs \\(D\\)") + "|" +
				regexSkeleton.replace("*", "Total Other Costs \\(I\\)");
		return "(" + regexBoldSections + ")";
	}
	
	public int getRowCount(InputData inputData) {
		if (inputData.isAlternativeView())
			return 8;
		else
			return 10;
	}

	public void draw(Page page, Object d) throws IOException {
		InputData data = (InputData)d;
		
		initializeTitleGrid(data);
		initializeDataGrid(data);
		
		this.location = page.height - page.leftMargin - titleGrid.height(page) - dataGrid.height(page) - topMargin + 24f/72f;
		if(data.isAlternativeView()){
			this.location = data.getAlternativeC2Crow() - titleGrid.height(page) - dataGrid.height(page) - 2*rowHeight - topMargin;
		}
		titleGrid.draw(page, page.leftMargin, this.location + dataGrid.height(page));
		dataGrid.draw(page, page.leftMargin, this.location);
	}
}
