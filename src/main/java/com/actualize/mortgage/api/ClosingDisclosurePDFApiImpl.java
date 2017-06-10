package com.actualize.mortgage.api;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.actualize.mortgage.domainmodels.PDFResponse;
import com.actualize.mortgage.services.impl.ClosingDisclosureServicesImpl;
/**
 * This class is the rest controller which defines all the APIs associated for Closing Disclosure PDF generation 
 * @author sboragala
 * @version 1.0
 * 
 */
@RestController
@RequestMapping(value="/actualize/transformx/documents/ucd/cd")
public class ClosingDisclosurePDFApiImpl {
	
	private static final Logger LOG = LogManager.getLogger(ClosingDisclosurePDFApiImpl.class);
	
	
	/**
	 * generates PDF for closing disclosure on giving xml as input in String format
	 * @param xmldoc
	 * @return PDF document 
	 * @throws Exception
	 */
    @RequestMapping(value = "/pdf", method = { RequestMethod.POST })
    public List<PDFResponse> saveModifiedUCD(@RequestBody String xmldoc) throws Exception {
    	ClosingDisclosureServicesImpl closingDisclosureServicesImpl = new ClosingDisclosureServicesImpl();
        return closingDisclosureServicesImpl.createPDF(xmldoc);
    }

}
