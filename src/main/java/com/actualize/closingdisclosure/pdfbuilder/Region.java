package com.actualize.closingdisclosure.pdfbuilder;

import java.io.IOException;
import java.util.ArrayList;

public class Region extends Drawable {
	private ArrayList<Drawable> drawables = new ArrayList<Drawable>();

	public float width(Page page) throws IOException {
		float maxwidth = 0;
		for (Drawable drawable : drawables) {
			float width = drawable.width(page);
			if (width > maxwidth)
				maxwidth = width;
		}
		return maxwidth;
	}

	public float height(Page page) throws IOException {
		float height = 0;
		for (Drawable drawable : drawables)
			if (drawable != null)
				height += drawable.height(page);
		return height;
	}

	public void draw(Page page, float x, float y) throws IOException {
		y += height(page);
		for (Drawable drawable : drawables) {
			if (drawable != null) {
				y -= drawable.height(page);
				drawable.draw(page, x, y);
			}
		}
	}

	public void draw(Page page, float x, float y, float width, float height) throws IOException {
		float nextY = startingY(page, x, y, width, height) + height(page, width);
		for (Drawable drawable : drawables) {
			Drawable item = drawable;
			Drawable remainder = null;
			while (item != null) {
				if (wrappable & drawable.width(page) > width & drawable.canSplitHorizontally(page, width)) {
					Drawable split[] = drawable.splitHorizontally(page, width);
					item = split[0];
					remainder = split[1];
				}
				float nextX = startingX(x, width, item.width(page));
				nextY -= item.height(page);
				item.draw(page, nextX, nextY);
				item = remainder;
				remainder = null;
			}
		}
	}

	public boolean canSplitHorizontally(Page page, float width) throws IOException {
		for (Drawable drawable : drawables)
			if (drawable != null &&  drawable.canSplitHorizontally(page, width))
				return true;
		return false;
	}

	public Drawable[] splitHorizontally(Page page, float width) throws IOException {
		Drawable parts[] = new Drawable[2];
		if (drawables.size() > 0) {
			Drawable head = drawables.get(0);
			Region remainder = new Region();
			if (head.canSplitHorizontally(page, width)) {
				Drawable[] split = head.splitHorizontally(page, width);
				parts[0] = split[0];
				remainder.append(split[1]);
			} else {
				parts[0] = head;
			}
			for (int i = 1; i < drawables.size(); i++)
				remainder.append(drawables.get(i));
			parts[1] = remainder;
		}
		return parts;
	}

	public Region append(Drawable drawable) {
		drawables.add(drawable);
		return this;
	}
	
	public void wrapAt(Page page, float width) throws IOException {
		ArrayList<Drawable> wrapped = new ArrayList<Drawable>();
		for (Drawable drawable : drawables) {
			Drawable remainder = drawable;
			//if (remainder != null) {
				while (remainder != null && remainder.canSplitHorizontally(page, width)) {
					Drawable parts[] = remainder.splitHorizontally(page, width);
					wrapped.add(parts[0]);
					remainder = parts[1];
				}
				if (remainder != null)
					wrapped.add(remainder);
			//}
		}
		drawables = wrapped;
	}
	
	public float height(Page page, float width) throws IOException {
		float height = 0;
		for (Drawable drawable : drawables)
			height += drawable.height(page, width);
		return height;
	}
	
	private float startingX(float x, float width, float itemWidth) {
		switch (hAlign) {
		case LEFT:
			return x;
		case CENTER:
			return x + (width - itemWidth) / 2;
		default: //case RIGHT:
			return x + width - itemWidth;
		}
	}

	private float startingY(Page page, float x, float y, float width, float height) throws IOException {
		float drawableHeight = height(page, width);
		switch (vAlign) {
		case TOP:
			return y + height - drawableHeight;
		case MIDDLE:
			return y + (height - drawableHeight) / 2;
		default: //case BOTTOM:
			return y;
		}
	}
}
