/**
 * 
 * @license Copyright 2015 Actualize Consulting 
 *
 */
package com.actualize.closingdisclosure.pdfbuilder;

import java.io.IOException;

public abstract class Drawable {
	public static class Alignment {
		static public enum Vertical { TOP, MIDDLE, BOTTOM; }
		static public enum Horizontal { LEFT, CENTER, RIGHT; }
	}
	public Alignment.Horizontal hAlign = Alignment.Horizontal.LEFT;
	public Alignment.Vertical   vAlign = Alignment.Vertical.BOTTOM;
	public boolean wrappable = true;

	/**
	 * Returns the width of the Drawable. 
	 */
	public abstract float width(Page page) throws IOException;

	/**
	 * Returns the height of the Drawable. 
	 */
	public abstract float height(Page page) throws IOException;

	/**
	 * Returns the height of the Drawable if it needs to be wrapped at width
	 *
	 * @param  width  the width of the drawing area
	 */
	public float height(Page page, float width) throws IOException { return height(page); }

	/**
	 * Renders the Drawable on a Page, anchored at a lower-left coordinate.
	 *
	 * @param  x  the bottom-most x-coordinate location for the rendering
	 * @param  y  the left-most y-coordinate location for the rendering
	 */
	public abstract void draw(Page page, float x, float y) throws IOException;

	/**
	 * Renders the Drawable on a Page, using alignment hints.
	 *
	 * @param  x       the bottom-most x-coordinate location for the rendering
	 * @param  y       the left-most y-coordinate location for the rendering
	 * @param  width   the width of the drawing area
	 * @param  height  the height of the drawing area
	 */
	public void draw(Page page, float x, float y, float width, float height) throws IOException { draw(page, x, y); }
	
	/**
	 * Determines if a Drawable, being drawn at coordinate x, can be split before
	 * coordinate endX.
	 * @throws IOException 
	 */
	public boolean canSplitHorizontally(Page page, float width) throws IOException { return false; }
	
	/**
	 * Determines if a Drawable, being drawn at coordinate y, can be split after
	 * coordinate endY.
	 */
	public boolean canSplitVertically(Page page, float height) { return false; }
		
	/**
	 * Splits a Drawable into at most two parts. Part one consists of a part up-to, but
	 * not exceeding width. Part two is any remaining portion.
	 * @throws IOException 
	 */
	public Drawable[] splitHorizontally(Page page, float width) throws IOException { return null; }
	
	/**
	* Splits a Drawable into at most two parts. Part one... TODO.
	*/
	public Drawable[] splitVertically(Page page, float height) { return null; }
}
