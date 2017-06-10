package com.actualize.mortgage.pdfbuilder;

import java.io.IOException;

import com.actualize.mortgage.pdfbuilder.TextBox.Direction;
import com.actualize.mortgage.pdfbuilder.TextBox.HorizontalAlignment;
import com.actualize.mortgage.pdfbuilder.TextBox.VerticalAlignment;

public class Grid extends Drawable {
	private class Border {
		Color color = null;
		float width = 1f/72f;
		private void draw(Page page, float x1, float y1, float x2, float y2) throws IOException {
			if (color != null && width > 0) {
				page.stream().setLineCapStyle(0);
				page.stream().setLineWidth(width*72);
				page.stream().setStrokingColor(color.red(), color.green(), color.blue());
				page.stream().drawLine(page.toPdfX(x1), page.toPdfY(y1), page.toPdfX(x2), page.toPdfY(y2));
			}
		}
	}

	public class Cell {
		private Color shade;
		private Drawable background;
		private Drawable foreground;
		
		private boolean wrappable = false;

		private float marginTop = 2f / 72f;
		private float marginBottom = 3f / 72f;
		private float marginLeft = 1f / 72f;
		private float marginRight = 1f / 72f;
		
		private HorizontalAlignment hAlign = HorizontalAlignment.LEFT;
		private VerticalAlignment vAlign = VerticalAlignment.BOTTOM;
		
		public Cell setBackground(Drawable background) {
			this.background = background;
			return this;
		}
		
		public Cell setForeground(Drawable foreground) {
			this.foreground = foreground;
			return this;
		}
		
		public Cell setShade(Color shade) {
			this.shade = shade;
			return this;
		}
		
		public Cell setWrap(boolean wrap) {
			this.wrappable = wrap;
			return this;
		}
		
		public Cell setHorizontalAlignment(HorizontalAlignment align) {
			this.hAlign = align;
			return this;
		}

		public Cell setVerticalAlignment(VerticalAlignment align) {
			this.vAlign = align;
			return this;
		}

		public Cell setMargin(Direction margin, float amount) {
			switch (margin) {
			case LEFT:
				marginLeft = amount;
				break;
			case RIGHT:
				marginRight = amount;
				break;
			case TOP:
				marginTop = amount;
				break;
			case BOTTOM:
				marginBottom = amount;
				break;
			}
			return this;
		}

		private float bestWidth(Page page) throws IOException {
			float fwidth = foreground == null ? 0 : (foreground.width(page) + marginLeft + marginRight);
			float bwidth = background == null ? 0 : background.width(page);
			return fwidth > bwidth ? fwidth : bwidth;
		}
		
		private float bestHeight(Page page) throws IOException {
			float fheight = foreground == null ? 0 : (foreground.height(page) + marginTop + marginBottom);
			float bheight = background == null ? 0 : background.height(page);
			return fheight > bheight ? fheight : bheight;
		}

		private void drawBackground(Page page, float x, float y, float width, float height) throws IOException {
			if (background != null) {
				background.draw(page, x, y);
			} else if (shade != null) {
				page.stream().setNonStrokingColor(shade.red(), shade.green(), shade.blue());
				page.stream().fillRect(page.toPdfX(x), page.toPdfY(y), page.scaleToPdf(width), page.scaleToPdf(height));
			}
		}

		private void drawForeground(Page page, float x, float y, float width, float height) throws IOException {
			if (foreground != null) {
				foreground.draw(page, x+horizontalAdjustment(page, width), y+verticalAdjustment(page, height));
			}
		}

		private float horizontalAdjustment(Page page, float width) throws IOException {
			float adjustment = 0;
			switch (this.hAlign) {
			case LEFT:
				adjustment = marginLeft;
				break;
			case CENTER:
				adjustment = (width - foreground.width(page) - marginRight + marginLeft) / 2;
				break;
			case RIGHT:
				adjustment = width - foreground.width(page) - marginRight;
				break;
			}
			return adjustment;
		}

