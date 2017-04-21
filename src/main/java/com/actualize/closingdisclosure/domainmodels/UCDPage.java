package com.actualize.closingdisclosure.domainmodels;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.actualize.closingdisclosure.datalayer.InputData;
import com.actualize.closingdisclosure.pdfbuilder.Color;
import com.actualize.closingdisclosure.pdfbuilder.FormattedText;
import com.actualize.closingdisclosure.pdfbuilder.Page;
import com.actualize.closingdisclosure.pdfbuilder.Text;
import com.actualize.closingdisclosure.pdfbuilder.Typeface;

public class UCDPage extends Page {
	
	final Text WATERMARK_STYLE = new Text(Color.WATERMARK, 56, Typeface.CALIBRI);
	final FormattedText WATERMARK = new FormattedText("DRAFT", WATERMARK_STYLE);

	public UCDPage(PDDocument doc) {
		super(doc);
	}
	
	public UCDPage(PDDocument doc, float width, float height) {
		super(doc, width, height);
	}

	public void drawWatermark(Page page, Object data) {
		// TODO If "CLOSING_INFORMATION_DETAIL.DocumentOrderClassificationType" = "Preliminary" then show draft watermark
		
		InputData inputData = (InputData)data;
		
		if (inputData.getClosingMap().getClosingMapValue("CLOSING_INFORMATION_DETAIL.DocumentOrderClassificationType").equalsIgnoreCase("Preliminary")) {
			try {
				float watermarkWidth = WATERMARK.width(page);
				WATERMARK.draw(page, (width - watermarkWidth)/2f, .2f); //(height - watermarkHeight)/2f);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}
}
