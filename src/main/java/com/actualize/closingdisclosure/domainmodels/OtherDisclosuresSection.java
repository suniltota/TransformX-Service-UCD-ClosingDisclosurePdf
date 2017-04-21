package com.actualize.closingdisclosure.domainmodels;

import java.io.IOException;

import com.actualize.closingdisclosure.datalayer.ClosingMap;
import com.actualize.closingdisclosure.datalayer.InputData;
import com.actualize.closingdisclosure.pdfbuilder.BoxedCharacter;
import com.actualize.closingdisclosure.pdfbuilder.Bullet;
import com.actualize.closingdisclosure.pdfbuilder.Color;
import com.actualize.closingdisclosure.pdfbuilder.FormattedText;
import com.actualize.closingdisclosure.pdfbuilder.Grid;
import com.actualize.closingdisclosure.pdfbuilder.LineFeed;
import com.actualize.closingdisclosure.pdfbuilder.Page;
import com.actualize.closingdisclosure.pdfbuilder.Region;
import com.actualize.closingdisclosure.pdfbuilder.Section;
import com.actualize.closingdisclosure.pdfbuilder.Text;
import com.actualize.closingdisclosure.pdfbuilder.Typeface;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Dimension;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Position;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.Direction;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.HorizontalAlignment;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.VerticalAlignment;

public class OtherDisclosuresSection implements Section {
	public static final Text TAB               = new Text(Color.WHITE, 11, Typeface.CALIBRI_BOLD);
	public static final Text TEXT_BOLD         = new Text(Color.BLACK, 10, Typeface.CALIBRI_BOLD);
	public static final Text TEXT              = new Text(Color.BLACK, 9, Typeface.CALIBRI);
	
	private float width = 3.75f;

	private static final float leftIndent  = 2f/72f;

	private float heights[] = { 12f / 72f };
	private float widths[] = { width };
	private Grid titleGrid = new Grid(1, heights, 1, widths);
	private Region info = new Region();

	private void initializeTitleGrid() {
		titleGrid.getCell(0, 0)
			.setBackground(new Tab())
			.setMargin(Direction.LEFT, leftIndent)
			.setForeground(new FormattedText("  Other Disclosures", TAB));
		titleGrid.setLineBorder(0, Position.BOTTOM, Color.BLACK);
	}