		private float verticalAdjustment(Page page, float height) throws IOException {
			float adjustment = 0;
			switch (this.vAlign) {
			case TOP:
				adjustment = height - foreground.height(page) - marginTop;
				break;
			case MIDDLE:
				adjustment = (height - foreground.height(page) - marginTop + marginBottom) / 2;
				break;
			case BOTTOM:
				adjustment = marginBottom;
				break;
			}
			return adjustment;
		}

		private void wrapAt(Page page, float width) throws IOException {
			if (foreground==null || !wrappable || !foreground.canSplitHorizontally(page, width))
				return;
			Region region = (new Region()).append(foreground);
			region.wrapAt(page, width - marginLeft - marginRight);
			foreground = region;
		}
	}

	public enum Position { TOP, LEFT, BOTTOM, RIGHT; }
	public enum Dimension { ROW, COLUMN; }
	
	public static final float DYNAMIC = -1;

	private float heights[];
	private float widths[];
	private Cell cell[][];
	private Border borderRow[][], borderColumn[][];

	public Grid(int nrows, float heights[], int ncolumns, float widths[]) {
		// Determine row heights
		this.heights = new float[nrows];
		for (int i = 0; i < nrows; i++)
			this.heights[i] = (i < heights.length) ? heights[i] : heights[heights.length-1];

		// Determine column widths
		this.widths  = new float[ncolumns];
		for (int i = 0; i < ncolumns; i++)
			this.widths[i] = (i < widths.length) ? widths[i] : widths[widths.length-1];

		// Create grid cells
		cell = new Cell[nrows][ncolumns];
		for (int row = 0; row < nrows; row++)
			for (int column = 0; column < ncolumns; column++)
				cell[row][column] = new Cell();
		
		// Create grid borders
		borderRow = new Border[nrows+1][ncolumns];
		for (int row = 0; row < nrows+1; row++)
			for (int column = 0; column < ncolumns; column++)
				borderRow[row][column] = new Border();
		borderColumn = new Border[nrows][ncolumns+1];
		for (int row = 0; row < nrows; row++)
			for (int column = 0; column < ncolumns+1; column++)
				borderColumn[row][column] = new Border();
	}
	
	private void drawBackground(Page page, float startX, float startY) throws IOException {
		float y = startY;
		for (int row = 0; row < heights.length; row++) {
			float x = startX;
			float height = getSize(page, Dimension.ROW, row);
			y -= height;
			for (int column = 0; column < widths.length; column++) {
				float width = getSize(page, Dimension.COLUMN, column);
				cell[row][column].drawBackground(page, x, y, width, height);
				x += width;
			}
		}
	}
	
	private void drawBorder(Page page, float startX, float startY) throws IOException {
		// Draw verticals
		float y = startY;
		for (int row = 0; row < heights.length + 1; row++) {
			float x = startX;
			float height = (row < heights.length) ? getSize(page, Dimension.ROW, row) : 0;
			for (int column = 0; column < widths.length + 1; column++) {
				float width = (column < widths.length) ? getSize(page, Dimension.COLUMN, column) : 0;
				if (height > 0) borderColumn[row][column].draw(page, x, y-height, x, y);
				x += width;
			}
			y -= height;
		}
		
		// Draw horizontals
		y = startY;
		for (int row = 0; row < heights.length + 1; row++) {
			float x = startX;
			float height = (row < heights.length) ? getSize(page, Dimension.ROW, row) : 0;
			for (int column = 0; column < widths.length + 1; column++) {
				float width = (column < widths.length) ? getSize(page, Dimension.COLUMN, column) : 0;
				if (width > 0) borderRow[row][column].draw(page, x, y, x+width, y);
				x += width;
			}
			y -= height;
		}
	}
	
