package com.actualize.mortgage.services;

import java.util.List;

import com.actualize.mortgage.domainmodels.PDFResponse;


public interface ClosingDisclosureServices {
	
	public List<PDFResponse> createPDF(String xmlDoc) throws Exception;

}
