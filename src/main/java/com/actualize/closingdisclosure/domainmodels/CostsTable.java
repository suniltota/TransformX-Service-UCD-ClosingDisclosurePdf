package com.actualize.closingdisclosure.domainmodels;

import java.io.IOException;
import java.util.ArrayList;

import com.actualize.closingdisclosure.domainmodels.CostsTableRow.Columns;
import com.actualize.closingdisclosure.pdfbuilder.Color;
import com.actualize.closingdisclosure.pdfbuilder.Drawable;
import com.actualize.closingdisclosure.pdfbuilder.FormattedText;
import com.actualize.closingdisclosure.pdfbuilder.Grid;
import com.actualize.closingdisclosure.pdfbuilder.Page;
import com.actualize.closingdisclosure.pdfbuilder.Text;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Dimension;
import com.actualize.closingdisclosure.pdfbuilder.Grid.Position;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.Direction;
import com.actualize.closingdisclosure.pdfbuilder.TextBox.HorizontalAlignment;

class CostsTable {
	protected Drawable headerLabel = null;
	protected Drawable headerAmt = null;
	private Color lineColor = Color.LIGHT_GRAY;
	private ArrayList<CostsTableRow> lines = new ArrayList<CostsTableRow>();
	private int preferedLines = 0;

	private static final float leftIndent  = 2f/72f;	
	
	protected Drawable getSellerAmount() {
		return null;
	}
	protected Drawable getSellerAmountOutside() {
		return null;
	}

	public static void stretch(Page page, ArrayList<CostsTable> tables, int[] stretchTables, float widths[], float height) {
		int numTables = tables.size();
		
		// Subtract already used lines
		for (int i = 0; i < numTables; i++)
			height -= tables.get(i).getHeight(page, widths);
		
		// Stretch tables by adding one line at a time to each stretch table until all tables have reached
		// their preferred size, or the height limit is reached
		int stretchTableIndex = 0;
		boolean done = false;
		int donecount = 0;
		while (!done) {
			if (donecount >= stretchTables.length || height < CostsTableRow.lineHeight)
				done = true;
			else if (tables.get(stretchTables[stretchTableIndex]).preferedLines <= tables.get(stretchTables[stretchTableIndex]).numLines()) {
		//		System.out.println("" + stretchTables[stretchTableIndex] + ", " + tables.get(stretchTables[stretchTableIndex]).preferedLines + ", " + tables.get(stretchTables[stretchTableIndex]).numLines());
				donecount++;
			} else {
				donecount = 0;
				float beforeHeight = tables.get(stretchTables[stretchTableIndex]).getHeight(page, widths);
				tables.get(stretchTables[stretchTableIndex]).addEmptyLines(1);			
				float afterHeight = tables.get(stretchTables[stretchTableIndex]).getHeight(page, widths);
				height = height - (afterHeight - beforeHeight);
		//		if (beforeHeight == afterHeight)
		//			done = true;
		//		System.out.println("" + stretchTables[stretchTableIndex] + ", " + beforeHeight + ", " + afterHeight + ", " + height);
			}
			stretchTableIndex = (stretchTableIndex + 1) % stretchTables.length;
		}
	}
	
	CostsTable() {
	}
	
	CostsTable(int preferedLines, Color lineColor) {
		this.preferedLines = preferedLines;
		this.lineColor = lineColor;
	}
	
	CostsTable setHeader(String label, String amt) {
		headerLabel = new FormattedText(label, Text.ROW_HEADER);
		headerAmt = new FormattedText(amt, Text.TABLE_TEXT_BOLD);
		return this;
	}

	void addEmptyLines(int count) {
		for (int i = 0; i < count; i++)
			lines.add(new CostsTableRow());
	}

	void addRow(CostsTableRow row) {
		lines.add(row);
	}

	void addRow(int i, CostsTableRow row) {
		lines.add(i, row);
	}
	
	CostsTableRow getRow(int i) {
		return lines.get(i);
	}
	
	float getHeight(Page page, float[] widths) {
		float height = 0;
		float[] heights = getHeights(page, widths);
		for (int i = 0; i < heights.length; i++)
			height += heights[i];
		return height;
	}
	
