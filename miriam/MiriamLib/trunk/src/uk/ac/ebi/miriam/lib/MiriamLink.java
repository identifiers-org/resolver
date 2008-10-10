/*
 * Java library to use MIRIAM Web Services.
 * MIRIAM is an online resource created to catalogue biological data types,  
 * their URIs and the corresponding physical URLs, 
 * whether these are controlled vocabularies or databases.
 * Ref. http://www.ebi.ac.uk/miriam/
 * 
 * Copyright (C) 2006-2008  Camille Laibe (EMBL - European Bioinformatics Institute, Computational Neurobiology Group)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */


package uk.ac.ebi.miriam.lib;


import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.encoding.XMLType;
import org.apache.log4j.Logger;


/**
 * <p>
 * Library for using MIRIAM Web Services easily (via a Java program).
 * </p>
 * <p>
 * MIRIAM is an online resource created to catalogue biological data types,  
 * their URIs and the corresponding physical URLs, 
 * whether these are controlled vocabularies or databases.
 * </p>
 * <p>
 * See <a href="http://www.ebi.ac.uk/miriam/" title="Web Site of MIRIAM Resources">MIRIAM Resources</a>, for more information. 
 * </p>
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2008 Camille Laibe (EMBL - European Bioinformatics Institute, Computational Neurobiology Group)
 * <br />
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <br />
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * </dd>
 * </dl>
 * </p>
 * 
 * @author Camille Laibe <camille.laibe@ebi.ac.uk>
 * 
 * @version 20080422
 * 
 */
public class MiriamLink
{
	/** name of the services */
	private final String webServicesName = "MiriamWebServices";
	/** default address to access to the services */
	private String address = "http://www.ebi.ac.uk/compneur-srv/miriamws-main/MiriamWebServices";
	/** Call object represents the dynamic call of the service */
	private Call call = null;
	/** logger */
	private Logger logger = Logger.getLogger(MiriamLink.class);
	/** version of the Java library */
    private final String JAVA_LIBRARY_VERSION = "20080421";
	
	
	/**
	 * Default constructor: initialisation of some parameters
	 */
	public MiriamLink()
	{
		// nothing
		logger.debug("You are using the MIRIAM Java Library for accessing MIRIAM Web Services");
	}
	
	/*
	 * Initialisation of the communication (creation of the SOAP message)
	 */
	private void init()
	{
		logger.debug("Initialisation of the connection...");   // DEBUG
		logger.info("Address used: " + address);
		logger.info("If this address is not correct, use 'setAddress()' to change it.");
		try
	    {
	        // creation of a Call object for the communication
	        ServiceFactory serviceFactory = ServiceFactory.newInstance();
	        Service service = serviceFactory.createService(new QName(webServicesName));
	        call = (Call) service.createCall();
	    
	        // address of the service
	        call.setTargetEndpointAddress(address);
	    }
	    catch (Exception e)
	    {
	        logger.error("Exception occurred during the init of the communication: " + e.toString());
	    }
	}


