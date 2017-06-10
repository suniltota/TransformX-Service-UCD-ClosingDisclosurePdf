package com.actualize.mortgage.datavalidation;

import java.io.File;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

public class MISMOValidation {
	private static String mismoXsd = "lib/MISMO_3.3.1_B307.xsd";
	private static Validator validator = null;
	
	public static void validateXML(InputStream stream) throws Exception {
		try {
			if (validator == null)
				validator = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(mismoXsd)).newValidator();
			validator.validate(new StreamSource(stream));
        } catch (Exception e) {
            throw new Exception("ERROR (001) " + e.getMessage());
        }
    }
}