	float getHeaderHeight(Page page, float[] widths) {
		float height = 0;
		try {
			if (headerLabel != null)
				height = headerLabel.height(page, widths[0] + widths[1] + widths[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return height;
	}

	float[] getHeights(Page page, float widths[]) {
		int offset = headerLabel != null ? 1 : 0;
		float[] heights = new float[lines.size() + offset];
		if (offset > 0)
			heights[0] = getHeaderHeight(page, widths);
		for (int i = 0; i < lines.size(); i++)
			heights[i+offset] = lines.get(i).getHeight(page, widths);
		return heights;
	}
	
	int numLines() {
		return lines.size();
	}
	
	void drawDataGrid(Page page, float[] widths, Columns[] columns, float x, float y) {
		float[] heights = getHeights(page, widths);
		Grid dataGrid = new Grid(heights.length, heights, widths.length, widths);
		int offset = headerLabel != null ? 1 : 0;
		try {
			if (offset > 0) {
				for (int i = 0; i < 3; i++)
					dataGrid.setCellShade(0, i, Color.LIGHT_GRAY);
				dataGrid.setLineBorder(0, Position.TOP, Color.BLACK, 1f/72f);
				//dataGrid.setLineBorder(0, Position.BOTTOM, Color.BLACK, 1f/72f);
				dataGrid.setLineBorder(0, Position.BOTTOM, Color.LIGHT_GRAY, 1f/72f);
				dataGrid.getCell(0, 0).setMargin(Direction.LEFT, leftIndent).setForeground(headerLabel);
				if (getSellerAmount() != null)
					dataGrid.getCell(0, 3).setForeground(getSellerAmount());
				if (getSellerAmountOutside() != null)
					dataGrid.getCell(0, 4).setForeground(getSellerAmountOutside());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < lines.size(); i++) {
			dataGrid.setLineBorder(i+offset, Position.BOTTOM, lineColor, 1f/72f);
			if (lines.get(i).item(Columns.Number) != null)
				dataGrid.getCell(i+offset, 0).setMargin(Direction.LEFT, leftIndent).setForeground(lines.get(i).item(Columns.Number));
			else
				dataGrid.getCell(i+offset, 0).setMargin(Direction.LEFT, leftIndent).setForeground(new FormattedText(String.format("%02d", i+1), Text.TABLE_NUMBER));
			dataGrid.setCellText(i+offset, 1, lines.get(i).item(Columns.CostLabel));
			dataGrid.setCellText(i+offset, 2, lines.get(i).item(Columns.ToEntity));
			for (int j = 0; j < columns.length; j++)
				dataGrid.setCellText(i+offset, j+3, lines.get(i).item(columns[j]));
		}
		for (int i = 0; i < columns.length; i++) {
			dataGrid.setLineHorizontalAlignment(i+3, Dimension.COLUMN, HorizontalAlignment.RIGHT);
			dataGrid.setLineMargin(i+3, Dimension.COLUMN, Direction.RIGHT, 2f/72f);
			dataGrid.setLineBorder(i+3, Position.LEFT, Color.DARK_GRAY, (i%2==0) ? 2f/72f : 1f/72f);
		}
		if (columns.length > 3 && dataGrid.rows() > 0)
			dataGrid.setCellBorder(0, 6, Position.LEFT, null, 0);
		try {
			dataGrid.draw(page, x, y);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void drawOverlayGrid(Page page, float[] widths, Columns[] columns, float x, float y) {
		if (headerLabel == null || columns.length == 0 || columns[0] != Columns.BuyerAtClosing)
			return;
		float[] owidths = { widths[0] + widths[1] + widths[2], widths[3] + widths[4], widths[widths.length - 1] };
		float[] heights = new float[1];
		heights[0] = getHeaderHeight(page, widths);
		if (heights[0] == 0)
			return;
		Grid overlayGrid = new Grid(heights.length, heights, owidths.length, owidths);
		try {
			overlayGrid.setLineBorder(0, Position.TOP, Color.BLACK, 1f/72f);
			overlayGrid.setLineBorder(1, Position.LEFT, Color.DARK_GRAY, 2f/72f);
			overlayGrid.setLineBorder(1, Position.RIGHT, Color.DARK_GRAY, 2f/72f);
			overlayGrid.setLineHorizontalAlignment(1, Dimension.COLUMN, HorizontalAlignment.CENTER);
			overlayGrid.setCellShade(0, 1, Color.LIGHT_GRAY);
			overlayGrid.setCellText(0, 1, headerAmt);
			overlayGrid.draw(page, x, y - heights[0]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void draw(Page page, float[] widths, Columns[] columns, float x, float y) {
		drawDataGrid(page, widths, columns, x, y);
		drawOverlayGrid(page, widths, columns, x, y + getHeight(page, widths));
	}
}