	private void drawForeground(Page page, float startX, float startY) throws IOException {
		float y = startY;
		for (int row = 0; row < heights.length; row++) {
			float x = startX;
			float height = getSize(page, Dimension.ROW, row);
			wrap(page, row);
			y -= height;
			for (int column = 0; column < widths.length; column++) {
				float width = getSize(page, Dimension.COLUMN, column);
				cell[row][column].drawForeground(page, x, y, width, height);
				x += width;
			}
		}
	}
	
	private void wrap(Page page, int row) throws IOException {
		for (int column = 0; column < widths.length; column++)
			if (widths[column] != DYNAMIC)
				cell[row][column].wrapAt(page, widths[column] - cell[row][column].marginLeft - cell[row][column].marginRight);
	}

	public int rows() {
		return heights.length;
	}
	
	public int columns() {
		return widths.length;
	}
	
	public float width(Page page) throws IOException {
		float width = 0;
		for (int column = 0; column < widths.length; column++)
			width += getSize(page, Dimension.COLUMN, column);
		return width;
	}
	
	public float height(Page page) throws IOException {
		float height = 0;
		for (int row = 0; row < heights.length; row++)
			height += getSize(page, Dimension.ROW, row);
		return height;
	}

	public void draw(Page page, float x, float y) throws IOException {
		y += height(page);
		drawBackground(page, x, y);
		drawBorder(page, x, y);
		drawForeground(page, x, y);
	}
	
	public float getSize(Page page, Dimension dim, int index) throws IOException {
		float size = 0;
		try {
			switch (dim) {
			case ROW:
				if (heights[index] != Grid.DYNAMIC)
					return heights[index];
				wrap(page, index);
				for (int column = 0; column < widths.length; column++) {
					float height = cell[index][column].bestHeight(page);
					if (height > size)
						size = height;
				}
				break;
			case COLUMN:
				if (widths[index] != Grid.DYNAMIC)
					return widths[index];
				for (int row = 0; row < heights.length; row++) {
					float width = cell[row][index].bestWidth(page);
					if (width > size)
						size = width;
				}
				break;
			}
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Grid:getSize - Array index " + index + " is out of Bounds" + e);
			throw e;
		}
		return size;
	}
	
	public Cell getCell(int row, int column) {
		try {
			return cell[row][column];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Grid:getCell - Array index (" + row + "," + column + ") is out of Bounds" + e);
			throw e;
		}
	}
	
	public Drawable getCellTextBox(int row, int column) {
		try {
			return cell[row][column].foreground;
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Grid:getCellTextBox - Array index (" + row + "," + column + ") is out of Bounds" + e);
			throw e;
		}
	}
	
	public void setCellBackground(int row, int column, Drawable drawable) {
		try {
			cell[row][column].setBackground(drawable);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Grid:setCellBackground - Array index (" + row + "," + column + ") is out of Bounds" + e);
			throw e;
		}
	}
	
	public void setCellForeground(int row, int column, Drawable drawable) {
		try {
			cell[row][column].setForeground(drawable);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Grid:setCellForeground - Array index (" + row + "," + column + ") is out of Bounds" + e);
			throw e;
		}
	}
	
	public void setCellBorder(int row, int column, Position position, Color color) {
		setCellBorder(row, column, position, color, 1f/72f);
	}
	
	public void setCellBorder(int row, int column, Position position, Color color, float width) {
		try {
			switch (position) {
			case BOTTOM:
				row += 1;
			case TOP:
				borderRow[row][column].color = color;
				borderRow[row][column].width = width;
				break;
			case RIGHT:
				column += 1;
			case LEFT:
				borderColumn[row][column].color = color;
				borderColumn[row][column].width = width;
			break;
			}
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Grid:setCellBorder - Array index (" + row + "," + column + ") is out of Bounds" + e);
			throw e;
		}
	}
	
	public void setCellShade(int row, int column, Color color) {
		try {
			cell[row][column].setShade(color);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Grid:setCellShade - Array index (" + row + "," + column + ") is out of Bounds" + e);
			throw e;
		}
	}
	
