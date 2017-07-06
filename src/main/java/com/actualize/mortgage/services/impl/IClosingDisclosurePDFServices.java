package com.actualize.mortgage.services.impl;

import java.util.List;

import com.actualize.mortgage.domainmodels.PDFResponse;

public interface IClosingDisclosurePDFServices {
	public List<PDFResponse> createPDF(String xmlDoc) throws Exception;
}
