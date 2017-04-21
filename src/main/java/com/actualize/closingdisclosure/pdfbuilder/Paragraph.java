package com.actualize.closingdisclosure.pdfbuilder;

import java.io.IOException;
import java.util.ArrayList;

public class Paragraph extends Drawable {
	ArrayList<Drawable> drawables = new ArrayList<Drawable>();
	
	public Paragraph append(Drawable drawable) {
		this.drawables.add(drawable);
		return this;
	}

	public float width(Page page) throws IOException {
		float width = 0;
		for (Drawable drawable : drawables)
			width += drawable.width(page);
		return width;
	}
	
	public float height(Page page) throws IOException {
		float height = 0.0f;
		for (Drawable drawable : this.drawables) {
			if (drawable.height(page) > height)
				height = drawable.height(page);
		}
		return height;
	}
	
	public void draw(Page page, float x, float y) throws IOException {
		for (Drawable drawable : this.drawables) {
			drawable.draw(page, x, y);
			x += drawable.width(page);
		}
	}
	
	public boolean canSplitHorizontally(Page page, float endX) throws IOException {
		float currentX = 0;
		for (Drawable drawable : this.drawables) {
			float width = drawable.width(page);
			if (currentX + width > endX)
				return (currentX != 0) || drawable.canSplitHorizontally(page, endX - currentX);
			else
				currentX += width;
		}
		return false;
	}

	public Drawable[] splitHorizontally(Page page, float endX) throws IOException {
		Paragraph parts[] = new Paragraph[2];
		if (endX <= 0)
			return parts;
		float currentX = 0;
		for (Drawable drawable : this.drawables) {
			float width = drawable.width(page);
			if (currentX + width > endX) {
				if (currentX > endX) {
					if (parts[1] == null)
						parts[1] = new Paragraph();
					parts[1].append(drawable);
				} else if (drawable.canSplitHorizontally(page, endX - currentX)) {
					Drawable split[] = drawable.splitHorizontally(page, endX - currentX);
					if (split[0] != null) {
						if (parts[0] == null)
							parts[0] = new Paragraph();
						parts[0].append(split[0]);
					}
					if (split[1] != null) {
						if (parts[1] == null)
							parts[1] = new Paragraph();
						parts[1].append(split[1]);
					}
				} else if (currentX == 0) {
					return parts;
				} else {
					if (parts[1] == null)
						parts[1] = new Paragraph();
					parts[1].append(drawable);
				}
			} else {
				if (parts[0] == null)
					parts[0] = new Paragraph();
				parts[0].append(drawable);
			}
			currentX += width;
		}
		return parts;
	}
}