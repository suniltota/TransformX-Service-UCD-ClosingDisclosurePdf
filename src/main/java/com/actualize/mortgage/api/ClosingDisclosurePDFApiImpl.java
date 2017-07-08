package com.actualize.mortgage.api;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.actualize.mortgage.domainmodels.PDFResponse;
import com.actualize.mortgage.services.impl.ClosingDisclosurePDFServicesImpl;
import com.actualize.mortgage.services.impl.IClosingDisclosurePDFServices;
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
	
	@Autowired
	private IClosingDisclosurePDFServices closingDisclosurePDFServices;
	/**
	 * generates PDF for closing disclosure on giving xml as input in String format 
	 * @param version
	 * @param xmldoc
	 * @return pdf document
	 * @throws Exception
	 */
    @RequestMapping(value = "/{version}/pdf", method = { RequestMethod.POST })
    public List<PDFResponse> saveModifiedUCD(@PathVariable String version, @RequestBody String xmldoc) throws Exception {
    	LOG.info("user "+SecurityContextHolder.getContext().getAuthentication().getName()+" used Service: CD MISMO XML to CD PDF");
        return closingDisclosurePDFServices.createPDF(xmldoc);
    }
    
    /**
     * checks the status of the service
     * @param version
     * @return String
     * @throws Exception
     */
    @RequestMapping(value = "/{version}/ping", method = { RequestMethod.GET })
    public String status(@PathVariable String version) throws Exception {
    	LOG.info("Service call: /ping for CD");
        return "The service for generating PDF for closing disclosure is running and ready to accept your request";
    }
}
