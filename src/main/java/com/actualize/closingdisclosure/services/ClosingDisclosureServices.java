package com.actualize.closingdisclosure.services;

import java.util.List;


public interface ClosingDisclosureServices {
	
	public List<PDFResponse> createPDF(String xmlDoc) throws Exception;

}
