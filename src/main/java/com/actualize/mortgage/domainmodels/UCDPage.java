package com.actualize.mortgage.domainmodels;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.actualize.mortgage.datalayer.InputData;
import com.actualize.mortgage.pdfbuilder.Color;
import com.actualize.mortgage.pdfbuilder.FormattedText;
import com.actualize.mortgage.pdfbuilder.Page;
import com.actualize.mortgage.pdfbuilder.Text;
import com.actualize.mortgage.pdfbuilder.Typeface;

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
