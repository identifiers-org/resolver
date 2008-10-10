/*
 * MIRIAM Resources (Web Services)
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


/*
 * TODO
 * - when catching the exception, sometimes need to exit the function instead of continues
 * -
 */

package uk.ac.ebi.miriam.ws;

// for SQL query
import java.sql.*;
// for logging
import org.apache.log4j.Logger;
// URL encoding
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * MIRIAM Web Services provider (server part).
 * 
 * <p>
 * Part of the MIRIAM Web Services package (uk.ac.ebi.miriam.ws)
 * 
 * <p>
 * Uses (with many thanks to these projects):
 * <ul>
 * <li>logging, with Log4J</li>
 * <li>connection pool, with DBCP</li>
 * </ul>
 * 
 * <p>
 * And:
 * <ul>
 * <li>Apache Tomcat</li>
 * <li>Apache Axis</li>
 * </ul>
 * 
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
 * @version 20080710
 * 
 */
public class MiriamProvider
{
    // for logging, NOT 'static' in a servlet or j2ee container!
	private Logger logger = Logger.getLogger(MiriamProvider.class);
	// dataSource
	private DataSourceManager database = null;
	// some constants
	private final int ID_NAME = 1;
	private final int ID_SYNONYM = 2;
	private final int ID_URL = 3;
	private final int ID_URN = 4;
	private final int ID_UNKNOWN = 5;
	// version of the Web Services
	private final String WEB_SERVICES_VERSION = "20080421";
	// version of the latest Java library
	private final String JAVA_LIBRARY_VERSION = "20080421";
	

	
	/**
	 * Constructor (initialises the logger and the database pool)
	 */
	public MiriamProvider()
	{
		// recovery of a dataSource in the pool
		logger.debug("DataSource creation...");
		database = DataSourceManager.getDataSourceInstance();
		logger.debug("DataSource created!");
	}
	
	
	/**
	 * Retrieves some information about these Web Services.
	 * @return information about the Web Services
	 */
	public String getServicesInfo()
	{
	    return "MIRIAM Resources Web Services. For more information: http://www.ebi.ac.uk/miriam/. Developed by: Camille Laibe <camille.laibe@ebi.ac.uk>";
	}
	
	
	/**
	 * Retrieves the current version of MIRIAM Web Services.  
	 * @return Current version of the Web Services
	 */
	public String getServicesVersion()
	{
	    return WEB_SERVICES_VERSION;
	}
	
	
	/**
	 * Retrieves the latest version of the Java library available.
	 * @return latest version of the Java library available  
	 */
	public String getJavaLibraryVersion()
	{
	    return JAVA_LIBRARY_VERSION;
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
		logger.debug("Call of the 'GetDataTypeURN()' request...");
		// SQL query
		String query = "SELECT uri FROM mir_uri WHERE (uri_type = 'URN' AND deprecated = '0' AND ptr_datatype IN (SELECT datatype_id FROM mir_datatype WHERE name = '" + name + "'))";
		// setup the context
		DataBaseContext context = new DataBaseContext();
		context.open(database);
        // request to the database
		ResultSet sqlResult = request(context.getStatement(), query);
		String result = new String();
        
	    try
	    {
			int nbLines = getRowCount(sqlResult);
			
			// only one result: primary name found
			if (nbLines == 1)
			{
				// no need for sqlResult.next(), because use of first() in getRowCount()
				result = sqlResult.getString(1);
			}
			else
			{
				// no result with the primary name: search for synonym(s)
				if (nbLines < 1)
				{
					query = "SELECT uri FROM mir_uri WHERE (uri_type = 'URN' AND deprecated = '0' AND ptr_datatype IN (SELECT ptr_datatype FROM mir_synonym WHERE name = '" + name + "'))";
					sqlResult = request(context.getStatement(), query);
					nbLines = getRowCount(sqlResult);
					// only one URN found
					if (nbLines == 1)
					{
						// no need for sqlResult.next(), because use of first() in getRowCount()
						result = sqlResult.getString(1);
					}
					else
					{
						if (nbLines > 1)
						{
							logger.warn("More than ONE URN found (based on synonyms) with GetDataTypeURN(" + name + ") !");
						}
					}
				}
				// more than one result with the primary name: problem
				else
				{
					logger.warn("More than ONE URN found (based on primary name) with GetDataTypeURN(" + name + ") !");
				}
			}
			sqlResult.close();
	    }
	    catch (SQLException e)
	    {
	    	logger.error("Error during the processing of the result of a query!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
	    }
        
	    // close the context
	    context.close();

		return result;
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
		logger.debug("Call of the 'GetDataTypeURNs()' request...");
		// SQL query
		String query = "SELECT uri, deprecated FROM mir_uri WHERE (uri_type = 'URN' AND ptr_datatype IN (SELECT datatype_id FROM mir_datatype WHERE name = '" + name + "'))  ORDER BY deprecated";
		// setup the context
		DataBaseContext context = new DataBaseContext();
		context.open(database);
        // request to the database
		ResultSet sqlResult = request(context.getStatement(), query);
		String[] result = null;
        
	    try
	    {
			int nbLines = getRowCount(sqlResult);
			
			if (nbLines >= 1)
			{
				result = new String[nbLines];
				for (int i=1; i<=nbLines; ++i)
				{
					result[i-1] = sqlResult.getString(1);
					sqlResult.next();
				}
			}
			// no result, the name in parameter isn't a primary name: search for synonym(s)
			else
			{
				query = "SELECT uri, deprecated FROM mir_uri WHERE (uri_type = 'URN' AND ptr_datatype IN (SELECT ptr_datatype FROM mir_synonym WHERE name = '" + name + "'))  ORDER BY deprecated";
				sqlResult = request(context.getStatement(), query);
				nbLines = getRowCount(sqlResult);
				result = new String[nbLines];
				for (int i=1; i<=nbLines; ++i)
				{
					result[i-1] = sqlResult.getString(1);
					sqlResult.next();
				}
			}
			sqlResult.close();
	    }
	    catch (SQLException e)
	    {
	    	logger.error("Error during the processing of the result of a query!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
	    }
        
	    // close the context
	    context.close();

		return result;
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
		logger.debug("Call of the 'GetDataTypeURL()' request...");
		// SQL query
		String query = "SELECT uri FROM mir_uri WHERE (uri_type = 'URL' AND deprecated = '0' AND ptr_datatype IN (SELECT datatype_id FROM mir_datatype WHERE name = '" + name + "'))";
		// setup the context
		DataBaseContext context = new DataBaseContext();
		context.open(database);
        // request to the database
		ResultSet sqlResult = request(context.getStatement(), query);
		String result = new String();
	    
	    try
	    {
			int nbLines = getRowCount(sqlResult);
			
			// only one result: primary name found
			if (nbLines == 1)
			{
				//sqlResult.next();
				result = sqlResult.getString(1);
			}
			else
			{
				// no result with the primary name: search for synonym(s)
				if (nbLines < 1)
				{
					query = "SELECT uri FROM mir_uri WHERE (uri_type = 'URL' AND deprecated = '0' AND ptr_datatype IN (SELECT ptr_datatype FROM mir_synonym WHERE name = '" + name + "'))";
					sqlResult = request(context.getStatement(), query);
					nbLines = getRowCount(sqlResult);
					// only one URL found
					if (nbLines == 1)
					{
						//sqlResult.next();
						result = sqlResult.getString(1);
					}
					else
					{
						if (nbLines > 1)
						{
							logger.warn("More than ONE URL found (based on synonyms) with GetDataTypeURL(" + name + ") !");
						}
					}
				}
				// more than one result with the primary name: problem
				else
				{
					logger.warn("More than ONE URL found (based on primary name) with GetDataTypeURL(" + name + ") !");
				}
			}
			sqlResult.close();
	    }
	    catch (SQLException e)
	    {
	    	logger.error("Error during the processing of the result of a query!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
	    }
        
	    // close the context
	    context.close();
    	
		return result;
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
		logger.debug("Call of the 'GetDataTypeURLs()' request...");
		// SQL query
		String query = "SELECT uri FROM mir_uri WHERE (uri_type = 'URL' AND ptr_datatype IN (SELECT datatype_id FROM mir_datatype WHERE name = '" + name + "'))";
		// setup the context
		DataBaseContext context = new DataBaseContext();
		context.open(database);
        // request to the database
		ResultSet sqlResult = request(context.getStatement(), query);
		String[] result = null;
        
	    try
	    {
			int nbLines = getRowCount(sqlResult);
			
			if (nbLines >= 1)
			{
				result = new String[nbLines];
				for (int i=1; i<=nbLines; ++i)
				{
					result[i-1] = sqlResult.getString(1);
					sqlResult.next();
				}
			}
			// no result, the name in parameter isn't a primary name: search for synonym(s)
			else
			{
				query = "SELECT uri FROM mir_uri WHERE (uri_type = 'URL' AND ptr_datatype IN (SELECT ptr_datatype FROM mir_synonym WHERE name = '" + name + "'))";
				sqlResult = request(context.getStatement(), query);
				nbLines = getRowCount(sqlResult);
				result = new String[nbLines];
				for (int i=1; i<=nbLines; ++i)
				{
					result[i-1] = sqlResult.getString(1);
					sqlResult.next();
				}
			}
			sqlResult.close();
	    }
	    catch (SQLException e)
	    {
	    	logger.error("Error during the processing of the result of a query!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
	    }
        
	    // close the context
	    context.close();
    	
		return result;
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
		logger.debug("Call of the 'GetDataTypeURI()' request...");
		String result = new String();
		
		if (type.equalsIgnoreCase("URN"))
		{
			result = getDataTypeURN(name);
		}
		// URL is the default (even in case of an empty "type" field)
		else
		{
			result = getDataTypeURL(name);
		}
		
		return result;
	}
	
	
	 /**
     * Retrieves the unique (official) URI of a data type (example: "urn:miriam:uniprot").
     * @param name name or synonym of a data type (examples: "UniProt") 
     * @return unique URI of the data type
     */
    public String getDataTypeURI(String name)
    {
        // TODO: add the possibility to call this method with the ID of the data type (example "MIR:00000005").
        
        
        logger.debug("Call of the official 'GetDataTypeURI()' request...");
        String result = new String();
        result = getDataTypeURN(name);
        
        return result;
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
		logger.debug("Call of the 'GetDataTypeURIs(String, String)' request...");
		String[] result = null;
		
		if (type.equalsIgnoreCase("URN"))
		{
			result = getDataTypeURNs(name);
		}
		// URL is the default (even in case of an empty "type" field)
		else
		{
			result = getDataTypeURLs(name);
		}
    	
		return result;
	}
    
    
    /**
     * Retrieves all the URIs of a data type, including all the deprecated ones (examples: "urn:miriam:uniprot", "http://www.uniprot.org/", "urn:lsid:uniprot.org:uniprot", ...).
     * @param name name (or synonym) of the data type (examples: "ChEBI", "UniProt")
     * @return all the URIs of a data type (including the deprecated ones)
     */
    public String[] getDataTypeURIs(String name)
    {
        // TODO: allow the usage of this method with an identifier (for example "MIR:00000005") and a URI (for example "urn:miriam:uniprot").
        
        
        logger.debug("Call of the 'GetDataTypeURIs(String)' request...");
        String[] resultURN = null;
        String[] resultURL = null;
        String[] result = null;
        
        resultURN = getDataTypeURNs(name);
        resultURL = getDataTypeURLs(name);
        
        result = arrayConcat(resultURN, resultURL);
        
        return result;
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
		logger.debug("Call of the 'GetDataTypeAllURIs()' request...");
		String[] resultURN = null;
		String[] resultURL = null;
		String[] result = null;
		
		resultURN = getDataTypeURNs(name);
		resultURL = getDataTypeURLs(name);
		
		result = arrayConcat(resultURL, resultURN);
		
		return result;
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
		logger.debug("Call of the 'GetURN()' request...");
		String result = new String();
		
		result = getDataTypeURN(name);
		// result is empty
		if (result.equalsIgnoreCase(""))
		{
			logger.warn("No URN for the data type named: " + name);
		}
		else
		{
			try
            {
                result += ":" + URLEncoder.encode(id, "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
                logger.error("An UnsupportedEncodingException exception occurred while encoding the identifier: '" + id + "'!");
                logger.error(e.getMessage());
            }
		}
    	
		return result;
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
		logger.debug("Call of the 'GetURL()' request...");
		String result = new String();
		
		result = getDataTypeURL(name);
		// result is empty
		if (result.equalsIgnoreCase(""))
		{
			logger.debug("No URL for the data type named: " + name);
		}
		else
		{
			result += "#" + id; 
		}
    	
		return result;
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
		logger.debug("Call of the 'GetURI(String, String, String)' request...");
		String result = new String();
		
		if (type.equalsIgnoreCase("URN"))
		{
			result = getURN(name, id);
		}
		// URL is the default (even in case of an empty "type" field)
		else
		{
			result = getURL(name, id);
		}
		
		return result;
	}
	
	
    /**
     * Retrieves the unique URI of a specific entity (example: "urn:miriam:obo.go:GO%3A0045202").
     * @param name name of a data type (examples: "ChEBI", "UniProt")
     * @param id identifier of an enity within the data type (examples: "GO:0045202", "P62158")
     * @return unique MIRIAM URI of a given entity
     */
	public String getURI(String name, String id)
	{
	    logger.debug("Call of the 'GetURI(String, String)' request...");
        String result = new String();
        
        result = getURN(name, id);
        
        return result;
	}
	
	
	/**
	 * Retrieves the definition of a data type.
	 * @param nickname name or URI (URN or URL) of a data type
	 * @return definition of the data type
	 */
	public String getDataTypeDef(String nickname)
	{
		logger.debug("Call of the 'GetDataTypeDef()' request...");
		// SQL query
		String query = "SELECT definition FROM mir_datatype WHERE name = '" + nickname + "'";
		// setup the context
		DataBaseContext context = new DataBaseContext();
		context.open(database);
        // request to the database
		ResultSet sqlResult = request(context.getStatement(), query);
		String result = new String();
		
	    try
	    {
			int nbLines = getRowCount(sqlResult);
			
			// only one result: primary name found
			if (nbLines == 1)
			{
				// no need for sqlResult.next(), because use of first() in getRowCount()
				result = sqlResult.getString(1);
			}
			else
			{
				// no result with the primary name: search for synonym(s)
				if (nbLines < 1)
				{
					query = "SELECT definition FROM mir_datatype WHERE (datatype_id IN (SELECT ptr_datatype FROM mir_synonym WHERE name = '" + nickname + "'))";
					sqlResult = request(context.getStatement(), query);
					nbLines = getRowCount(sqlResult);
					// only one URN found
					if (nbLines == 1)
					{
						// no need for sqlResult.next(), because use of first() in getRowCount()
						result = sqlResult.getString(1);
					}
					else
					{
						// no result with the synonyms (and previously, with the name): search for URI (URN and URL)
						if (nbLines < 1)
						{
							query = "SELECT definition FROM mir_datatype WHERE (datatype_id IN (SELECT ptr_datatype FROM mir_uri WHERE uri = '" + nickname + "'))";
							sqlResult = request(context.getStatement(), query);
							// we take the first result (can have more than one URI: URN, URL and deprecated)
							sqlResult.next();
							result = sqlResult.getString(1);
						}
						// more than one result: problem
						else
						{
							logger.warn("More than ONE definition found (based on synonyms) with GetDataTypeDef(" + nickname + ") !");
						}
					}
				}
				// more than one result with the primary name: problem
				else
				{
					logger.warn("More than ONE definition found (based on primary name) with GetDataTypeDef(" + nickname + ") !");
				}
			}
			sqlResult.close();
	    }
	    catch (SQLException e)
	    {
	    	logger.error("Error during the processing of the result of a query!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
	    }
        
	    // close the context
	    context.close();
		
		return result;
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
		logger.debug("Call of the 'GetDataEntries(nickname, id)' request...");
		
		// encodes the identifier part (if necessary)
		id = identifierEncode(id);
		
		// setup the context
		DataBaseContext context = new DataBaseContext();
		context.open(database);
		
		int type;
		String query = new String();
		ResultSet sqlResult;
		String[] result = null;
		
		type = getType(nickname);
		
		switch(type)
		{
			case ID_NAME:
			{
				query = "SELECT url_element_prefix, url_element_suffix FROM mir_resource WHERE (ptr_datatype IN (SELECT datatype_id FROM mir_datatype WHERE name='" + nickname + "'))";
				break;
			}
			case ID_SYNONYM:
			{
				query = "SELECT url_element_prefix, url_element_suffix FROM mir_resource WHERE (ptr_datatype IN (SELECT ptr_datatype FROM mir_synonym WHERE (name = '"+ nickname + "')))";
				break;
			}
			case ID_URL:
			{
				query = "SELECT url_element_prefix, url_element_suffix FROM mir_resource WHERE (ptr_datatype IN (SELECT ptr_datatype FROM mir_uri WHERE (uri_type = 'URL' AND uri = '"+ nickname + "')))";
				break;
			}
			case ID_URN:
			{
				query = "SELECT url_element_prefix, url_element_suffix FROM mir_resource WHERE (ptr_datatype IN (SELECT ptr_datatype FROM mir_uri WHERE (uri_type = 'URN' AND uri = '"+ nickname + "')))";
				break;
			}
			case ID_UNKNOWN:
			{
				logger.info("First parameter for the 'getDataEntries()' request is unknown: " + nickname);
				logger.info("--> authorized types for this parameter: name, synonym, URL or URN of a data type.");
                
                context.close();
				return null;
			}
		}
		
		sqlResult = request(context.getStatement(), query);
		int nbRows = getRowCount(sqlResult);
		result = new String[nbRows];
		try
		{
			for (int i=1; i<=nbRows; ++i)
			{
				result[i-1] = sqlResult.getString(1) + id + sqlResult.getString(2); 
				sqlResult.next();
			}
		}
	    catch (SQLException e)
	    {
	    	logger.error("Error during the processing of the result of a query!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
	    }
	    
		// close the context
	    context.close();
		
		return result;
	}
	
	
	/**
     * Retrieves the physical locationS (URLs) of web pageS providing knowledge about an entity.
     * @param nickname name (can be a synonym) or URI of a data type (examples: "Gene Ontology", "UniProt")
     * @param id identifier of an entity within the given data type (examples: "GO:0045202", "P62158")
     * @return physical locationS of web pageS providing knowledge about the given entity
     */
	public String[] getLocations(String nickname, String id)
    {
	    // TODO: allow the usage of this method with the ID of a data type (example: "MIR:00000005").
	    
	    
        logger.debug("Call of the 'GetLocations(nickname, id)' request...");
        
        // encodes the identifier part (if necessary)
        id = identifierEncode(id);
        
        // setup the context
        DataBaseContext context = new DataBaseContext();
        context.open(database);
        
        int type;
        String query = new String();
        ResultSet sqlResult;
        String[] result = null;
        
        type = getType(nickname);
        
        switch(type)
        {
            case ID_NAME:
            {
                query = "SELECT url_element_prefix, url_element_suffix FROM mir_resource WHERE (ptr_datatype IN (SELECT datatype_id FROM mir_datatype WHERE name='" + nickname + "'))";
                break;
            }
            case ID_SYNONYM:
            {
                query = "SELECT url_element_prefix, url_element_suffix FROM mir_resource WHERE (ptr_datatype IN (SELECT ptr_datatype FROM mir_synonym WHERE (name = '"+ nickname + "')))";
                break;
            }
            case ID_URL:
            {
                query = "SELECT url_element_prefix, url_element_suffix FROM mir_resource WHERE (ptr_datatype IN (SELECT ptr_datatype FROM mir_uri WHERE (uri_type = 'URL' AND uri = '"+ nickname + "')))";
                break;
            }
            case ID_URN:
            {
                query = "SELECT url_element_prefix, url_element_suffix FROM mir_resource WHERE (ptr_datatype IN (SELECT ptr_datatype FROM mir_uri WHERE (uri_type = 'URN' AND uri = '"+ nickname + "')))";
                break;
            }
            case ID_UNKNOWN:
            {
                logger.info("First parameter for the 'getDataEntries()' request is unknown: " + nickname);
                logger.info("--> authorized types for this parameter: name, synonym, URL or URN of a data type.");
                
                context.close();
                return null;
            }
        }
        
        sqlResult = request(context.getStatement(), query);
        int nbRows = getRowCount(sqlResult);
        result = new String[nbRows];
        try
        {
            for (int i=1; i<=nbRows; ++i)
            {
                result[i-1] = sqlResult.getString(1) + id + sqlResult.getString(2); 
                sqlResult.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error during the processing of the result of a query!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        
        // close the context
        context.close();
        
        return result;
    }
    
    
    /**
     * Retrieves the physical locationS (URLs) of web pageS about the data-entry.
     * @param uri MIRIAM URI of an element (example: 'urn:miriam:obo.go:GO%3A0045202')
     * @return physical locationS of web pageS about the data-entry
     * @deprecated Use {@link #getLocations(String)} instead.
     */
	@Deprecated
    public String[] getDataEntries(String uri)
    {
        logger.debug("Call of the 'GetDataEntries(uri)' request...");
        
        // setup the context
        DataBaseContext context = new DataBaseContext();
        context.open(database);
        
        int type;
        String query = new String();
        ResultSet sqlResult;
        String[] result = null;
        
        // encodes the identifier part (if necessary)
        uri = urlMagicEncode(uri);
        
        // separation of the data type URI and the element ID from the MIRIAM URI
        String datatype = getDataPart(uri);
        String id = getElementPart(uri);
        
        // the id was retrieved successfully from the URI
        if (id != null)
        {
            type = getType(datatype);
            switch (type)
            {
                case ID_URL:
                {
                    query = "SELECT url_element_prefix, url_element_suffix FROM mir_resource WHERE (ptr_datatype IN (SELECT ptr_datatype FROM mir_uri WHERE (uri_type = 'URL' AND uri = '"+ datatype + "')))";
                    break;
                }
                case ID_URN:
                {
                    query = "SELECT url_element_prefix, url_element_suffix FROM mir_resource WHERE (ptr_datatype IN (SELECT ptr_datatype FROM mir_uri WHERE (uri_type = 'URN' AND uri = '"+ datatype + "')))";
                    break;
                }
                case ID_UNKNOWN:
                {
                    logger.info("First parameter for the 'getDataEntries()' request is unknown: " + datatype);
                    logger.info("--> authorized types for this parameter: name, synonym, URL or URN of a data type.");
                    
                    context.close();
                    return null;
                }
            }
            
            sqlResult = request(context.getStatement(), query);
            int nbRows = getRowCount(sqlResult);
            result = new String[nbRows];
            try
            {
                for (int i=1; i<=nbRows; ++i)
                {
                    result[i-1] = sqlResult.getString(1) + id + sqlResult.getString(2); 
                    sqlResult.next();
                }
            }
            catch (SQLException e)
            {
                logger.error("Error during the processing of the result of a query!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
        }
        else   // URI not standard
        {
            // nothing: returns null 
        }
        
        // close the context
        context.close();
        
        return result;
    }
	
	
    /**
     * Retrieves the physical locationS (URLs) of web pageS providing knowledge about a specific entity.
     * @param uri MIRIAM URI of an entity (example: 'urn:miriam:obo.go:GO%3A0045202')
     * @return physical locationS of web pageS providing knowledge about the given entity
     */
    public String[] getLocations(String uri)
    {
        logger.debug("Call of the 'GetLocations(uri)' request...");
        
        // setup the context
        DataBaseContext context = new DataBaseContext();
        context.open(database);
        
        int type;
        String query = new String();
        ResultSet sqlResult;
        String[] result = null;
        
        // encodes the identifier part (if necessary)
        uri = urlMagicEncode(uri);
        
        // separation of the data type URI and the element ID from the MIRIAM URI
        String datatype = getDataPart(uri);
        String id = getElementPart(uri);
        
        // the id was retrieved successfully from the URI
        if (id != null)
        {
            type = getType(datatype);
            switch (type)
            {
                case ID_URL:
                {
                    query = "SELECT url_element_prefix, url_element_suffix FROM mir_resource WHERE (ptr_datatype IN (SELECT ptr_datatype FROM mir_uri WHERE (uri_type = 'URL' AND uri = '"+ datatype + "')))";
                    break;
                }
                case ID_URN:
                {
                    query = "SELECT url_element_prefix, url_element_suffix FROM mir_resource WHERE (ptr_datatype IN (SELECT ptr_datatype FROM mir_uri WHERE (uri_type = 'URN' AND uri = '"+ datatype + "')))";
                    break;
                }
                case ID_UNKNOWN:
                {
                    logger.info("First parameter for the 'getDataEntries()' request is unknown: " + datatype);
                    logger.info("--> authorized types for this parameter: name, synonym, URL or URN of a data type.");
                    
                    context.close();
                    return null;
                }
            }
            
            sqlResult = request(context.getStatement(), query);
            int nbRows = getRowCount(sqlResult);
            result = new String[nbRows];
            try
            {
                for (int i=1; i<=nbRows; ++i)
                {
                    result[i-1] = sqlResult.getString(1) + id + sqlResult.getString(2); 
                    sqlResult.next();
                }
            }
            catch (SQLException e)
            {
                logger.error("Error during the processing of the result of a query!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
        }
        else   // URI not standard
        {
            // nothing: returns null 
        }
        
        // close the context
        context.close();
        
        return result;
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
        logger.debug("Call of the 'GetDataEntry()' request...");
        
        // encodes the identifier part (if necessary)
        uri = urlMagicEncode(uri);
        
        // setup the context
        DataBaseContext context = new DataBaseContext();
        context.open(database);
        
        String query = new String();
        ResultSet sqlResult;
        String result = null;
        String prefix = null;
        String suffix = null;
        
        query = "SELECT url_element_prefix, url_element_suffix FROM mir_resource WHERE (resource_id = '" + resource + "')";
        
        sqlResult = request(context.getStatement(), query);
        int nbLines = getRowCount(sqlResult);
        
        // only one result: ok
        if (nbLines == 1)
        {
            try
            {
                // no need for sqlResult.next(), because use of first() in getRowCount()
                prefix = sqlResult.getString("url_element_prefix");
                suffix = sqlResult.getString("url_element_suffix");
                
                // separation of the data type URI and the element ID from the MIRIAM URI
                String id = getElementPart(uri);
                
                if (id != null)
                {
                    // concatenation: prefix + id + suffix
                    result = prefix + id + suffix;
                }
                else   // URI not standard
                {
                    // nothing: returns null
                }
            }
            catch (SQLException e)
            {
                logger.error("Error during the processing of the result of a query!");
                logger.error("SQL Exception raised: " + e.getMessage());
                //return null;
            }
        }
        else
        {
            logger.warn("There are more than only one resource with the URI '" + resource + "' in the database!");
        }
        
        // close the context
        context.close();
        
        return result;
    }
    
    
    /**
     * Retrieves the physical location (URL) of a web page providing knowledge about a specific entity, using a specific resource.
     * @param uri MIRIAM URI of an entity (example: 'urn:miriam:obo.go:GO%3A0045202')
     * @param resource internal identifier of a resource (example: 'MIR:00100005')
     * @return physical location of a web page providing knowledge about the given entity, using a specific resource
     */
    public String getLocation(String uri, String resource)
    {
        logger.debug("Call of the 'GetLocation()' request...");
        
        // encodes the identifier part (if necessary)
        uri = urlMagicEncode(uri);
        
        // setup the context
        DataBaseContext context = new DataBaseContext();
        context.open(database);
        
        String query = new String();
        ResultSet sqlResult;
        String result = null;
        String prefix = null;
        String suffix = null;
        
        query = "SELECT url_element_prefix, url_element_suffix FROM mir_resource WHERE (resource_id = '" + resource + "')";
        
        sqlResult = request(context.getStatement(), query);
        int nbLines = getRowCount(sqlResult);
        
        // only one result: ok
        if (nbLines == 1)
        {
            try
            {
                // no need for sqlResult.next(), because use of first() in getRowCount()
                prefix = sqlResult.getString("url_element_prefix");
                suffix = sqlResult.getString("url_element_suffix");
                
                // separation of the data type URI and the element ID from the MIRIAM URI
                String id = getElementPart(uri);
                
                if (id != null)
                {
                    // concatenation: prefix + id + suffix
                    result = prefix + id + suffix;
                }
                else   // URI not standard
                {
                    // nothing: returns null
                }
            }
            catch (SQLException e)
            {
                logger.error("Error during the processing of the result of a query!");
                logger.error("SQL Exception raised: " + e.getMessage());
                //return null;
            }
        }
        else
        {
            logger.warn("There are more than only one resource with the URI '" + resource + "' in the database!");
        }
        
        // close the context
        context.close();
        
        return result;
    }
    
	
	/**
	 * Retrieves all the physical locations (URLs) of the services providing the data type (web page).
	 * @param nickname name (can be a synonym) or URL or URN of a data type name (or synonym) or URI (URL or URN)
	 * @return array of strings containing all the address of the main page of the resources of the data type
	 */
	public String[] getDataResources(String nickname)
	{
		logger.debug("Call of the 'getDataResources()' request...");
		
		// setup the context
		DataBaseContext context = new DataBaseContext();
		context.open(database);
		
		int type;
		String query = new String();
		ResultSet sqlResult;
		String[] result = null;
		
		type = getType(nickname);
		
		switch(type)
		{
			case ID_NAME:
			{
				query = "SELECT url_resource FROM mir_resource WHERE (ptr_datatype IN (SELECT datatype_id FROM mir_datatype WHERE name= '" + nickname + "'))";
				break;
			}
			case ID_SYNONYM:
			{
				query = "SELECT url_resource FROM mir_resource WHERE (ptr_datatype IN (SELECT ptr_datatype FROM mir_synonym WHERE (name = '"+ nickname + "')))";
				break;
			}
			case ID_URL:
			{
				query = "SELECT url_resource FROM mir_resource WHERE (ptr_datatype IN (SELECT ptr_datatype FROM mir_uri WHERE (uri_type = 'URL' AND uri = '"+ nickname + "')))";
				break;
			}
			case ID_URN:
			{
				query = "SELECT url_resource FROM mir_resource WHERE (ptr_datatype IN (SELECT ptr_datatype FROM mir_uri WHERE (uri_type = 'URN' AND uri = '"+ nickname + "')))";
				break;
			}
			case ID_UNKNOWN:
			{
				logger.info("First parameter for the 'getDataEntries()' request is unknown: " + nickname);
				logger.info("--> authorized types for this parameter: name, synonym, URL or URN of a data type.");
                context.close();
				return null;
			}
		}
		
		sqlResult = request(context.getStatement(), query);
		int nbRows = getRowCount(sqlResult);
		result = new String[nbRows];
		try
		{
			for (int i=1; i<=nbRows; ++i)
			{
				result[i-1] = sqlResult.getString(1);
				sqlResult.next();
			}
		}
	    catch (SQLException e)
	    {
	    	logger.error("Error during the processing of the result of a query!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
	    }
	    
		// close the context
	    context.close();
		
		return result;
	}
	
	
	/**
	 * Says if a URI of a data type is deprecated.
	 * @param uri (URN or URL) of a data type
	 * 
	 * TODO: extend this method for all MIRIAM URIS (not only data types) 
	 * 
	 * @return answer ("true" or "false") to the question: is this URI deprecated?
	 */
	public String isDeprecated(String uri)
	{
		logger.debug("Call of the 'isDeprecated()' request...");
		// SQL query
		String query = "SELECT deprecated FROM mir_uri WHERE uri = '" + uri + "'";
		// setup the context
		DataBaseContext context = new DataBaseContext();
		context.open(database);
        // request to the database
		ResultSet sqlResult = request(context.getStatement(), query);
		String result = new String();
		
	    try
	    {
			int nbLines = getRowCount(sqlResult);
			
			// only one result: primary name found
			if (nbLines == 1)
			{
				// no need for sqlResult.next(), because use of first() in getRowCount()
				result = sqlResult.getString(1);
			}
			else
			{
				if (nbLines < 1)
				{
					// the parameter transmitted is not a URI present in the database
					// nothing to do here, 'result' is empty
				}
				else
				{
					logger.warn("The URI (" + uri + ") is not unique in the database!");
				}
			}
	    }
	    catch (SQLException e)
	    {
	    	logger.error("Error during the processing of the result of a query!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
	    }
	    
	    if (result.equalsIgnoreCase("0"))
		{
	    	result = "false";
		}
	    if (result.equalsIgnoreCase("1"))
		{
	    	result = "true";
		}
		
		// close the context
	    context.close();
		
		return result;
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
		logger.debug("Call of the 'getOfficialURI()' request...");
		
		String result = new String();
		String query = new String();
		String id = new String();
		ResultSet sqlResult;
		
		// setup the context
		DataBaseContext context = new DataBaseContext();
		context.open(database);
		
		// request to the database
		query = "SELECT ptr_datatype FROM mir_uri WHERE uri = '" + uri + "'";
		sqlResult = request(context.getStatement(), query);
		
	    try
	    {
			int nbLines = getRowCount(sqlResult);
			
			// only one result: ok
			if (nbLines == 1)
			{
				// no need for sqlResult.next(), because use of first() in getRowCount()
				id = sqlResult.getString(1);
			}
			else
			{
				if (nbLines < 1)
				{
					// the parameter transmitted is not a URI present in the database
					// nothing to do here, 'result' is empty
				}
				else
				{
					logger.warn("The URI (" + uri + ") is not unique in the database!");
				}
			}
	    }
	    catch (SQLException e)
	    {
	    	logger.error("Error during the processing of the result of a query!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
	    }
	    query = "SELECT uri FROM mir_uri WHERE (deprecated = '0' AND uri_type = 'URN' AND ptr_datatype = '" + id + "')";
	    /*
		int type = getType(uri);
		
		switch (type)
		{
			case ID_URL:
			{
				query = "SELECT uri FROM mir_uri WHERE (deprecated = '0' AND uri_type = 'URL' AND ptr_datatype = '" + id + "')";
				break;
			}
			case ID_URN:
			{
				query = "SELECT uri FROM mir_uri WHERE (deprecated = '0' AND uri_type = 'URN' AND ptr_datatype = '" + id + "')";
				break;
			}
			default:
			{
				return result;
			}
		}
		*/
		
		sqlResult = request(context.getStatement(), query);
		
	    try
	    {
			int nbLines = getRowCount(sqlResult);
			
			// only one result: ok
			if (nbLines == 1)
			{
				// no need for sqlResult.next(), because use of first() in getRowCount()
				result = sqlResult.getString(1);
			}
			else
			{
				if (nbLines < 1)
				{
					// the parameter transmitted is not a URI present in the database
					// nothing to do here, 'result' is empty
				}
				else
				{
				    logger.warn("The data type (" + id + ") has more than one official URI of the type URN!");
				    /*
					switch (type)
					{
						case ID_URL:
						{
							logger.warn("The data type (" + id + ") has more than URI of the type URL!");
							break;
						}
						default:
						{
							logger.warn("The data type (" + id + ") has more than URI of the type URN!");
							break;
						}
					}
					*/
				}
			}
	    }
	    catch (SQLException e)
	    {
	    	logger.error("Error during the processing of the result of a query!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
	    }
		
		// close the context
	    context.close();
		
		return result;
	}
	
	
	/**
     * Retrieves the official URI (it will always be URN) of a data type corresponding to the deprecated one.
     * @param uri deprecated URI (URN or URL) of a data type 
     * @return the official URI of a data type corresponding to the deprecated one
     */
    public String getOfficialDataTypeURI(String uri)
    {
        logger.debug("Call of the 'getOfficialDataTypeURI()' request...");
        
        String result = new String();
        String query = new String();
        String id = new String();
        ResultSet sqlResult;
        
        // setup the context
        DataBaseContext context = new DataBaseContext();
        context.open(database);
        
        // request to the database
        query = "SELECT ptr_datatype FROM mir_uri WHERE uri = '" + uri + "'";
        sqlResult = request(context.getStatement(), query);
        
        try
        {
            int nbLines = getRowCount(sqlResult);
            
            // only one result: ok
            if (nbLines == 1)
            {
                // no need for sqlResult.next(), because use of first() in getRowCount()
                id = sqlResult.getString(1);
            }
            else
            {
                if (nbLines < 1)
                {
                    // the parameter transmitted is not a URI present in the database
                    // nothing to do here, 'result' is empty
                }
                else
                {
                    logger.warn("The URI (" + uri + ") is not unique in the database!");
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Error during the processing of the result of a query!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        query = "SELECT uri FROM mir_uri WHERE (deprecated = '0' AND uri_type = 'URN' AND ptr_datatype = '" + id + "')";
        
        sqlResult = request(context.getStatement(), query);
        
        try
        {
            int nbLines = getRowCount(sqlResult);
            
            // only one result: ok
            if (nbLines == 1)
            {
                // no need for sqlResult.next(), because use of first() in getRowCount()
                result = sqlResult.getString(1);
            }
            else
            {
                if (nbLines < 1)
                {
                    // the parameter transmitted is not a URI present in the database
                    // nothing to do here, 'result' is empty
                }
                else
                {
                    logger.warn("The data type (" + id + ") has more than one official URI of the type URN!");
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Error during the processing of the result of a query!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        
        // close the context
        context.close();
        
        return result;
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
        logger.debug("Call of the 'getMiriamURI()' request...");
        
        String newData = new String();
        String query = new String();
        String dataId = new String();
        ResultSet sqlResult;
        
        // setup the context
        DataBaseContext context = new DataBaseContext();
        context.open(database);
        
        // encodes the identifier part (if necessary)
        uri = urlMagicEncode(uri);
        
        // separation of the data type URI and the element ID from the MIRIAM URI
        String datatype = getDataPart(uri);
        String id = identifierEncode(getElementPart(uri));
        
        // request to the database
        query = "SELECT ptr_datatype FROM mir_uri WHERE uri = '" + datatype + "'";
        sqlResult = request(context.getStatement(), query);
        
        try
        {
            int nbLines = getRowCount(sqlResult);
            
            // only one result: ok
            if (nbLines == 1)
            {
                // no need for sqlResult.next(), because use of first() in getRowCount()
                dataId = sqlResult.getString(1);
            }
            else
            {
                if (nbLines < 1)
                {
                    // the parameter transmitted is not a URI present in the database
                    // nothing to do here, 'result' is empty
                }
                else
                {
                    logger.warn("The URI (" + uri + ") is not unique in the database!");
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Error during the processing of the result of a query!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        query = "SELECT uri FROM mir_uri WHERE (deprecated = '0' AND uri_type = 'URN' AND ptr_datatype = '" + dataId + "')";
        sqlResult = request(context.getStatement(), query);
        
        try
        {
            int nbLines = getRowCount(sqlResult);
            
            // only one result: ok
            if (nbLines == 1)
            {
                // no need for sqlResult.next(), because use of first() in getRowCount()
                newData = sqlResult.getString(1);
            }
            else
            {
                if (nbLines < 1)
                {
                    // the parameter transmitted is not a URI present in the database
                    // nothing to do here, 'result' is empty
                }
                else
                {
                    logger.warn("The data type (" + dataId + ") has more than one official URI of the type URN!");
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Error during the processing of the result of a query!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        
        // close the context
        context.close();
        
        return newData + ":" + id;
    }
	
	
	/**
	 * Retrieves the pattern (regular expression) used by the identifiers within a data type.
	 * @param nickname data type name (or synonym) or URI (URL or URN)
	 * @return pattern of the data type
	 */
	public String getDataTypePattern(String nickname)
	{
		logger.debug("Call of the 'getDataTypePattern()' request...");
		
		String result = new String();
		String query = new String();
		
		int type = getType(nickname);
		
		switch (type)
		{
			case ID_NAME:
			{
				query = "SELECT pattern FROM mir_datatype WHERE (name = '" + nickname + "')";
				break;
			}
			case ID_SYNONYM:
			{
				query = "SELECT pattern FROM mir_datatype WHERE (datatype_id IN (SELECT ptr_datatype FROM mir_synonym WHERE name = '" + nickname + "'))";
				break;
			}
			case ID_URL: // do the same things as ID_URN
			case ID_URN:
			{
				query = "SELECT pattern FROM mir_datatype WHERE (datatype_id IN (SELECT ptr_datatype FROM mir_uri WHERE uri = '" + nickname + "'))";
				break;
			}
			default:
			{
				// the parameter is not a name or a URI of a data type of the database
				return null;
			}
		}
		
		// setup the context
		DataBaseContext context = new DataBaseContext();
		context.open(database);
		
		// request to the database
		ResultSet sqlResult = request(context.getStatement(), query);
		
	    try
	    {
			int nbLines = getRowCount(sqlResult);
			
			// only one result: ok
			if (nbLines == 1)
			{
				// no need for sqlResult.next(), because use of first() in getRowCount()
				result = sqlResult.getString(1);
			}
			else
			{
				if (nbLines < 1)
				{
					// the parameter transmitted is not a URI present in the database
					// nothing to do here, the empty result is already sent (this case should never happen)
				}
				else
				{
					switch (type)
					{
						case ID_NAME:
						{
							logger.warn("The data type named '" + nickname + "' is not unique in the database!");
							break;
						}
						case ID_SYNONYM:
						{
							logger.warn("The synonym '" + nickname + "' of a data type, is not unique in the database!");
							break;
						}
						case ID_URN:
						{
							logger.warn("The URN '" + nickname + "' is not unique in the database!");
							break;
						}
						case ID_URL:
						{
							logger.warn("The URL '" + nickname + "' is not unique in the database!");
							break;
						}
					}
				}
			}
	    }
	    catch (SQLException e)
	    {
	    	logger.error("Error during the processing of the result of a query!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
	    }
		
		// close the context
	    context.close();
		
		return result;
	}
	
	
	/**
	 * Retrieves the general information about a precise resource of a data type.
	 * @param id identifier of a resource (example: MIR:MIR:00100005)
	 * @return general information about a resource
	 */
	public String getResourceInfo(String id)
	{
		logger.debug("Call of the 'getResourceInfo()' request...");
		
		String result = new String();
		String query = new String();
		
		query = "SELECT info FROM mir_resource WHERE (resource_id = '" + id + "')";
		
		// setup the context
		DataBaseContext context = new DataBaseContext();
		context.open(database);
		
		// request to the database
		ResultSet sqlResult = request(context.getStatement(), query);
		
	    try
	    {
			int nbLines = getRowCount(sqlResult);
			
			// only one result: ok
			if (nbLines == 1)
			{
				// no need for sqlResult.next(), because use of first() in getRowCount()
				result = sqlResult.getString(1);
			}
			else
			{
				if (nbLines < 1)
				{
					// the parameter transmitted is not a URI present in the database
					// nothing to do here, the empty result is already sent (this case should never happen)
				}
				else
				{
					logger.warn("The resource, which id is '" + id + "' is not unique in the database!");
				}
			}
	    }
	    catch (SQLException e)
	    {
	    	logger.error("Error during the processing of the result of a query!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
	    }
		
		// close the context
	    context.close();
		
		return result;
	}
	
	
	/**
	 * Retrieves the institution which manages a precise resource of a data type.
	 * @param id identifier of a resource (example: MIR:MIR:00100005)
	 * @return institution which manages a resource
	 */
	public String getResourceInstitution(String id)
	{
		logger.debug("Call of the 'getResourceInstitution()' request...");
		
		String result = new String();
		String query = new String();
		
		query = "SELECT institution FROM mir_resource WHERE (resource_id = '" + id + "')";
		
		// setup the context
		DataBaseContext context = new DataBaseContext();
		context.open(database);
		
		// request to the database
		ResultSet sqlResult = request(context.getStatement(), query);
		
	    try
	    {
			int nbLines = getRowCount(sqlResult);
			
			// only one result: ok
			if (nbLines == 1)
			{
				// no need for sqlResult.next(), because use of first() in getRowCount()
				result = sqlResult.getString(1);
			}
			else
			{
				if (nbLines < 1)
				{
					// the parameter transmitted is not a URI present in the database
					// nothing to do here, the empty result is already sent (this case should never happen)
				}
				else
				{
					logger.warn("The resource, which id is '" + id + "' is not unique in the database!");
				}
			}
	    }
	    catch (SQLException e)
	    {
	    	logger.error("Error during the processing of the result of a query!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
	    }
		
		// close the context
	    context.close();
		
		return result;
	}
	
	/**
	 * Retrieves the location of the servers of a location.
	 * @param id identifier of a resource (example: MIR:MIR:00100005)
	 * @return location of the servers of a resource
	 */
	public String getResourceLocation(String id)
	{
		logger.debug("Call of the 'getResourceLocation()' request...");
		
		String result = new String();
		String query = new String();
		
		query = "SELECT location FROM mir_resource WHERE (resource_id = '" + id + "')";
		
		// setup the context
		DataBaseContext context = new DataBaseContext();
		context.open(database);
		
		// request to the database
		ResultSet sqlResult = request(context.getStatement(), query);
		
	    try
	    {
			int nbLines = getRowCount(sqlResult);
			
			// only one result: ok
			if (nbLines == 1)
			{
				// no need for sqlResult.next(), because use of first() in getRowCount()
				result = sqlResult.getString(1);
			}
			else
			{
				if (nbLines < 1)
				{
					// the parameter transmitted is not a URI present in the database
					// nothing to do here, the empty result is already sent (this case should never happen)
				}
				else
				{
					logger.warn("The resource, which id is '" + id + "' is not unique in the database!");
				}
			}
	    }
	    catch (SQLException e)
	    {
	    	logger.error("Error during the processing of the result of a query!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
	    }
		
		// close the context
	    context.close();
		
		return result;
	}
	
	
	/**
	 * Retrieves all the synonym names of a data type (this list includes the original name).
	 * @param name name or synonym of a data type
	 * @return all the synonym names of the data type
	 */
	public String[] getDataTypeSynonyms(String name)
	{
		logger.debug("Call of the 'GetDataTypeSynonyms()' request...");
		
		String query = new String();
		ResultSet sqlResult = null;
		String[] result = null;
		int nbLines;
		
		int type = getType(name);
		
		// setup the context
		DataBaseContext context = new DataBaseContext();
		context.open(database);
		
		switch (type)
		{
			case ID_NAME:
			{
				query = "SELECT name FROM mir_synonym WHERE (ptr_datatype IN (SELECT datatype_id FROM mir_datatype WHERE name = '" + name + "'))";
				sqlResult = request(context.getStatement(), query);
				nbLines = getRowCount(sqlResult);
			    try
			    {
					if (nbLines >= 1)
					{
						result = new String[nbLines+1];
						result[0] = name;   // we add the primary name
						for (int i=1; i<=nbLines; ++i)
						{
							result[i] = sqlResult.getString(1);
							sqlResult.next();
						}
					}
					else
					{
						result = new String[1];
						result[0] = name;
					}
			    }
			    catch (SQLException e)
			    {
			    	logger.error("Error during the processing of the result of a query!");
			    	logger.error("SQL Exception raised: " + e.getMessage());
                    context.close();
			    	return null;
			    }
				break;
			}
			case ID_SYNONYM:
			{
				// recovery of the (internal) id of the data type
				String id = new String();
				query = "SELECT ptr_datatype FROM mir_synonym WHERE name = '" + name + "'";
				sqlResult = request(context.getStatement(), query);
				nbLines = getRowCount(sqlResult);
				if (nbLines == 1)
				{
					try
					{
						// no need for sqlResult.next(), because use of first() in getRowCount()
						id = sqlResult.getString(1);
					}
					catch (SQLException e)
					{
						logger.error("Error during the processing of the result of a query!");
				    	logger.error("SQL Exception raised: " + e.getMessage());
                        context.close();
				    	return null;
					}
				}
				else
				{
					logger.warn("There are more than only one synonym named '" + name + "' in the database!");
				}
				// recovery of the primary name of the data type
				String primary = new String();
				query = "SELECT name FROM mir_datatype WHERE datatype_id = '" + id + "'";
				sqlResult = request(context.getStatement(), query);
				nbLines = getRowCount(sqlResult);
				if (nbLines == 1)
				{
					try
					{
						// no need for sqlResult.next(), because use of first() in getRowCount()
						primary = sqlResult.getString(1);
					}
					catch (SQLException e)
					{
						logger.error("Error during the processing of the result of a query!");
				    	logger.error("SQL Exception raised: " + e.getMessage());
                        context.close();
				    	return null;
					}
				}
				else
				{
					if (nbLines < 1)
					{
						logger.warn("The synonym named '" + name + "' is orphan: is not related to an existing data type in the database!");
                        context.close();
						return null;
					}
					else
					{
						logger.warn("There are more than only one data type with the (internal) ID '" + id + "' in the database!");
					}
				}
				// recovery of all the synonyms
				query = "SELECT name FROM mir_synonym WHERE ptr_datatype = '" + id + "'";
				sqlResult = request(context.getStatement(), query);
				nbLines = getRowCount(sqlResult);
			    try
			    {
					if (nbLines >= 1)
					{
						result = new String[nbLines+1];
						result[0] = primary;   // we add the primary name
						for (int i=1; i<=nbLines; ++i)
						{
							result[i] = sqlResult.getString(1);
							sqlResult.next();
						}
					}
					else
					{
						result = new String[1];
						result[0] = primary;
					}
			    }
			    catch (SQLException e)
			    {
			    	logger.error("Error during the processing of the result of a query!");
			    	logger.error("SQL Exception raised: " + e.getMessage());
                    context.close();
			    	return null;
			    }
				break;
			}
			default:
			{
				// the parameter is not an primary name nor a synonym of a name
                context.close();
				return null;
			}
		}
        
	    // close the context
	    context.close();

		return result;
	}
	
	
	/**
	 * Retrieves the common name of a data type.
	 * @param uri URI (URL or URN) of a data type
	 * @return the common name of the data type
	 */
	public String getName(String uri)
	{
		logger.debug("Call of the 'getName()' request...");
		
		String result = new String();
		String query = new String();
		
		int type = getType(uri);
		
		switch (type)
		{
			case ID_URL: // do the same things as ID_URN
			case ID_URN:
			{
				query = "SELECT name FROM mir_datatype WHERE (datatype_id IN (SELECT ptr_datatype FROM mir_uri WHERE uri = '" + uri + "'))";
				break;
			}
			default:
			{
				// the parameter is not a URI of a data type of the database
				return null;
			}
		}
		
		// setup the context
		DataBaseContext context = new DataBaseContext();
		context.open(database);
		
		// request to the database
		ResultSet sqlResult = request(context.getStatement(), query);
		
		int nbLines = getRowCount(sqlResult);
		
		// only one result: ok
		if (nbLines == 1)
		{
			try
			{
				// no need for sqlResult.next(), because use of first() in getRowCount()
				result = sqlResult.getString(1);
			}
			catch (SQLException e)
			{
				logger.error("Error during the processing of the result of a query!");
		    	logger.error("SQL Exception raised: " + e.getMessage());
                
                context.close();
		    	return null;
			}
		}
		else
		{
			logger.warn("There are more than only one data type with the URI '" + uri + "' in the database!");
		}
		
		// close the context
	    context.close();
		
		return result;
	}
	
	
	/**
	 * Retrieves all the names (with synonyms) of a data type.
	 * @param uri URI (URL or URN) of a data type
	 * @return the common name of the data type and all the synonyms
	 */
	public String[] getNames(String uri)
	{
		logger.debug("Call of the 'getNames()' request...");
		
		String result[] = null;
		String name = new String();
		String query = new String();
		
		int type = getType(uri);
		
		switch (type)
		{
			case ID_URL: // do the same things as ID_URN
			case ID_URN:
			{
				query = "SELECT name FROM mir_datatype WHERE (datatype_id IN (SELECT ptr_datatype FROM mir_uri WHERE uri = '" + uri + "'))";
				break;
			}
			default:
			{
				// the parameter is not a URI of a data type of the database
				return null;
			}
		}
		
		// setup the context
		DataBaseContext context = new DataBaseContext();
		context.open(database);
		
		// request to the database
		ResultSet sqlResult = request(context.getStatement(), query);
		
		int nbLines = getRowCount(sqlResult);
		
		// only one result: ok
		if (nbLines == 1)
		{
			try
			{
				// no need for sqlResult.next(), because use of first() in getRowCount()
				name = sqlResult.getString(1);
			}
			catch (SQLException e)
			{
				logger.error("Error during the processing of the result of a query!");
		    	logger.error("SQL Exception raised: " + e.getMessage());
                
                context.close();
		    	return null;
			}
		}
		else
		{
			logger.warn("There are more than only one data type with the URI '" + uri + "' in the database!");
		}
		
		// search for the synonyms
		query = "SELECT name FROM mir_synonym WHERE (ptr_datatype IN (SELECT ptr_datatype FROM mir_uri WHERE uri = '" + uri + "'))";
		sqlResult = request(context.getStatement(), query);
		nbLines = getRowCount(sqlResult);
		
		// one or more synonyms found
		if (nbLines >= 1)
		{
			result = new String[nbLines + 1];
			result[0] = name;   // add the official name to the list
			
			try
			{			
				for (int i=1; i<=nbLines; ++i)
				{
					result[i] = sqlResult.getString(1);
					sqlResult.next();
				}
			}
			catch (SQLException e)
			{
				logger.error("Error during the processing of the result of a query!");
		    	logger.error("SQL Exception raised: " + e.getMessage());
                
                context.close();
		    	return null;
			}
		}
		else
		{
			result = new String[1];
			result[0] = name;   // add the official name to the list
		}
		
        // close the context
        context.close();
        
		return result;
	}
	
	
    /**
     * Retrieves the list of names of all the data types available.
     * @return list of the name of all the data types
     */
    public String[] getDataTypesName()
    {
        String[] result = null;
        
        // setup the context
        DataBaseContext context = new DataBaseContext();
        context.open(database);
        
        // request to the database
        String query = "SELECT name FROM mir_datatype";
        ResultSet sqlResult = request(context.getStatement(), query);
        int nbLines = getRowCount(sqlResult);
        
        if (nbLines >= 1)
        {
            result = new String[nbLines];
            
            try
            {           
                for (int i=0; i<nbLines; ++i)
                {
                    result[i] = sqlResult.getString("name");
                    sqlResult.next();
                }
            }
            catch (SQLException e)
            {
                logger.error("Error during the processing of the result of a query!");
                logger.error("SQL Exception raised: " + e.getMessage());
                
                context.close();
                return null;
            }
        }
        else
        {
            logger.warn("No data type stored in the database!");
        }
        
        // close the context
        context.close();
        
        return result;
    }
    
    
    /**
     * Retrieves the internal identifier (stable and perennial) of all the data types (for example: "MIR:00000005").
     * @return list of the identifier of all the data types
     */
    public String[] getDataTypesId()
    {
        String[] result = null;
        
        // setup the context
        DataBaseContext context = new DataBaseContext();
        context.open(database);
        
        // request to the database
        String query = "SELECT datatype_id FROM mir_datatype";
        ResultSet sqlResult = request(context.getStatement(), query);
        int nbLines = getRowCount(sqlResult);
        
        if (nbLines >= 1)
        {
            result = new String[nbLines];
            
            try
            {           
                for (int i=0; i<nbLines; ++i)
                {
                    result[i] = sqlResult.getString("datatype_id");
                    sqlResult.next();
                }
            }
            catch (SQLException e)
            {
                logger.error("Error during the processing of the result of a query!");
                logger.error("SQL Exception raised: " + e.getMessage());
                
                context.close();
                return null;
            }
        }
        else
        {
            logger.warn("No data type stored in the database!");
        }
        
        // close the context
        context.close();
        
        return result;
    }
    
    
    /**
     * Checks if the identifier given follows the regular expression of the data type (also provided).
     * @param identifier internal identifier used by the data type
     * @param datatype name, synonym or MIRIAM URI of a data type
     * @return "true" if the identifier follows the regular expression, "false" otherwise
     */
    public String checkRegExp(String identifier, String datatype)
    {
        String result = "false";
        
        // looks for the pattern of the data type
        String pattern = getDataTypePattern(datatype);
        if ((pattern == null) || (pattern.equalsIgnoreCase("")))
        {
            logger.warn("There is no regular expression attached to the data type: " + datatype);
            result = "true";   // we cannot compare to anything, so...
        }
        else
        {
            if (identifier.matches(pattern))
            {
                result = "true";
            }
            else
            {
                result = "false";
            }
        }
        
        return result;
    }
    
    
    /*
     * Retrieves the identifier (stable and perennial) of all the resources which belong to a precise data type.
     * @param datatype name, synonym or MIRIAM URI of a data type
     * @return list of all the resources (their identifier) which belong to the given data type
     */
    /*
    public String[] getResourcesId(String datatype)
    {
        String[] result = null;
        String query = null;
        
        // looks for the kind of information given (name or synonym or MIRIAM URI)
        int type = getType(datatype);
        switch (type)
        {
            case ID_NAME:
            {
                query = "";
                break;
            }
            case ID_SYNONYM:
            {
                query = "";
                break;
            }
            case ID_URL: // do the same things as ID_URN
            case ID_URN:
            {
                query = "";
                break;
            }
            default:
            {
                // the parameter is not a name or a URI of a data type of the database
                return result;   // empty string
            }
        }
        
        // setup the context
        DataBaseContext context = new DataBaseContext();
        context.open(database);
        
        // request to the database
        ResultSet sqlResult = request(context.getStatement(), query);
        int nbLines = getRowCount(sqlResult);
        
        // TODO
         
        // SELECT resource_id FROM mir_resource WHERE (ptr_datatype=' ID ')
        // SELECT datatype_id FROM mir_datatype WHERE (name = ' NAME ')
        // if MIRIAM URI???
        // if synonym???         
        
        // close the context
        context.close();
        
        return null;
    }
    */
    
    
	/*
	 * Retrieves the type of the element
	 * @param name, synonym, URN or URL of a data type
	 * @return the type of the element, using an integer based constant with the following possible meaning: 'name', 'synonym', 'url', 'urn' or 'unknown'
	 */
	private int getType(String element)
	{
		String query = new String();
		ResultSet sqlResult = null;
		int nbRows;
		int result;
		
		// setup the context
		DataBaseContext context = new DataBaseContext();
		context.open(database);
		
		// is it a name?
		query = "SELECT datatype_id FROM mir_datatype WHERE (name = '" + element + "')";
		sqlResult = request(context.getStatement(), query);
		nbRows = getRowCount(sqlResult);
		if (nbRows >= 1)
		{
			result = ID_NAME;
		}
		// is it a synonym?
		else
		{
			query = "SELECT ptr_datatype FROM mir_synonym WHERE (name = '" + element + "')";
			sqlResult = request(context.getStatement(), query);
			nbRows = getRowCount(sqlResult);
			if (nbRows >= 1)
			{
				result = ID_SYNONYM;
			}
			// is it a URL?
			else
			{
				query = "SELECT uri_id FROM mir_uri WHERE (uri_type = 'URL' AND uri = '" + element + "')";
				sqlResult = request(context.getStatement(), query);
				nbRows = getRowCount(sqlResult);
				if (nbRows >= 1)
				{
					result = ID_URL;
				}
				// is it a URN?
				else
				{
					query = "SELECT uri_id FROM mir_uri WHERE (uri_type = 'URN' AND uri = '" + element + "')";
					sqlResult = request(context.getStatement(), query);
					nbRows = getRowCount(sqlResult);
					if (nbRows >= 1)
					{
						result = ID_URN;
					}
					// we don't know this thing
					else
					{
						result = ID_UNKNOWN;
					}
				}
			}
		}
		
		// close the context
	    context.close();
		
		return result;
	}
	
	
	/*
	 * Performs a SQL request using the Statement in parameter
	 * @param Statement object (already created)
	 * @param SQL query
	 * @return ResultSet object
	 */
	private ResultSet request(Statement stmt, String query)
	{
		try
		{
			logger.debug("SQL query: " + query);
			return stmt.executeQuery(query);
		}
		catch (SQLException e)
		{
	    	logger.error("Error during the execution of a query!");
	    	logger.error("Query: " + query);
	    	logger.error("SQL Exception raised: " + e.getMessage());
	    	return null;
		}
	}
	
	
	/*
	 * Counts the number of rows in the ResultSet object (like 'getColumnCount()' for the columns)
	 * @param ResultSet object
	 * @return the number of rows in the ResultSet object
	 */
	private int getRowCount(ResultSet data)
	{
		int result = -1;
		
		try
		{
			data.last();
			result = data.getRow();
			data.first();
		}
		catch (SQLException e)
		{
			// nothing to do here
		}
		
		return result;
	}
	
	
	/*
	 * Concatenates 2 String[] (Arrays)
	 * @param first String
	 * @param second String
	 * @return concatenation string[]
	 */
	private String[] arrayConcat(String[] first, String[] second)
	{
		String[] result = null;
		int size = first.length + second.length;
		int counter = 0;
		
		result = new String[size];
		for (int i=0; i<first.length; ++i)
		{
			result[i] = first[i];
			counter = i;
		}
		for (int i=0; i<second.length; ++i)
		{
			result[counter+i+1] = second[i];
		}
		
		return result;
	}
    
    
    /**
     * Returns the data type part of a URI (everything before "#")
     * @param uri a URI (example: "urn:miriam:pubmed:10812475" or the deprecated form "http://www.pubmed.gov/#10812475")
     * @return the data type part of a URI
     */
    private String getDataPart(String uri)
    {
        int index;
        int type = getUriType(uri);
        
        if (type == ID_URL)   // URL
        {
            index = uri.lastIndexOf("#");
        }
        else   // URN
        {
            index = uri.lastIndexOf(":");
        }
        
        // data type found
        if (index != -1)
        {
            return uri.substring(0, index);
        }
        // data type part not found, returns the full URI
        else
        {
            return uri;
        }
    }
    
    
    /**
     * Returns the identifier part of a URI (everything before "#")
     * 
     * TODO (or not: test all resources to see...): Transforms the identifier if it is encoded in hexadecimal (percent-encoded): 'GO%3A0045202' -> 'GO:0045202'
     * 
     * @param uri a URI (example: "urn:miriam:pubmed:10812475" or the deprecated form "http://www.pubmed.gov/#10812475")
     * @return the identifier part of a URI (or null if the part was not found)
     */
    private String getElementPart(String uri)
    {
        int index;
        int type = getUriType(uri);
        
        if (type == ID_URL)   // URL
        {
            index = uri.lastIndexOf("#");
        }
        else   // URN
        {
            index = uri.lastIndexOf(":");
        }
        // identifier part found
        if (index != -1)
        {
            return uri.substring(index+1, uri.length());
        }
        // identifier part not found, returns null
        else
        {
            return null;
        }
    }
    
    /*
     * Retrieves the type of the element
     * @param URN or URL
     * @return the type of the element, using an integer based constant with the following possible 'url' or 'urn'
     */
    private int getUriType(String element)
    {
       int result;
       
       if (element.startsWith("urn:"))
       {
           result = ID_URN;
       }
       else
       {
           result = ID_URL;
       }
       
       return result;
    }
    
    /*
     * Encodes URIs in order to make them comply with the URN syntax.
     * This encodes the character ':' (if any) in the identifier part with its percent-encoded equivalent '%3A'. Works as well with any other reserved character. 
     * This should only be useful if the MIRIAM URIs don't follow the URN syntax!
     */
    private String urlMagicEncode(String uri)
    {
        StringBuilder temp = null;
        String id;
        
        // no transformation necessary for URLs
        if (getUriType(uri) == ID_URN)
        {
            String[] parts = uri.split(":");
            // ':' presents in the identifier part
            // One exception: "urn:lsid:uniprot.org:uniprot:P12345"
            if ((parts.length == 5) && (! parts[3].equalsIgnoreCase("uniprot")))
            {
                temp = new StringBuilder();
                
                // data type part: no encoding
                for (int i=0; i<3; ++i)
                {
                    temp.append(parts[i] + ":");
                }
                // identifier part: URLencode
                id = parts[3] + ":" + parts[4];
                temp.append(identifierEncode(id));
            }
        }
        
        // no transformation necessary
        if (null == temp)
        {
            temp = new StringBuilder();
            temp.append(uri);
        }
        
        return temp.toString();
    }
    
    /*
     * Encodes the identifier if necessary: if it contains a reserved character (":" only, so far)
     */
    private String identifierEncode(String id)
    {
        if (id.contains(":"))
        {
            try
            {
                id = URLEncoder.encode(id, "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
                logger.error("An error occurred when encoding the following identifier: '" + id + "'!");
                logger.error("UnsupportedEncodingException raised: " + e.getMessage());
            }
        }
        
        return id;
    }

}
