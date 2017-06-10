package com.actualize.mortgage.pdfbuilder;

public enum Color {
	BLACK       (0, 0, 0),
	LIGHT_GRAY  (212, 212, 212),
	MEDIUM_GRAY (196, 196, 196),
	DARK_GRAY	(164, 164, 164),
	WHITE	    (255, 255, 255),
	WATERMARK   (255, 212, 212);

	private final int red, green, blue;
	
	Color(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public int red()   { return red; }
	public int green() { return green; }
	public int blue()  { return blue; }
}
