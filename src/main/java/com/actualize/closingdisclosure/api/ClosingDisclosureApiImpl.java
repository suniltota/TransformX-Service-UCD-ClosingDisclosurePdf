package com.actualize.closingdisclosure.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.actualize.closingdisclosure.services.ClosingDisclosureServices;
import com.actualize.closingdisclosure.services.PDFResponse;

@RestController
@RequestMapping(value="/actualize/transformx/documents/ucd/cd")
public class ClosingDisclosureApiImpl {
	
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

}
