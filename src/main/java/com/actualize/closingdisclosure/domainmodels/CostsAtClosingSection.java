package com.actualize.closingdisclosure.domainmodels;

import java.io.IOException;

import com.actualize.closingdisclosure.datalayer.ClosingMap;
import com.actualize.closingdisclosure.datalayer.ID_Subsection;
import com.actualize.closingdisclosure.datalayer.InputData;
import com.actualize.closingdisclosure.pdfbuilder.BoxedCharacter;
import com.actualize.closingdisclosure.pdfbuilder.Color;
import com.actualize.closingdisclosure.pdfbuilder.FormattedText;
import com.actualize.closingdisclosure.pdfbuilder.Grid;
import com.actualize.closingdisclosure.pdfbuilder.LineFeed;
import com.actualize.closingdisclosure.pdfbuilder.Page;
import com.actualize.closingdisclosure.pdfbuilder.Paragraph;
import com.actualize.closingdisclosure.pdfbuilder.Region;
import com.actualize.closingdisclosure.pdfbuilder.Section;
import com.actualize.closingdisclosure.pdfbuilder.Text;
import com.actualize.closingdisclosure.pdfbuilder.Typeface;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Dimension;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Position;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.Direction;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.HorizontalAlignment;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.VerticalAlignment;

public class CostsAtClosingSection implements Section {
	public static final Text SUMMARY_AMOUNT    = new Text(Color.BLACK, 14, Typeface.CALIBRI);
	public static final Text TAB               = new Text(Color.WHITE, 11, Typeface.CALIBRI_BOLD);
	public static final Text TEXT              = new Text(Color.BLACK, 10, Typeface.CALIBRI);
	public static final Text TEXT_BOLD         = new Text(Color.BLACK, 10, Typeface.CALIBRI_BOLD);
	public static final Text TEXT_ITALICS      = new Text(Color.BLACK, 10, Typeface.CALIBRI_OBLIQUE);

	private static final float borderWidth = 1f/72f;
	private static final float col1Width = 1.85f;
	private static final float leftIndent  = 2f/72f;
	
	Grid titleGrid, dataGrid;
	
	private void initializeTitleGrid() {
		float heights[] = { 22f/72f };
		float widths[] = {1.75f, 2.0f};
		titleGrid = new Grid(1, heights, 2, widths);
		titleGrid.getCell(0, 0)
			.setHorizontalAlignment(HorizontalAlignment.LEFT)
			.setMargin(Direction.LEFT, leftIndent)
			.setBackground(new Tab(col1Width))
			.setForeground(new FormattedText("Costs at Closing", TAB));
	}
	
