package com.actualize.mortgage.pdfbuilder;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormattedText extends Drawable {
	private String str;
	private final Text style;
	private final boolean underline;
	static private final Pattern whitespace = Pattern.compile("[\\s+]|[@]");
	
	public FormattedText(String str, Text style) {
		this.str = str;
		this.style = style;
		this.underline = false;
	}
	
	public FormattedText(String str, Text style, boolean underline) {
		this.str = str;
		this.style = style;
		this.underline = underline;
	}
	
	public float width(Page page) throws IOException {
		return width(page, str);
	}
	
	public float height(Page page) throws IOException {
		return style.size * style.typeface.font(page.doc(), page.stream()).getFontBoundingBox().getHeight() / 72000;
	}
	
	public void draw(Page page, float x, float y) throws IOException {
		page.stream().beginText();
		style.apply(page.doc(), page.stream());
		page.stream().moveTextPositionByAmount(x*72, y*72);
		page.stream().drawString(str);
		page.stream().endText();
		if (underline) {
			page.stream().setLineWidth(0.5f);
			page.stream().drawLine(x*72, y*72 - 1, (x+width(page))*72, y*72 - 1);
		}
	}
	
	public boolean canSplitHorizontally(Page page, float endX) throws IOException {
		if (width(page) < endX)
			return false;
//		if (str.indexOf('@') != -1)
//			return true;
		Matcher matcher = whitespace.matcher(str);
        if (matcher.find()) {
        	return true;
//        	int start = matcher.start();
//        	String substr = str.substring(0, start == 0 ? 1 : start);
//        	float w = width(page, substr);
//        	return w < endX;
        }
		return false;
	}

	public Drawable[] splitHorizontally(Page page, float endX) throws IOException {
		FormattedText parts[] = new FormattedText[2];
		String tmp;
		
		// First line wrap on '@' which should only show up in contact section
//		if (str.indexOf('@') != -1) {
//			tmp = str.substring(0);
//			parts[0] = new FormattedText(str.substring(0, str.indexOf('@')+1), style);
//			parts[1] = new FormattedText(str.substring(str.indexOf('@')+1, str.length()).replaceFirst("^\\s+", ""), style);
//			return parts;
//		}
		
		Matcher matcher = whitespace.matcher(str);
		int firstend = 0;
		int remainder = 0;
		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			if (start > 0 && width(page, str.substring(0, start)) > endX)
				break;
			else {
				// fix for email
				if (str.substring(start, end).equals( "@")) {
					str = str.substring(0, start) + "@ " + str.substring(end);
					start++;
					end++;
				}
				remainder = end;
				firstend = start;
			}
		}
		if (firstend > 0) {
			parts[0] = new FormattedText(str.substring(0, firstend), style);
			if (remainder != 0 && remainder < str.length())
				parts[1] = new FormattedText(str.substring(remainder, str.length()), style);
		} else {
			int index = str.indexOf(' ');
			if (index != -1) {
				parts[0] = new FormattedText(str.substring(0, index), style);
				parts[1] = new FormattedText(str.substring(index+1), style);
			} else
				parts[0] = new FormattedText(str, style);
		}
		return parts;
	}
	
	private float width(Page page, String str) throws IOException {
		return style.size * style.typeface.font(page.doc(), page.stream()).getStringWidth(str) / 72000.0f;
	}
}
