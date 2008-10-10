/*
 * MIRIAM Resources (Web Application)
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


package uk.ac.ebi.miriam.db;


import uk.ac.ebi.miriam.db.DbPoolConnect;
import uk.ac.ebi.miriam.web.MiriamUtilities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 * <p>Adds database connectivity features (storage, retrieving, ...) to <code>DataType</code> objects.
 * <p>
 * WARNING!
 * As we now use MyISAM as our storage engine in MySQL (instead of InnoDB in the past, more powerful but reluctant to work well with NFS), 
 * we rely on semaphores to secure the critical section when we create/update a data type. 
 * The drawback to rely on such a system is that another application (threads of the same application should be blocked) can still access the database (as it is not locked at the database level, but the application level).
 * Normally such situation will never happen, as far as only one application has access to the content of the database with writting rights.
 * <p>
 * Locking table with the MYSQL feature doesn't seem a good idea: 
 * If a thread obtains a WRITE lock on a table, only the thread holding the lock can write to the table. Other threads are blocked from reading or writing the table until the lock has been released.
 * cf. http://dev.mysql.com/doc/refman/5.0/en/lock-tables.html  
 * <p>
 * P.S.
 * Yes, I know: there is a spell mistake in the name...
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
 * @version 20080611
 */
public class DataTypeHybernate extends DataType
{
	private Logger logger = Logger.getLogger(DataTypeHybernate.class);
	private final int DATATYPE = 1;   /* constant used in the 'generateID()' method */
	private final int RESOURCE = 2;   /* constant used in the 'generateID()' method */
	private final int CURA_DATATYPE = 3;   /* constant used in the 'generateID()' method */
	private static Object lock = new Object();   /* lock used to protect concurrent access to critical section */
	private static Map<String, String> docResources = new HashMap<String, String>();
    
	
	/**
	 * Static initialisation
	 */
	static
	{
	    docResources.put("PMID", "MIR:00000015");   // PubMed
        docResources.put("DOI", "MIR:00000019");   // DOI
	}
	
	
	/**
     * <p>Default constructor (generates an empty object).  
     */
    public DataTypeHybernate()
    {
        super();
        // nothing else.
    }
    
    
    /**
	 * <p>Tests if the data type already exists (according to identifier, because the ID is not modified during an update)
     * 
     * <p>
     * Two main usages:
     * <ul>
     *      <li>'full' equals 'null': checks only if the identifier already exists in the database 
     *      <li>'full' equals 'true': does not check the identifier, but the main attributes of the <code>DataType</code> object
     * </ul>
     * 
	 * @param poolName name of the database pool used
     * @param full boolean which indicate is the user wants a full check or just if the identifier already exists
	 * @return boolean "true" if a data-type is already in the database using the same identifier
	 */
	public boolean isExisting(String poolName, boolean full)
	{
		DbPoolConnect pool;
		ResultSet result = null;
		String sql = new String();
		boolean exist = true;   // defaut: we need to proof the uniqueness
		
		// connection to the database pool
		pool = new DbPoolConnect(poolName);
		
		// test without 'newConnection()' before, let's see...
		pool.getConnection();
		
        // a full check is required
        if (full)
        {
            logger.debug("Complex test of existence.");
            
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
                            break;
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
                                break;
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
                                        break;
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
        }
        else
        {
            logger.debug("Simple test of existence.");
            
    		sql = "SELECT name FROM mir_datatype WHERE (datatype_id='" + getId() + "')";
    		result = pool.request(pool.getStatement(), sql);
    		
    		// one and only one data-type already existing
    		if (DbPoolConnect.getRowCount(result) < 1)
    		{
    			exist = false;
    		}
        }
		
		// without closing the statement, let's see...
		pool.closeConnection();
		
		return exist;
	}
	
	
	/**
	 * Performs all the SQL queries to add the data type to the database
	 * @param poolName name of the database pool used
	 * @return int result of the queries: 1 equals success, 0 equals failure
	 */
	public int storeObject(String poolName)
	{
		logger.debug("Begin of the addition of a new data type...");
		
		//private DbPoolConnect pool;
		DbPoolConnect pool;
		
		/* database connection start */
		int resultStatus = -1;
		int resultGlobal = 1;   // we consider that everything is ok by default
		String index = new String();
		//ResultSet result = null;
		
		// connection to the database pool
		pool = new DbPoolConnect(poolName);
		
		// test without 'newConnection()' before, let's see...
		pool.getConnection();
		
		// critical section to protected against concurrent access (the 'setAutoCommit' can't be used any more...)
		synchronized(lock)
		{
			// begin of the transaction
			//pool.setAutoCommit(false);   CAN'T BE USED ANY MORE, SINCE WE MOVED TO MYISAM ENGINE FOR NFS LOCKS ISSUES!!! 
			
			// generation of a new ID for the data type
			index = generateID(pool, DATATYPE);
			
			// resource main information
			PreparedStatement stmt01 = pool.getPreparedStatement("INSERT INTO mir_datatype (datatype_id, name, pattern, definition, date_creation, date_modif, obsolete, obsolete_comment, replacement) VALUES (?, ?, ?, ?, NOW(), NOW(), ?, ?, ?)");
            try
            {
                stmt01.setString(1, index);
                stmt01.setString(2, getName());
                stmt01.setString(3, getRegexp());
                stmt01.setString(4, getDefinition());
                if (isObsolete())
                {
                    stmt01.setInt(5, 1);
                }
                else
                {
                    stmt01.setInt(5, 0);
                }
                stmt01.setString(6, getObsoleteComment());
                stmt01.setString(7, getReplacedBy());
                logger.debug("SQL query: " + stmt01.toString());
                resultStatus = stmt01.executeUpdate();
                stmt01.close();
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during the processing or closing of a prepared statement!");
                logger.error("SQL Exception raised: " + e.getMessage());
                resultStatus = 0;   // failure
            }
            finally
            {
                logger.info("Result of the adding query #1 (new data-type, resource): " + resultStatus);
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
            }
            
            
			// synonym(s)
            PreparedStatement stmt02 = pool.getPreparedStatement("INSERT INTO mir_synonym (name, ptr_datatype) VALUES (?, ?)");
            for (int i=0; i<getSynonyms().size(); ++i)
            {
                try
                {
                    stmt02.setString(1, getSynonym(i));
                    stmt02.setString(2, index);
                    logger.debug("SQL query: " + stmt02.toString());
                    resultStatus = stmt02.executeUpdate();
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                    logger.info("Result of the adding query #2." + i + " (new data-type, synonyms): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
            try
            {
                stmt02.close();
            }
            catch (SQLException e)
            {
                logger.warn("An exception occured during the closing of a prepared statement!");
                logger.warn("SQL Exception raised: " + e.getMessage());
            }
			
            
			// official URL
            if (! MiriamUtilities.isEmpty(getURL()))
            {
                PreparedStatement stmt03 = pool.getPreparedStatement("INSERT INTO mir_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URL', '0', ?)");
                try
                {
                    stmt03.setString(1, getURL());
                    stmt03.setString(2, index);
                    logger.debug("SQL query: " + stmt03.toString());
                    resultStatus = stmt03.executeUpdate();
                    stmt03.close();
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing or closing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                    logger.info("Result of the adding query #3 (new data-type, URL): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
			
            
			// official URN
            if (! MiriamUtilities.isEmpty(getURN()))
            {
                PreparedStatement stmt04 = pool.getPreparedStatement("INSERT INTO mir_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URN', '0', ?)");
                try
                {
                    stmt04.setString(1, getURN());
                    stmt04.setString(2, index);
                    logger.debug("SQL query: " + stmt04.toString());
                    resultStatus = stmt04.executeUpdate();
                    stmt04.close();
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing or closing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                    logger.info("Result of the adding query #4 (new data-type, URN): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
			
            
			// obsolete URI(s)
            PreparedStatement stmt05Urn = pool.getPreparedStatement("INSERT INTO mir_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URN', '1', ?)");
            PreparedStatement stmt05Url = pool.getPreparedStatement("INSERT INTO mir_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URL', '1', ?)");
            for (int i=0; i<getDeprecatedURIs().size(); ++i)
            {
                try
                {
                    if (isDeprecatedURN(i))
                    {
                        stmt05Urn.setString(1, getDeprecatedURI(i));
                        stmt05Urn.setString(2, index);
                        logger.debug("SQL query: " + stmt05Urn.toString());
                        resultStatus = stmt05Urn.executeUpdate();
                    }
                    else
                    {
                        stmt05Url.setString(1, getDeprecatedURI(i));
                        stmt05Url.setString(2, index);
                        logger.debug("SQL query: " + stmt05Url.toString());
                        resultStatus = stmt05Url.executeUpdate();
                    }
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                    logger.info("Result of the adding query #5." + i +" (new data-type, deprecated URIs): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
            try
            {
                stmt05Urn.close();
                stmt05Url.close();
            }
            catch (SQLException e)
            {
                logger.warn("An exception occured during the closing of a prepared statement!");
                logger.warn("SQL Exception raised: " + e.getMessage());
            }
			
            
			// resources (physical locations)
            ListIterator<Resource> it = getResources().listIterator();
            int cpt = 0;
            PreparedStatement stmt06 = pool.getPreparedStatement("INSERT INTO mir_resource (resource_id, url_element_prefix, url_element_suffix, url_resource, info, institution, location, example, obsolete, ptr_datatype) VALUES (?, ?, ?, ?, ?, ?, ?, ?, '0', ?)");
            while (it.hasNext())
            {
                // generation of a new ID for the resource
                Resource temp = (Resource) it.next();
                
                try
                {
                    stmt06.setString(1, generateID(pool, RESOURCE));
                    stmt06.setString(2, temp.getUrl_prefix());
                    stmt06.setString(3, temp.getUrl_suffix());
                    stmt06.setString(4, temp.getUrl_root());
                    stmt06.setString(5, temp.getInfo());
                    stmt06.setString(6, temp.getInstitution());
                    stmt06.setString(7, temp.getLocation());
                    stmt06.setString(8, temp.getExample());
                    stmt06.setString(9, index);
                    logger.debug("SQL query: " + stmt06.toString());
                    resultStatus = stmt06.executeUpdate();
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                    logger.info("Result of the adding query #6." + cpt +" (new data-type, resources): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                    cpt++;
                }
            }
            try
            {
                stmt06.close();
            }
            catch (SQLException e)
            {
                logger.warn("An exception occured during the closing of a prepared statement!");
                logger.warn("SQL Exception raised: " + e.getMessage());
            }
			
            
			// documentation ID(s)
            PreparedStatement stmt07 = pool.getPreparedStatement("INSERT INTO mir_doc (uri, uri_type, ptr_type, ptr_datatype, ptr_resource) VALUES (?, ?, 'data', ?, NULL)");
            for (int i=0; i<getDocumentationIDs().size(); ++i)
            {
                try
                {
                    stmt07.setString(1, getDocumentationID(i));
                    stmt07.setString(2, getDocumentationIDType(i));
                    stmt07.setString(3, index);
                    logger.debug("SQL query: " + stmt07.toString());
                    resultStatus = stmt07.executeUpdate();
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                    logger.info("Result of the adding query #7." + i +" (new data-type, documentation IDs): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
            try
            {
                stmt07.close();
            }
            catch (SQLException e)
            {
                logger.warn("An exception occured during the closing of a prepared statement!");
                logger.warn("SQL Exception raised: " + e.getMessage());
            }
            
			
			// documentation URL(s)
            PreparedStatement stmt08 = pool.getPreparedStatement("INSERT INTO mir_doc (uri, uri_type, ptr_type, ptr_datatype, ptr_resource) VALUES (?, 'URL', 'data', ?, NULL)");
            for (int i=0; i<getDocumentationURLs().size(); ++i)
            {
                try
                {
                    stmt08.setString(1, getDocumentationURL(i));
                    stmt08.setString(2, index);
                    logger.debug("SQL query: " + stmt08.toString());
                    resultStatus = stmt08.executeUpdate();
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                    logger.info("Result of the adding query #8." + i +" (new data-type, documentation URLs): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
            try
            {
                stmt08.close();
            }
            catch (SQLException e)
            {
                logger.warn("An exception occured during the closing of a prepared statement!");
                logger.warn("SQL Exception raised: " + e.getMessage());
            }
            
			// end of the transaction
			//pool.commit();   CAN'T BE USED ANY MORE!
		}
		
		// without closing the statements, let's see...
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
	 * Retrieves all the information about a data type, based on its ID or his name, from the database
	 * WARNING: a preliminary test is necessary to be sure that a data type with this name exists in the database
	 * @param poolName name of the database pool used
	 * @param index internal ID of the data type you want to retrieve from the database
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
		ArrayList<String> syn = new ArrayList<String>();
		syn = (ArrayList<String>) MiriamUtilities.ArrayConvert(rs);
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
		ArrayList<String> deprecated = (ArrayList<String>) MiriamUtilities.ArrayConvert(rs);
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
		
		// search if obsolete
		sql = "SELECT obsolete FROM mir_datatype WHERE (datatype_id = '" + index + "')";
		rs = pool.request(pool.getStatement(), sql);
		int obs = MiriamUtilities.intConvert(rs);
		setObsolete(obs);
		
		// search replacement and comment (if obsolete)
		if (isObsolete())
		{
		    sql = "SELECT replacement FROM mir_datatype WHERE (datatype_id = '" + index + "')";
	        rs = pool.request(pool.getStatement(), sql);
	        String repl = MiriamUtilities.StringConvert(rs);
	        setReplacedBy(repl);
	        
	        sql = "SELECT obsolete_comment FROM mir_datatype WHERE (datatype_id = '" + index + "')";
            rs = pool.request(pool.getStatement(), sql);
            String obsoleteComment = MiriamUtilities.StringConvert(rs);
            setObsoleteComment(obsoleteComment);
		}
		
		// search the resources (physical locations)
		sql = "SELECT mr.resource_id, mr.url_element_prefix, mr.url_element_suffix, mr.url_resource, mr.info, mr.institution, mr.location, mr.example, mr.obsolete FROM mir_datatype mdt, mir_resource mr WHERE ((mr.ptr_datatype = mdt.datatype_id) AND (mdt.datatype_id = '" + index + "'))";
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
				temp.setExample(rs.getString("example"));
				if (rs.getString("obsolete").compareToIgnoreCase("0") == 0)
				{
					temp.setObsolete(false);
				}
				else
				{
					temp.setObsolete(true);
				}
				// add the resource to the data type
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
		
		// search documentation information (URLs) linked to the data type
		sql = "SELECT md.uri FROM mir_datatype mdt, mir_doc md WHERE ((md.ptr_datatype = mdt.datatype_id) AND (mdt.datatype_id = '" + index + "') AND (md.ptr_type = 'data') AND (md.uri_type = 'URL'))";
		rs = pool.request(pool.getStatement(), sql);
		ArrayList<String> docs_url = null;
		docs_url = (ArrayList<String>) MiriamUtilities.ArrayConvert(rs, true);
		setDocumentationURLs(docs_url);
		
		// search documentation information (MIRIAM URIs) linked to the data type
		sql = "SELECT md.uri, md.uri_type FROM mir_datatype mdt, mir_doc md WHERE ((md.ptr_datatype = mdt.datatype_id) AND (mdt.datatype_id = '" + index + "') AND (md.ptr_type = 'data') AND (md.uri_type != 'URL'))";
		rs = pool.request(pool.getStatement(), sql);
		try
        {
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                addDocumentationID(rs.getString("md.uri"));
                addDocumentationIDType(rs.getString("md.uri_type"));
                
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error during the transformation URIs to URLs (for display)!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
		
		// modifies the Miriam URIs into URLS using MIRIAM Database (isn't it a beautiful recursive process?) and stored them into 'docHtmlURLs'
		ArrayList<String> temp = new ArrayList<String>(docs_url);   // by default, the URLs are already ready to be put in this list
		for (int i=0; i<getDocumentationIDs().size(); ++i)
		{
			String uri = (String) getDocumentationID(i);
			// retrieves the physical location(s) corresponding to this URI
			String uriElement = MiriamUtilities.getElementPart(uri);
		    // transforms the URI into a URL
		    String[] result = transDocumentationURLs(pool, docResources.get(getDocumentationIDType(i)), uriElement);   // depends on the type: PMID, DOI, ...
		    
		    // checks the answer
		    if ((result == null) || (result.length == 0))
		    {
		    	logger.warn("A Miriam URI (" + uri + ") corresponding to a piece of documentation has no physical location");
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

		// searches for the date of creation
		sql = "SELECT date_creation FROM mir_datatype WHERE datatype_id = '" + index + "'";
		rs = pool.request(pool.getStatement(), sql);
		String dateCreationStr = MiriamUtilities.StringConvert(rs);
		Date dateCreation = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
		  dateCreation = format.parse(dateCreationStr);
		}
		catch (Exception e)
		{
			logger.error("Date conversion error (" + dateCreationStr + ")" + e);
			dateCreation = new Date(0);   // 1st January 1970
		}
		setDateCreation(dateCreation);
		
		// search for the date of last modification
		sql = "SELECT date_modif FROM mir_datatype WHERE datatype_id = '" + index + "'";
		rs = pool.request(pool.getStatement(), sql);
		String dateModifStr = MiriamUtilities.StringConvert(rs);
		Date dateModif = new Date();
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
			dateModif = format2.parse(dateModifStr);
		}
		catch (Exception e)
		{
			logger.error("Date conversion error (" + dateModifStr + ")" + e);
			dateModif = new Date(0);   // 1st January 1970
		}
		setDateModification(dateModif);

		// without closing the statement, let's see...
		pool.closeConnection();
	}
	
	
	/**
	 * Performs all the SQL queries to update the data type in the database
	 * @param poolName name of the database pool used
	 * @return int result of the queries: 1 equals success, 0 equals failure
	 */
	public int updateObject(DataTypeHybernate oldOne, String poolName)
	{
		logger.debug("Begin of the update of a data type...");
		
		//private DbPoolConnect pool;
		DbPoolConnect pool;
		
		/* database connection start */
		int resultStatus = -1;
		int resultGlobal = 1;   // we consider that everything is ok by default
		String sql = new String();
		ResultSet rs = null;
		
		// connection to the database pool
		pool = new DbPoolConnect(poolName);
		
		// test without 'newConnection()' before, let's see...
		pool.getConnection();
		
		// critical section to protected against concurrent access (the 'setAutoCommit' can't be used any more)
		synchronized(lock)
		{
			// begin of the transaction
			//pool.setAutoCommit(false);   CAN'T BE USED ANY MORE
			
			// resource main information (we just update it in order to keep the ID stable)
            PreparedStatement stmt01 = pool.getPreparedStatement("UPDATE mir_datatype SET name=?, definition=?, pattern=?, date_modif=NOW(), obsolete=?, obsolete_comment=?, replacement=? WHERE (datatype_id=?)");
            try
            {
                stmt01.setString(1, getName());
                stmt01.setString(2, getDefinition());
                stmt01.setString(3, getRegexp());
                
                if (isObsolete())
                {
                    stmt01.setInt(4, 1);
                }
                else
                {
                    stmt01.setInt(4, 0);
                }
                stmt01.setString(5, getObsoleteComment());
                stmt01.setString(6, getReplacedBy());
                stmt01.setString(7, getId());
                logger.debug("SQL query: " + stmt01.toString());
                resultStatus = stmt01.executeUpdate();
                stmt01.close();
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during the processing or closing of a prepared statement!");
                logger.error("SQL Exception raised: " + e.getMessage());
                resultStatus = 0;   // failure
            }
            finally
            {
                logger.info("Result of the update query #1 (edit data-type, resource): " + resultStatus);
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
            }
            
			
			// synonym(s): removes all the previous one(s), if necessary
			sql = "SELECT * FROM mir_synonym WHERE (ptr_datatype='" + getId() + "')";
			rs = pool.request(pool.getStatement(), sql);
			if (DbPoolConnect.getRowCount(rs) > 0)
			{
				sql = "DELETE FROM mir_synonym WHERE (ptr_datatype='" + getId() + "')";
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the update query #2 (edit data-type, remove old synonyms): " + resultStatus);
				/* DELETE returns the number of entries deleted, so it is not wise to use it to check if the update process is a success or not
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)*/
			}
			else
			{
				logger.info("No update query #2 (edit data-type, remove old synonyms): no synonym to delete.");
			}
            
			
			// synonym(s): add the new one(s)
            PreparedStatement stmt03 = pool.getPreparedStatement("INSERT INTO mir_synonym (name, ptr_datatype) VALUES (?, ?)");
            for (int i=0; i<getSynonyms().size(); ++i)
            {
                try
                {
                    stmt03.setString(1, getSynonym(i));
                    stmt03.setString(2, getId());
                    logger.debug("SQL query: " + stmt03.toString());
                    resultStatus = stmt03.executeUpdate();
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                    logger.info("Result of the update query #3." + i + " (edit data-type, synonyms): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
            try
            {
                stmt03.close();
            }
            catch (SQLException e)
            {
                logger.warn("An exception occured during the closing of a prepared statement!");
                logger.warn("SQL Exception raised: " + e.getMessage());
            }
            
			
			// URL and URN (official and deprecated): removes all the previous one(s)
			sql = "DELETE FROM mir_uri WHERE (ptr_datatype='" + getId() + "')";
			resultStatus = pool.requestUpdate(pool.getStatement(), sql);
			logger.info("Result of the update query #4 (edit data-type, remove old URIs): " + resultStatus);
			/* DELETE returns the number of entries deleted, so it is not wise to use it to check if the update process is a success or not
			resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK) */
            
			
			// official URL: add the new one
            if (! MiriamUtilities.isEmpty(getURL()))
            {
                PreparedStatement stmt05 = pool.getPreparedStatement("INSERT INTO mir_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URL', '0', ?)");
                try
                {
                    stmt05.setString(1, getURL());
                    stmt05.setString(2, getId());
                    logger.debug("SQL query: " + stmt05.toString());
                    resultStatus = stmt05.executeUpdate();
                    stmt05.close();
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing or closing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                    logger.info("Result of the adding query #5 (edit data-type, URL): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
            
			
			// official URN: add the new one
            if (! MiriamUtilities.isEmpty(getURN()))
            {
                PreparedStatement stmt06 = pool.getPreparedStatement("INSERT INTO mir_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URN', '0', ?)");
                try
                {
                    stmt06.setString(1, getURN());
                    stmt06.setString(2, getId());
                    logger.debug("SQL query: " + stmt06.toString());
                    resultStatus = stmt06.executeUpdate();
                    stmt06.close();
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing or closing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                    logger.info("Result of the adding query #6 (edit data-type, URN): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
            
            
			// obsolete URI(s): add the new one(s)
            PreparedStatement stmt07Urn = pool.getPreparedStatement("INSERT INTO mir_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URN', '1', ?)");
            PreparedStatement stmt07Url = pool.getPreparedStatement("INSERT INTO mir_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URL', '1', ?)");
            for (int i=0; i<getDeprecatedURIs().size(); ++i)
            {
                try
                {
                    if (isDeprecatedURN(i))
                    {
                        stmt07Urn.setString(1, getDeprecatedURI(i));
                        stmt07Urn.setString(2, getId());
                        logger.debug("SQL query: " + stmt07Urn.toString());
                        resultStatus = stmt07Urn.executeUpdate();
                    }
                    else
                    {
                        if (isDeprecatedURL(i))
                        {
                            stmt07Url.setString(1, getDeprecatedURI(i));
                            stmt07Url.setString(2, getId());
                            logger.debug("SQL query: " + stmt07Url.toString());
                            resultStatus = stmt07Url.executeUpdate();
                        }
                        else
                        {
                            logger.warn("This URI '" + getDeprecatedURI(i) + "' should be deprecated but is not...");
                            //TODO: check that in the logs...
                        }
                    }
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                    logger.info("Result of the adding query #7." + i +" (edit data-type, deprecated URIs): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
            try
            {
                stmt07Urn.close();
                stmt07Url.close();
            }
            catch (SQLException e)
            {
                logger.warn("An exception occured during the closing of a prepared statement!");
                logger.warn("SQL Exception raised: " + e.getMessage());
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
			
			// retrieves the deleted resources (by comparison of two datasets)
			ArrayList[] diff = new ArrayList[3];
			diff = differentiateResources(oldOne.getResources(), getResources());
			
			ArrayList<Resource> oldResources = new ArrayList<Resource>(diff[0]);
			ArrayList<Resource> newResources = new ArrayList<Resource>(diff[1]);
			ArrayList<Resource> deletedResources = new ArrayList<Resource>(diff[2]);
			
			
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
			
			// retrieves the deleted resources (by comparison of two datasets), in order to mark them as "deprecated"
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
            Iterator<Resource> itr = oldResources.iterator();
            int cpt = 0;
            PreparedStatement stmt08 = pool.getPreparedStatement("UPDATE mir_resource SET url_element_prefix=?, url_element_suffix=?, url_resource=?, info=?, institution=?, location=?, example=?, obsolete='0' WHERE (resource_id=?)");
            PreparedStatement stmt08obs = pool.getPreparedStatement("UPDATE mir_resource SET url_element_prefix=?, url_element_suffix=?, url_resource=?, info=?, institution=?, location=?, example=?, obsolete='1' WHERE (resource_id=?)");
            while (itr.hasNext())
            {
                Resource r = (Resource) itr.next();
                try
                {
                    if (r.isObsolete())   // resource obsolete
                    {
                        stmt08obs.setString(1, r.getUrl_prefix());
                        stmt08obs.setString(2, r.getUrl_suffix());
                        stmt08obs.setString(3, r.getUrl_root());
                        stmt08obs.setString(4, r.getInfo());
                        stmt08obs.setString(5, r.getInstitution());
                        stmt08obs.setString(6, r.getLocation());
                        stmt08obs.setString(7, r.getExample());
                        stmt08obs.setString(8, r.getId());
                        logger.debug("SQL query: " + stmt08obs.toString());
                        resultStatus = stmt08obs.executeUpdate();
                    }
                    else
                    {
                        stmt08.setString(1, r.getUrl_prefix());
                        stmt08.setString(2, r.getUrl_suffix());
                        stmt08.setString(3, r.getUrl_root());
                        stmt08.setString(4, r.getInfo());
                        stmt08.setString(5, r.getInstitution());
                        stmt08.setString(6, r.getLocation());
                        stmt08.setString(7, r.getExample());
                        stmt08.setString(8, r.getId());
                        logger.debug("SQL query: " + stmt08.toString());
                        resultStatus = stmt08.executeUpdate();
                    }
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                    logger.info("Result of the update query #8. " + cpt + " (edit data-type, update old resources): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                    cpt++;
                }
            }
            try
            {
                stmt08.close();
                stmt08obs.close();
            }
            catch (SQLException e)
            {
                logger.warn("An exception occured during the closing of a prepared statement!");
                logger.warn("SQL Exception raised: " + e.getMessage());
            }
            
			
			// adds the new resources
            ListIterator it3 = newResources.listIterator();
            cpt = 0;
            // a new resource can't be 'obsolete'
            PreparedStatement stmt09 = pool.getPreparedStatement("INSERT INTO mir_resource (resource_id, url_element_prefix, url_element_suffix, url_resource, info, institution, location, example, obsolete, ptr_datatype) VALUES (?, ?, ?, ?, ?, ?, ?, ?, '0', '" + getId() + "')");
            while (it3.hasNext())
            {
                Resource temp = (Resource) it3.next();
                try
                {
                    stmt09.setString(1, generateID(pool, RESOURCE));
                    stmt09.setString(2, temp.getUrl_prefix());
                    stmt09.setString(3, temp.getUrl_suffix());
                    stmt09.setString(4, temp.getUrl_root());
                    stmt09.setString(5, temp.getInfo());
                    stmt09.setString(6, temp.getInstitution());
                    stmt09.setString(7, temp.getLocation());
                    stmt09.setString(8, temp.getExample());
                    logger.debug("SQL query: " + stmt09.toString());
                    resultStatus = stmt09.executeUpdate();
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                    logger.info("Result of the adding query #9." + cpt + " (edit data-type, add new resources): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
            try
            {
                stmt09.close();
            }
            catch (SQLException e)
            {
                logger.warn("An exception occured during the closing of a prepared statement!");
                logger.warn("SQL Exception raised: " + e.getMessage());
            }
			
            
			// indicates as 'obsolete' the resources "removed"
			ListIterator it4 = deletedResources.listIterator();
			cpt = 0;
			while (it4.hasNext())
			{
				Resource temp = (Resource) it4.next();
				sql = "UPDATE mir_resource SET obsolete='1' WHERE (resource_id='" + temp.getId() + "')";
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the update query #10." + cpt + " (edit data-type, update obsolete resources): " + resultStatus);
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
			}
			
            
			// documentation IDs and URLs: removes all the previous one(s), if necessary (means if any stored)
			sql = "SELECT * FROM mir_doc WHERE (ptr_datatype='" + getId() + "')";
			rs = pool.request(pool.getStatement(), sql);
			if (DbPoolConnect.getRowCount(rs) > 0)
			{
				sql = "DELETE FROM mir_doc WHERE (ptr_datatype='" + getId() + "')";
				resultStatus = pool.requestUpdate(pool.getStatement(), sql);
				logger.info("Result of the update query #11 (edit data-type, remove old documentation IDs and URIs): " + resultStatus);
				/* DELETE returns the number of entries deleted, so it is not wise to use it to check if the update process is a success or not
				resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
				*/
			}
			else
			{
				logger.info("No update query #10 (edit data-type, remove old documentation IDs and URIs): no documentation to delete.");
			}
			
            
			// documentation ID(s): add the new (or old ones that have been deleted in the previous step) one(s)
            PreparedStatement stmt12 = pool.getPreparedStatement("INSERT INTO mir_doc (uri, uri_type, ptr_type, ptr_datatype, ptr_resource) VALUES (?, ?, 'data', '" + getId() + "', NULL)");
            
            for (int i=0; i<getDocumentationIDs().size(); ++i)
            {
                try
                {
                    stmt12.setString(1, getDocumentationID(i));
                    stmt12.setString(2, getDocumentationIDType(i));
                    logger.debug("SQL query: " + stmt12.toString());
                    resultStatus = stmt12.executeUpdate();
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing or closing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                    logger.info("Result of the adding query #12." + i +" (edit data-type, documentation IDs): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
            try
            {
                stmt12.close();
            }
            catch (SQLException e)
            {
                logger.warn("An exception occured during the closing of a prepared statement!");
                logger.warn("SQL Exception raised: " + e.getMessage());
            }
            
            
			// documentation URL(s)
            PreparedStatement stmt13 = pool.getPreparedStatement("INSERT INTO mir_doc (uri, uri_type, ptr_type, ptr_datatype, ptr_resource) VALUES (?, 'URL', 'data', '" + getId() + "', NULL)");
            for (int i=0; i<getDocumentationURLs().size(); ++i)
            {
                try
                {
                    stmt13.setString(1, getDocumentationURL(i));
                    logger.debug("SQL query: " + stmt13.toString());
                    resultStatus = stmt13.executeUpdate();
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing or closing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                    logger.info("Result of the adding query #13." + i +" (edit data-type, documentation URLs): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
            try
            {
                stmt13.close();
            }
            catch (SQLException e)
            {
                logger.warn("An exception occured during the closing of a prepared statement!");
                logger.warn("SQL Exception raised: " + e.getMessage());
            }
            
			
			// end of the transaction
			//pool.commit();   CAN'T BE USED ANY MORE
		}
		
		// without closing the statement, let's see...
		pool.closeConnection();
		
		logger.debug("... end of the update of a data type (GLOBAL RESULT: " + resultGlobal + ")");
		
		return resultGlobal;
	}
	
	
	/*
	 * Generates an ID for a new data type or for a new data type or a resource (physical location).
	 * Warning: the use of this method followed by a creation of a new entity needs to be protected by a 
	 *          semaphore/lock to prevent any update of the database in the meantime.
	 * 
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
		if (type == CURA_DATATYPE)
		{
		    id = "MIR:009";
		    sql = "SELECT datatype_id FROM cura_datatype";
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
								logger.error("Generation of the ID of the new data type impossible: size overflow!");
								id = "MIR:00000001";   // ID already existing: should generate a SQL error if tried to use like that...
							}
							if (type == RESOURCE)
							{
								logger.error("Generation of the ID of the new resource impossible: size overflow!");
								id = "MIR:00100001";   // ID already existing: should generate a SQL error if tried to use like that...								
							}
							if (type == CURA_DATATYPE)
							{
							    logger.error("Generation of the ID of the new temporary data type impossible: size overflow!");
                                id = "MIR:00900001";   // ID already existing: should generate a SQL error if tried to use like that...
							}
						} 
					}
				} 
			}
		}
		
		if (type == DATATYPE)
		{
			logger.info("ID generated for a new data type: " + id);
		}
		if (type == RESOURCE)
		{
			logger.info("ID generated for a new resource: " + id);
		}
		
		//pool.closeConnection();
		
		return id;
	}
	
	
	/*
	 * Transforms URIs into physical locationS (URLs) (used, for example, to get the links towards the documentation)
	 * @param database connection pool
	 * @param URI of a data-type (example: "http://www.uniprot.org/")
	 * @param URI of the element (example: "P47757")
	 * @return physical locationS of the element
	 */
	private String[] transDocumentationURLs(DbPoolConnect pool, String uriDataType, String uriElement)
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
		
	    //pool.closeConnection();
	    
		return result;
	}
    
		
	/**
	 * Returns three lists of <code>Resource</code> objects. 
	 * The first list contains the old resources (that have not been added or deleted). 
	 * The second list contains the new resources (that have just been added). 
	 * The third list contains the resources that have been deleted during the update (in order to be able to mark them as "deprecated").
	 * The resources are differentiated by their ID.
	 * @param oldList list of <code>Resource</code> objects: the resources previously stored
	 * @param newList list of <code>Resource</code> objects: the updated resources (some unchanged, some deleted and some added)
	 * @return list of old <code>Resource</code> objects, list of new <code>Resource</code> objects and list of deleted <code>Resource</code> objects
	 */
	public static ArrayList[] differentiateResources(ArrayList<Resource> oldList, ArrayList<Resource> newList)
	{
        
        // TODO: test this method!!!
        
/* OLD CODE: bug during the detection of deleted resources
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
                System.out.println("OLD RESOURCE: " + res.getId() + " (" + res.getUrl_prefix() + ")");   // DEBUG
				oldResources.add(res);
			}
			else   // this is a new added resource
			{
                System.out.println("NEW RESOURCE: " + res.getId() + " (" + res.getUrl_prefix() + ")");   // DEBUG
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
                System.out.println("DELETED RESOURCE: " + obj.getId() + " (" + obj.getUrl_prefix() + ")");   // TODO
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
*/
        ArrayList[] result = new ArrayList[3];
        ArrayList<Resource> deletedResources = new ArrayList<Resource>();   // removed resources (to put as "deprecated")
        ArrayList<Resource> newResources = new ArrayList<Resource>();   // new resources
        ArrayList<Resource> oldResources = new ArrayList<Resource>();   // old undeleted resources
        
        // separates the resources: new/old ones (according to the ID)
        ListIterator<Resource> it = newList.listIterator();
        while (it.hasNext())
        {
            Resource res = (Resource) it.next();
            
            // this is an old resource (not a new addition)
            if (! (res.getId()).equalsIgnoreCase("null"))
            {
                oldResources.add(res);
            }
            else   // this is a new added resource
            {
                newResources.add(res);
            }
        }
        
        // retrieves the deleted resources (by comparison of two datasets)
        ListIterator<Resource> it2 = oldList.listIterator();
        while (it2.hasNext())
        {
            Resource obj = (Resource) it2.next();
            ListIterator<Resource> temp = newList.listIterator();
            Resource tmp = null;
            boolean find = false;
            while (temp.hasNext())
            {
                tmp = (Resource) temp.next();
                if (tmp.equals(obj))
                {
                    find = true;
                    break;
                }
            }
            
            // resource not removed
            if (find)
            {
                // resource not removed: nothing to do.
            }
            else   // resource removed: "deprecated"
            {
                deletedResources.add(obj);
            }
        }
		
		// create the result
		result[0] = oldResources;
		result[1] = newResources;
		result[2] = deletedResources;
		
		return result;
	}
    
    
    /**
     * Finds differences between two <code>DataTypeHybernate</code>.
     * 
     * <p>
     * In order to keep things clean, 'this' corresponds to the new data and 'data' to the old one.
     * 
     * @param data the other data-type to compare this one to (the old one)
     * @return all the differences founded
     */
    public String diff(DataTypeHybernate data)
    {
        String diff = new String();
        
        // identifier
        if (! data.getId().equals(this.getId()))
        {
            diff += "Id:\n";
            diff += "\t< " + data.getId() + "\n";
            diff += "\t> " + this.getId() + "\n\n";
        }
        
        // name
        if (! data.getName().equals(this.getName()))
        {
            diff += "Name:\n";
            diff += "\t< " + data.getName() + "\n";
            diff += "\t> " + this.getName() + "\n\n";
        }
        
        // synonyms
        diff += "Synonyms:\n";
        for (int i=0; i<this.getSynonyms().size(); ++i)
        {
            if (data.getSynonyms().contains(this.getSynonym(i)))
            {
                // nothing: no change have been done on this synonym
            }
            else
            {
                // a sysnonym has been added
                diff += "\t> " + this.getSynonym(i) + "\n";
            }
        }
        for (int i=0; i<data.getSynonyms().size(); ++i)
        {
            if (this.getSynonyms().contains(data.getSynonym(i)))
            {
                // nothing: no change have been done on this synonym
            }
            else
            {
                // a sysnonym has been removed
                diff += "\t< " + data.getSynonym(i) + "\n";
            }
        }
        
        // URL
        if (! data.getURL().equals(this.getURL()))
        {
            diff += "Official URL:\n";
            diff += "\t< " + data.getURL() + "\n";
            diff += "\t> " + this.getURL() + "\n\n";
        }
        
        // URN
        if (! data.getURN().equals(this.getURN()))
        {
            diff += "Official URN:\n";
            diff += "\t< " + data.getURN() + "\n";
            diff += "\t> " + this.getURN() + "\n\n";
        }
        
        // deprecatedURIs
        diff += "Deprecated URIs:\n";
        for (int i=0; i<this.getDeprecatedURIs().size(); ++i)
        {
            if (data.getDeprecatedURIs().contains(this.getDeprecatedURI(i)))
            {
                // nothing: no change have been done on this deprecated URI
            }
            else
            {
                // a deprecated URI has been added
                diff += "\t> " + this.getDeprecatedURI(i) + "\n";
            }
        }
        for (int i=0; i<data.getDeprecatedURIs().size(); ++i)
        {
            if (this.getDeprecatedURIs().contains(data.getDeprecatedURI(i)))
            {
                // nothing: no change have been done on this deprecated URI
            }
            else
            {
                // a deprecated URI has been removed
                diff += "\t< " + data.getDeprecatedURI(i) + "\n";
            }
        }
        
        // definition
        if (! data.getDefinition().equals(this.getDefinition()))
        {
            diff += "Definition:\n";
            diff += "\t< " + data.getDefinition() + "\n";
            diff += "\t> " + this.getDefinition() + "\n\n";
        }
        
        // regexp
        if (! data.getRegexp().equals(this.getRegexp()))
        {
            diff += "Regular expression:\n";
            diff += "\t< " + data.getRegexp() + "\n";
            diff += "\t> " + this.getRegexp() + "\n\n";
        }
        
        // resources (two cases: identifiers available or not)
        diff += "Resources:\n";
        
        // checks if some resources have been added or have been modified: (data.getResources().contains(this.getResource(i)))
        for (int i=0; i<this.getResources().size(); ++i)
        {
            boolean find1 = false;
            boolean modif1 = false;
            ListIterator<Resource> it1 = data.getResources().listIterator();
            Resource tmp1 = null;
            while (it1.hasNext())
            {
                tmp1 = (Resource) it1.next();
                
                boolean test = false;
                //if (identifierAvailable)
                if ((! (tmp1.getId()).equalsIgnoreCase("null")) && (! this.getResource(i).getId().equalsIgnoreCase("null")))
                {
                    test = tmp1.equals(this.getResource(i));
                }
                else
                {
                    test = tmp1.couldBeSimilar(this.getResource(i));
                }
                
                if (test)
                {
                    find1 = true;
                    
                    if (! tmp1.hasSameContent(this.getResource(i)))
                    {
                        modif1 = true;
                    }
                    
                    break;
                }
            }
            
            if (find1)
            {
                // some changes have been done on this resource
                if (modif1)
                {
                    diff += "\t<< " + tmp1 + "\n";   // ld version of the resource
                    diff += "\t>> " + this.getResource(i) + "\n";   // new version of the resource
                }
            }
            else
            {
                // a resource has been added
                diff += "\t> " + this.getResource(i) + "\n";
            }
        }
        
        // checks if some resources have been deleted: (this.getResources().contains(data.getResource(i)))
        for (int i=0; i<data.getResources().size(); ++i)
        {
            boolean find2 = false;
            ListIterator<Resource> it2 = this.getResources().listIterator();
            Resource tmp2 = null;
            
            while (it2.hasNext())
            {
                tmp2 = (Resource) it2.next();
            
                boolean test = false;
                //if (identifierAvailable)
                if ((! (tmp2.getId()).equalsIgnoreCase("null")) && (! data.getResource(i).getId().equalsIgnoreCase("null")))
                {
                    test = tmp2.equals(data.getResource(i));
                }
                else
                {
                    test = tmp2.couldBeSimilar(data.getResource(i));
                }
                
                if (test)
                {
                    find2 = true;
                    break;
                }
            }
            
            if (! find2)
            {
                // a resource has been removed
                diff += "\t< " + data.getResource(i) + "\n";
            }
        }
        
        
        // documentationURLs
        diff += "URLs towards pieces of documentation:\n";
        for (int i=0; i<this.getDocumentationURLs().size(); ++i)
        {
            if (data.getDocumentationURLs().contains(this.getDocumentationURL(i)))
            {
                // nothing: no change have been done on this URL towards a piece of documentation
            }
            else
            {
                // an URL towards a piece of documentation has been added
                diff += "\t> " + this.getDocumentationURL(i) + "\n";
            }
        }
        for (int i=0; i<data.getDocumentationURLs().size(); ++i)
        {
            if (this.getDocumentationURLs().contains(data.getDocumentationURL(i)))
            {
                // nothing: no change have been done on this URL towards a piece of documentation
            }
            else
            {
                // an URL towards a piece of documentation has been removed
                diff += "\t< " + data.getDocumentationURL(i) + "\n";
            }
        }
        
        // documentationIDs
        diff += "IDs of pieces of documentation:\n";
        for (int i=0; i<this.getDocumentationIDs().size(); ++i)
        {
            if (data.getDocumentationIDs().contains(this.getDocumentationID(i)))
            {
                // nothing: no change have been done on this ID of a piece of documentation
            }
            else
            {
                // an ID of a piece of documentation has been added
                diff += "\t> " + this.getDocumentationID(i) + "\n";
            }
        }
        for (int i=0; i<data.getDocumentationIDs().size(); ++i)
        {
            if (this.getDocumentationIDs().contains(data.getDocumentationID(i)))
            {
                // nothing: no change have been done on this ID of a piece of documentation
            }
            else
            {
                // an ID of a piece of documentation has been removed
                diff += "\t< " + data.getDocumentationID(i) + "\n";
            }
        }
        
        return diff;
    }
}
