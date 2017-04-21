package com.actualize.closingdisclosure.domainmodels;

import java.io.IOException;

import com.actualize.closingdisclosure.datalayer.ClosingMap;
import com.actualize.closingdisclosure.datalayer.InputData;
import com.actualize.closingdisclosure.pdfbuilder.Color;
import com.actualize.closingdisclosure.pdfbuilder.FormattedText;
import com.actualize.closingdisclosure.pdfbuilder.Grid;
import com.actualize.closingdisclosure.pdfbuilder.Page;
import com.actualize.closingdisclosure.pdfbuilder.Paragraph;
import com.actualize.closingdisclosure.pdfbuilder.Section;
import com.actualize.closingdisclosure.pdfbuilder.Text;
import com.actualize.closingdisclosure.pdfbuilder.Typeface;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Dimension;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Position;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.Direction;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.HorizontalAlignment;

public class LoanCalculationsSection implements Section {
	public static final Text TAB               = new Text(Color.WHITE, 11, Typeface.CALIBRI_BOLD);
	public static final Text TEXT_BOLD         = new Text(Color.BLACK, 10, Typeface.CALIBRI_BOLD);
	public static final Text AMOUNTS           = new Text(Color.BLACK, 11, Typeface.CALIBRI);
	public static final Text TEXT              = new Text(Color.BLACK, 9, Typeface.CALIBRI);

	private static final float leftIndent  = 2f/72f;

	private Grid titleGrid, dataGrid;
	
	ClosingMap closingMap = null;
	float totalAmount = 0f;

	// Column numbers
	private final int colDescription = 0;
	private final int colValue = 1;

	private void initializeTitleGrid() {
		float heights[] = { 12f / 72f };
		float widths[] = { Grid.DYNAMIC, 5.5f / 2 };
		titleGrid = new Grid(1, heights, 1, widths);
		titleGrid.getCell(0, 0)
			.setBackground(new Tab())
			.setMargin(Direction.LEFT, leftIndent)
			.setForeground(new FormattedText("Loan Calculations", TAB));
		titleGrid.setLineBorder(0, Position.BOTTOM, Color.BLACK);
	}

	private void initializeDataGrid() {
		float heights[] = { 43f/72f, 31f/72f, 31f/72f, 43f/72f, 43f/72f };
		float widths[] = { 2.7f, 0.8f };
		// look ahead to handle amounts of 1 million or more
		if (totalAmount > 999999.99)
		  	 widths[1] = 0.92f;
		dataGrid = new Grid(5, heights, 2, widths);
		for (int row = 0; row < dataGrid.rows(); row++)
			dataGrid.setLineBorder(row, Position.TOP, Color.BLACK);
		dataGrid.setLineBorder(dataGrid.rows() - 1, Position.BOTTOM, Color.BLACK);
		dataGrid.setLineBorder(colValue, Position.LEFT, Color.BLACK);
		dataGrid.setLineHorizontalAlignment(colValue, Dimension.COLUMN, HorizontalAlignment.RIGHT);
	}

