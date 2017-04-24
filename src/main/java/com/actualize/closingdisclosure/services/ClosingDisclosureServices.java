package com.actualize.closingdisclosure.services;

import java.util.List;

import com.actualize.closingdisclosure.domainmodels.PDFResponse;


public interface ClosingDisclosureServices {
	
	public List<PDFResponse> createPDF(String xmlDoc) throws Exception;

}
