package com.actualize.closingdisclosure.domainmodels;

import java.io.IOException;

import com.actualize.closingdisclosure.datalayer.InputData;
import com.actualize.closingdisclosure.datalayer.SubjectProperty;
import com.actualize.closingdisclosure.pdfbuilder.Color;
import com.actualize.closingdisclosure.pdfbuilder.FormattedText;
import com.actualize.closingdisclosure.pdfbuilder.Grid;
import com.actualize.closingdisclosure.pdfbuilder.Page;
import com.actualize.closingdisclosure.pdfbuilder.Paragraph;
import com.actualize.closingdisclosure.pdfbuilder.Section;
import com.actualize.closingdisclosure.pdfbuilder.Text;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Position;

public class PropertyAddendumSection implements Section {
	private Grid titleGrid, propertyGrid;
	private InputData data;
	public float rowHeight = 12f/72f;
	
	public static Boolean IsSectionRequired(InputData data) {
		SubjectProperty property = data.getSubjectProperty();
		return !property.getUnparsedLegalDescription().isEmpty();
	}
	
	private void initializeTitleGrid() {
		float heights[] = { rowHeight };
		float widths[] = { 7.5f };
		titleGrid = new Grid(1, heights, 1, widths);
		titleGrid.setCellBackground(0, 0, new Tab(2.75f));
		titleGrid.setCellText(0, 0, new FormattedText("   Property Addendum", Text.SECTION_HEADER));
		titleGrid.setLineBorder(0, Position.BOTTOM, Color.BLACK);
	}
	
	private Grid initializePropertyGrid(SubjectProperty property) {
		float heights[] = { 1.5f*rowHeight, rowHeight };
		float widths[]  = { 7.5f };
		
		propertyGrid = new Grid(2, heights, 1, widths);
		Paragraph label = new Paragraph().append(new FormattedText("Property Legal Description",Text.TABLE_HEADER));
		propertyGrid.setCellText(0, 0, label);
		Paragraph description = new Paragraph().append(new FormattedText(property.getUnparsedLegalDescription(),Text.TABLE_TEXT));
		propertyGrid.setCellText(1, 0, description);
		return propertyGrid;
	}

	public void draw(Page page, Object d) throws IOException {
		data = (InputData)d;
		SubjectProperty property = data.getSubjectProperty();
		float thisLocation = page.height - page.topMargin - rowHeight;
		initializeTitleGrid();
		titleGrid.draw(page, page.leftMargin, page.height - page.topMargin - rowHeight);
		if (!property.getUnparsedLegalDescription().isEmpty()) {
			propertyGrid = initializePropertyGrid(property);
			thisLocation = thisLocation - propertyGrid.height(page) - rowHeight;
			propertyGrid.draw(page, page.leftMargin, thisLocation);
		}
	}
}
