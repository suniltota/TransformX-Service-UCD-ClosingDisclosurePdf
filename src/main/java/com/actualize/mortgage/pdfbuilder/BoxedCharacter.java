package com.actualize.mortgage.pdfbuilder;

import java.io.IOException;

public class BoxedCharacter extends Drawable {
	
	private String str;
	private float size;
	private Color color;
	
	public static final BoxedCharacter CHECK_BOX_EMPTY = new BoxedCharacter(' ', 8f/72f, Color.BLACK);
	public static final BoxedCharacter CHECK_BOX_NO = new BoxedCharacter('5', 8f/72f, Color.BLACK);
	public static final BoxedCharacter CHECK_BOX_YES = new BoxedCharacter('3', 8f/72f, Color.BLACK);
	
	public BoxedCharacter(char c, float size, Color color) {
		this.str = "" + c;
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
		float scale = 7f/8f;
		float left = x*72;
		float right = (x+scale*this.size)*72;
		float top = (y+scale*this.size-this.size/8)*72;
		float bottom = (y-this.size/8)*72;
		page.stream().setStrokingColor(this.color.red(), this.color.green(), this.color.blue());
		page.stream().setLineWidth(1);
		page.stream().setLineCapStyle(0);
		page.stream().drawLine(left, bottom, left, top);
		page.stream().drawLine(left, top, right, top);
		page.stream().drawLine(right, top, right, bottom);
		page.stream().drawLine(right, bottom, left, bottom);
		page.stream().setNonStrokingColor(this.color.red(), this.color.green(), this.color.blue());
		page.stream().setFont(Typeface.WINGDINGS.font(page.doc(), page.stream()), this.size*90);
		page.stream().beginText();
		page.stream().moveTextPositionByAmount(left, bottom);
		page.stream().drawString(this.str);
		page.stream().endText();
	}

}
