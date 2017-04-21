package com.actualize.closingdisclosure.pdfbuilder;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

/**
 * Page is the abstract base class for all document pages. A page
 * consists of sections, e.g. header, footer, body, etc.
 * 
 * @author      Tim McLuckie
 */
public class Page {
	public final float scale = 72f;
	
	// Page constants
	public final float width;
	public final float height;
	public final float leftMargin   = 0.5f;
	public final float rightMargin  = 0.5f;
	public final float topMargin    = 0.5f;
	public final float bottomMargin = 0.5f;

	private PDDocument          doc      = null;
	private PDPageContentStream stream   = null;
	private ArrayList<Section>  sections = new ArrayList<Section>();

	/**
	 * Instantiates a Page object. 
	 */
	public Page(PDDocument doc, float width, float height) {
		this.width = width;
		this.height = height;
		this.doc = doc;
	}

	/**
	 * Instantiates a Page object of letter size. 
	 */
	public Page(PDDocument doc) {
		this.width = 8.5f;
		this.height = 11f;
		this.doc = doc;
	}

	/**
	 * Adds a section to a page.
	 *
	 * @param  section  adds a section to a page
	 */
	public Page addSection(Section section) {
		sections.add(section);
		return this;
	}

	/**
	 * Renders a page in a document.
	 *
	 * @param  doc  the PDFBox document the page will be rendered onto
	 * @param  data  user data intended to supply business information to the rendoring
	 */
	public final void render(PDDocument doc, Object data) throws IOException {
		this.doc = doc;
		PDPage page = new PDPage(PDPage.PAGE_SIZE_LETTER);
		doc.addPage(page);
		try {
			this.stream = new PDPageContentStream(doc, page);
		} catch (IOException e) {
			System.err.println("ERROR: Can't initialize page");
			throw e;
		}
		try {
			drawWatermark(this, data);
			for (Section section : sections)
				section.draw(this, data);
		} catch (IOException e) {
			System.err.println("ERROR: Problem drawing page.");
			throw e;
		} finally {
			if (stream != null) stream.close();
			stream = null;
		}
	}

	public void drawWatermark(Page page, Object data) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Returns the PDDocument containing the page.
	 */
	public PDDocument doc() {
		return doc;
	}

	/**
	 * Returns the PDFBoxContentStream associated with the page.
	 */
	public PDPageContentStream stream() {
		return stream;
	}
	
	/**
	 * Converts a page X coordinate into a PDFBox X coordinate
	 */
	public final float toPdfX(float x) {
		return scaleToPdf(x); //scaleToPdf(height - x);
	}
	
	/**
	 * Converts a page Y coordinate into a PDFBox Y coordinate
	 */
	public final float toPdfY(float y) {
		return scaleToPdf(y);
	}
	
	/**
	 * Converts a PDFBox X coordinate into a page X coordinate
	 */
	public final float toPageX(float x) {
		return scaleToPage(x); //height - scaleToPage(x);
	}
	
	/**
	 * Converts a PDFBox Y coordinate into a page Y coordinate
	 */
	public final float toPageY(float y) {
		return scaleToPage(y);
	}
	
	/**
	 * Converts a page number to a PDFBox number
	 */
	public final float scaleToPdf(float num) {
		return num*scale;
	}
	
	/**
	 * Converts a PDFBox number to a Page number
	 */
	public final float scaleToPage(float num) {
		return num/scale;
	}
}
