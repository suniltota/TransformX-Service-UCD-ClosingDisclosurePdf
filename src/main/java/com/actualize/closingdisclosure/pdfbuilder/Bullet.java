package com.actualize.closingdisclosure.pdfbuilder;

import java.io.IOException;

public class Bullet extends Drawable {
	
	private String str;
	private float size;
	private Color color;
	
	public static final Bullet BULLET = new Bullet(8f/72f, Color.BLACK);
	
	public Bullet(float size, Color color) {
		this.str = "l";
		this.size = size;
		this.color = color;
	}

	public float width(Page page) throws IOException {
		return this.size;
	}

	public float height(Page page) throws IOException {
		return this.size;
	}

	public void draw(Page page, float x, float y) throws IOException {
		page.stream().setNonStrokingColor(this.color.red(), this.color.green(), this.color.blue());
		page.stream().setFont(Typeface.WINGDINGS.font(page.doc(), page.stream()), this.size*30);
		page.stream().beginText();
		page.stream().moveTextPositionByAmount(x*72+size*27, y*72+size*18);
		page.stream().drawString(this.str);
		page.stream().endText();
	}
}
