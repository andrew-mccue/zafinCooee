package org.karora.cooee.osgi;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class OWASPmethods {

//	private static Boolean doXMLExternalEntityInjectionPrevention = null;
//	
//	private final static String Zafin_XMLEEIP = "Zafin_XMLEEIP";
//	
//	
//	public static boolean getDoXMLExternalEntityInjectionPrevention(){
//		if( doXMLExternalEntityInjectionPrevention == null ){
//			String setting = System.getProperty(Zafin_XMLEEIP);
//			if( setting != null ){
//				doXMLExternalEntityInjectionPrevention = Boolean.valueOf(setting);
//			}
//		}
//		return( doXMLExternalEntityInjectionPrevention.booleanValue() );
//	}
	
	public static void documentBuilderFactoryOWASPSettings( DocumentBuilderFactory factory ){
		// Public Recommended Code From XML External Entity (XXE) Prevention Cheat Sheet
		// https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet
		// System.out.println( "OWASPmethods:documentBuilderFactoryOWASPSettings:doXMLExternalEntityInjectionPrevention:Zafin_XMLEEIP["+getDoXMLExternalEntityInjectionPrevention()+"]");
		// if( getDoXMLExternalEntityInjectionPrevention() ){
			String FEATURE = null;
			try {
				// This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all XML entity attacks are prevented
				// Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
				FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
				factory.setFeature(FEATURE, true);
	
				// If you can't completely disable DTDs, then at least do the following:
				// Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
				// Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
				// JDK7+ - http://xml.org/sax/features/external-general-entities    
				FEATURE = "http://xml.org/sax/features/external-general-entities";
				factory.setFeature(FEATURE, false);
	
				// Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
				// Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
				// JDK7+ - http://xml.org/sax/features/external-parameter-entities    
				FEATURE = "http://xml.org/sax/features/external-parameter-entities";
				factory.setFeature(FEATURE, false);
	
				// Disable external DTDs as well
				FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
				factory.setFeature(FEATURE, false);
	
				// and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
				factory.setXIncludeAware(false);
				factory.setExpandEntityReferences(false);
	
				// And, per Timothy Morgan: "If for some reason support for inline DOCTYPEs are a requirement, then 
				// ensure the entity settings are disabled (as shown above) and beware that SSRF attacks
				// (http://cwe.mitre.org/data/definitions/918.html) and denial 
				// of service attacks (such as billion laughs or decompression bombs via "jar:") are a risk."
	
				// remaining parser logic
				
				// Zafin items from original settings for factory instances brought here so that they are all in the same place
				factory.setNamespaceAware(true);
				factory.setValidating(false);
				factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.valueOf(false));
	
	            factory.setNamespaceAware(true);
	            
			} catch (ParserConfigurationException e) {
				// This should catch a failed setFeature feature
				System.out.println("Cooee:documentBuilderFactoryOWASPSettings:ParserConfigurationException was thrown. The feature '" +
						FEATURE + "' is probably not supported by your XML processor.");
			}
		//}
	}

}
