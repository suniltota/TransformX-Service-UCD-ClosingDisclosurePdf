package com.actualize.closingdisclosure.api;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.actualize.closingdisclosure.domainmodels.PDFResponse;
import com.actualize.closingdisclosure.services.ClosingDisclosureServices;
/**
 * This class is the rest controller which defines all the APIs associated for Closing Disclosure PDF generation 
 * @author sboragala
 * @version 1.0
 * 
 */
@RestController
@RequestMapping(value="/actualize/transformx/documents/ucd/")
public class ClosingDisclosureApiImpl {
	
	private static final Logger LOG = LogManager.getLogger(ClosingDisclosureApiImpl.class);
	
	@Autowired
	ClosingDisclosureServices closingDisclosureServices;
	
	/**
	 * generates PDF for closing disclosure on giving xml as input in String format
	 * @param xmldoc
	 * @return PDF document 
	 * @throws Exception
	 */
    @RequestMapping(value = "/pdf", method = { RequestMethod.POST })
    public List<PDFResponse> saveModifiedUCD(@RequestBody String xmldoc) throws Exception {
        return closingDisclosureServices.createPDF(xmldoc);
    }
    
    @RequestMapping(value = "/status", method = { RequestMethod.GET })
    public String checkStatus() throws Exception {
        return "The service for generating PDF for Closing Disclosure is running and ready to accept your requests";
    }

}
