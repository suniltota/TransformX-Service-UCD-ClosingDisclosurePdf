package com.actualize.mortgage.pdfbuilder;

import java.io.IOException;

public class TextBox extends Drawable {
	public enum Direction { LEFT, RIGHT, TOP, BOTTOM };
	public enum HorizontalAlignment { LEFT, CENTER, RIGHT }
	public enum VerticalAlignment { TOP, MIDDLE, BOTTOM }
	
	private float width, height;
	private float marginTop = 2f / 72f;
	private float marginBottom = 2f / 72f;
	private float marginLeft = 1f / 72f;
	private float marginRight = 1f / 72f;
	private HorizontalAlignment hAlign = HorizontalAlignment.LEFT;
	private VerticalAlignment vAlign = VerticalAlignment.BOTTOM;
	private Drawable text;
	
	public float width(Page page) {
		return this.width;
	}
		
	public float height(Page page) {
		return this.height;
	}

	public void draw(Page page, float x, float y) throws IOException {
		if (text != null)
			text.draw(page, x + horizontalAdjustment(page), y + verticalAdjustment(page));
	}
	
	public TextBox setText(Drawable text) {
		this.text = text;
		return this;
	}
	
	public TextBox setDimentions(float width, float height) {
		this.width = width;
		this.height = height;
		return this;
	}

	public TextBox setMargin(Direction margin, float amount) {
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
	
	public TextBox setHorizontalAlignment(HorizontalAlignment align) {
		this.hAlign = align;
		return this;
	}

	public TextBox setVerticalAlignment(VerticalAlignment align) {
		this.vAlign = align;
		return this;
	}
	
	public float bestWidth(Page page) throws IOException {
		return this.textWidth(page) + this.marginLeft + this.marginRight;
	}
	
	public float bestHeight(Page page) throws IOException {
		if (text == null)
			return 0;
		return this.textHeight(page) + this.marginTop + this.marginBottom;
	}
	
	private float horizontalAdjustment(Page page) throws IOException {
		float adjustment = 0;
		switch (this.hAlign) {
		case LEFT:
			adjustment = this.marginLeft;
			break;
		case CENTER:
			adjustment = (this.width - this.textWidth(page) - this.marginRight + this.marginLeft) / 2;
			break;
		case RIGHT:
			adjustment = this.width - this.textWidth(page) - this.marginRight;
			break;
		}
		return adjustment;
	}
	
	private float verticalAdjustment(Page page) throws IOException {
		float adjustment = 0;
		switch (this.vAlign) {
		case TOP:
			adjustment = this.height - this.textHeight(page) - this.marginTop;
			break;
		case MIDDLE:
			adjustment = (this.height - this.marginTop - this.textHeight(page) + this.marginBottom) / 2;
			break;
		case BOTTOM:
			adjustment = this.marginBottom;
			break;
		}
		return adjustment;
	}
	
	private float textWidth(Page page) throws IOException {
		return this.text == null ? 0 : this.text.width(page);
	}
	
	private float textHeight(Page page) throws IOException {
		return this.text == null ? 0 : this.text.height(page);
	}
}