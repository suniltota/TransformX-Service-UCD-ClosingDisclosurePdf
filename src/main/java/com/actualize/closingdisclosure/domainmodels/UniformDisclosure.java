package com.actualize.closingdisclosure.domainmodels;

/**
 * 
 * @author EdBergerActualize Consulting
 *         eberger@actualizeconsuling.com
 * @license
 * Copyright 2015 Actualize Consulting 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 http://www.apache.org/licenses/LICENSE-2.0
 *
 * unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class UniformDisclosure {
	String progname = "xxx";
	String outFile = null;
	String inFile = null;
	String refFile = null;
	String difFile = null;
	boolean validate = true;
	RunMode mode = RunMode.OUTPUTMODE;
	String comparetool = "diff-pdf"; // default - assumes diff-pdf tool available in path
	
	public enum RunMode { OUTPUTMODE, DIFFMODE, RAWMODE, RESULTSMODE }
	
	static private final UniformDisclosureResults builder = new UniformDisclosureResults();
	
	private void usage()
	{
		System.out.println("Usage: " + progname + " [-validate TRUE|FALSE]" + "[-mode OUTPUTMODE|DIFFMODE|RAWMODE|RESULTSMODE]" + " [-in <filename>.xml]" + " [-out <filename>[.pdf|.txt]");
		System.exit(1);
	}
	
	public void setparams(CommandLineParser parser) throws FileNotFoundException {
		
		// Get mode
		if (parser.arg("-mode") != null)
			mode = RunMode.valueOf(parser.arg("-mode"));
		
		// Get input file
		if (parser.arg("-in") != null) {
			inFile = parser.arg("-in");
			if (!inFile.endsWith(".xml"))
				usage();
		}
		
		// Get pdf output file
		if (parser.arg("-out") != null) {
			outFile = parser.arg("-out");
		}
		else if (inFile != null) {
			if (mode == RunMode.RESULTSMODE)
				outFile = inFile.replace(".xml", "_results.xml");
			else
				outFile = inFile.replace(".xml", ".pdf");
		}
		else if (mode != RunMode.RAWMODE)
			usage();
		
		// Validate?
		if (parser.arg("-validate") != null)
			validate = Boolean.valueOf(parser.arg("-validate"));
		
		// Compare tool
		if (parser.arg("-comparetool") != null)
			comparetool = parser.arg("-comparetool");
		
		// Baseline reference file
		if (parser.arg("-referencefile") != null)	
			refFile = parser.arg("-referencefile");
		else if (outFile != null)
			refFile = outFile.replace("sampleData", "referenceOutput");
		else if (mode == RunMode.DIFFMODE)
			usage();
		
		// Difference file
		if (parser.arg("-differencefile") != null)	
			difFile = parser.arg("-differencefile");
		else if (outFile != null)
			difFile = outFile.replace(".pdf", "_diff_" + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + ".pdf");
		else if (mode == RunMode.DIFFMODE)
			usage();
	}
	
	private void outputModeWrite(Document xmldoc) throws Exception {
		OutputStream ostream = null;
		
		// Loop through the results writing each pdf document to 'out'
		NodeList nodes = xmldoc.getElementsByTagName("Document");
		for (int i = 0; i < nodes.getLength(); i++) {
			Element documentElement = (Element)nodes.item(i);
			
			// Create output stream
			File file = null;
			if (nodes.getLength() > 1) // Multiple disclosures, so rename file
				file = new File(outFile.replace(".pdf", "_" + String.valueOf(i) + ".pdf"));
			else
				file = new File(outFile);
			ostream = new FileOutputStream(file);

			// Add file name to XML
			documentElement.appendChild(xmldoc.createElement("File")).setTextContent(file.getCanonicalPath());
			
			// Find the encoded pdf, decode it, write it
			Element pdfDocumentElement = (Element)documentElement.getElementsByTagName("PdfDocument").item(0);
			if (pdfDocumentElement != null) {
				byte[] pdf = UniformDisclosureResults.decoder.decode(pdfDocumentElement.getTextContent());
				ostream.write(pdf);
			}
			
			// Close the stream
			ostream.flush();
			ostream.close();

			// Compare files
			diff(outFile);

			// Remove the encoded pdf from the xml (so that only status information remains)
			if (pdfDocumentElement != null)
				pdfDocumentElement.getParentNode().removeChild(pdfDocumentElement);
		}
		
		// Write execution results
		writeDocument(xmldoc, System.err);		
	}

	public void run() throws Exception {
		
		// Set the input stream
		InputStream in  = System.in;
		if (inFile != null)
			in = new FileInputStream(new File(inFile));
		
		// Get the marshalled results
		Document xmldoc = builder.run(in, validate);
		
		// Output document
		switch (mode) {
		case RAWMODE:
			writeDocument(xmldoc, System.out);
			break;
		case RESULTSMODE:
			File file = new File(outFile);
			FileOutputStream ostream = new FileOutputStream(file);
			writeDocument(xmldoc, ostream);
			ostream.flush();
			ostream.close();
			break;
		default:
			outputModeWrite(xmldoc);
		}
	}
	
	private void writeDocument(Document xmldoc, OutputStream out) throws TransformerFactoryConfigurationError, TransformerException {

		// Prepare document to write
		Transformer tr = TransformerFactory.newInstance().newTransformer();
	    tr.setOutputProperty(OutputKeys.INDENT, "yes");
	    tr.setOutputProperty(OutputKeys.METHOD, "xml");
	    tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

	    // Write xmldoc to stream out
	    tr.transform(new DOMSource(xmldoc), new StreamResult(out));
	}

	private void diff(String outFile) {
		if (mode == RunMode.DIFFMODE) {
			Runtime rt = Runtime.getRuntime();
		    try {
		    	String command = comparetool + " --output-diff=" + difFile + " " + outFile + " " + refFile;
		        rt.exec(command);

		    } catch (IOException e) {
		    	System.err.println(e);
		    }
		}
	}
	
	public static void main(String args[]) {
		try {
			UniformDisclosure builder = new UniformDisclosure();
			builder.setparams(new CommandLineParser(args));
			builder.run();
	    } catch (Exception e) {
	        System.err.println(e);
	    	StackTraceElement[] stackTraceElement = e.getStackTrace();
	    	for (StackTraceElement element : stackTraceElement)
	    		System.err.println(element.toString());
	    } finally {
			System.exit(0);
		}
	}
}