	private void insertText(Page page, InputData data) throws IOException {
		final float headerPre  = 2f/72f;
		final float headerPost = 0f/72f;
		ClosingMap closingMap = data.getClosingMap();
		Grid grid;
		String str;

		// Appraisal Paragraph
		info.append(new LineFeed(headerPre))
			.append(new FormattedText("Appraisal", TEXT_BOLD))
			.append(new LineFeed(headerPost));
		info.append(new FormattedText("If the property was appraised for your loan, your lender is required to give " +
			"you a copy at no additional cost at least 3 days before closing.               If you have not yet received it, please " +
			"contact your lender at the information listed below.", TEXT));
		
		// Contract Details Paragraph
		info.append(new LineFeed(5f/72f))
			.append(new FormattedText("Contract Details", TEXT_BOLD))
			.append(new LineFeed(headerPost))
			.append(new FormattedText("See your note and security instrument for information about", TEXT)).append(new LineFeed(4f/72f));
		final float cdHeights[] = { Grid.DYNAMIC };
		final float cdWidths[] = { 12f/72f, width - 10f/72f };
		grid = new Grid(4, cdHeights, cdWidths.length, cdWidths);
		grid.setLineVerticalAlignment(0, Dimension.COLUMN, VerticalAlignment.TOP);
		grid.setLineVerticalAlignment(1, Dimension.COLUMN, VerticalAlignment.TOP);
	    grid.setLineMargin(0, Dimension.COLUMN, Direction.TOP, -1f/72f);
		grid.setLineMargin(1, Dimension.COLUMN, Direction.TOP, -1f/72f);
		grid.setLineWrap(1, Dimension.COLUMN, true);
		//grid.setLineHorizontalAlignment(0, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		grid.setLineHorizontalAlignment(0, Dimension.COLUMN, HorizontalAlignment.RIGHT);
		grid.getCell(0, 0).setForeground(Bullet.BULLET).setMargin(Direction.RIGHT, -3f/72f);
		grid.getCell(0, 1).setForeground(new FormattedText(" what happens if you fail to make your payments,", TEXT)).setMargin(Direction.TOP, -4f/72f);
		grid.getCell(1, 0).setForeground(Bullet.BULLET).setMargin(Direction.RIGHT, -3f/72f);
		grid.getCell(1, 1).setForeground(new FormattedText(" what is a default on the loan,", TEXT)).setMargin(Direction.TOP, -4f/72f);
		grid.getCell(2, 0).setForeground(Bullet.BULLET).setMargin(Direction.RIGHT, -3f/72f);
		grid.getCell(2, 1).setForeground(new FormattedText(" situations in which your lender can require early "
			+ "repayment of the       loan, and", TEXT)).setMargin(Direction.TOP, -4f/72f);
		grid.getCell(3, 0).setForeground(Bullet.BULLET).setMargin(Direction.RIGHT, -3f/72f);
		grid.getCell(3, 1).setForeground(new FormattedText(" the rules for making payments before they are due.", TEXT)).setMargin(Direction.TOP, -4f/72f);
		info.append(grid);
		
		// Liability after Foreclosure Paragraph
		str = closingMap.getClosingMapValue("FORECLOSURE_DETAIL.DeficiencyRightsPreservedIndicator");
		Boolean rightsPreserved = Boolean.parseBoolean(str);
		BoxedCharacter boxMayProtect = BoxedCharacter.CHECK_BOX_EMPTY;
		BoxedCharacter boxDoesntProtect = BoxedCharacter.CHECK_BOX_EMPTY;
		if (rightsPreserved == true)
			boxDoesntProtect = BoxedCharacter.CHECK_BOX_NO;
		else
			boxMayProtect = BoxedCharacter.CHECK_BOX_NO;
		info.append(new LineFeed(4f/72f))
			.append(new FormattedText("Liability after Foreclosure", TEXT_BOLD))
			.append(new LineFeed(headerPost))
			.append(new FormattedText("If your lender forecloses on this property and the foreclosure does not cover " +
				"the amount of unpaid balance on this loan,", TEXT));
		grid = new Grid(2, cdHeights, cdWidths.length, cdWidths);
		grid.setLineVerticalAlignment(0, Dimension.COLUMN, VerticalAlignment.TOP);
		grid.setLineVerticalAlignment(1, Dimension.COLUMN, VerticalAlignment.TOP);
		grid.setLineMargin(0, Dimension.COLUMN, Direction.TOP, 1f/72f);
		grid.setLineMargin(1, Dimension.COLUMN, Direction.TOP, -2f/72f);
		grid.setLineWrap(1, Dimension.COLUMN, true);
		grid.getCell(0, 0).setForeground(boxMayProtect).setMargin(Direction.TOP, 4f/72f);
		grid.getCell(0, 1).setForeground(new FormattedText("state law may protect you from liability for the unpaid " +
			"balance. If you refinance or take on any additional debt on this property, you may    lose this protection " +
			"and have to pay any debt remaining even after foreclosure. You may want to consult a lawyer for more " +
			"information.", TEXT)).setMargin(Direction.TOP, 2f/72f);
		grid.getCell(1, 0).setForeground(boxDoesntProtect);
		grid.getCell(1, 1).setForeground(new FormattedText("state law does not protect you from liability for the " +
			"unpaid balance.", TEXT));
		info.append(grid);

		// Loan Acceptance Section
		if (closingMap.getClosingMapValue("DOCUMENT_CLASSIFICATION_DETAIL.DocumentSignatureRequiredIndicator").equalsIgnoreCase("false")) {
			info.append(new LineFeed(headerPre))
				.append(new FormattedText("Loan Acceptance", TEXT_BOLD))
				.append(new LineFeed(headerPost))
				.append(new FormattedText("You do not have to accept this loan because you have received this form or signed a loan application.", TEXT));
		}
		
		// Refinance Section
		info.append(new LineFeed(7f/72f))
			.append(new FormattedText("Refinance", TEXT_BOLD))
			.append(new LineFeed(headerPost))
			.append(new FormattedText("Refinancing this loan will depend on your future financial situation,         the property value, and market " +
				"conditions. You may not be able to refinance this loan.", TEXT));
		
		// Tax Deductions Section
		info.append(new LineFeed(7f/72f))
			.append(new FormattedText("Tax Deductions", TEXT_BOLD))
			.append(new LineFeed(headerPost))
			.append(new FormattedText("If you borrow more than this property is worth, the interest on the      loan " +
					"amount above this property's fair market value is not deductible from your federal income " +
					"taxes. You should consult a tax advisor for more information.", TEXT));
	}

	public void draw(Page page, Object d) throws IOException {
		InputData data = (InputData) d;

		initializeTitleGrid();
		insertText(page, data);
		info.wrapAt(page, width);
		
		final float location = page.height - page.topMargin - titleGrid.height(page) - info.height(page);
		titleGrid.draw(page, page.width - page.rightMargin - width, location + info.height(page));		
		info.draw(page, page.width - page.rightMargin - width, location);
	}

	public float height(Page page) throws IOException {
		return titleGrid.height(page) + info.height(page);
	}
}
