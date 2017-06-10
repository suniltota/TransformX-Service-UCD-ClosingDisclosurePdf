package com.actualize.mortgage.domainmodels;

import java.io.IOException;

import com.actualize.mortgage.pdfbuilder.Color;
import com.actualize.mortgage.pdfbuilder.Drawable;
import com.actualize.mortgage.pdfbuilder.Page;

public class Box extends Drawable {
	private final Color color;
	private final float width;  // in inches
	private final float height; // in inches
	private final float border; // in inches

	public Box(float width, float height, float border, Color color) {
		this.width = width;
		this.height = height;
		this.border = border;
		this.color = color;
	}

	public float width(Page page) throws IOException {
		return this.width;
	}

	public float height(Page page) throws IOException {
		return this.height;
	}

	public float border(Page page) throws IOException {
		return this.border;
	}

	public void draw(Page page, float x, float y) throws IOException {
		// Convert to page dimensions
		float x1 = page.toPdfX(x);
		float y1 = page.toPdfY(y);
		float x2 = page.toPdfX(x + width);
		float y2 = page.toPdfY(y + height);
		float pageBorder = page.scaleToPdf(border);

		// Draw top and bottom lines
		page.stream().setStrokingColor(color.red(), color.green(), color.blue());
		page.stream().setLineCapStyle(1);
		page.stream().setLineWidth(pageBorder);
		page.stream().drawLine(x1 + pageBorder/2, y1 + pageBorder/2, x2 - pageBorder/2, y1 + pageBorder/2);
		page.stream().drawLine(x1 + pageBorder/2, y2 - pageBorder/2, x2 - pageBorder/2, y2 - pageBorder/2);

		// Draw rectangle
		page.stream().setNonStrokingColor(color.red(), color.green(), color.blue());
		page.stream().fillRect(x1, y1 + pageBorder/2, x2 - x1, y2 - y1 - pageBorder);
	}

}
