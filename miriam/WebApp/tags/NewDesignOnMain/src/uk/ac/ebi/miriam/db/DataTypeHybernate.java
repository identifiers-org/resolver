/*
 * Author: Camille Laibe
 * Copyright: EMBL-EBI, Computational Neurobiology Group
 * Project: Miriam
 */


/*
 * TODO:
 * 
 * - test all the "isExisting" functionality
 * - test the upadte of the DB for adding a new data-type
 * 
 */

package uk.ac.ebi.miriam.db;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.TreeSet;
import org.apache.log4j.Logger;

import uk.ac.ebi.miriam.db.DbPoolConnect;
import uk.ac.ebi.miriam.web.MiriamUtilities;


/**
 * <p>
 * Class built above 'DataType', in order to add database connectivity features to the object.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>EMBL-EBI, Computational Neurobiology Group</dd>
 * </dl>
 * 
 * @author Camille Laibe
 * @version 20070125
 */
public class DataTypeHybernate extends DataType
{
	private Logger logger = Logger.getLogger(DataTypeHybernate.class);
	private final int DATATYPE = 1;   /* constant used in the 'generateID()' method */
	private final int RESOURCE = 2;   /* constant used in the 'generateID()' method */
	private static Object lock = new Object();   /* lock used to protect concurrent access to critical section */
	
	
	/**
	 * Tests if tha data-type already exists (according to identifier, because the ID is not modified during an update)
	 * @param poolName name of the database pool used
	 * @return boolean "true" if a data-type is already in the database using the same identifier
	 */
	public boolean isExisting(String poolName)
	{
		DbPoolConnect pool;
		ResultSet result = null;
		String sql = new String();
		boolean exist = true;   // by defaut, we need to proof the uniqueness
		
		// connection to the database pool
		pool = new DbPoolConnect(poolName);
		
		// test without 'newConnection()' before, let's see...
		pool.getConnection();
		
		sql = "SELECT name FROM mir_datatype WHERE (datatype_id='" + getId() + "')";
		result = pool.request(pool.getStatement(), sql);
		
		// one and only one data-type already existing
		if (DbPoolConnect.getRowCount(result) == 1)
		{
			exist = true;
		}
		else
		{
			exist = false;
		}
		
/*
		// query to test the uniqueness of the name (part one)
		sql = "SELECT name FROM mir_datatype WHERE (name='" + getName() + "')";
		result = pool.request(pool.getStatement(), sql);
		
		// no name of existing data-types is equivalent of the name of the data-type
		if (DbPoolConnect.getRowCount(result) == 0)
		{
			// query to test the uniqueness of the name (part two)
			sql = "SELECT name FROM mir_synonym WHERE (name='" + getName() + "')";
			result = pool.request(pool.getStatement(), sql);
			
			// no synonym of existing data-types is equivalent to the name of the new data-type  
			if (DbPoolConnect.getRowCount(result) == 0)
			{
				boolean tempExist1 = false;
				
				// queries to test the uniqueness of the synonyms (part one)
				for (int i=0; i<getSynonyms().size(); ++i)
				{
					sql = "SELECT name FROM mir_datatype WHERE (name='" + getSynonym(i) + "')";
					result = pool.request(pool.getStatement(), sql);
					
					// one of the synonyms of the new data-type is equivalent to the name of an existing data-type
					if (DbPoolConnect.getRowCount(result) != 0)
					{
						tempExist1 = true;
					}
				}
				
				// no name of existing data-types is equivalent to the synonyms of the new data-type
				if (! tempExist1)
				{
					boolean tempExist2 = false;
					
					// queries to test the uniqueness of the synonyms (part two)
					for (int i=0; i<getSynonyms().size(); ++i)
					{
						sql = "SELECT name FROM mir_synonym WHERE (name='" + getSynonym(i) + "')";
						result = pool.request(pool.getStatement(), sql);
						
						if (DbPoolConnect.getRowCount(result) != 0)
						{
							tempExist2 = true;
						}
					}
					
					// no synonym of existing data-types is equivalent to a synonym of the new data-type
					if (! tempExist2)
					{
						// query to test the uniqueness of the URIs
						sql = "SELECT uri FROM mir_uri WHERE ((uri='" + getURL() + "') OR (uri='" + getURN()  + "'))";
						result = pool.request(pool.getStatement(), sql);
						
						// no URI of existing data-types is equivalent to one of the URIs of the new data-type 
						if (DbPoolConnect.getRowCount(result) == 0)
						{
							boolean tempExist3 = false;
							
							// queries to test the uniqueness of the deprecated URI(s)
							for (int i=0; i<getDeprecatedURIs().size(); ++i)
							{
								sql = "SELECT uri FROM mir_uri WHERE (uri='" + getDeprecatedURI(i) + "')";
								result = pool.request(pool.getStatement(), sql);
								
								if (DbPoolConnect.getRowCount(result) != 0)
								{
									tempExist3 = true;
								}
							}
							
							// no URI of existing data-types is equivalent to one of the deprecated URI(s) of the new data-type
							if (! tempExist3)
							{
								exist = false;  // and finally we have our proof of uniqueness
							}
						}
					}
				}
			}
		}
*/
		
		// without closing the statement, let's see...
		pool.closeConnection();
		
		return exist;
	}
	
	
	/**
	 * Performs all the SQL queries to add the data-type to the database
	 * @param poolName name of the database pool used
	 * @return int result of the queries: 1 equals success, 0 equals failure
	 */
	public int storeObject(String poolName)
	{
		logger.debug("Begin of the adding of a new data-type...");
		
		//private DbPoolConnect pool;
		DbPoolConnect pool;
		
		/* database connection start */
		int resultStatus;
		int resultGlobal = 1;   // we consider that everything is ok by default
		String index = new String();
		//ResultSet result = null;
		
		String sql = new String();
		
		// connection to the database pool
		pool = new DbPoolConnect(poolName);
		
		// test without 'newConnection()' before, let's see...
		pool.getConnection();
		
		// critical section to protected against concurrent access (the 'setAutoCommit' should be enough, but...)
		synchronized(lock)
		{
			// begin of the transaction
			pool.setAutoCommit(false);   // TO TEST!!!!!!!!!!!
			
			// generation of a new ID for the data-type
			index = generateID(pool, DATATYPE);
			
			// resource main information
			sql = "INSERT INTO mir_datatype (datatype_id, name, pattern, definition) VALUES ('" + index + "', '" + getName() + "', '" + getRegexp() + "', '" + getDefinition() + "')";
			resultStatus = pool.requestUpdate(pool.getStatement(), sql);
			logger.info("Result of the adding query #1 (new data-type, resource): " + resultStatus);
			resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
			
	/*
	//useless now: the ID is not 'auto incremented' any more
	
			// index of the last element included
			sql = "SELECT LAST_INSERT_ID()";
			result = pool.request(pool.getStatement(), sql);
			try
			{
				result.first();
				index = Integer.parseInt(result.getString(1));
			}
			catch (SQLException e)
			{
				logger.debug("Cannot retrieve the index of the last element included (to add the data-type '" + getName() + "')!");
				logger.debug("Error: " + e.getMessage());
				resultGlobal *= 0;
			}
	*/
			
			// synonym(s)
			for (int i=0; i<getSynonyms().size(); ++i)
			{
				sql = "INSERT INTO mir_synonym (name, ptr_datatype) VALUES ('" + getSynonym(i)  + "', '" + index + "')";
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the adding query #2." + i + " (new data-type, synonyms): " + resultStatus);
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
			}
			
			// official URL
			if (! MiriamUtilities.isEmpty(getURL()))
			{
				sql = "INSERT INTO mir_uri (uri, uri_type, deprecated, ptr_datatype) VALUES ('" + getURL() + "', 'URL', '0', '" + index + "')";
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the adding query #3 (new data-type, URL): " + resultStatus);
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
			}
			
			// official URN
			if (! MiriamUtilities.isEmpty(getURN()))
			{
				sql = "INSERT INTO mir_uri (uri, uri_type, deprecated, ptr_datatype) VALUES ('" + getURN()  + "', 'URN', '0', '" + index + "')";
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the adding query #4 (new data-type, URN): " + resultStatus);
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
			}
			
			// deprecated URI(s)
			for (int i=0; i<getDeprecatedURIs().size(); ++i)
			{
				if (isDeprecatedURN(i))
				{
					sql = "INSERT INTO mir_uri (uri, uri_type, deprecated, ptr_datatype) VALUES ('" + getDeprecatedURI(i) + "', 'URN', '1', '" + index + "')";
				}
				else
				{
					sql = "INSERT INTO mir_uri (uri, uri_type, deprecated, ptr_datatype) VALUES ('" + getDeprecatedURI(i) + "', 'URL', '1', '" + index + "')";
				}
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the adding query #5." + i +" (new data-type, deprecated URIs): " + resultStatus);
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
			}
			
			// physical locations
			ListIterator it = getResources().listIterator();
			int cpt = 0;
			while (it.hasNext())
			{
				// generation of a new ID for the resource
				String indexResource = generateID(pool, RESOURCE);
				Resource temp = (Resource) it.next();
				// TODO: add a feature to be able to change the 'obsolete' parameter
				//sql = "INSERT INTO mir_resource (resource_id, url_element_prefix, url_element_suffix, url_resource, info, institution, location, obsolete, ptr_datatype) VALUES ('" + indexResource + "', '" + temp.getUrl_prefix() + "', '" + temp.getUrl_suffix() + "', '" + temp.getUrl_root() + "', '" + temp.getInfo() + "', '" + temp.getInstitution() + "', '" + temp.getLocation() + "', '" + temp.isObsolete() + "', '" + index +  "')";
				sql = "INSERT INTO mir_resource (resource_id, url_element_prefix, url_element_suffix, url_resource, info, institution, location, obsolete, ptr_datatype) VALUES ('" + indexResource + "', '" + temp.getUrl_prefix() + "', '" + temp.getUrl_suffix() + "', '" + temp.getUrl_root() + "', '" + temp.getInfo() + "', '" + temp.getInstitution() + "', '" + temp.getLocation() + "', '0', '" + index +  "')";
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the adding query #6." + cpt +" (new data-type, resources): " + resultStatus);
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
				cpt++;
			}
			
			// documentation ID(s)
			for (int i=0; i<getDocumentationIDs().size(); ++i)
			{
				sql = "INSERT INTO mir_doc (uri, uri_type, ptr_type, ptr_datatype, ptr_resource) VALUES ('" + getDocumentationID(i) + "', 'MIRIAM', 'data', '" + index + "', NULL)";
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the adding query #7." + i +" (new data-type, documentation IDs): " + resultStatus);
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
			}
			
			// documentation URL(s)
			for (int i=0; i<getDocumentationURLs().size(); ++i)
			{
				sql = "INSERT INTO mir_doc (uri, uri_type, ptr_type, ptr_datatype, ptr_resource) VALUES ('" + getDocumentationURL(i) + "', 'URL', 'data', '" + index + "', NULL)";
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the adding query #8." + i +" (new data-type, documentation URLs): " + resultStatus);	
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
			}
			
			// end of the transaction
			pool.commit();   // TO TEST!!!!!!!!!!!
		}
		
		// without closing the statement, let's see...
		pool.closeConnection();
		
		logger.debug("... end of the adding of a new data-type (GLOBAL RESULT: " + resultGlobal + ")");
		
		return resultGlobal;
	}
	
	
/*
	 * Retrieves all the information about a data-type, based on its name
	 * WARNING: a preliminary test is necessary to be sure that a data-type with this name exists in the database
	 * @param poolName name of the database pool used
	 * @param dataName name of the data-type you want to retrieve from the database
	public void retrieveData(String poolName, String webServicesEndpoint, String dataName)
	{
		DbPoolConnect pool;
		String sql = new String();
		ResultSet rs = null;
		
		// connexion pool management
		pool = new DbPoolConnect(poolName);
		
		// test without 'newConnection()' before, let's see...
		pool.getConnection();
		
		// set the name
		setName(dataName);
		
		// set the HTML name
		setNameURL(MiriamUtilities.nameTrans(dataName));
		
		// search the internal ID
		sql = "SELECT datatype_id FROM mir_datatype WHERE (name='" + dataName + "')";
		rs = pool.request(pool.getStatement(), sql);
		int id = Integer.parseInt(MiriamUtilities.StringConvert(rs));
		setInternalId(id);
		
		// search the synonyms
		sql = "SELECT ms.name FROM mir_datatype md, mir_synonym ms WHERE ((ms.ptr_datatype = md.datatype_id) AND (md.name = '" + dataName + "'))";
        rs = pool.request(pool.getStatement(), sql);
		ArrayList syn = new ArrayList();
		syn = MiriamUtilities.ArrayConvert(rs);
		//if (syn.isEmpty())
		//{
		//	syn.add("<i>No synonym</i>");
		//}
        setSynonyms(syn);
		
		// search the official URL
		sql = "SELECT mu.uri FROM mir_datatype md, mir_uri mu WHERE ((mu.ptr_datatype = md.datatype_id) AND (md.name = '" + dataName + "') AND (mu.uri_type = 'URL') AND (mu.deprecated = '0'))";
		rs = pool.request(pool.getStatement(), sql);
		String url = MiriamUtilities.StringConvert(rs);
		setURL(url);
		
		// search the official URN
		sql = "SELECT mu.uri FROM mir_datatype md, mir_uri mu WHERE ((mu.ptr_datatype = md.datatype_id) AND (md.name = '" + dataName + "') AND (mu.uri_type = 'URN') AND (mu.deprecated = '0'))";
		rs = pool.request(pool.getStatement(), sql);
		String urn = MiriamUtilities.StringConvert(rs);
		setURN(urn);
		
		// search for deprecated URIs
		sql = "SELECT mu.uri FROM mir_datatype md, mir_uri mu WHERE ((mu.ptr_datatype = md.datatype_id) AND (md.name = '" + dataName + "') AND (mu.deprecated = '1'))";
		rs = pool.request(pool.getStatement(), sql);
		ArrayList deprecated = MiriamUtilities.ArrayConvert(rs);
		//if (deprecated.isEmpty())
		//{
		//	deprecated.add("<i>No element</i>");
		//}
        setDeprecatedURIs(deprecated);
        
		// search the definition
		sql = "SELECT definition FROM mir_datatype WHERE (name = '" + dataName + "')";
		rs = pool.request(pool.getStatement(), sql);
		String def = MiriamUtilities.StringConvert(rs);
		setDefinition(def);
		
		// search the regular expression
		sql = "SELECT pattern FROM mir_datatype WHERE (name = '" + dataName + "')";
		rs = pool.request(pool.getStatement(), sql);
		String rexp = MiriamUtilities.StringConvert(rs);
		setRegexp(rexp);
		
		// search the physical locations
		sql = "SELECT mr.url_element_prefix, mr.url_element_suffix, mr.url_resource FROM mir_datatype md, mir_resource mr WHERE ((mr.ptr_datatype = md.datatype_id) AND (md.name = '" + dataName + "'))";
		rs = pool.request(pool.getStatement(), sql);
		ArrayList urls = MiriamUtilities.ArrayConvert(rs, 3, true);
		setLocations(urls);
		
		// search documentation information (URLs) linked to the data-type
		sql = "SELECT md.uri FROM mir_datatype mdt, mir_doc md WHERE ((md.ptr_datatype = mdt.datatype_id) AND (mdt.name = '" + dataName + "') AND (ptr_type = 'data') AND (md.uri_type = 'URL'))";
		rs = pool.request(pool.getStatement(), sql);
		ArrayList docs_url = null;
		docs_url = MiriamUtilities.ArrayConvert(rs, true);
		
		// search documentation information (MIRIAM URIs) linked to the data-type
		sql = "SELECT md.uri FROM mir_datatype mdt, mir_doc md WHERE ((md.ptr_datatype = mdt.datatype_id) AND (mdt.name = '" + dataName + "') AND (ptr_type = 'data') AND (md.uri_type = 'MIRIAM'))";
		rs = pool.request(pool.getStatement(), sql);
		ArrayList docs_id = null;
		docs_id = MiriamUtilities.ArrayConvert(rs);
		
		// we will now modify the Miriam URIs into URLS using MIRIAM Database (Isn't it a beautiful recursive process?) and store them into 'docHtmlURLs'
		ArrayList temp = new ArrayList(docs_url);   // by default, the URLs are already ready to be put in this list
		for (int i=0; i<docs_id.size(); ++i)
		{
			String uri = (String) docs_id.get(i);
			// retrieve the physical location(s) corresponding to this URI (using web services)
			
			// TO DO: modify that and create a "getDataEntriesAnswer(completeURI)" method for the Web Services
			
			String uriDataType = getDataPart(uri);
			String uriElement = getElement(uri);
			// invokation of the web service
			String endPoint = webServicesEndpoint;
			// processing the request
		    MiriamLink mir = new MiriamLink(endPoint);
		    String[] result = mir.getDataEntriesAnswer(uriDataType, uriElement);
		    // checking of the answer
		    if ((result == null) || (result.length == 0))
		    {
		    	logger.warn("A Miriam URI (" + uri + ") correponding to a piece of documentation has not physical location");
		    }
		    else
		    {
			    for (int j=0; j<result.length; ++j)
			    {
			    	temp.add(MiriamUtilities.transURL(result[j], '&', "&amp;"));
		    	}
		    }
		}
		setDocHtmlURLs(temp);
		
		// we add all this information to the data-type
		//if (docs_id.isEmpty())
		//{
		//	docs_id.add("<i>No identifier of a documentation</i>");
		//}
		setDocumentationIDs(docs_id);
		//if (docs_url.isEmpty())
		//{
		//	docs_url.add("<i>No URL to a documentation</i>");
		//}
		setDocumentationURLs(docs_url);
		
		// without closing the statement, let's see...
		pool.closeConnection();
	}
*/
	
	
	/**
	 * Retrieves all the information about a data-type, based on its ID or his name, from the database
	 * WARNING: a preliminary test is necessary to be sure that a data-type with this name exists in the database
	 * @param poolName name of the database pool used
	 * @param index internal ID of the data-type you want to retrieve from the database
	 */
	public void retrieveData(String poolName, String index)
	{
		DbPoolConnect pool;
		String sql = new String();
		ResultSet rs = null;
		
		// connexion pool management
		pool = new DbPoolConnect(poolName);
		
		// test without 'newConnection()' before, let's see...
		pool.getConnection();
		
		// is the 'index' parameter a name or an ID?
		if (index.indexOf("MIR:") != -1)   // the parameter is an ID
		{
			// set the internal ID
			setId(index);
			
			// search the name
			sql = "SELECT name FROM mir_datatype WHERE (datatype_id='" + index + "')";
			rs = pool.request(pool.getStatement(), sql);
			String name = MiriamUtilities.StringConvert(rs);
			setName(name);
		}
		else   // the parameter is a name
		{
			// set the name
			setName(index);
			
			// search the ID
			sql = "SELECT datatype_id FROM mir_datatype WHERE (name='" + index + "')";
			rs = pool.request(pool.getStatement(), sql);
			index = MiriamUtilities.StringConvert(rs);
			setId(index);
		}
		
		// set the HTML name
		setNameURL(MiriamUtilities.nameTrans(getName()));
		
		// search the synonyms
		sql = "SELECT ms.name FROM mir_datatype md, mir_synonym ms WHERE ((ms.ptr_datatype = md.datatype_id) AND (md.datatype_id = '" + index + "'))";
        rs = pool.request(pool.getStatement(), sql);
		ArrayList syn = new ArrayList();
		syn = MiriamUtilities.ArrayConvert(rs);
        setSynonyms(syn);
		
		// search the official URL
		sql = "SELECT mu.uri FROM mir_datatype md, mir_uri mu WHERE ((mu.ptr_datatype = md.datatype_id) AND (md.datatype_id = '" + index + "') AND (mu.uri_type = 'URL') AND (mu.deprecated = '0'))";
		rs = pool.request(pool.getStatement(), sql);
		String url = MiriamUtilities.StringConvert(rs);
		setURL(url);
		
		// search the official URN
		sql = "SELECT mu.uri FROM mir_datatype md, mir_uri mu WHERE ((mu.ptr_datatype = md.datatype_id) AND (md.datatype_id = '" + index + "') AND (mu.uri_type = 'URN') AND (mu.deprecated = '0'))";
		rs = pool.request(pool.getStatement(), sql);
		String urn = MiriamUtilities.StringConvert(rs);
		setURN(urn);
		
		// search for deprecated URIs
		sql = "SELECT mu.uri FROM mir_datatype md, mir_uri mu WHERE ((mu.ptr_datatype = md.datatype_id) AND (md.datatype_id = '" + index + "') AND (mu.deprecated = '1'))";
		rs = pool.request(pool.getStatement(), sql);
		ArrayList deprecated = MiriamUtilities.ArrayConvert(rs);
        setDeprecatedURIs(deprecated);
        
		// search the definition
		sql = "SELECT definition FROM mir_datatype WHERE (datatype_id = '" + index + "')";
		rs = pool.request(pool.getStatement(), sql);
		String def = MiriamUtilities.StringConvert(rs);
		setDefinition(def);
		
		// search the regular expression
		sql = "SELECT pattern FROM mir_datatype WHERE (datatype_id = '" + index + "')";
		rs = pool.request(pool.getStatement(), sql);
		String rexp = MiriamUtilities.StringConvert(rs);
		setRegexp(rexp);
		
		// search the resources (physical locations)
		sql = "SELECT mr.resource_id, mr.url_element_prefix, mr.url_element_suffix, mr.url_resource, mr.info, mr.institution, mr.location, mr.obsolete FROM mir_datatype mdt, mir_resource mr WHERE ((mr.ptr_datatype = mdt.datatype_id) AND (mdt.datatype_id = '" + index + "'))";
		rs = pool.request(pool.getStatement(), sql);
		try
		{
			boolean notEmpty = rs.next();
			while (notEmpty)
			{
				Resource temp = new Resource();
				temp.setId(rs.getString("resource_id"));
				temp.setUrl_prefix(rs.getString("url_element_prefix"));
				temp.setUrl_suffix(rs.getString("url_element_suffix"));
				temp.setUrl_root(rs.getString("url_resource"));
				temp.setInfo(rs.getString("info"));
				temp.setInstitution(rs.getString("institution"));
				temp.setLocation(rs.getString("location"));
				if (rs.getString("obsolete").compareToIgnoreCase("0") == 0)
				{
					temp.setObsolete(false);
				}
				else
				{
					temp.setObsolete(true);
				}
				// add the resource to the data-type
				addResource(temp);
				// next resource (if it exists)
				notEmpty = rs.next();
			}
		}				
		catch (SQLException e)
		{
	    	logger.error("Error while searching the resources!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
		}
		
		// search documentation information (URLs) linked to the data-type
		sql = "SELECT md.uri FROM mir_datatype mdt, mir_doc md WHERE ((md.ptr_datatype = mdt.datatype_id) AND (mdt.datatype_id = '" + index + "') AND (md.ptr_type = 'data') AND (md.uri_type = 'URL'))";
		rs = pool.request(pool.getStatement(), sql);
		ArrayList docs_url = null;
		docs_url = MiriamUtilities.ArrayConvert(rs, true);
		setDocumentationURLs(docs_url);
		
		// search documentation information (MIRIAM URIs) linked to the data-type
		sql = "SELECT md.uri FROM mir_datatype mdt, mir_doc md WHERE ((md.ptr_datatype = mdt.datatype_id) AND (mdt.datatype_id = '" + index + "') AND (md.ptr_type = 'data') AND (md.uri_type = 'MIRIAM'))";
		rs = pool.request(pool.getStatement(), sql);
		ArrayList docs_id = null;
		docs_id = MiriamUtilities.ArrayConvert(rs);
		setDocumentationIDs(docs_id);
		
		// we will now modify the Miriam URIs into URLS using MIRIAM Database (Isn't it a beautiful recursive process?) and stored them into 'docHtmlURLs'
		ArrayList temp = new ArrayList(docs_url);   // by default, the URLs are already ready to be put in this list
		for (int i=0; i<docs_id.size(); ++i)
		{
			String uri = (String) docs_id.get(i);
			// retrieve the physical location(s) corresponding to this URI
			//String uriDataType = getDataPart(uri);   // useless here: we only search for documentation (PubMed, http://www.pubmed.gov/, MIR:00000015)
			String uriElement = MiriamUtilities.getElementPart(uri);
		    // transforms the URI into a URL
		    String[] result = documentationURLs(pool, "MIR:00000015", uriElement);
		    
		    
		    // checking of the answer
		    if ((result == null) || (result.length == 0))
		    {
		    	logger.warn("A Miriam URI (" + uri + ") correponding to a piece of documentation has not physical location");
		    }
		    else
		    {
			    for (int j=0; j<result.length; ++j)
			    {
			    	temp.add(MiriamUtilities.transURL(result[j], '&', "&amp;"));
		    	}
		    }
		}
		setDocHtmlURLs(temp);
		
		// without closing the statement, let's see...
		pool.closeConnection();
	}


	/**
	 * Performs all the SQL queries to update the data-type in the database
	 * @param poolName name of the database pool used
	 * @return int result of the queries: 1 equals success, 0 equals failure
	 */
	public int updateObject(DataTypeHybernate oldOne, String poolName)
	{
		logger.debug("Begin of the update of a data-type...");
		
		//private DbPoolConnect pool;
		DbPoolConnect pool;
		
		/* database connection start */
		int resultStatus;
		int resultGlobal = 1;   // we consider that everything is ok by default
		String sql = new String();
		ResultSet rs = null;
		
		// connection to the database pool
		pool = new DbPoolConnect(poolName);
		
		// test without 'newConnection()' before, let's see...
		pool.getConnection();
		
		// critical section to protected against concurrent access (the 'setAutoCommit' should be enough, but...)
		synchronized(lock)
		{
			// begin of the transaction
			pool.setAutoCommit(false);   // TO TEST!!!!!!!!!!!
			
			// resource main information (we just update it in order to keep the ID stable)
			sql = "UPDATE mir_datatype SET name='" + getName() + "', pattern='" + getRegexp() + "', definition='" + getDefinition() + "' WHERE (datatype_id='" + getId() + "')";
			resultStatus = pool.requestUpdate(pool.getStatement(), sql);
			logger.info("Result of the update query #1 (edit data-type, resource): " + resultStatus);
			resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
			
			// synonym(s): removes all the previous one(s), if necessary
			sql = "SELECT * FROM mir_synonym WHERE (ptr_datatype='" + getId() + "')";
			rs = pool.request(pool.getStatement(), sql);
			if (DbPoolConnect.getRowCount(rs) > 0)
			{
				sql = "DELETE FROM mir_synonym WHERE (ptr_datatype='" + getId() + "')";
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the update query #2 (edit data-type, remove old synonyms): " + resultStatus);
				/* DELETE returns the number of entries deleted, so it is not wise to use it to check if the update process is a success or not
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
				*/
			}
			else
			{
				logger.info("No update query #2 (edit data-type, remove old synonyms): no synonym to delete.");
			}
			
			// synonym(s): add the new one(s)
			for (int i=0; i<getSynonyms().size(); ++i)
			{
				sql = "INSERT INTO mir_synonym (name, ptr_datatype) VALUES ('" + getSynonym(i)  + "', '" + getId() + "')";
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the update query #3." + i + " (edit data-type, synonyms): " + resultStatus);
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
			}
			
			// URL and URN (official and deprecated): removes all the previous one(s)
			sql = "DELETE FROM mir_uri WHERE (ptr_datatype='" + getId() + "')";
			resultStatus = pool.requestUpdate(pool.getStatement(), sql);
			logger.info("Result of the update query #4 (edit data-type, remove old URIs): " + resultStatus);
			/* DELETE returns the number of entries deleted, so it is not wise to use it to check if the update process is a success or not
			resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
			*/
			
			// official URL: add the new one
			if (! MiriamUtilities.isEmpty(getURL()))
			{
				sql = "INSERT INTO mir_uri (uri, uri_type, deprecated, ptr_datatype) VALUES ('" + getURL() + "', 'URL', '0', '" + getId() + "')";
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the adding query #5 (edit data-type, URL): " + resultStatus);
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
			}
			
			// official URN: add the new one
			if (! MiriamUtilities.isEmpty(getURN()))
			{
				sql = "INSERT INTO mir_uri (uri, uri_type, deprecated, ptr_datatype) VALUES ('" + getURN()  + "', 'URN', '0', '" + getId() + "')";
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the adding query #6 (edit data-type, URN): " + resultStatus);
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
			}
			
			// deprecated URI(s): add the new one(s)
			for (int i=0; i<getDeprecatedURIs().size(); ++i)
			{
				if (isDeprecatedURN(i))
				{
					sql = "INSERT INTO mir_uri (uri, uri_type, deprecated, ptr_datatype) VALUES ('" + getDeprecatedURI(i) + "', 'URN', '1', '" + getId() + "')";
				}
				else
				{
					if (isDeprecatedURL(i))
					{
						sql = "INSERT INTO mir_uri (uri, uri_type, deprecated, ptr_datatype) VALUES ('" + getDeprecatedURI(i) + "', 'URL', '1', '" + getId() + "')";
					}
					else
					{
						logger.warn("This URI '" + getDeprecatedURI(i) + "' should be deprecated but is not...");
						sql = "";
						//TODO: check that in the logs...
					}
				}
				
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the adding query #7." + i +" (edit data-type, deprecated URIs): " + resultStatus);
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
			}
			
	/* previous piece of code that never worked and I still don't know why! 
			// separates the resources: new/old ones (according to the ID)
			ArrayList newResources = new ArrayList(getResources());
			TreeSet oldResources = new TreeSet();
			boolean added;
			Resource res;
			ListIterator it = newResources.listIterator();
			logger.debug("____Content 'newResources' before: " + newResources.toString());
			for (int j=0; j<newResources.size(); ++j)
			{
				logger.debug("* resource: " + ((Resource) newResources.get(j)).getId());
				logger.debug("**********: " + ((Resource) newResources.get(j)).getUrl_prefix() + "$id" + ((Resource) newResources.get(j)).getUrl_suffix());
			}
			while (it.hasNext())
			{
				logger.debug("YOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");   // REMOVE THAT!
				res = (Resource) it.next();
				logger.debug("YOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO (bis)");   // REMOVE THAT!
				//if (res.getId().compareToIgnoreCase("null") != 0)   // TEST
				if (res.getId().compareToIgnoreCase("camille") != 0)
				{
					logger.debug("--> before add");
					// adds this old resource to the TreeSet
					added = oldResources.add(res);
					logger.debug("--> after add");
					// the object hasn't been added (because another resource with the same ID is already in the TreeSet: that should never happen...)
					if (! added)
					{
						logger.equals("Two different resources have the same ID! (discovered while trying to update a data-type)");
					}
					// removes this old resource to the list of new ones
					logger.debug("--> before remove");
					newResources.remove(res);
					logger.debug("--> after remove");
				}
			}
			
			logger.debug("____Content 'newResources' after: " + newResources.toString());
			logger.debug("____Content 'oldResources' after: " + oldResources.toString());
			
			logger.debug("VIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII");   // REMOVE THAT!
			
			// retrieve the removed resources (by comparison of two datasets)
			ArrayList veryOldResources = new ArrayList(oldOne.getResources());
			ListIterator it2 = veryOldResources.listIterator();
			while (it2.hasNext())
			{
				logger.debug("OUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU");
				Resource obj = (Resource) it2.next();
				// resource not removed
				if (oldResources.contains(obj))
				{
					// deletion of the resource, in order to keep in 'veryOldResources' only the removed resources
					veryOldResources.remove(obj);
				}
			}
	*/
			
			// retrieve the deleted resources (by comparison of two datasets)
			ArrayList[] diff = new ArrayList[3];
			diff = differentiateResources(oldOne.getResources(), getResources());
			
			ArrayList oldResources = new ArrayList(diff[0]);
			ArrayList newResources = new ArrayList(diff[1]);
			ArrayList deletedResources = new ArrayList(diff[2]);
			
			
	/*
			// separates the resources: new/old ones (according to the ID)
			ArrayList allNewResources = new ArrayList(getResources());
			ArrayList newResources = new ArrayList();
			TreeSet oldResources = new TreeSet();
			ListIterator it = allNewResources.listIterator();
			while (it.hasNext())
			{
				Resource res = (Resource) it.next();
				
				// this is an old resource (not a new addition)
				if ((res.getId()).compareToIgnoreCase("null") != 0)
				{
					oldResources.add(res);
				}
				else   // this is a new added resource
				{
					newResources.add(res);
				}
			}
			
			// retrieve the deleted resources (by comparison of two datasets), in order to mark them as "deprecated"
			ArrayList allOldResources = new ArrayList(oldOne.getResources());
			ArrayList deletedResources = new ArrayList();
			
			ListIterator it2 = allOldResources.listIterator();
			while (it2.hasNext())
			{
				Resource obj = (Resource) it2.next();
				// resource not removed
				if (oldResources.contains(obj))
				{
					// resource not removed: nothing to do.
				}
				else   // resource removed: "deprecated"
				{
					deletedResources.add(obj);
				}
			}
	*/
			
			// modifies the resources already existing
			Iterator itr = oldResources.iterator();
			int cpt = 0;
			while (itr.hasNext())
			{
				Resource r = (Resource) itr.next();
				if (r.isObsolete())   // resource obsolete
				{
					sql = "UPDATE mir_resource SET url_element_prefix='" + r.getUrl_prefix() + "', url_element_suffix='" + r.getUrl_suffix() + "', url_resource='" + r.getUrl_root() + "', info='" + r.getInfo() + "', institution='" + r.getInstitution() + "', location='" + r.getLocation() + "', obsolete='1' WHERE (resource_id='" + r.getId() + "')";
				}
				else
				{
					sql = "UPDATE mir_resource SET url_element_prefix='" + r.getUrl_prefix() + "', url_element_suffix='" + r.getUrl_suffix() + "', url_resource='" + r.getUrl_root() + "', info='" + r.getInfo() + "', institution='" + r.getInstitution() + "', location='" + r.getLocation() + "', obsolete='0' WHERE (resource_id='" + r.getId() + "')";
				}
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the update query #8. " + cpt + " (edit data-type, update old resources): " + resultStatus);
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
				cpt++;
			}
			
			// adds the new resources
			ListIterator it3 = newResources.listIterator();
			cpt = 0;
			while (it3.hasNext())
			{
				Resource temp = (Resource) it3.next();
				//sql = "INSERT INTO mir_resource (resource_id, url_element_prefix, url_element_suffix, url_resource, info, institution, location, obsolete, ptr_datatype) VALUES ('" + generateID(pool, RESOURCE) + "', '" + temp.getUrl_prefix() + "', '" + temp.getUrl_suffix() + "', '" + temp.getUrl_root() + "', '" + temp.getInfo() + "', '" + temp.getInstitution() + "', '" + temp.getLocation() + "', '" + temp.isObsolete() + "')";
				// a new resource can't be 'obsolete'
				sql = "INSERT INTO mir_resource (resource_id, url_element_prefix, url_element_suffix, url_resource, info, institution, location, obsolete, ptr_datatype) VALUES ('" + generateID(pool, RESOURCE) + "', '" + temp.getUrl_prefix() + "', '" + temp.getUrl_suffix() + "', '" + temp.getUrl_root() + "', '" + temp.getInfo() + "', '" + temp.getInstitution() + "', '" + temp.getLocation() + "', '0', '" + getId() + "')";
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the adding query #9." + cpt + " (edit data-type, add new resources): " + resultStatus);
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
				cpt++;
			}
			
			//  indicates as 'obsolete' the resources "removed"
			ListIterator it4 = deletedResources.listIterator();
			cpt = 0;
			while (it4.hasNext())
			{
				Resource temp = (Resource) it4.next();
				sql = "UPDATE mir_resource SET obsolete='1' WHERE (resource_id='" + temp.getId() + "')";
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the update query #10." + cpt + " (edit data-type, update obsolete resources): " + resultStatus);
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
			}
			
			// documentation IDs and URLs: removes all the previous one(s), if necessary
			sql = "SELECT * FROM mir_doc WHERE (ptr_datatype='" + getId() + "')";
			rs = pool.request(pool.getStatement(), sql);
			if (DbPoolConnect.getRowCount(rs) > 0)
			{
				sql = "DELETE FROM mir_doc WHERE (ptr_datatype='" + getId() + "')";
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the update query #11 (edit data-type, remove old documentation IDs and URIs): " + resultStatus);
				/* DELETE returns the number of entries deleted, so it is not wise to use it to check if the update process is a success or not
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
				*/
			}
			else
			{
				logger.info("No update query #10 (edit data-type, remove old documentation IDs and URIs): no documentation to delete.");
			}
			
			// documentation ID(s): add the new one(s)
			for (int i=0; i<getDocumentationIDs().size(); ++i)
			{
				sql = "INSERT INTO mir_doc (uri, uri_type, ptr_type, ptr_datatype, ptr_resource) VALUES ('" + getDocumentationID(i) + "', 'MIRIAM', 'data', '" + getId() + "', NULL)";
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the adding query #12." + i +" (edit data-type, documentation IDs): " + resultStatus);
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
			}
			
			// documentation URL(s)
			for (int i=0; i<getDocumentationURLs().size(); ++i)
			{
				sql = "INSERT INTO mir_doc (uri, uri_type, ptr_type, ptr_datatype, ptr_resource) VALUES ('" + getDocumentationURL(i) + "', 'URL', 'data', '" + getId() + "', NULL)";
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the adding query #13." + i +" (edit data-type, documentation URLs): " + resultStatus);	
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
			}
			
			// end of the transaction
			pool.commit();   // TO TEST!!!!!!!!!!!
		}
		
		// without closing the statement, let's see...
		pool.closeConnection();
		
		logger.debug("... end of the update of a data-type (GLOBAL RESULT: " + resultGlobal + ")");
		
		return resultGlobal;
	}
	
	
	/*
	 * Generate an ID for a new data-type or for a new resource (physical location)
	 * @param database connection pool
	 * @param type of ID to generate: for a data-type ('DATATYPE') or a resource ('RESOURCE')
	 */
	private String generateID(DbPoolConnect pool, int type)
	{
		ResultSet rs = null;
		String id = new String();
		String sql = new String();
		int size;
		
		if (type == DATATYPE)
		{
			id = "MIR:000";
			sql = "SELECT datatype_id FROM mir_datatype";
		}
		if (type == RESOURCE)
		{
			id = "MIR:001";
			sql = "SELECT resource_id FROM mir_resource";
		}
		
		
		rs = pool.request(pool.getStatement(), sql);
		size = MiriamUtilities.getRowCount(rs);
		size += 1;
		
		if (size < 10)
		{
			id += "0000" + size;
		}
		else
		{
			if (size < 100)
			{
				id += "000" + size;
			}
			else
			{
				if (size < 1000)
				{
					id += "00" + size;
				}
				else
				{
					if (size < 10000)
					{
						id += "0" + size;
					}
					else
					{
						if (size < 100000)
						{
							id += size;
						}
						else
						{
							if (type == DATATYPE)
							{
								logger.error("Generation of the ID of the new data-type impossible: size overflow!");
								id = "MIR:00000001";   // ID already existing: should generate a SQL error if tried to use like that...
							}
							if (type == RESOURCE)
							{
								logger.error("Generation of the ID of the new resource impossible: size overflow!");
								id = "MIR:00100001";   // ID already existing: should generate a SQL error if tried to use like that...								
							}
						} 
					}
				} 
			}
		}
		
		if (type == DATATYPE)
		{
			logger.info("ID generated for a new data-type: " + id);
		}
		if (type == RESOURCE)
		{
			logger.info("ID generated for a new resource: " + id);
		}
		
		return id;
	}
	
	
	/*
	 * Transform URIs into physical locationS (URLs) (used, for example, to get the links towards the documentation)
	 * @param database connection pool
	 * @param URI of a data-type (example: "http://www.uniprot.org/")
	 * @param URI of the element (example: "P47757")
	 * @return physical locationS of the element
	 */
	private String[] documentationURLs(DbPoolConnect pool, String uriDataType, String uriElement)
	{
		logger.debug("Transformation URIs to URLs...");
		
		ResultSet sqlResult;
		String[] result = null;
		
		String query = "SELECT url_element_prefix, url_element_suffix FROM mir_resource WHERE (ptr_datatype = '"+ uriDataType + "')";
		sqlResult = pool.request(pool.getStatement(), query);
		
		int nbRows = MiriamUtilities.getRowCount(sqlResult);
		result = new String[nbRows];
		try
		{
			for (int i=1; i<=nbRows; ++i)
			{
				result[i-1] = sqlResult.getString("url_element_prefix") + uriElement + sqlResult.getString("url_element_suffix"); 
				sqlResult.next();
			}
		}
	    catch (SQLException e)
	    {
	    	logger.error("Error during the transformation URIs to URLs!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
	    }
		
		return result;
	}
	
	
	/**
	 * Modify the MIRIAM IDs of the documentations in order to keep only the 'element' part
	 * (without the 'data-type' part and the '#')
	 */
	public void transDocIds()
	{
		String tmp = new String();
		for (int i=0; i<getDocumentationIDs().size(); ++i)
		{
			tmp = MiriamUtilities.getElementPart(getDocumentationID(i));
			
			setDocumentationID(i, tmp);
		}
	}
	
	
	/**
	 * Returns three lists of "Resource" objects. 
	 * The first list contains the old resources (that have not been added or deleted). 
	 * The second list contains the new resources (that have just been added). 
	 * The third list contains the resources that have been deleted during the update (in order to be able to mark them as "deprecated").
	 * The resources are differentiated by their ID.
	 * @param oldList list of "Resource" objects: the resources previously stored
	 * @param newList list of "Resource" objects: the updated resources (some unchanged, some deleted and some added)
	 * @return list of old "Resource" objects, list of new "Resource" objects and list of deleted "Resource" objects
	 */
	public static ArrayList[] differentiateResources(ArrayList oldList, ArrayList newList)
	{
		ArrayList[] result = new ArrayList[3];
		ArrayList deletedResources = new ArrayList();   // removed resources (to put as "deprecated")
		ArrayList newResources = new ArrayList();   // new resources
		TreeSet oldResources = new TreeSet();   // old undeleted resources
		
		// separates the resources: new/old ones (according to the ID)
		ListIterator it = newList.listIterator();
		while (it.hasNext())
		{
			Resource res = (Resource) it.next();
			
			// this is an old resource (not a new addition)
			if ((res.getId()).compareToIgnoreCase("null") != 0)
			{
				oldResources.add(res);
			}
			else   // this is a new added resource
			{
				newResources.add(res);
			}
		}
		
		// retrieve the deleted resources (by comparison of two datasets)
		ListIterator it2 = oldList.listIterator();
		while (it2.hasNext())
		{
			Resource obj = (Resource) it2.next();
			// resource not removed
			if (oldResources.contains(obj))
			{
				// resource not removed: nothing to do.
			}
			else   // resource removed: "deprecated"
			{
				deletedResources.add(obj);
			}
		}
		
		// conversion of the old resources (a TreeSet) to a Arraylist
		ArrayList oldResourcesList = new ArrayList();
		Iterator itr = oldResources.iterator();
		while (itr.hasNext())
		{
			Resource tmp = (Resource) itr.next();
			oldResourcesList.add(tmp);
		}
		
		// create the result
		result[0] = oldResourcesList;
		result[1] = newResources;
		result[2] = deletedResources;
		
		return result;
	}

}
