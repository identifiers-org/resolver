/*
 * Author: Camille Laibe
 * Copyright: EMBL-EBI, Computational Neurobiology Group
 * Project: Miriam
 */

/**
 * TODO:
 * - use MiriamLib instead putting all the code for using web services here!
 * - 
 */

package uk.ac.ebi.miriam.wsi;


import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.encoding.XMLType;
import org.apache.log4j.Logger;


/**
 * <p>Creates a link between the different servlets and the MIRIAM database (using Web Services) ("model" part of the web application)
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>EMBL-EBI, Computational Neurobiology Group</dd>
 * </dl>
 * 
 * @author Camille Laibe
 * @version 20060804
 */
public class MiriamLink
{
	// physical URL of the web services
	private String endPoint = new String();
	
    // for logging, NOT 'static' in a servlet or j2ee container!
	private Logger logger = Logger.getLogger(MiriamLink.class);
	private Call call = null;
	
	/**
	 * Constructor
	 */
	public MiriamLink(String endPoint)
	{
		this.endPoint = endPoint;
		logger.debug("Web Services endPoint used: " + this.endPoint);
	    try
	    {
	        // creation of a Call object for the communication
	        ServiceFactory serviceFactory = ServiceFactory.newInstance();
	        Service service = serviceFactory.createService(new QName("MiriamWebServices"));
	        call = (Call) service.createCall();
	    
	        // endpoint of the service
	        call.setTargetEndpointAddress(endPoint);
	    }
	    catch (Exception e)
	    {
	        logger.error("Exception occured: " + e.toString());
	    }
	}
	
	
	/*
	 * Template for calling a web service
	 * with one parameter and returning a String 
	 */
	private String simpleStringCall(String method, String param)
	{
		String result = new String();

	    try
	    {
	        // name of the method invocated
	        call.setOperationName(new QName(method));
	
	        // type of the parameters and return type
	        call.addParameter("param1", XMLType.XSD_STRING, ParameterMode.IN);
	        call.setReturnType(XMLType.XSD_STRING);
	        
	        // at least, invocation of the service
	        result = (String) call.invoke(new Object[] { new String(param) });
	    }
	    catch (Exception e)
	    {
	        logger.error("Exception occured: " + e.toString());
	    }
	    
		return result;
	}
	
	
	/*
	 * Template for calling a web service
	 * with two parameters and returning a String 
	 */
	private String complexStringCall(String method, String param1, String param2)
	{
	    try
	    {
	        // name of the method invocated
	        call.setOperationName(new QName(method));
	
	        // type of the parameters and return type
	        call.addParameter("param1", XMLType.XSD_STRING, ParameterMode.IN);
	        call.addParameter("param2", XMLType.XSD_STRING, ParameterMode.IN);
	        call.setReturnType(XMLType.XSD_STRING);
	
	        // at least, invocation of the service
	        String result = (String) call.invoke(new Object[] { new String(param1), new String(param2) });
	        
	        return result;
	    }
	    catch (Exception e)
	    {
	    	logger.error("Exception occured: " + e.toString());
	    	return null;
	    }
	}
	
	
	/*
	 * Template for calling a web service
	 * with three parameters and returning a String
	 */
	public String tooComplexStringCall(String method, String param1, String param2, String param3)
	{
	    try
	    {
	        // name of the method invocated
	        call.setOperationName(new QName(method));
	
	        // type of the parameters and return type
	        call.addParameter("param1", XMLType.XSD_STRING, ParameterMode.IN);
	        call.addParameter("param2", XMLType.XSD_STRING, ParameterMode.IN);
	        call.addParameter("param3", XMLType.XSD_STRING, ParameterMode.IN);
	        call.setReturnType(XMLType.XSD_STRING);
	
	        // at least, invocation of the service
	        String result = (String) call.invoke(new Object[] { new String(param1), new String(param2), new String(param3) });
	        
	        return result;
	    }
	    catch (Exception e)
	    {
	    	logger.error("Exception occured: " + e.toString());
	    	return null;
	    }
	}
	
	
	/*
	 * Template for calling a web service
	 * with one parameter and returning a Array of Strings
	 */
	private String[] simpleArrayCall(String method, String param)
	{
	    try
	    {
	        // name of the method invocated
	        call.setOperationName(new QName(method));
	
	        // type of the parameters and return type
	        call.addParameter("param1", XMLType.XSD_STRING, ParameterMode.IN);
	        call.setReturnType(XMLType.SOAP_ARRAY);
	
	        // at least, invocation of the service
	        String[] result = (String[]) call.invoke(new Object[] { new String(param) });
	        
	        return result;
	    }
	    catch (Exception e)
	    {
	    	logger.error("Exception occured: " + e.toString());
	    	return null;
	    }
	}
	
	
	/*
	 * Template for calling a web service
	 * with two parameters and returning a Array of Strings 
	 */
	private String[] complexArrayCall(String method, String param1, String param2)
	{
	    try
	    {
	        // name of the method invocated
	        call.setOperationName(new QName(method));
	
	        // type of the parameters and return type
	        call.addParameter("param1", XMLType.XSD_STRING, ParameterMode.IN);
	        call.addParameter("param2", XMLType.XSD_STRING, ParameterMode.IN);
	        call.setReturnType(XMLType.SOAP_ARRAY);
	
	        // at least, invocation of the service
	        String[] result = (String[]) call.invoke(new Object[] { new String(param1), new String(param2) });
	        
	        return result;
	    }
	    catch (Exception e)
	    {
	    	logger.error("Exception occured: " + e.toString());
	    	return null;
	    }
	}
	
	
	/**
	 * Retrieves the unique URN of a data-type (example: "urn:lsid:uniprot.org") (using Web Services).
	 * @param param name of a data-type
	 * @return unique URN of the data-type
	 */
	public String getDataTypeURNAnswer(String param)
	{
		return simpleStringCall("getDataTypeURN", param);
	}
	
	
	/**
	 * Retrieves the unique (official) URN of the data-type (example: "urn:lsid:uniprot.org") and all the deprecated ones (using Web Services).
	 * @param param name of a data-type
	 * @return unique URN + deprecated URNs of the data-type
	 */
	public String[] getDataTypeURNsAnswer(String param)
	{
		return simpleArrayCall("getDataTypeURNs", param);
	}
	
	
	/**
	 * Retrieves unique (official) URL (not a physical URL but a URI) of the data-type (example: "http://www.taxonomy.org/") (using Web Services).
	 * @param param name of a data-type (example: "ChEBI", "UniProt", ...)
	 * @return unique URL of the data-type (example: "http://uniprot.org")
	 */
	public String getDataTypeURLAnswer(String param)
	{
		return simpleStringCall("getDataTypeURL", param);
	}
	
	
	/**
	 * Retrieves the unique (official) URL (not a physical URL but a URI) of the data-type (example: "http://www.taxonomy.org/") and all the deprecated ones (using Web Services).
	 * @param param name of a data-type (example: "ChEBI", "UniProt", ...)
	 * @return unique URL + deprecated URLs of the data-type
	 */
	public String[] getDataTypeURLsAnswer(String param)
	{
		return simpleArrayCall("getDataTypeURLs", param);
	}
	
	
	/**
	 * Retrieves the unique (official) URL or URN of the data-type (example: "http://www.taxonomy.org/", "urn:lsid:uniprot.org") (using Web Services).
	 * @param param name of a data-type (example: "ChEBI", "UniProt", ...)
	 * @param type type of the returned URI (URN or URL)
	 * @return unique URI (URN or URL) of the data-type
	 */
	public String getDataTypeURIAnswer(String param, String type)
	{
		return complexStringCall("getDataTypeURI", param, type);
	}
	
	
    /**
     * Retrieves all the URLs or URNs of the data-type (examples: "http://www.taxonomy.org/", "urn:lsid:uniprot.org") including all the deprecated ones.
     * @param name name of the data-type (examples: "ChEBI", "UniProt")
     * @param type type of the URI the user wants to recover ('URN' or 'URL'), can be empty
     * @return all the URIs (URLs or URNs) of the data-type including all the deprecated ones
     */
	public String[] getDataTypeURIsAnswer(String name, String type)
	{
		return complexArrayCall("getDataTypeURIs", name, type);
	}
	
	
	/**
	 * Retrieves all the URLs and URNs of the data-type (examples: "http://www.taxonomy.org/", "urn:lsid:uniprot.org") including all the deprecated ones.
	 * @param name name of the data-type (examples: "ChEBI", "UniProt")
	 * @return all the URIs (URLs and URNs) of the data-type including all the deprecated ones
	 */
	public String[] getDataTypeAllURIsAnswer(String name)
	{
		return simpleArrayCall("getDataTypeAllURIs", name);
	}
	
	
	/**
	 * Retrieves the definition of the data-type (using Web Services).
	 * @param param name, synonym or URI (URN or URL) of a data-type
	 * @return defnition of the data-type
	 */
	public String getDataTypeDefAnswer(String param)
	{
		return simpleStringCall("getDataTypeDef", param);
	}
	
	
	/**
	 * Retrieves the unique URN of the data-entry (example: "urn:lsid:uniprot.org:P62158") (using Web Services).
	 * @param name name of a data-type
	 * @param id identifier of an element (pathway, organism, ...)
	 * @return unique URN of the element
	 */
	public String getURNAnswer(String name, String id)
	{
		return complexStringCall("getURN", name, id);
	}
	
	
	/**
	 * Retrieves unique URL of the data-entry (example: "http://www.geneontology.org/#GO:0045202") (using Web Services).
	 * @param name name of a data-type
	 * @param id identifier of an element (pathway, organism, ...)
	 * @return unique URL of the element
	 */
	public String getURLAnswer(String name, String id)
	{
		return complexStringCall("getURL", name, id);
	}
	