	private void insertText(Page page, InputData data) throws IOException {
		//ClosingMap closingMap = data.getClosingMap();
		Paragraph para;
		String str;
		int row = 0;

		// Line 1
		para = (new Paragraph()).append(new FormattedText("Total of Payments. ", TEXT_BOLD))
			.append(new FormattedText("Total you will have paid after you make all payments of principal, interest, mortgage insurance, and loan " +
				"costs, as scheduled.", TEXT));
		dataGrid.setLineMargin(row, Dimension.ROW, Direction.BOTTOM, 9f/72f);
		dataGrid.setCellText(row, colDescription, para);
		dataGrid.setCellWrap(row, colDescription, true);
		str = closingMap.getClosingMapValue("FEE_SUMMARY_DETAIL.FeeSummaryTotalOfAllPaymentsAmount");
		if (!str.equals("")){
			if (data.isDocsDirect()){
				dataGrid.setCellText(row, colValue, new FormattedText(StringFormatter.DOLLARS.formatString(str), Text.SECTION_TEXT));
			} else {
				dataGrid.setCellText(row, colValue, new FormattedText(StringFormatter.DOLLARS.formatString(str), AMOUNTS));
			}
		}

		// Line 2
		para = (new Paragraph()).append(new FormattedText("Finance Charge. ", TEXT_BOLD))
			.append(new FormattedText("The dollar amount the loan will cost you.", TEXT));
		dataGrid.setLineMargin(++row, Dimension.ROW, Direction.BOTTOM, 9f/72f);
		dataGrid.setCellText(row, colDescription, para);
		dataGrid.setCellWrap(row, colDescription, true);
		str = closingMap.getClosingMapValue("FEE_SUMMARY_DETAIL.FeeSummaryTotalFinanceChargeAmount");
		if (!str.equals(""))
			if (data.isDocsDirect()){
				dataGrid.setCellText(row, colValue, new FormattedText(StringFormatter.DOLLARS.formatString(str), Text.SECTION_TEXT));
			} else {
				dataGrid.setCellText(row, colValue, new FormattedText(StringFormatter.DOLLARS.formatString(str), AMOUNTS));
			}

		// Line 3
		para = (new Paragraph()).append(new FormattedText("Amount Financed. ", TEXT_BOLD))
			.append(new FormattedText("The loan amount available after paying your upfront finance charge.", TEXT));
		dataGrid.setLineMargin(++row, Dimension.ROW, Direction.BOTTOM, 9f/72f);
		dataGrid.setCellText(row, colDescription, para);
		dataGrid.setCellWrap(row, colDescription, true);
		str = closingMap.getClosingMapValue("FEE_SUMMARY_DETAIL.FeeSummaryTotalAmountFinancedAmount");
		if (!str.equals("")){
			if (data.isDocsDirect()){
				dataGrid.setCellText(row, colValue, new FormattedText(StringFormatter.DOLLARS.formatString(str), Text.SECTION_TEXT));
			} else {
				dataGrid.setCellText(row, colValue, new FormattedText(StringFormatter.DOLLARS.formatString(str), AMOUNTS));
			}
		}

		// Line 4
		para = (new Paragraph()).append(new FormattedText("Annual Percentage Rate (APR). ", TEXT_BOLD))
			.append(new FormattedText("Your costs over the loan term expressed as a rate. This is not your interest rate.", TEXT));
		dataGrid.setLineMargin(++row, Dimension.ROW, Direction.BOTTOM, 9f/72f);
		dataGrid.setCellText(row, colDescription, para);
		dataGrid.setCellWrap(row, colDescription, true);
		str = closingMap.getClosingMapValue("FEE_SUMMARY_DETAIL.APRPercent");
		if (!str.equals("")){
			if (data.isDocsDirect()){
				dataGrid.setCellText(row, colValue, new FormattedText(StringFormatter.NUMBERTHREEDIGITS.formatString(str) + "%", Text.SECTION_TEXT));
			} else {
				dataGrid.setCellText(row, colValue, new FormattedText(StringFormatter.NUMBERTHREEDIGITS.formatString(str) + "%", AMOUNTS));
			}
		}

		// Line 5
		para = (new Paragraph()).append(new FormattedText("Total Interest Percentage (TIP). ", TEXT_BOLD))
			.append(new FormattedText("The total amount of interest that you will pay over the loan term as a percentage of your " +
				"loan amount.", TEXT));
		dataGrid.setLineMargin(++row, Dimension.ROW, Direction.BOTTOM, 9f/72f);
		dataGrid.setCellText(row, colDescription, para);
		dataGrid.setCellWrap(row, colDescription, true);
		str = closingMap.getClosingMapValue("FEE_SUMMARY_DETAIL.FeeSummaryTotalInterestPercent");
		if (!str.equals("")){
			if (data.isDocsDirect()){
				dataGrid.setCellText(row, colValue, new FormattedText(StringFormatter.NUMBERTHREEDIGITS.formatString(str) + "%", Text.SECTION_TEXT));
			} else {
				dataGrid.setCellText(row, colValue, new FormattedText(StringFormatter.NUMBERTHREEDIGITS.formatString(str) + "%", AMOUNTS));
			}
		}
	}

	public void draw(Page page, Object d) throws IOException {
		InputData data = (InputData) d;
		
		initializeTitleGrid();
		//look ahead to handle Loan Calculation section amount 1 mill and over
		closingMap = data.getClosingMap();
		if (!closingMap.getClosingMapValue("FEE_SUMMARY_DETAIL.FeeSummaryTotalOfAllPaymentsAmount").isEmpty())
			totalAmount = Float.valueOf(closingMap.getClosingMapValue("FEE_SUMMARY_DETAIL.FeeSummaryTotalOfAllPaymentsAmount"));
		initializeDataGrid();
		insertText(page, data);
		
		final float location = page.height - page.topMargin - titleGrid.height(page) - dataGrid.height(page);
		titleGrid.draw(page, page.leftMargin, location + dataGrid.height(page));
		dataGrid.draw(page, page.leftMargin, location);
	}

	public float height(Page page) throws IOException {
		return titleGrid.height(page) + dataGrid.height(page);
	}
}
