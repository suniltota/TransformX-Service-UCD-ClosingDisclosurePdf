package com.actualize.mortgage.domainmodels;

import java.io.IOException;

import com.actualize.mortgage.datalayer.InputData;
import com.actualize.mortgage.pdfbuilder.Color;
import com.actualize.mortgage.pdfbuilder.FormattedText;
import com.actualize.mortgage.pdfbuilder.Grid;
import com.actualize.mortgage.pdfbuilder.Page;
import com.actualize.mortgage.pdfbuilder.Paragraph;
import com.actualize.mortgage.pdfbuilder.Section;
import com.actualize.mortgage.pdfbuilder.Text;
import com.actualize.mortgage.pdfbuilder.Typeface;
import com.actualize.mortgage.pdfbuilder.TextBox.Direction;
import com.actualize.mortgage.pdfbuilder.TextBox.VerticalAlignment;

public class QuestionsSection implements Section {
	public static final Text QUESTION          = new Text(Color.LIGHT_GRAY, 72, Typeface.CALIBRI_BOLD);
	public static final Text TEXT              = new Text(Color.BLACK, 9, Typeface.CALIBRI);
	public static final Text TEXT_BOLD         = new Text(Color.BLACK, 9, Typeface.CALIBRI_BOLD);

	final float boxWidth = 3.5f;
	final float boxHeight = 1.3f;
	final float boxBorder = 0.1f;
	final float boxRound = 0.3f;
	
	float x = 0.5f;
	float y = 6.0f;
	
	QuestionsSection(float locationX, float locationY){
		y = locationY;
		x = locationX;
	}
	
	final private Box greyBox = new Box(boxWidth, boxHeight, boxRound, Color.LIGHT_GRAY);
	final private Box whiteBox = new Box(boxWidth-2*boxBorder, boxHeight-2*boxBorder, boxRound/2, Color.WHITE);
	
	final float widths[] = { 0.6f, 2.48f };
	final float heights[] = { 0.7f };
	final private Grid grid = new Grid(heights.length, heights, widths.length, widths);

	private void insertText(Page page, InputData data) throws IOException {
		grid.getCell(0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM).setMargin(Direction.BOTTOM, 8f/72f).setForeground(new FormattedText("?", QUESTION));
		grid.getCell(0, 1).setWrap(true).setMargin(Direction.LEFT, 0).setMargin(Direction.RIGHT, 0).setForeground(new Paragraph()
			.append(new FormattedText("Questions?", TEXT_BOLD))
			.append(new FormattedText(" If you have questions about the   loan terms or costs on this form, "
				+ "use the contact information " + (data.isSellerOnly() ? "above" : "below")
				+ ". To get more information        or make a complaint, "
				+ "contact the Consumer Financial Protection Bureau at", TEXT))
			.append(new FormattedText("www.consumerfinance.gov/mortgage-closing", TEXT_BOLD)));
	}

	public void draw(Page page, Object d) throws IOException {
		insertText(page, (InputData)d);
		greyBox.draw(page, x, y);
		whiteBox.draw(page, x + boxBorder, y + boxBorder);
		grid.draw(page, x + 0.15f, y + 0.2f);
	}

	public float height(Page page) throws IOException {
		return grid.height(page);
	}
}
