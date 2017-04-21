package com.actualize.closingdisclosure.services.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.actualize.closingdisclosure.datalayer.InputData;
import com.actualize.closingdisclosure.datalayer.PopulateInputData;
import com.actualize.closingdisclosure.domainmodels.UniformDisclosureBuilder;
import com.actualize.closingdisclosure.domainmodels.UniformDisclosureBuilderSeller;
import com.actualize.closingdisclosure.services.ClosingDisclosureServices;
import com.actualize.closingdisclosure.services.PDFResponse;

public class ClosingDisclosureServicesImpl implements ClosingDisclosureServices {

	@Override
	public List<PDFResponse> createPDF(String xmlDoc) throws Exception {
		 PopulateInputData reader = new PopulateInputData();
		    List<InputData> inputData = reader.getData(new ByteArrayInputStream(xmlDoc.getBytes("utf-8")));
		    ByteArrayOutputStream pdfOutStream = null;
		    List<PDFResponse> pdfResponseList = new ArrayList<>();
		    for (InputData data : inputData) {
		    PDFResponse outputResponse = new PDFResponse();
		    outputResponse.setFilename("ClosingDisclosure");
		    outputResponse.setOutputType("application/pdf");
		        if (data.isSellerOnly()) {
		            UniformDisclosureBuilderSeller pdfbuilder = new UniformDisclosureBuilderSeller();
		            pdfOutStream = pdfbuilder.run(data);
		            outputResponse.setResponseData(pdfOutStream.toByteArray());
		        } else {
		            UniformDisclosureBuilder pdfbuilder = new UniformDisclosureBuilder();
		            pdfOutStream = pdfbuilder.run(data);
		            outputResponse.setResponseData(pdfOutStream.toByteArray());
		        }
		        pdfResponseList.add(outputResponse);
		    }
		return pdfResponseList;
	}

}