	private void initializeDataGrid(InputData inputData) {
		ClosingMap closingMap = inputData.getClosingMap();
		final float amtWidth = 1.4f;

		float heights[] = { 28f/72f };
		float widths[] = { col1Width, amtWidth, 7.5f - col1Width - amtWidth };
		dataGrid = new Grid(2, heights, 3, widths);
		dataGrid.setLineBorder(1, Position.LEFT, Color.BLACK);
		dataGrid.setLineHorizontalAlignment(1, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		dataGrid.setLineMargin(1, Dimension.COLUMN, Direction.LEFT, 0.2f);
		dataGrid.setLineMargin(1, Dimension.COLUMN, Direction.RIGHT, 0.1f);
		dataGrid.setLineMargin(1, Dimension.COLUMN, Direction.TOP, 2f/72f);
		
		//6.1
		dataGrid.setLineBorder(0, Position.TOP, Color.BLACK, borderWidth);
		dataGrid.setLineVerticalAlignment(0, Dimension.ROW, VerticalAlignment.TOP);
		dataGrid.setLineHorizontalAlignment(1, Dimension.COLUMN, HorizontalAlignment.LEFT);
		dataGrid.getCell(0, 0).setMargin(Direction.LEFT, leftIndent).setForeground(new FormattedText("Closing Costs", TEXT_BOLD));
		dataGrid.getCell(0, 1).setHorizontalAlignment(HorizontalAlignment.LEFT);
		dataGrid.getCell(0, 1).setMargin(Direction.LEFT, 0.15f);
		dataGrid.setCellText(0, 1, new FormattedText(StringFormatter.DOLLARS.formatString(
				closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.TotalClosingCosts")), SUMMARY_AMOUNT));
		String str = "Includes ";
		str +=  StringFormatter.DOLLARS.formatString(closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.TotalLoanCosts"))
				+ " in Loan Costs + ";
		str +=  StringFormatter.DOLLARS.formatString(closingMap.getClosingMapValue("INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL.TotalOtherCosts"))
				+ " in Other Costs - ";
		String lenderCredits = "0";
		for (ID_Subsection idsLocal:inputData.getIdsList())
			if (idsLocal.getIntegratedDisclosureSubsectionType().equals("LenderCredits")) {
				lenderCredits = idsLocal.getPaymentAmount();
				break;
			}
		str += StringFormatter.ABSDOLLARS.formatString(lenderCredits);
		dataGrid.setCellText(0, 2, new Region()
			.append(new FormattedText(str, TEXT))
			.append(new LineFeed(-1f/72f))
			.append(new Paragraph()
				.append(new FormattedText("in Lender Credits.", TEXT))
				.append(new FormattedText(" See page 2 for details.", TEXT_ITALICS))));
		dataGrid.setLineBorder(1, Position.BOTTOM, Color.BLACK);
		
		//6.2
		dataGrid.setLineBorder(1, Position.TOP, Color.BLACK);
		dataGrid.setLineVerticalAlignment(1, Dimension.ROW, VerticalAlignment.TOP);
		dataGrid.getCell(1, 0).setMargin(Direction.LEFT, leftIndent).setForeground(new FormattedText("Cash To Close", TEXT_BOLD));
		dataGrid.getCell(1, 1).setHorizontalAlignment(HorizontalAlignment.LEFT);
		dataGrid.getCell(1, 1).setMargin(Direction.LEFT, 0.15f);
		if (!closingMap.getClosingMapValue("CLOSING_INFORMATION_DETAIL.CashFromBorrowerAtClosingAmount").equals("")) {
			dataGrid.setCellText(1, 1, new FormattedText(StringFormatter.ABSDOLLARS.formatString(
					closingMap.getClosingMapValue("CLOSING_INFORMATION_DETAIL.CashFromBorrowerAtClosingAmount")+"     "), SUMMARY_AMOUNT));
		} else if (!closingMap.getClosingMapValue("CLOSING_INFORMATION_DETAIL.CashToBorrowerAtClosingAmount").equals("")) {
			if (inputData.isAlternativeView())
				dataGrid.setCellText(1, 1, new FormattedText(StringFormatter.ABSDOLLARS.formatString(
						closingMap.getClosingMapValue("CLOSING_INFORMATION_DETAIL.CashToBorrowerAtClosingAmount")+"     "), SUMMARY_AMOUNT));
			else
				dataGrid.setCellText(1, 1, new FormattedText(StringFormatter.DOLLARS.formatString(
						closingMap.getClosingMapValue("CLOSING_INFORMATION_DETAIL.CashToBorrowerAtClosingAmount")+"     "), SUMMARY_AMOUNT));
		}
		Region cashToCloseDesc = new Region()
		    .append(new Paragraph()
            .append(new FormattedText("Includes Closing Costs.", TEXT))
            .append(new FormattedText(" See Calculating Cash to Close on page 3 for details.", TEXT_ITALICS)));
		if (inputData.isAlternativeView()) {
			cashToCloseDesc.append(new LineFeed(-1f/72f));
			if (!closingMap.getClosingMapValue("CLOSING_INFORMATION_DETAIL.CashFromBorrowerAtClosingAmount").equals("")) {
				cashToCloseDesc.append(new Paragraph()
				    .append(BoxedCharacter.CHECK_BOX_NO)
				    .append(new FormattedText(" From  ", Text.TABLE_TEXT))
				    .append(BoxedCharacter.CHECK_BOX_EMPTY)
				    .append(new FormattedText(" To  Borrower", Text.TABLE_TEXT)));
			} else if (!closingMap.getClosingMapValue("CLOSING_INFORMATION_DETAIL.CashToBorrowerAtClosingAmount").equals("")) {
				cashToCloseDesc.append(new Paragraph()
				    .append(BoxedCharacter.CHECK_BOX_EMPTY)
			        .append(new FormattedText(" From  ", Text.TABLE_TEXT))
			        .append(BoxedCharacter.CHECK_BOX_NO)
			        .append(new FormattedText(" To  Borrower", Text.TABLE_TEXT)));
			}
		}
		dataGrid.setCellText(1, 2, cashToCloseDesc);
		dataGrid.setLineBorder(1, Position.BOTTOM, Color.BLACK, borderWidth);
	}

	public void draw(Page page, Object d) throws IOException {
		final float location = page.bottomMargin + 1.4f;
		InputData data = (InputData)d;
		initializeTitleGrid();
		initializeDataGrid(data);
		titleGrid.draw(page, page.leftMargin, location);
		dataGrid.draw(page, page.leftMargin, location - dataGrid.height(page));
	}

	public float height(Page page) throws IOException {
		return titleGrid.height(page);
	}

}