	/**
	 * Retrieves the unique URI (URL or URN) of the data-entry (example: "http://www.geneontology.org/#GO:0045202", "urn:lsid:uniprot.org") (using Web Services).
	 * @param name name of a data-type (example: "ChEBI", "UniProt", ...)
	 * @param id identifier of an element (pathway, organism, ...)
	 * @param type type of the returned URI (URN or URL)
	 * @return unique URI (URN or URL) of the data-type
	 */
	public String getURIAnswer(String name, String id, String type)
	{
		return tooComplexStringCall("getURI", name, id, type);
	}
	
	
	/**
	 * Retrieves the physical locationS (URLs) of web pageS about the data-entry (using Web Services).
	 * @param name name or URI of a data-type
	 * @param id identifier of an element (pathway, organism, ...)
	 * @return physical URLs where you can find information about the data-entry (link to the good page)
	 */
	public String[] getDataEntriesAnswer(String name, String id)
	{
		return complexArrayCall("getDataEntries", name, id);
	}
	
	
	/**
	 * Retrieves all the physical locations (URLs) of the services providing the data-type (web page) (using Web Services).
	 * @param name name or URI of a data-type
	 * @return physical URLs where you can find information about the data-type (link to the front page)
	 */
	public String[] getDataResourcesAnswer(String name)
	{
		return simpleArrayCall("getDataResources", name);
	}
	
	
	/**
	 * Says if a URI of a data-type is deprecated (using Web Services).
	 * @param uri URI (URN or URL) of a data-type
	 * @return "true" or "false" to the question: is this URI deprecated? 
	 */
	public String isDeprecatedAnswer(String uri)
	{
		return simpleStringCall("isDeprecated", uri);
	}
	
	
	/**
	 * Retrieves the offical URI (URN if the user entered a URN, URL if not) of a data-type corresponding to the deprecated one (using Web Services).
	 * @param uri deprecated URI (URN or URL) of a data-type
	 * @return non deprecated URI of the data-type, the type of the URI returned (URL or URN) is the same of the one of the parameter
	 */
	public String getOfficialURIAnswer(String uri)
	{
		return simpleStringCall("getOfficialURI", uri);
	}
	
	
	/**
	 * Retrieves the pattern (regular expression) of the data-type (using Web Services).
	 * @param uri URI (URn or URL) of a data-type
	 * @return regular expression of the data-type
	 */
	public String getDataTypePatternAnswer(String uri)
	{
		return simpleStringCall("getDataTypePattern", uri);
	}
	
	
	/**
	 * Retrieves all the synonym names of the data-type, with the original name (using Web Services).
	 * @param name name (can be a synonym) of a data-type
	 * @return all the synonym names of the data-type
	 */
	public String[] getDataTypeSynonymsAnswer(String name)
	{
		return simpleArrayCall("getDataTypeSynonyms", name);
	}
	
	
	/**
	 * Retrieves the common name of a data-type (using Web Services).
	 * @param uri URI (URL or URN) of a data-type
	 * @return the common name of the data-type
	 */
	public String getNameAnswer(String uri)
	{
		return simpleStringCall("getName", uri);
	}
	
	
	/**
	 * Retrieves all the names (with synonyms) of a data-type (using Web Services).
	 * @param uri URI (URL or URN) of a data-type
	 * @return the common name of the data-type and all the synonyms
	 */
	public String[] getNamesAnswer(String uri)
	{
		return simpleArrayCall("getNames", uri);
	}
}
