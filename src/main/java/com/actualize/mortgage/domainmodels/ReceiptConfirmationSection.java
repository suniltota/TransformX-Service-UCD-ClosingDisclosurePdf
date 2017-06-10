package com.actualize.mortgage.domainmodels;

import java.io.IOException;

import com.actualize.mortgage.datalayer.ClosingMap;
import com.actualize.mortgage.datalayer.InputData;
import com.actualize.mortgage.pdfbuilder.Color;
import com.actualize.mortgage.pdfbuilder.FormattedText;
import com.actualize.mortgage.pdfbuilder.Grid;
import com.actualize.mortgage.pdfbuilder.Page;
import com.actualize.mortgage.pdfbuilder.Section;
import com.actualize.mortgage.pdfbuilder.Text;
import com.actualize.mortgage.pdfbuilder.Typeface;
import com.actualize.mortgage.pdfbuilder.Grid.Dimension;
import com.actualize.mortgage.pdfbuilder.Grid.Position;
import com.actualize.mortgage.pdfbuilder.TextBox.Direction;
import com.actualize.mortgage.pdfbuilder.TextBox.HorizontalAlignment;
import com.actualize.mortgage.pdfbuilder.TextBox.VerticalAlignment;

public class ReceiptConfirmationSection implements Section {
	public static final Text TAB               = new Text(Color.WHITE, 11, Typeface.CALIBRI_BOLD);
	public static final Text TEXT              = new Text(Color.BLACK, 9, Typeface.CALIBRI);
	private Grid titleGrid, signatureGrid;
	private float location;
	private static final float leftIndent  = 2f/72f;

	ReceiptConfirmationSection(float location) {
		this.location = location;
	}
	
	private void initializeTitleGrid() {
		float heights[] = { 12f/72f, 0.75f };
		float widths[] = { 7.5f };
		titleGrid = new Grid(2, heights, 1, widths);
		titleGrid.setLineVerticalAlignment(1, Dimension.ROW, VerticalAlignment.TOP);
		titleGrid.setLineMargin(1, Dimension.ROW, Direction.TOP, 0f/72f);
		titleGrid.setLineWrap(1, Dimension.ROW, true);
		titleGrid.getCell(0, 0)
			.setBackground(new Tab())
			.setMargin(Direction.LEFT, leftIndent)
			.setForeground(new FormattedText("Confirm Receipt", TAB));
		titleGrid.setLineBorder(0, Position.BOTTOM, Color.BLACK);
		titleGrid.setCellText(1, 0, new FormattedText("By signing, you are only confirming that you have received this " +
				"form. You do not have to accept this loan because you have signed or received      this form.", TEXT));

	}
	
	private void initializeSignatureGrid() {
		float heights[] = { 12f/72f };
		float widths[] = { 1.75f, 1.75f, 0.5f, 1.75f, 1.75f };
		signatureGrid = new Grid(1, heights, 5, widths);
		signatureGrid.setLineBorder(0, Position.TOP, Color.BLACK);
		signatureGrid.setCellBorder(0, 2, Position.TOP, null);
		signatureGrid.setCellText(0, 0, new FormattedText("Applicant Signature", TEXT));
		signatureGrid.setCellText(0, 1, new FormattedText("Date", TEXT));
		signatureGrid.setCellText(0, 3, new FormattedText("Co-Applicant Signature", TEXT));	
		signatureGrid.setCellText(0, 4, new FormattedText("Date", TEXT));
		signatureGrid.setLineHorizontalAlignment(1, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		signatureGrid.setLineHorizontalAlignment(4, Dimension.COLUMN, HorizontalAlignment.RIGHT);
	}
	
	public void draw(Page page, Object d) throws IOException {
		InputData data = (InputData)d;
		ClosingMap closingMap = data.getClosingMap();
		if (!closingMap.getClosingMapValue("DOCUMENT_CLASSIFICATION_DETAIL.DocumentSignatureRequiredIndicator").equalsIgnoreCase("false")) {
			initializeTitleGrid();
			initializeSignatureGrid();	
			titleGrid.draw(page, 0.5f, this.location + signatureGrid.height(page));
			signatureGrid.draw(page, 0.5f, this.location);
		}
	}
	public static void drawTitleGrid(Page page,float location) throws IOException{
	    ReceiptConfirmationSection section = new ReceiptConfirmationSection(location);
	    section.initializeTitleGrid();
	    section.getTitleGrid().draw(page, 0.5f, location);
	}
	public static void drawMethod(Page page, Object d, float location) throws IOException{
		ReceiptConfirmationSection section = new ReceiptConfirmationSection(location);
		section.draw(page, d);
		}

	
	public float height(Page page) throws IOException {
		return titleGrid.height(page) + signatureGrid.height(page);
	}
	
	public Grid getTitleGrid(){
	    return titleGrid;
	}
}
