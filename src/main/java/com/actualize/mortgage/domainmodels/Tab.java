package com.actualize.mortgage.domainmodels;

import java.io.IOException;

import com.actualize.mortgage.pdfbuilder.Color;
import com.actualize.mortgage.pdfbuilder.Drawable;
import com.actualize.mortgage.pdfbuilder.Page;

public class Tab extends Drawable {
	private final Color color = Color.BLACK;
	private final float width;  // in pts
	private final float height; // in pts
	private final float cornerHeight; // in pts

	public Tab() {
		this.width = 126;
		this.height = 11;
		this.cornerHeight = 8;
	}

	public Tab(float width) {
		this.width = width*72;
		this.height = 11;
		this.cornerHeight = 8;
	}

	public float width(Page page) throws IOException {
		return this.width/72f;
	}

	public float height(Page page) throws IOException {
		return (this.height + this.cornerHeight/2)/72f;
	}

	public void draw(Page page, float x, float y) throws IOException {
		page.stream().setNonStrokingColor(color.red(), color.green(), color.blue());
		page.stream().fillRect(x*72f, y*72f, width, height);
		page.stream().setStrokingColor(color.red(), color.green(), color.blue());
		page.stream().setLineCapStyle(1);
		page.stream().setLineWidth(cornerHeight);
		page.stream().drawLine(x*72+cornerHeight/2, y*72+height, x*72+width-cornerHeight/2, y*72+height);
	}
}