	/**
	 * Setter of the address of the web services (endPoint).
	 * @param address whole address of the web services
	 */
	public void setAddress(String address)
	{
		this.address = address;
		logger.debug("A new address to access the Web Services has been set (" + this.address + ")");
	}
	
	
	/**
	 * Getter of the address of the web services (endPoint).
	 * @return whole address of the web services
	 */
	public String getAddress()
	{
		return this.address;
	}
	
	
	/**
     * Retrieves the current version of MIRIAM Web Services Library.  
     * @return Current version of the Library
	 * @return
	 */
	public String getLibraryVersion()
	{
	    return JAVA_LIBRARY_VERSION;
	}
	
	
	/**
	 * Tests if the Java library used is the latest available.
	 * @return is the Java library currently used is the latest or not?
	 */
	public boolean isLibraryUpdated()
	{
	    String latestLibrary = verySimpleStringCall("getJavaLibraryVersion");
	    int latestLibraryVersion = Integer.parseInt(latestLibrary);
	    int currentLibraryVersion = Integer.parseInt(JAVA_LIBRARY_VERSION);
	    
	    return (currentLibraryVersion >= latestLibraryVersion);
	}
	
	
	/**
     * Retrieves some information about these Web Services.
     * @return information about the Web Services
     */
    public String getServicesInfo()
    {
        return verySimpleStringCall("getServicesInfo");
    }
	
	
	/**
     * Retrieves the current version of MIRIAM Web Services.  
     * @return Current version of the Web Services
	 */
    public String getServicesVersion()
    {
        return verySimpleStringCall("getServicesVersion");
    }
    
    
    /**
     * Retrieves the unique official URN of a data type (example: <em>urn:miriam:uniprot</em>).
     * @param name name primary name (or synonym) of a data type (examples: "ChEBI", "UniProt", "GO")
     * @return unique (official) URN of the data type
     * @deprecated Use {@link #getDataTypeURI(String)} instead.
     */
    @Deprecated
    public String getDataTypeURN(String name)
    {
    	return simpleStringCall("getDataTypeURN", name);
    }
    
    
    /**
     * Retrieves the unique (official) URN of the data type (example: "urn:miriam:uniprot") and all the deprecated ones.
     * @param name name or synonym of a data type (examples: "ChEBI", "UniProt")
     * @return unique URN and all the deprecated ones
     * @deprecated Use {@link #getDataTypeURIs(String)} instead.
     */
    @Deprecated
    public String[] getDataTypeURNs(String name)
    {
    	return simpleArrayCall("getDataTypeURNs", name);
    }
    
    
    /**
     * Retrieves the unique (official) URL (not a physical URL but a URI) of the data type (example: "http://www.taxonomy.org/").
     * @param name name of a data type (examples: "ChEBI", "UniProt")
     * @return unique URL of the data type
     * @deprecated URLs are not used any more as identifiers in MIRIAM. A new scheme as been released based on URNs. Use {@link #getDataTypeURI(String)} instead.
     */
    @Deprecated
    public String getDataTypeURL(String name)
    {
    	return simpleStringCall("getDataTypeURL", name);
    }
    
    
    /**
     * Retrieves the unique (official) URL (not a physical URL but a URI) of the data type (example: "http://www.taxonomy.org/") and all the deprecated ones.
     * @param name name of a data type (examples: "ChEBI", "UniProt")
     * @return unique URL of the data type and all the deprecated ones
     * @deprecated URLs are not used any more as identifiers in MIRIAM. A new scheme as been released based on URNs. You can use {@link #getDataTypeURIs(String)} instead.
     */
    @Deprecated
    public String[] getDataTypeURLs(String name)
    {
    	return simpleArrayCall("getDataTypeURLs", name);
    }
    
    
    /**
     * Retrieves the unique (official) URL or URN of the data type (example: "http://www.taxonomy.org/", "urn:lsid:uniprot.org").
     * @param name name of the data type (examples: "ChEBI", "UniProt")
     * @param type type of the URI the user wants to retrieve ('URN' or 'URL')
     * @return unique URL or URN of the data type
     * @deprecated URLs are not used any more as identifiers in MIRIAM. A new scheme as been released based on URNs. Use {@link #getDataTypeURI(String)} instead.
     */
    @Deprecated
    public String getDataTypeURI(String name, String type)
    {
    	return complexStringCall("getDataTypeURI", name, type);
    }
    
    
    /**
     * Retrieves the unique (official) URI of a data type (example: "urn:miriam:uniprot").
     * @param name name or synonym of a data type (examples: "UniProt")
     * @return unique URI of the data type
     */
    public String getDataTypeURI(String name)
    {
        return simpleStringCall("getDataTypeURI", name);
    }
    
    
    /**
     * Retrieves all the URLs or URNs of the data type (examples: "urn:miriam:uniprot", "http://www.taxonomy.org/") including all the deprecated ones.
     * @param name name of the data type (examples: "ChEBI", "UniProt")
     * @param type type of the URI the user wants to recover ('URN' or 'URL')
     * @return all the URIs (URLs or URNs) of the data type including all the deprecated ones
     * @deprecated URLs are not used any more as identifiers in MIRIAM. A new scheme as been released based on URNs. Use {@link #getDataTypeURIs(String)} instead.
     */
    @Deprecated
    public String[] getDataTypeURIs(String name, String type)
    {
    	return complexArrayCall("getDataTypeURIs", name, type);
    }
    
    
    /**
     * Retrieves all the URIs (URNs and URLs) of the data type
     * @param name name of a data type (examples: "ChEBI", "UniProt")
     * @return all the URIs (URLs and URNs) of the data type including all the deprecated ones
     * @deprecated Use {@link #getDataTypeURIs(String)} instead.
     */
    @Deprecated
	public String[] getDataTypeAllURIs(String name)
	{
		return simpleArrayCall("getDataTypeAllURIs", name);
	}
    
    
    /**
     * Retrieves all the URIs of a data type, including all the deprecated ones (examples: "urn:miriam:uniprot", "http://www.uniprot.org/", "urn:lsid:uniprot.org:uniprot", ...).
     * @param name name (or synonym) of the data type (examples: "ChEBI", "UniProt")
     * @return all the URIs of a data type (including the deprecated ones)
     */
    public String[] getDataTypeURIs(String name)
    {
        return simpleArrayCall("getDataTypeURIs", name);
    }
	
	
	/**
	 * Retrieves the location (or country) of a resource (example: "United Kingdom").
	 * @param id identifier of a resource (example: "MIR:00100009")
	 * @return the location (the country) where the resource is managed
	 */
	public String getResourceLocation(String id)
	{
		return simpleStringCall("getResourceLocation", id);
	}
	
	
	/**
	 * Retrieves the institution of a resource (example: "European Bioinformatics Institute").
	 * @param id identifier of a resource (example: "MIR:00100009")
	 * @return the institution managing the resource
	 */
	public String getResourceInstitution(String id)
	{
		return simpleStringCall("getResourceInstitution", id);
	}
	
	
	/**
	 * Retrieves the information about a resource.
	 * @param id identifier of a resource (example: "MIR:00100009")
	 * @return some information about the resource
	 */
	public String getResourceInfo(String id)
	{
		return simpleStringCall("getResourceInfo", id);
	}
    
    
    /**
     * Retrieves the unique URN of the data-entry (example: "urn:miriam:uniprot:P62158").
     * @param name name of a data type (examples: "ChEBI", "UniProt")
     * @param id identifier of an element (examples: "GO:0045202", "P62158")
     * @return unique URN of the data-entry
     * @deprecated Use {@link #getURI(String, String)} instead.
     */
    @Deprecated
    public String getURN(String name, String id)
    {
    	return complexStringCall("getURN", name, id);
    }
    
    
    /**
     * Retrieves unique URL of the data-entry (example: "urn:miriam:obo.go:GO%3A0045202").
     * @param name name of a data type (examples: "ChEBI", "UniProt")
     * @param id identifier of an element (examples: "GO:0045202", "P62158")
     * @return unique URN of the data-entry
     * @deprecated Use {@link #getURI(String, String)} instead.
     */
    @Deprecated
    public String getURL(String name, String id)
    {
    	return complexStringCall("getURL", name, id);
    }
    
    
    /**
     * Retrieves the unique URI (URL or URN) of the data-entry (example: "urn:miriam:obo.go:GO%3A0045202").
     * @param name name of a data type (examples: "ChEBI", "UniProt")
     * @param id identifier of an element (examples: "GO:0045202", "P62158")
     * @param type type of the URI the user wants to recover ('URN' or 'URL')
     * @return unique URI of the data-entry
     * @deprecated Use {@link #getURI(String, String)} instead.
     */
    @Deprecated
    public String getURI(String name, String id, String type)
    {
    	return tooComplexStringCall("getURI", name, id, type);
    }
    
    
    /**
     * Retrieves the unique MIRIAM URI of a specific entity (example: "urn:miriam:obo.go:GO%3A0045202").
     * @param name name of a data type (examples: "ChEBI", "UniProt")
     * @param id identifier of an enity within the data type (examples: "GO:0045202", "P62158")
     * @return unique MIRIAM URI of a given entity
     */
    public String getURI(String name, String id)
    {
        return complexStringCall("getURI", name, id);
    }
    
    
    /**
	 * Retrieves the definition of a data type.
	 * @param nickname name or URI (URN or URL) of a data type
	 * @return definition of the data type
	 */
    public String getDataTypeDef(String nickname)
    {
    	return simpleStringCall("getDataTypeDef", nickname);
    }
    
    
    /**
	 * Retrieves the physical locationS (URLs) of web pageS about the data-entry.
	 * @param nickname name (can be a synonym) or URN or URL of a data type (examples: "ChEBI", "UniProt")
	 * @param id identifier of an element (examples: "GO:0045202", "P62158")
	 * @return physical locationS of web pageS about the data-entry
	 * @deprecated Use {@link #getLocations(String, String)} instead.
	 */
    @Deprecated
    public String[] getDataEntries(String nickname, String id)
    {
    	return complexArrayCall("getDataEntries", nickname, id);
    }
    
    
    /**
     * Retrieves the physical locationS (URLs) of web pageS about the data-entry.
     * @param nickname name (can be a synonym) or URN or URL of a data type (examples: "ChEBI", "UniProt")
     * @param id identifier of an element (examples: "GO:0045202", "P62158")
     * @return physical locationS of web pageS about the data-entry
     * @deprecated Use {@link #getLocations(String, String)} instead.
     */
    @Deprecated
    public String[] getDataEntries(String uri)
    {
        return simpleArrayCall("getDataEntries", uri);
    }
    
    
    /**
     * Retrieves the physical location (URL) of a web page about the data-entry, using a specific resource.
     * @param uri MIRIAM URI of an element (example: 'urn:miriam:obo.go:GO%3A0045202')
     * @param resource internal identifier of a resource (example: 'MIR:00100005')
     * @return physical location of a web page about the data-entry, using a specific resource
     * @deprecated Use {@link #getLocation(String, String)} instead.
     */
    @Deprecated
    public String getDataEntry(String uri, String resource)
    {
        return complexStringCall("getDataEntry", uri, resource);
    }
    
    
    /**
     * Retrieves the physical location (URL) of a web page providing knowledge about a specific entity, using a specific resource.
     * @param uri MIRIAM URI of an entity (example: 'urn:miriam:obo.go:GO%3A0045202')
     * @param resource internal identifier of a resource (example: 'MIR:00100005')
     * @return physical location of a web page providing knowledge about the given entity, using a specific resource
     */
    public String getLocation(String uri, String resource)
    {
        return complexStringCall("getLocation", uri, resource);
    }
    
    
    /**
     * Retrieves the physical locationS (URLs) of web pageS providing knowledge about a specific entity.
     * @param uri MIRIAM URI of an entity (example: 'urn:miriam:obo.go:GO%3A0045202')
     * @return physical locationS of web pageS providing knowledge about the given entity
     */
    public String[] getLocations(String uri)
    {
        return simpleArrayCall("getLocations", uri);
    }
    
    
    /**
     * Retrieves the physical locationS (URLs) of web pageS providing knowledge about an entity.
     * @param nickname name (can be a synonym) or URI of a data type (examples: "Gene Ontology", "UniProt")
     * @param id identifier of an entity within the given data type (examples: "GO:0045202", "P62158")
     * @return physical locationS of web pageS providing knowledge about the given entity
     */
    public String[] getLocations(String nickname, String id)
    {
        return complexArrayCall("getLocations", nickname, id);
    }
    
    
    /**
     * Transforms a MIRIAM URI into its official equivalent (to transform obsolete URIs into current valid ones).
     * The parameter can be an obsolete URI (URN or URL), but the returned one will always be a URN.
     * This process involve a percent-encoding of all reserved characters (like ':').
     * @param uri deprecated URI (URN or URL), example: "http://www.ebi.ac.uk/chebi/#CHEBI:17891"
     * @return the official URI corresponding to the deprecated one, example: "urn:miriam:obo.chebi:CHEBI%3A17891"
     */
    public String getMiriamURI(String uri)
    {
        return simpleStringCall("getMiriamURI", uri);
    }
    
    
    /**
     * Retrieves all the physical locations (URLs) of the services providing the data type (web page).
     * @param nickname name (can be a synonym) or URL or URN of a data type name (or synonym) or URI (URL or URN)
     * @return array of strings containing all the address of the main page of the resources of the data type
	 */
    public String[] getDataResources(String nickname)
    {
    	return simpleArrayCall("getDataResources", nickname);
    }
    
    
    /**
	 * To know if a URI of a data type is deprecated.
	 * @param uri (URN or URL) of a data type
	 * @return answer ("true" or "false") to the question: is this URI deprecated?
	 */
    public boolean isDeprecated(String uri)
    {
    	if (simpleStringCall("isDeprecated", uri).equalsIgnoreCase("true"))
        {
    	    return true;   
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * Retrieves the official URI (it will always be URN) of a data type corresponding to the deprecated one.
     * @param uri deprecated URI (URN or URL) of a data type 
     * @return the official URI corresponding to the deprecated one
     * @deprecated Use {@link #getOfficialDataTypeURI(String)} instead.
     */
    @Deprecated
    public String getOfficialURI(String uri)
    {
    	return simpleStringCall("getOfficialURI", uri);
    }
    
    
    /*
     * Transforms a MIRIAM URI into its official equivalent (to transform obsolete URIs into current valid ones).
     * The parameter can be an obsolete URI (URN or URL), but the returned one will always be a URN.
     * This process involve a percent-encoding of all reserved characters (like ':').
     * @param uri deprecated URI (URN or URL), example: "http://www.ebi.ac.uk/chebi/#CHEBI:17891"
     * @return the official URI corresponding to the deprecated one, example: "urn:miriam:obo.chebi:CHEBI%3A17891"
     
    public String getOfficialEntryURI(String uri)
    {
        return simpleStringCall("getOfficialEntryURI", uri);
    }
    */
    
    /**
     * Retrieves the pattern (regular expression) used by the identifiers within a data type.
     * @param nickname data type name (or synonym) or URI (URL or URN)
     * @return pattern of the data type
	 */
    public String getDataTypePattern(String nickname)
    {
    	return simpleStringCall("getDataTypePattern", nickname);
    }
    
    
    /**
     * Retrieves all the synonym names of a data type (this list includes the original name).
     * @param name name or synonym of a data type
     * @return all the synonym names of the data type
	 */
    public String[] getDataTypeSynonyms(String name)
    {
    	return simpleArrayCall("getDataTypeSynonyms", name);
    }
    
    
    /**
	 * Retrieves the common name of a data type.
	 * @param uri URI (URL or URN) of a data type
	 * @return the common name of the data type
	 */
    public String getName(String uri)
    {
    	return simpleStringCall("getName", uri);
    }
    
    
    /**
	 * Retrieves all the names (with synonyms) of a data type.
	 * @param uri URI (URL or URN) of a data type
	 * @return the common name of the data type and all the synonyms
	 */
    public String[] getNames(String uri)
    {
    	return simpleArrayCall("getNames", uri);
    }
    
    /**
     * Retrieves the list of names of all the data types available.
     * @return list of names of all the data types
     */
    public String[] getDataTypesName()
    {
        return verySimpleArrayCall("getDataTypesName");
    }
    
    
    /**
     * Retrieves the internal identifier (stable and perennial) of all the data types (for example: "MIR:00000005").
     * @return list of the identifier of all the data types
     */
    public String[] getDataTypesId()
    {
        return verySimpleArrayCall("getDataTypesId");
    }
    
    
    /**
     * Retrieves the official URI (it will always be URN) of a data type corresponding to the deprecated one.
     * @param uri deprecated URI (URN or URL) of a data type 
     * @return the official URI of a data type corresponding to the deprecated one
     */
    public String getOfficialDataTypeURI(String uri)
    {
        return simpleStringCall("getOfficialDataTypeURI", uri);
    }
    
    
    /**
     * Checks if the identifier given follows the regular expression of its data type (also provided).
     * @param identifier internal identifier used by the data type
     * @param datatype name, synonym or URI of a data type
     * @return "true" if the identifier follows the regular expression, "false" otherwise
     */
    public boolean checkRegExp(String identifier, String datatype)
    {
        if (complexStringCall("checkRegExp", identifier, datatype).equalsIgnoreCase("true"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    /*
     * Template for calling a Web Service
     * with zero parameter and returning a <code>String</code>
     */
    private String verySimpleStringCall(String method)
    {
        // initialisation of the connection
        init();

        try
        {
            // name of the method invoked
            call.setOperationName(new QName(method));
    
            // type of the parameters and return type
            call.setReturnType(XMLType.XSD_STRING);
            
            // at least, invocation of the service
            String result = (String) call.invoke(new Object[] {});
            
            return result;
        }
        catch (Exception e)
        {
            logger.error("Exception occurred during the call of the Web Services: " + e.toString());
            return null;
        }
    }
    
    
    /*
	 * Template for calling a Web Service
	 * with one parameter and returning a <code>String</code> 
	 */
	private String simpleStringCall(String method, String param)
	{
		String result = null;
		
		if (isNotEmpty(param))
		{
    		// connection initialisation
    		init();
    
    	    try
    	    {
    	        // name of the method invoked
    	        call.setOperationName(new QName(method));
    	
    	        // type of the parameters and return type
    	        call.addParameter("param1", XMLType.XSD_STRING, ParameterMode.IN);
    	        call.setReturnType(XMLType.XSD_STRING);
    	        
    	        // at least, invocation of the service
    	        result = (String) call.invoke(new Object[] { new String(param) });
    	    }
    	    catch (Exception e)
    	    {
    	        logger.error("Exception occurred during the call of the Web Services: " + e.toString());
    	    }
		}
	    
		return result;
	}    
    
	
	/*
	 * Template for calling a Web Service
	 * with two parameters and returning a <code>String</code> 
	 */
	private String complexStringCall(String method, String param1, String param2)
	{
	    if ((isNotEmpty(param1)) && (isNotEmpty(param2)))
	    {
    		// initialisation of the connection
    		init();
    		
    	    try
    	    {
    	        // name of the method invoked
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
    	    	logger.error("Exception occurred during the call of the Web Services: " + e.toString());
    	    	return null;
    	    }
	    }
	    else
	    {
	        return null;
	    }
	}
	
	
	/*
	 * Template for calling a Web Service
	 * with three parameters and returning a <code>String</code>
	 */
	private String tooComplexStringCall(String method, String param1, String param2, String param3)
	{
	    if ((isNotEmpty(param1)) && (isNotEmpty(param2)) && (isNotEmpty(param3)))
	    {
    		// initialisation of the connection 
    		init();
    		
    	    try
    	    {
    	        // name of the method invoked
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
    	    	logger.error("Exception occurred during the call of the Web Services: " + e.toString());
    	    	return null;
    	    }
	    }
	    else
	    {
	        return null;
	    }
	}
    
    
    /*
     * Template for calling a Web Service
     * with zero parameter and returning an Array of <code>String</code>
     */
    private String[] verySimpleArrayCall(String method)
    {
        // connection initialisation
        init();

        try
        {
            // name of the method invoked
            call.setOperationName(new QName(method));
    
            // type of the parameters and return type
            call.setReturnType(XMLType.SOAP_ARRAY);
            
            // at least, invocation of the service
            String[] result = (String[]) call.invoke(new Object[] {});
            
            return result;
        }
        catch (Exception e)
        {
            logger.error("Exception occurred during the call of the Web Services: " + e.toString());
            return null;
        }
    }
	
	
	/*
	 * Template for calling a Web Service
	 * with one parameter and returning an Array of <code>String</code>
	 */
	private String[] simpleArrayCall(String method, String param)
	{
	    if (isNotEmpty(param))
	    {
    		// connection initialisation
    		init();
    		
    	    try
    	    {
    	        // name of the method invoked
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
    	    	logger.error("Exception occurred during the call of the Web Services: " + e.toString());
    	    	return null;
    	    }
	    }
	    else
	    {
	        return null;
	    }
	}
	
	
	/*
	 * Template for calling a Web Service
	 * with two parameters and returning an Array of <code>String</code>
	 */
	private String[] complexArrayCall(String method, String param1, String param2)
	{
	    if ((isNotEmpty(param1)) && (isNotEmpty(param2)))
	    {
    		// connection initialisation
    		init();
    		
    	    try
    	    {
    	        // name of the method invoked
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
    	    	logger.error("Exception occurred during the call of the Web Services: " + e.toString());
    	    	return null;
    	    }
	    }
	    else
	    {
	        return null;
	    }
	}
	
	
	/**
	 * 
	 * @param str
	 * @return
	 */
	private boolean isNotEmpty(String str)
	{
	    return ((null != str) && (str.length() != 0) && (! str.matches("\\s*")));
	}
}
