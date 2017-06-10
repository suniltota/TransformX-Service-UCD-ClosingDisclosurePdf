package com.actualize.mortgage.domainmodels;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.actualize.mortgage.datalayer.InputData;
import com.actualize.mortgage.datalayer.PopulateInputData;
import com.actualize.mortgage.datavalidation.MISMOValidation;
/**
 * This class defines all the functionalities of inserting pdf results into pdf Document 
 * @author sboragala
 *
 */
public class UniformDisclosureResults {
	public static final Encoder encoder = Base64.getEncoder();
	public static final Decoder decoder = Base64.getDecoder();
	
	/**
	 * validates the MISMO XML
	 * @param in
	 * @param validate
	 * @return Document
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public Document run(InputStream in, boolean validate) throws ParserConfigurationException, IOException {
		// Create the results document and root element ("Results")
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document xmldoc = builder.newDocument();
		Element resultsElement = xmldoc.createElement("Results");
        xmldoc.appendChild(resultsElement);
        Element statusElement = xmldoc.createElement("Status");
        resultsElement.appendChild(statusElement);

        // Add the start and end times
		Element startElement = xmldoc.createElement("StartTime");
		resultsElement.appendChild(startElement);
		Element endElement = xmldoc.createElement("EndTime");
		resultsElement.appendChild(endElement);

		// Stamp the start time
		startElement.appendChild(xmldoc.createTextNode(new Date().toString()));

    	// Save input stream into ByteArrayOutputStream (needed for multiple purposes, e.g. validation and input)
    	ByteArrayOutputStream baos = inputStreamToByteArrayOutputStream(in);
		in.close();

    	// Validate incoming xml
		try {
			if (validate)
				MISMOValidation.validateXML(new ByteArrayInputStream(baos.toByteArray()));
		} catch (Exception e) { // Oops, ERROR!
			insertError(e, xmldoc, resultsElement, statusElement);
		}

		// Read data
		PopulateInputData reader = new PopulateInputData();
		List<InputData> data = reader.getData(new ByteArrayInputStream(baos.toByteArray()));
		int count = 0;
		for(InputData inputDeal:data){
			// Insert results element along with embedded PDF document
			inputDeal.setTransactionId(Integer.toString(count++));
			
		//	System.out.println("Second Testing");
	    	insertPdfResults(inputDeal, xmldoc, resultsElement);
		}

		// Stamp the end time
    	endElement.appendChild(xmldoc.createTextNode(new Date().toString()));

    	// Set status to success
    	statusElement.appendChild(xmldoc.createTextNode("Success"));
    	System.out.println("saving to pdf++++");
    	return xmldoc;
	}
	
	/**
	 * populates all the elements in the MISMO XML
	 * @param in
	 * @return Document
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public Document run(Document in) throws ParserConfigurationException, IOException {
		// Create the results document and root element ("Results")
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document xmldoc = builder.newDocument();
		Element resultsElement = xmldoc.createElement("Results");
        xmldoc.appendChild(resultsElement);
        Element statusElement = xmldoc.createElement("Status");
        resultsElement.appendChild(statusElement);

        // Add the start and end times
		Element startElement = xmldoc.createElement("StartTime");
		resultsElement.appendChild(startElement);
		Element endElement = xmldoc.createElement("EndTime");
		resultsElement.appendChild(endElement);

		// Stamp the start time
		startElement.appendChild(xmldoc.createTextNode(new Date().toString()));

		// Read data
		PopulateInputData reader = new PopulateInputData();
		List<InputData> data = reader.getData(in);
		int count = 0;
		for(InputData inputDeal:data ){
			// Insert results element along with embedded PDF document
			inputDeal.setTransactionId(Integer.toString(count++));
	    	insertPdfResults(inputDeal, xmldoc, resultsElement);
		}

		// Stamp the end time
    	endElement.appendChild(xmldoc.createTextNode(new Date().toString()));

    	// Set status to success
    	statusElement.appendChild(xmldoc.createTextNode("Success"));
    	return xmldoc;
	}

	/**
	 * creates pdf  
	 * @param data
	 * @param xmldoc
	 * @param rootElement
	 */
	private void insertPdfResults(InputData data, Document xmldoc, Element rootElement) {
 
		// Append UniformDisclosureResults and UniformDisclosureResults/Status elements
		Element documentElement = xmldoc.createElement("Document");
		rootElement.appendChild(documentElement);
        Element statusElement = xmldoc.createElement("Status");
        documentElement.appendChild(statusElement);

        try {
        	
        	// Create PDF document
    		ByteArrayOutputStream pdfOutStream = new ByteArrayOutputStream();
    		if (data.isSellerOnly()){
    			UniformDisclosureBuilderSeller pdfbuilder = new UniformDisclosureBuilderSeller();
    			//System.out.println("First");
    			pdfbuilder.run(data, pdfOutStream);
    		} else {
    			UniformDisclosureBuilder pdfbuilder = new UniformDisclosureBuilder();
    			//System.out.println("Second");
    			pdfbuilder.run(data, pdfOutStream);
    		}
        	
        	// Encode the document in base64
            String encodedPdf = encoder.encodeToString(pdfOutStream.toByteArray());
            
//            BASE64Decoder decoder = new BASE64Decoder();
//            byte[] decodedBytes = decoder.decodeBuffer(encodedPdf);
            
            /*try(OutputStream out = new FileOutputStream("C:\\file\\runparametersandcsvfile\\filename.pdf")){
                out.write(decodedBytes );
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            
            
            
        	// Append UniformDisclosureResults/Document element with embedded pdf
            Element pdfElement = xmldoc.createElement("PdfDocument");
            documentElement.appendChild(pdfElement);
            pdfElement.appendChild(xmldoc.createTextNode(encodedPdf));
            
            // Set UniformDisclosureResults/Status to Success
            statusElement.appendChild(xmldoc.createTextNode("Success"));
    		
		} catch (Exception e) { // Oops, ERROR!
			insertError(e, xmldoc, documentElement, statusElement);
		}
	}
	
	/**
	 * inserts errors 
	 * @param e
	 * @param xmldoc
	 * @param documentElement
	 * @param statusElement
	 */
	private void insertError(Exception e, Document xmldoc, Element documentElement, Element statusElement) {

        // Set UniformDisclosureResults/Status to Error
		statusElement.appendChild(xmldoc.createTextNode("Error"));
		
		// Create error element
        Element errorElement = xmldoc.createElement("Error");
        documentElement.appendChild(errorElement);
        
        // Insert error code into error element
        Element codeElement  = xmldoc.createElement("Code");
        errorElement.appendChild(codeElement);
        codeElement.appendChild(xmldoc.createTextNode("004"));
        
        // Insert summary message into error element
        Element messageElement  = xmldoc.createElement("Message");
        errorElement.appendChild(messageElement);
        messageElement.appendChild(xmldoc.createTextNode("Service failure"));
        
        // Insert message details into error element
        Element detailsElement  = xmldoc.createElement("MessageDetails");
		errorElement.appendChild(detailsElement);
        detailsElement.appendChild(xmldoc.createTextNode("Exception(" + e.toString() + ")"));
        
        // Insert stack trace into error element
        Element stackElement  = xmldoc.createElement("StackTrace");
		errorElement.appendChild(stackElement);
		stackElement.appendChild(xmldoc.createCDATASection("\n" + getStackTrace(e)));
	}
	
	/**
	 * converts the inputstream to bytearrayoutput stream
	 * @param in
	 * @return ByteArrayOutputStream
	 * @throws IOException
	 */
	public static ByteArrayOutputStream inputStreamToByteArrayOutputStream(InputStream in) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) > -1 ) {
		    baos.write(buffer, 0, len);
		}
		baos.flush();
		return baos;
	}
	
	/**
	 * formats the exception message
	 * @param aThrowable
	 * @return string
	 */
	public static String getStackTrace(Throwable aThrowable) {
		StringBuilder result = new StringBuilder();
		for (StackTraceElement element : aThrowable.getStackTrace()) {
			result.append("at ");
			result.append(element);
			result.append("\n");
		}
		return result.toString();
	}

}