	public void setCellText(int row, int column, Drawable text) {
		try {
			cell[row][column].setForeground(text);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Grid:setCellText - Array index (" + row + "," + column + ") is out of Bounds" + e);
			throw e;
		}
	}
	
	public void setCellWrap(int row, int column, boolean wrap) {
		try {
			cell[row][column].setWrap(wrap);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Grid:setCellBackground - Array index (" + row + "," + column + ") is out of Bounds" + e);
			throw e;
		}
	}

	public void setLineHorizontalAlignment(int index, Dimension dim, HorizontalAlignment align) {
		try {
			switch (dim) {
			case ROW:
				for (int column = 0; column < widths.length; column++)
					cell[index][column].setHorizontalAlignment(align);
				break;
			case COLUMN:
				for (int row = 0; row < heights.length; row++)
					cell[row][index].setHorizontalAlignment(align);
				break;
			}
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Grid:setLineHorizontalAlignment - Array index " + index + " is out of Bounds" + e);
			throw e;
		}
	}

	public void setLineVerticalAlignment(int index, Dimension dim, VerticalAlignment align) {
		try {
			switch (dim) {
			case ROW:
				for (int column = 0; column < widths.length; column++)
					cell[index][column].setVerticalAlignment(align);
				break;
			case COLUMN:
				for (int row = 0; row < heights.length; row++)
					cell[row][index].setVerticalAlignment(align);
				break;
			}
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Grid:setLineVerticalAlignment - Array index " + index + " is out of Bounds" + e);
			throw e;
		}
	}

	public void setLineWrap(int index, Dimension dim, boolean wrap) {
		try {
			switch (dim) {
			case ROW:
				for (int column = 0; column < widths.length; column++)
					cell[index][column].setWrap(wrap);
				break;
			case COLUMN:
				for (int row = 0; row < heights.length; row++)
					cell[row][index].setWrap(wrap);
				break;
			}
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Grid:setLineVerticalAlignment - Array index " + index + " is out of Bounds" + e);
			throw e;
		}
	}
	
	public void setLineBorder(int index, Position position, Color color) {
		setLineBorder(index, position, color, 1f/72f);
	}
	
	public void setLineBorder(int index, Position position, Color color, float width) {
		try {
			switch (position) {
			case TOP:
			case BOTTOM:
				for (int column = 0; column < widths.length; column++)
					setCellBorder(index, column, position, color, width);
				break;
			case LEFT:
			case RIGHT:
				for (int row = 0; row < heights.length; row++)
					setCellBorder(row, index, position, color, width);
				break;
			}
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Grid:setLineBorder - Array index " + index + " is out of Bounds" + e);
			throw e;
		}
	}

	public void setLineHeight(int index, float amount) {
		try {
			heights[index] = amount;
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Grid:setLineMargin - Array index " + index + " is out of Bounds" + e);
			throw e;
		}
	}

	public void setLineMargin(int index, Dimension dim, Direction direction, float amount) {
		try {
			switch (dim) {
			case ROW:
				for (int column = 0; column < widths.length; column++)
					cell[index][column].setMargin(direction, amount);
				break;
			case COLUMN:
				for (int row = 0; row < heights.length; row++)
					cell[row][index].setMargin(direction, amount);
				break;
			}
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Grid:setLineMargin - Array index " + index + " is out of Bounds" + e);
			throw e;
		}
	}

	public void setLineShade(int index, Dimension dim, Color color) {
		try {
			switch (dim) {
			case ROW:
				for (int column = 0; column < widths.length; column++)
					cell[index][column].shade = color;
				break;
			case COLUMN:
				for (int row = 0; row < heights.length; row++)
					cell[row][index].shade = color;
				break;
			}
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Grid:setLineShade - Array index " + index + " is out of Bounds" + e);
			throw e;
		}
	}
}
