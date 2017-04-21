package com.actualize.closingdisclosure.pdfbuilder;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

public class  Text {
	public static final Text HEADER_LARGE       = new Text(Color.BLACK, 20, Typeface.CALIBRI_BOLD);
	public static final Text HEADER_MEDIUM      = new Text(Color.BLACK, 14, Typeface.CALIBRI_BOLD);
	public static final Text HEADER_SMALL       = new Text(Color.BLACK, 10, Typeface.CALIBRI_BOLD);
	public static final Text PAGE_FOOTER        = new Text(Color.BLACK, 9, Typeface.CALIBRI);
	public static final Text SECTION_HEADER     = new Text(Color.WHITE, 12, Typeface.CALIBRI_BOLD);
	public static final Text SECTION_INFO       = new Text(Color.BLACK, 10, Typeface.CALIBRI_BOLD);
	public static final Text SECTION_LARGE      = new Text(Color.BLACK, 12, Typeface.CALIBRI);
	public static final Text SECTION_XLARGE     = new Text(Color.BLACK, 18, Typeface.CALIBRI);
	public static final Text SECTION_TEXT       = new Text(Color.BLACK, 10, Typeface.CALIBRI);
	public static final Text SECTION_OBLIQUE    = new Text(Color.BLACK, 10, Typeface.CALIBRI_OBLIQUE);
	public static final Text TABLE_HEADER       = new Text(Color.BLACK, 8, Typeface.CALIBRI_BOLD);
	public static final Text TABLE_HEADER_LARGE = new Text(Color.BLACK, 10, Typeface.CALIBRI_BOLD);
	public static final Text TABLE_HEADER_XLRG  = new Text(Color.BLACK, 12, Typeface.CALIBRI_BOLD);
	public static final Text TABLE_NUMBER       = new Text(Color.MEDIUM_GRAY, 8, Typeface.CALIBRI);
	public static final Text TABLE_OBLIQUE      = new Text(Color.BLACK, 8, Typeface.CALIBRI_OBLIQUE);
	public static final Text TABLE_OBLIQUE_BOLD = new Text(Color.BLACK, 8, Typeface.CALIBRI_BOLD_OBLIQUE);
	public static final Text TABLE_TEXT         = new Text(Color.BLACK, 8, Typeface.CALIBRI);
	public static final Text TABLE_TEXT_BOLD    = new Text(Color.BLACK, 8, Typeface.CALIBRI_BOLD);
	public static final Text WINGDINGS          = new Text(Color.BLACK, 10, Typeface.WINGDINGS);
	public static final Text ROW_HEADER      =    new Text(Color.BLACK, 9, Typeface.CALIBRI_BOLD);

	final Color     color;
	final float     size;
	final Typeface  typeface;
	
	public Text(Color color, float size, Typeface typeface) {
		this.color    = color;
		this.size     = size;
		this.typeface = typeface;
	}
	
	public void apply(PDDocument doc, PDPageContentStream content) throws IOException {
		content.setNonStrokingColor(color.red(), color.green(), color.blue());
		content.setFont(typeface.font(doc, content), size);
	}
}
