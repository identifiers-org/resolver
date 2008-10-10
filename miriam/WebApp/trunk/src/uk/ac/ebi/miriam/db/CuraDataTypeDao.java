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


import uk.ac.ebi.miriam.web.MiriamUtilities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.apache.log4j.Logger;


/**
 * <p>Performs all the persistence features (link with the database) of a <code>CuraDataType</code> object, such as "retrieve" or "save".
 * <p>It is necessary to use the method "setParameters()" before any of the other methods available!
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
 * @version 20080807
 */
public class CuraDataTypeDao
{
    private Logger logger = Logger.getLogger(CuraDataTypeDao.class);
    private String poolName;
    private static Object lock = new Object();   /* lock used to protect concurrent access to critical section */
    private final int DATATYPE = 1;   /* constant used in the 'generateID()' method */
    private final int RESOURCE = 2;   /* constant used in the 'generateID()' method */
    private final int CURA_DATATYPE = 3;   /* constant used in the 'generateID()' method */
    private final String SUBMITTED = "Submitted";
    private final String CURATION = "Curation";
    private final String PUBLISHED = "Canceled";
    private final String PENDING = "Pending";
    private final String CANCELED = "Published";
    private final String ALL = "All";
    
    /**
     * Default Constructor.
     */
    public CuraDataTypeDao(String poolName)
    {
        this.poolName = poolName;
    }
    
    
    public int save(CuraDataType dataType)
    {
        
        // TODO: complete that!
        
        return 13;
    }
    
    /**
     * Retrieves all the information about a data type, based on its ID.
     * @param internal identifier of the data type
     * @return can be null
     */
    public CuraDataType retrieve(String id)
    {
        DbPoolConnect pool;
        ResultSet rs = null;
        CuraDataType dataType = null;
        boolean exist = false;
        
        // connection to the database pool
        pool = new DbPoolConnect(poolName);
        
        // test without 'newConnection()' before, let's see...
        pool.getConnection();
        
        // retrieves basic info about the data type
        PreparedStatement stmt01 = pool.getPreparedStatement("SELECT datatype_id, name, pattern, definition, date_creation, date_modif, obsolete, obsolete_comment, replacement FROM cura_datatype WHERE (datatype_id=?)");
        try
        {
            stmt01.setString(1, id);
            logger.debug("SQL query: " + stmt01.toString());
            rs = stmt01.executeQuery();
            
            int nb = MiriamUtilities.getRowCount(rs);
            if (nb == 1)
            {
                exist = true;
                
                dataType = new CuraDataType();
                dataType.setId(rs.getString("datatype_id"));
                dataType.setName(rs.getString("name"));
                dataType.setRegexp(rs.getString("pattern"));
                dataType.setDefinition(rs.getString("definition"));
                dataType.setDateCreation(rs.getTimestamp("date_creation"));
                dataType.setDateModification(rs.getTimestamp("date_modif"));
                dataType.setObsolete(rs.getBoolean("obsolete"));
                dataType.setObsoleteComment(rs.getString("obsolete_comment"));
                dataType.setReplacedBy(rs.getString("replacement"));
            }
            else
            {
                if (nb > 1)
                {
                    logger.warn("The data type '" + id + "' in the curation pipeline is not unique!");
                }
                exist = false;
            }
            
            stmt01.close();
        }
        catch (SQLException e)
        {
            logger.error("An exception occured during the processing or closing of a prepared statement (retrieving data type general info from curation pipeline)!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        
        // retrieves the other information attached to the data type (if it exists)
        if (exist)
        {
            // retrieves the synonyms (if any)
            PreparedStatement stmt02 = pool.getPreparedStatement("SELECT ms.name FROM cura_datatype md, cura_synonym ms WHERE ((ms.ptr_datatype = md.datatype_id) AND (md.datatype_id=?))");
            try
            {
                stmt02.setString(1, id);
                logger.debug("SQL query: " + stmt02.toString());
                rs = stmt02.executeQuery();
                ArrayList<String> syn = new ArrayList<String>();
                syn = (ArrayList<String>) MiriamUtilities.ArrayConvert(rs);
                dataType.setSynonyms(syn);
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during the processing or closing of a prepared statement (retrieving data type synonyms from curation pipeline)!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            
            // retrieves the official URL (none normally)
            String sql1 = "SELECT mu.uri FROM cura_datatype md, cura_uri mu WHERE ((mu.ptr_datatype = md.datatype_id) AND (md.datatype_id = '" + id + "') AND (mu.uri_type = 'URL') AND (mu.deprecated = '0'))";
            rs = pool.request(pool.getStatement(), sql1);
            String url = MiriamUtilities.StringConvert(rs);
            dataType.setURL(url);
            
            // retrieves the official URN
            String sql2 = "SELECT mu.uri FROM cura_datatype md, cura_uri mu WHERE ((mu.ptr_datatype = md.datatype_id) AND (md.datatype_id = '" + id + "') AND (mu.uri_type = 'URN') AND (mu.deprecated = '0'))";
            rs = pool.request(pool.getStatement(), sql2);
            String urn = MiriamUtilities.StringConvert(rs);
            dataType.setURN(urn);
            
            // retrieves the deprecated URIs
            String sql3 = "SELECT mu.uri FROM cura_datatype md, cura_uri mu WHERE ((mu.ptr_datatype = md.datatype_id) AND (md.datatype_id = '" + id + "') AND (mu.deprecated = '1'))";
            rs = pool.request(pool.getStatement(), sql3);
            ArrayList<String> deprecated = (ArrayList<String>) MiriamUtilities.ArrayConvert(rs);
            dataType.setDeprecatedURIs(deprecated);
            
            // retrieves the resources (physical locations)
            String sql4 = "SELECT mr.resource_id, mr.url_element_prefix, mr.url_element_suffix, mr.url_resource, mr.info, mr.institution, mr.location, mr.example, mr.obsolete FROM cura_datatype mdt, cura_resource mr WHERE ((mr.ptr_datatype = mdt.datatype_id) AND (mdt.datatype_id = '" + id + "'))";
            rs = pool.request(pool.getStatement(), sql4);
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
                    dataType.addResource(temp);
                    // next resource (if it exists)
                    notEmpty = rs.next();
                }
            }               
            catch (SQLException e)
            {
                logger.error("Error while searching the resources (in curation pipeline)!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            
            // retrieves documentation information (URLs) linked to the data type
            String sql5 = "SELECT md.uri FROM cura_datatype mdt, cura_doc md WHERE ((md.ptr_datatype = mdt.datatype_id) AND (mdt.datatype_id = '" + id + "') AND (md.ptr_type = 'data') AND (md.uri_type = 'URL'))";
            rs = pool.request(pool.getStatement(), sql5);
            ArrayList<String> docs_url = null;
            docs_url = (ArrayList<String>) MiriamUtilities.ArrayConvert(rs, true);
            dataType.setDocumentationURLs(docs_url);
            
            // search documentation information (MIRIAM URIs) linked to the data type
            String sql6 = "SELECT md.uri, md.uri_type FROM cura_datatype mdt, cura_doc md WHERE ((md.ptr_datatype = mdt.datatype_id) AND (mdt.datatype_id = '" + id + "') AND (md.ptr_type = 'data') AND (md.uri_type != 'URL'))";
            rs = pool.request(pool.getStatement(), sql6);
            try
            {
                boolean notEmpty = rs.next();
                while (notEmpty)
                {
                    dataType.addDocumentationID(rs.getString("md.uri"));
                    dataType.addDocumentationIDType(rs.getString("md.uri_type"));
                    
                    notEmpty = rs.next();
                }
            }
            catch (SQLException e)
            {
                logger.error("Error during the transformation URIs to URLs (for display in curation pipeline)!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            
            // retrieves some information for curation purposes
            String sql7 = "SELECT comment, state, sub_info, public_id FROM cura_material WHERE (ptr_datatype='" + id + "')";
            rs = pool.request(pool.getStatement(), sql7);
            try
            {
                if (rs.first())
                {
                    dataType.setComment(rs.getString("comment"));
                    dataType.setState(rs.getString("state"));
                    dataType.setSubInfo(rs.getString("sub_info"));
                    dataType.setPublicId(rs.getString("public_id"));
                }
            }               
            catch (SQLException e)
            {
                logger.error("Error while searching the curation specific information!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
        }
        
        // without closing the statements, let's see...
        pool.closeConnection();
        
        return dataType;
    }
    
    
    /**
     * Retrieves all the data types in the curation pipeline.
     * @return list of data types
     */
    public List<SimpleCuraDataType> retrieveAll()
    {
        List<SimpleCuraDataType> result = new ArrayList<SimpleCuraDataType>();
        DbPoolConnect pool;
        ResultSet rs = null;
        
        // connection to the database pool
        pool = new DbPoolConnect(poolName);
        
        // test without 'newConnection()' before, let's see...
        pool.getConnection();
        
        String sql = "SELECT d.name, d.definition, d.datatype_id, m.state, d.date_creation, m.public_id FROM cura_datatype d, cura_material m WHERE (d.datatype_id = m.ptr_datatype) ORDER BY d.date_creation";
        rs = pool.request(pool.getStatement(), sql);
        boolean notEmpty;
        try
        {
            notEmpty = rs.next();
            while (notEmpty)
            {
                SimpleCuraDataType temp = new SimpleCuraDataType();
                temp.setId(rs.getString("datatype_id"));
                temp.setName(rs.getString("name"));
                temp.setDefinition(rs.getString("definition"));
                temp.setState(rs.getString("state"));
                temp.setSubmissionDate(rs.getTimestamp("date_creation"));
                temp.setPublicId(rs.getString("public_id"));
                
                // adds this simple data type to the list
                result.add(temp);
                
                // next data type (if it exists)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while searching the data types in the curated pipeline!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        
        // without closing the statements, let's see...
        pool.closeConnection();
        
        return result;
    }
    
    
    /**
     * Tests if a data type exists in the curation pipeline, based on its main attributes.
     * @param data type
     * @return
     */
    public boolean exists(CuraDataType data)
    {
        boolean state = true;   // defaut: we need to proof the uniqueness
        ResultSet result = null;
        DbPoolConnect pool;
        String sql;
        
        // connection to the database pool
        pool = new DbPoolConnect(poolName);
        
        // test without 'newConnection()' before, let's see...
        pool.getConnection();
        
        // query to test the uniqueness of the name (part one)
        sql = "SELECT name FROM cura_datatype WHERE (name='" + data.getName() + "')";
        result = pool.request(pool.getStatement(), sql);
        
        // no name of existing data types is equivalent of the name of the data type
        if (DbPoolConnect.getRowCount(result) == 0)
        {
            // query to test the uniqueness of the name (part two)
            sql = "SELECT name FROM cura_synonym WHERE (name='" + data.getName() + "')";
            result = pool.request(pool.getStatement(), sql);
            
            // no synonym of existing data types is equivalent to the name of the new data type  
            if (DbPoolConnect.getRowCount(result) == 0)
            {
                boolean tempExist1 = false;
                
                // queries to test the uniqueness of the synonyms (part one)
                for (int i=0; i<data.getSynonyms().size(); ++i)
                {
                    sql = "SELECT name FROM cura_datatype WHERE (name='" + data.getSynonym(i) + "')";
                    result = pool.request(pool.getStatement(), sql);
                    
                    // one of the synonyms of the new data type is equivalent to the name of an existing data type
                    if (DbPoolConnect.getRowCount(result) != 0)
                    {
                        tempExist1 = true;
                        break;
                    }
                }
                
                // no name of existing data types is equivalent to the synonyms of the new data type
                if (! tempExist1)
                {
                    boolean tempExist2 = false;
                    
                    // queries to test the uniqueness of the synonyms (part two)
                    for (int i=0; i<data.getSynonyms().size(); ++i)
                    {
                        sql = "SELECT name FROM cura_synonym WHERE (name='" + data.getSynonym(i) + "')";
                        result = pool.request(pool.getStatement(), sql);
                        
                        if (DbPoolConnect.getRowCount(result) != 0)
                        {
                            tempExist2 = true;
                            break;
                        }
                    }
                    
                    // no synonym of existing data types is equivalent to a synonym of the new data type
                    if (! tempExist2)
                    {
                        // query to test the uniqueness of the URIs
                        sql = "SELECT uri FROM cura_uri WHERE ((uri='" + data.getURL() + "') OR (uri='" + data.getURN()  + "'))";
                        result = pool.request(pool.getStatement(), sql);
                        
                        // no URI of existing data types is equivalent to one of the URIs of the new data type 
                        if (DbPoolConnect.getRowCount(result) == 0)
                        {
                            boolean tempExist3 = false;
                            
                            // queries to test the uniqueness of the deprecated URI(s)
                            for (int i=0; i<data.getDeprecatedURIs().size(); ++i)
                            {
                                sql = "SELECT uri FROM cura_uri WHERE (uri='" + data.getDeprecatedURI(i) + "')";
                                result = pool.request(pool.getStatement(), sql);
                                
                                if (DbPoolConnect.getRowCount(result) != 0)
                                {
                                    tempExist3 = true;
                                    break;
                                }
                            }
                            
                            // no URI of existing data types is equivalent to one of the deprecated URI(s) of the new data type
                            if (! tempExist3)
                            {
                                state = false;  // and finally we have our proof of uniqueness
                            }
                        }
                    }
                }
            }
        }
        
        // without closing the statements, let's see...
        pool.closeConnection();
        
        return state;
    }


    /**
     * Performs all the SQL queries to update a data type in the curation pipeline.
     * @param data new data type
     * @param oldOne old data type
     * @return result of the queries: 1 equals success, 0 equals failure
     */
    public int update(CuraDataType data, CuraDataType oldOne)
    {
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
            PreparedStatement stmt01 = pool.getPreparedStatement("UPDATE cura_datatype SET name=?, definition=?, pattern=?, date_modif=NOW(), obsolete=?, obsolete_comment=?, replacement=? WHERE (datatype_id=?)");
            try
            {
                stmt01.setString(1, data.getName());
                stmt01.setString(2, data.getDefinition());
                stmt01.setString(3, data.getRegexp());
                if (data.isObsolete())
                {
                    stmt01.setInt(4, 1);
                }
                else
                {
                    stmt01.setInt(4, 0);
                }
                stmt01.setString(5, data.getObsoleteComment());
                stmt01.setString(6, data.getReplacedBy());
                stmt01.setString(7, data.getId());
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
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                logger.info("Result of the update query #1 (edit data type curation, resource): " + resultStatus + " (" + resultGlobal + ")");
            }
            
            
            // synonym(s): removes all the previous one(s), if necessary
            sql = "SELECT * FROM cura_synonym WHERE (ptr_datatype='" + data.getId() + "')";
            rs = pool.request(pool.getStatement(), sql);
            if (DbPoolConnect.getRowCount(rs) > 0)
            {
                sql = "DELETE FROM cura_synonym WHERE (ptr_datatype='" + data.getId() + "')";
                resultStatus = pool.requestUpdate(pool.getStatement(), sql);
                logger.info("Result of the update query #2 (edit data type curation, remove old synonyms): " + resultStatus + "*");
                /* DELETE returns the number of entries deleted, so it is not wise to use it to check if the update process is a success or not
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)*/
            }
            else
            {
                logger.info("No update query #2 (edit data type curation, remove old synonyms): no synonym to delete.");
            }
            
            
            // synonym(s): add the new one(s)
            PreparedStatement stmt03 = pool.getPreparedStatement("INSERT INTO cura_synonym (name, ptr_datatype) VALUES (?, ?)");
            for (int i=0; i<data.getSynonyms().size(); ++i)
            {
                try
                {
                    stmt03.setString(1, data.getSynonym(i));
                    stmt03.setString(2, data.getId());
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
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                    logger.info("Result of the update query #3." + i + " (edit data type curation, synonyms): " + resultStatus + " (" + resultGlobal + ")");
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
            sql = "DELETE FROM cura_uri WHERE (ptr_datatype='" + data.getId() + "')";
            resultStatus = pool.requestUpdate(pool.getStatement(), sql);
            logger.info("Result of the update query #4 (edit data type curation, remove old URIs): " + resultStatus + "*");
            /* DELETE returns the number of entries deleted, so it is not wise to use it to check if the update process is a success or not
            resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK) */
            
            
            // official URL: add the new one
            if (! MiriamUtilities.isEmpty(data.getURL()))
            {
                PreparedStatement stmt05 = pool.getPreparedStatement("INSERT INTO cura_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URL', '0', ?)");
                try
                {
                    stmt05.setString(1, data.getURL());
                    stmt05.setString(2, data.getId());
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
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                    logger.info("Result of the adding query #5 (edit data type curation, URL): " + resultStatus + " (" + resultGlobal + ")");
                }
            }
            
            
            // official URN: add the new one
            if (! MiriamUtilities.isEmpty(data.getURN()))
            {
                PreparedStatement stmt06 = pool.getPreparedStatement("INSERT INTO cura_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URN', '0', ?)");
                try
                {
                    stmt06.setString(1, data.getURN());
                    stmt06.setString(2, data.getId());
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
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                    logger.info("Result of the adding query #6 (edit data type curation, URN): " + resultStatus + " (" + resultGlobal + ")");
                }
            }
            
            
            // obsolete URI(s): add the new one(s)
            PreparedStatement stmt07Urn = pool.getPreparedStatement("INSERT INTO cura_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URN', '1', ?)");
            PreparedStatement stmt07Url = pool.getPreparedStatement("INSERT INTO cura_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URL', '1', ?)");
            for (int i=0; i<data.getDeprecatedURIs().size(); ++i)
            {
                try
                {
                    if (data.isDeprecatedURN(i))
                    {
                        stmt07Urn.setString(1, data.getDeprecatedURI(i));
                        stmt07Urn.setString(2, data.getId());
                        logger.debug("SQL query: " + stmt07Urn.toString());
                        resultStatus = stmt07Urn.executeUpdate();
                    }
                    else
                    {
                        if (data.isDeprecatedURL(i))
                        {
                            stmt07Url.setString(1, data.getDeprecatedURI(i));
                            stmt07Url.setString(2, data.getId());
                            logger.debug("SQL query: " + stmt07Url.toString());
                            resultStatus = stmt07Url.executeUpdate();
                        }
                        else
                        {
                            logger.warn("This URI '" + data.getDeprecatedURI(i) + "' should be deprecated but is not...");
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
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                    logger.info("Result of the adding query #7." + i +" (edit data type curation, deprecated URIs): " + resultStatus + " (" + resultGlobal + ")");
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
            
            // retrieves the deleted resources (by comparison of two datasets)
            ArrayList[] diff = new ArrayList[3];
            diff = differentiateResources(oldOne.getResources(), data.getResources());
            
            ArrayList<Resource> oldResources = new ArrayList<Resource>(diff[0]);
            ArrayList<Resource> newResources = new ArrayList<Resource>(diff[1]);
            ArrayList<Resource> deletedResources = new ArrayList<Resource>(diff[2]);
            
            // modifies the resources already existing
            Iterator<Resource> itr = oldResources.iterator();
            int cpt = 0;
            PreparedStatement stmt08 = pool.getPreparedStatement("UPDATE cura_resource SET url_element_prefix=?, url_element_suffix=?, url_resource=?, info=?, institution=?, location=?, example=?, obsolete='0' WHERE (resource_id=?)");
            PreparedStatement stmt08obs = pool.getPreparedStatement("UPDATE cura_resource SET url_element_prefix=?, url_element_suffix=?, url_resource=?, info=?, institution=?, location=?, example=?, obsolete='1' WHERE (resource_id=?)");
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
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                    logger.info("Result of the update query #8." + cpt + " (edit data type curation, update old resources): " + resultStatus + " (" + resultGlobal + ")");
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
            PreparedStatement stmt09 = pool.getPreparedStatement("INSERT INTO cura_resource (resource_id, url_element_prefix, url_element_suffix, url_resource, info, institution, location, example, obsolete, ptr_datatype) VALUES (?, ?, ?, ?, ?, ?, ?, ?, '0', '" + data.getId() + "')");
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
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                    logger.info("Result of the adding query #9." + cpt + " (edit data type curation, add new resources): " + resultStatus + " (" + resultGlobal + ")");
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
                sql = "UPDATE cura_resource SET obsolete='1' WHERE (resource_id='" + temp.getId() + "')";
                resultStatus = pool.requestUpdate(pool.getStatement(), sql);
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                logger.info("Result of the update query #10." + cpt + " (edit data type curation, update obsolete resources): " + resultStatus + " (" + resultGlobal + ")");
            }
            
            
            // documentation IDs and URLs: removes all the previous one(s), if necessary (means if any stored)
            sql = "SELECT * FROM cura_doc WHERE (ptr_datatype='" + data.getId() + "')";
            rs = pool.request(pool.getStatement(), sql);
            if (DbPoolConnect.getRowCount(rs) > 0)
            {
                sql = "DELETE FROM cura_doc WHERE (ptr_datatype='" + data.getId() + "')";
                resultStatus = pool.requestUpdate(pool.getStatement(), sql);
                logger.info("Result of the update query #11 (edit data type curation, remove old documentation IDs and URIs): " + resultStatus + "*");
                /* DELETE returns the number of entries deleted, so it is not wise to use it to check if the update process is a success or not
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                */
            }
            else
            {
                logger.info("No update query #11 (edit data type curation, remove old documentation IDs and URIs): no documentation to delete.");
            }
            
            
            // documentation ID(s): add the new (or old ones that have been deleted in the previous step) one(s)
            PreparedStatement stmt12 = pool.getPreparedStatement("INSERT INTO cura_doc (uri, uri_type, ptr_type, ptr_datatype, ptr_resource) VALUES (?, ?, 'data', '" + data.getId() + "', NULL)");
            
            for (int i=0; i<data.getDocumentationIDs().size(); ++i)
            {
                try
                {
                    stmt12.setString(1, data.getDocumentationID(i));
                    stmt12.setString(2, data.getDocumentationIDType(i));
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
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                    logger.info("Result of the adding query #12." + i +" (edit data type curation, documentation IDs): " + resultStatus + " (" + resultGlobal + ")");
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
            PreparedStatement stmt13 = pool.getPreparedStatement("INSERT INTO cura_doc (uri, uri_type, ptr_type, ptr_datatype, ptr_resource) VALUES (?, 'URL', 'data', '" + data.getId() + "', NULL)");
            for (int i=0; i<data.getDocumentationURLs().size(); ++i)
            {
                try
                {
                    stmt13.setString(1, data.getDocumentationURL(i));
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
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                    logger.info("Result of the adding query #13." + i +" (edit data type curation, documentation URLs): " + resultStatus + " (" + resultGlobal + ")");
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
            
            
            // curation stuff
            PreparedStatement stmt14 = pool.getPreparedStatement("UPDATE cura_material SET comment=?, state=? WHERE (ptr_datatype=?)");
            try
            {
                stmt14.setString(1, data.getComment());
                stmt14.setString(2, data.getState());
                stmt14.setString(3, data.getId());
                logger.debug("SQL query: " + stmt14.toString());
                resultStatus = stmt14.executeUpdate();
                stmt14.close();
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during the processing or closing of a prepared statement!");
                logger.error("SQL Exception raised: " + e.getMessage());
                resultStatus = 0;   // failure
            }
            finally
            {
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                logger.info("Result of the update query #14 (edit data type curation, curation stuff): " + resultStatus + " (" + resultGlobal + ")");
            }
            
            
            // end of the transaction
            //pool.commit();   CAN'T BE USED ANY MORE
        }
        
        // without closing the statement, let's see...
        pool.closeConnection();
        
        logger.debug("... end of the update of a curation data type (GLOBAL RESULT: " + resultGlobal + ")");
        
        return resultGlobal;
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
    private static ArrayList[] differentiateResources(ArrayList<Resource> oldList, ArrayList<Resource> newList)
    {
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
        
        // creates the result
        result[0] = oldResources;
        result[1] = newResources;
        result[2] = deletedResources;
        
        return result;
    }
    
    
    public int storePendingObject(DataType data, String subInfo)
    {
        logger.debug("Begin of the addition of a new data type in the curator's interface...");
        
        DbPoolConnect pool;
        int resultStatus = -1;
        int resultGlobal = 1;   // we consider that everything is ok by default
        String index;
        
        // connection to the database pool
        pool = new DbPoolConnect(poolName);
        // test without 'newConnection()' before, let's see...
        pool.getConnection();
        
        // critical section to protected against concurrent access (the 'setAutoCommit' can't be used any more...)
        synchronized(lock)
        {
            // begin of the transaction
            pool.setAutoCommit(false);   //CAN'T BE USED ANY MORE, SINCE WE MOVED TO MYISAM ENGINE FOR NFS LOCKS ISSUES!!! 
            
            // generation of a new ID for the data type in the curation pipeline
            index = generateID(pool, CURA_DATATYPE);
            
            // resource main information
            PreparedStatement stmt01 = pool.getPreparedStatement("INSERT INTO cura_datatype (datatype_id, name, pattern, definition, date_creation, date_modif, obsolete, obsolete_comment, replacement) VALUES (?, ?, ?, ?, NOW(), NOW(), ?, ?, ?)");
            try
            {
                stmt01.setString(1, index);
                stmt01.setString(2, data.getName());
                stmt01.setString(3, data.getRegexp());
                stmt01.setString(4, data.getDefinition());
                if (data.isObsolete())
                {
                    stmt01.setInt(5, 1);
                }
                else
                {
                    stmt01.setInt(5, 0);
                }
                stmt01.setString(6, data.getObsoleteComment());
                stmt01.setString(7, data.getReplacedBy());
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
                logger.info("Result of the adding query #1 (new data type, resource): " + resultStatus);
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
            }
            
            
            // synonym(s)
            PreparedStatement stmt02 = pool.getPreparedStatement("INSERT INTO cura_synonym (name, ptr_datatype) VALUES (?, ?)");
            for (int i=0; i<data.getSynonyms().size(); ++i)
            {
                try
                {
                    stmt02.setString(1, data.getSynonym(i));
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
                    logger.info("Result of the adding query #2." + i + " (new data type, synonyms): " + resultStatus);
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
            if (! MiriamUtilities.isEmpty(data.getURL()))
            {
                PreparedStatement stmt03 = pool.getPreparedStatement("INSERT INTO cura_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URL', '0', ?)");
                try
                {
                    stmt03.setString(1, data.getURL());
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
                    logger.info("Result of the adding query #3 (new data type, URL): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
            
            
            // official URN
            if (! MiriamUtilities.isEmpty(data.getURN()))
            {
                PreparedStatement stmt04 = pool.getPreparedStatement("INSERT INTO cura_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URN', '0', ?)");
                try
                {
                    stmt04.setString(1, data.getURN());
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
                    logger.info("Result of the adding query #4 (new data type, URN): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
            
            
            // obsolete URI(s)
            PreparedStatement stmt05Urn = pool.getPreparedStatement("INSERT INTO cura_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URN', '1', ?)");
            PreparedStatement stmt05Url = pool.getPreparedStatement("INSERT INTO cura_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URL', '1', ?)");
            for (int i=0; i<data.getDeprecatedURIs().size(); ++i)
            {
                try
                {
                    if (data.isDeprecatedURN(i))
                    {
                        stmt05Urn.setString(1, data.getDeprecatedURI(i));
                        stmt05Urn.setString(2, index);
                        logger.debug("SQL query: " + stmt05Urn.toString());
                        resultStatus = stmt05Urn.executeUpdate();
                    }
                    else
                    {
                        stmt05Url.setString(1, data.getDeprecatedURI(i));
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
                    logger.info("Result of the adding query #5." + i +" (new data type, deprecated URIs): " + resultStatus);
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
            ListIterator<Resource> it = data.getResources().listIterator();
            int cpt = 0;
            PreparedStatement stmt06 = pool.getPreparedStatement("INSERT INTO cura_resource (resource_id, url_element_prefix, url_element_suffix, url_resource, info, institution, location, example, obsolete, ptr_datatype) VALUES (?, ?, ?, ?, ?, ?, ?, ?, '0', ?)");
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
                    logger.info("Result of the adding query #6." + cpt +" (new data type, resources): " + resultStatus);
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
            PreparedStatement stmt07 = pool.getPreparedStatement("INSERT INTO cura_doc (uri, uri_type, ptr_type, ptr_datatype, ptr_resource) VALUES (?, ?, 'data', ?, NULL)");
            for (int i=0; i<data.getDocumentationIDs().size(); ++i)
            {
                try
                {
                    stmt07.setString(1, data.getDocumentationID(i));
                    stmt07.setString(2, data.getDocumentationIDType(i));
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
                    logger.info("Result of the adding query #7." + i +" (new data type, documentation IDs): " + resultStatus);
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
            PreparedStatement stmt08 = pool.getPreparedStatement("INSERT INTO cura_doc (uri, uri_type, ptr_type, ptr_datatype, ptr_resource) VALUES (?, 'URL', 'data', ?, NULL)");
            for (int i=0; i<data.getDocumentationURLs().size(); ++i)
            {
                try
                {
                    stmt08.setString(1, data.getDocumentationURL(i));
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
                    logger.info("Result of the adding query #8." + i +" (new data type, documentation URLs): " + resultStatus);
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
            
            // submission info
            PreparedStatement stmt09 =  pool.getPreparedStatement("INSERT INTO cura_material (ptr_datatype, comment, state, sub_info, public_id) VALUES (?, ?, ?, ?, ?)");
            try
            {
                stmt09.setString(1, index);
                stmt09.setString(2, "New submission");
                stmt09.setString(3, "Submitted");
                stmt09.setString(4, subInfo);
                stmt09.setString(5, "");
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
                logger.info("Result of the adding query #9 (new data type, curation material): " + resultStatus);
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
            }try
            {
                stmt09.close();
            }
            catch (SQLException e)
            {
                logger.warn("An exception occured during the closing of a prepared statement!");
                logger.warn("SQL Exception raised: " + e.getMessage());
            }
            
            
            // end of the transaction
            pool.commit();   // CAN'T BE USED ANY MORE!
        }
        
        // without closing the statements, let's see...
        pool.closeConnection();
        
        logger.debug("... end of the adding of a new data type in curation (GLOBAL RESULT: " + resultGlobal + ")");
        
        return resultGlobal;
    }
    
    
    /*
     * Generates an ID for a new data type or for a new data type or a resource (physical location).
     * Warning: the use of this method followed by a creation of a new entity needs to be protected by a 
     *          semaphore/lock to prevent any update of the database in the meantime.
     * 
     * @param database connection pool
     * @param type of ID to generate: for a data type ('DATATYPE') or a resource ('RESOURCE')
     */
    private String generateID(DbPoolConnect pool, int type)
    {
        ResultSet rs = null;
        String id = new String();
        String sql = new String();
        int size;
        
        if (type == RESOURCE)
        {
            id = "MIR:001";
            sql = "SELECT resource_id FROM cura_resource";
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
        
        if (type == RESOURCE)
        {
            logger.info("ID generated for a new resource: " + id);
        }
        
        //pool.closeConnection();
        
        return id;
    }
    
    
    /**
     * Finds differences between two <code>DataTypeHybernate</code>.
     * 
     * <p>
     * In order to keep things clean, 'this' corresponds to the new data and 'data' to the old one.
     * 
     * @param data data type to compare the other one
     * @param otherData
     * @return all the differences founded
     */
    public String diff(CuraDataType data, CuraDataType otherData)
    {
        String diff = new String();
        
        // identifier
        if (! data.getId().equals(otherData.getId()))
        {
            diff += "Id:\n";
            diff += "\t< " + data.getId() + "\n";
            diff += "\t> " + otherData.getId() + "\n\n";
        }
        
        // name
        if (! data.getName().equals(otherData.getName()))
        {
            diff += "Name:\n";
            diff += "\t< " + data.getName() + "\n";
            diff += "\t> " + otherData.getName() + "\n\n";
        }
        
        // synonyms
        diff += "Synonyms:\n";
        for (int i=0; i<otherData.getSynonyms().size(); ++i)
        {
            if (data.getSynonyms().contains(otherData.getSynonym(i)))
            {
                // nothing: no change have been done on this synonym
            }
            else
            {
                // a sysnonym has been added
                diff += "\t> " + otherData.getSynonym(i) + "\n";
            }
        }
        for (int i=0; i<data.getSynonyms().size(); ++i)
        {
            if (otherData.getSynonyms().contains(data.getSynonym(i)))
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
        if (! data.getURL().equals(otherData.getURL()))
        {
            diff += "Official URL:\n";
            diff += "\t< " + data.getURL() + "\n";
            diff += "\t> " + otherData.getURL() + "\n\n";
        }
        
        // URN
        if (! data.getURN().equals(otherData.getURN()))
        {
            diff += "Official URN:\n";
            diff += "\t< " + data.getURN() + "\n";
            diff += "\t> " + otherData.getURN() + "\n\n";
        }
        
        // deprecatedURIs
        diff += "Deprecated URIs:\n";
        for (int i=0; i<otherData.getDeprecatedURIs().size(); ++i)
        {
            if (data.getDeprecatedURIs().contains(otherData.getDeprecatedURI(i)))
            {
                // nothing: no change have been done on this deprecated URI
            }
            else
            {
                // a deprecated URI has been added
                diff += "\t> " + otherData.getDeprecatedURI(i) + "\n";
            }
        }
        for (int i=0; i<data.getDeprecatedURIs().size(); ++i)
        {
            if (otherData.getDeprecatedURIs().contains(data.getDeprecatedURI(i)))
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
        if (! data.getDefinition().equals(otherData.getDefinition()))
        {
            diff += "Definition:\n";
            diff += "\t< " + data.getDefinition() + "\n";
            diff += "\t> " + otherData.getDefinition() + "\n\n";
        }
        
        // regexp
        if (! data.getRegexp().equals(otherData.getRegexp()))
        {
            diff += "Regular expression:\n";
            diff += "\t< " + data.getRegexp() + "\n";
            diff += "\t> " + otherData.getRegexp() + "\n\n";
        }
        
        // resources (two cases: identifiers available or not)
        diff += "Resources:\n";
        
        // checks if some resources have been added or have been modified: (data.getResources().contains(this.getResource(i)))
        for (int i=0; i<otherData.getResources().size(); ++i)
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
                if ((! (tmp1.getId()).equalsIgnoreCase("null")) && (! otherData.getResource(i).getId().equalsIgnoreCase("null")))
                {
                    test = tmp1.equals(otherData.getResource(i));
                }
                else
                {
                    test = tmp1.couldBeSimilar(otherData.getResource(i));
                }
                
                if (test)
                {
                    find1 = true;
                    
                    if (! tmp1.hasSameContent(otherData.getResource(i)))
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
                    diff += "\t>> " + otherData.getResource(i) + "\n";   // new version of the resource
                }
            }
            else
            {
                // a resource has been added
                diff += "\t> " + otherData.getResource(i) + "\n";
            }
        }
        
        // checks if some resources have been deleted: (this.getResources().contains(data.getResource(i)))
        for (int i=0; i<data.getResources().size(); ++i)
        {
            boolean find2 = false;
            ListIterator<Resource> it2 = otherData.getResources().listIterator();
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
        for (int i=0; i<otherData.getDocumentationURLs().size(); ++i)
        {
            if (data.getDocumentationURLs().contains(otherData.getDocumentationURL(i)))
            {
                // nothing: no change have been done on this URL towards a piece of documentation
            }
            else
            {
                // an URL towards a piece of documentation has been added
                diff += "\t> " + otherData.getDocumentationURL(i) + "\n";
            }
        }
        for (int i=0; i<data.getDocumentationURLs().size(); ++i)
        {
            if (otherData.getDocumentationURLs().contains(data.getDocumentationURL(i)))
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
        for (int i=0; i<otherData.getDocumentationIDs().size(); ++i)
        {
            if (data.getDocumentationIDs().contains(otherData.getDocumentationID(i)))
            {
                // nothing: no change have been done on this ID of a piece of documentation
            }
            else
            {
                // an ID of a piece of documentation has been added
                diff += "\t> " + otherData.getDocumentationID(i) + "\n";
            }
        }
        for (int i=0; i<data.getDocumentationIDs().size(); ++i)
        {
            if (otherData.getDocumentationIDs().contains(data.getDocumentationID(i)))
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


    /**
     * Publishes a data type from the curation pipeline to the public website.
     * @param dataId identifier of a data type in the curation pipeline (for example: 'MIR:00900002')
     * @return can be null (if an error happened during the process) otherwise returns the public identifier of the data type
     */
    public String publish(String dataId)
    {
        String result = null;
        CuraDataType data = retrieve(dataId);
        DataTypeHybernate toPublish = copyDataTypeContent(data);
        
        logger.debug("Publication of: " + dataId);
        
        // publish the data type
        int success = toPublish.storeObject(poolName);
        
        // everything is ok so far...
        if (success == 1)
        {
            DbPoolConnect pool;
            pool = new DbPoolConnect(poolName);
            pool.getConnection();
            String id = null;
            // retrieves the public identifier of the newly public data type
            String sql1 = "SELECT datatype_id FROM mir_datatype WHERE (name='" + toPublish.getName() + "')";
            ResultSet rs = pool.request(pool.getStatement(), sql1);
            try
            {
                if (rs.first())
                {
                    id = rs.getString("datatype_id");
                }
                else
                {
                    logger.error("Can't retrieve the public identifier of a newly published data type: '" + toPublish.getName() + "'!");
                }
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during the retrieval of the identifier of a newly published data type: '" + toPublish.getName() + "'!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            
            // updates the state of the now published data type in the curation pipeline and add a comment
            String sql2 = "UPDATE cura_material SET state='Published', public_id='" + id + "' WHERE (ptr_datatype='" + dataId + "')";
            int resultStatus = pool.requestUpdate(pool.getStatement(), sql2);
            pool.closeConnection();
            
            if (resultStatus == 1)
            {
                result = id;
            }
            else
            {
                logger.error("An error occurred while updating the curation records of a newly published data type: " + dataId + "!");
            }
        }
        
        return result;
    }
    
    
    /**
     * Retrieves the state of a data type
     * @param dataId identifier of a data type in the curation pipeline (for example: 'MIR:00900002')
     * @return can be null
     */
    public String getState(String dataId)
    {
        DbPoolConnect pool;
        ResultSet rs = null;
        String result = null;
        
        // connection to the database pool
        pool = new DbPoolConnect(poolName);
        
        // test without 'newConnection()' before, let's see...
        pool.getConnection();
        
        PreparedStatement stmt = pool.getPreparedStatement("SELECT state FROM cura_material WHERE (ptr_datatype=?)");
        try
        {
            stmt.setString(1, dataId);
            logger.debug("SQL query: " + stmt.toString());
            rs = stmt.executeQuery();
            if (rs.first())
            {
                result = rs.getString("state");
            }
            stmt.close();
        }
        catch (SQLException e)
        {
            logger.error("An exception occured during the retrieval of the state of the data type: " + dataId + "!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        
        pool.closeConnection();
        
        return result;
    }


    /**
     * Checks if a data type exists in the curation pipeline, based on its identifier.
     * @param dataId identifier of a data type in the curation pipeline (for example: 'MIR:00900002');
     * @return True or False
     */
    public boolean existsById(String dataId)
    {
        DbPoolConnect pool;
        ResultSet rs = null;
        boolean exist = false;
        
        // connection to the database pool
        pool = new DbPoolConnect(poolName);
        
        // test without 'newConnection()' before, let's see...
        pool.getConnection();
        
        PreparedStatement stmt = pool.getPreparedStatement("SELECT name FROM cura_datatype WHERE (datatype_id=?)");
        try
        {
            stmt.setString(1, dataId);
            logger.debug("SQL query: " + stmt.toString());
            rs = stmt.executeQuery();
            if (rs.first())
            {
                exist = true;
            }
            stmt.close();
        }
        catch (SQLException e)
        {
            logger.error("An exception occured during the check of existence of a data type in curation: " + dataId + "!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        
        pool.closeConnection();
        
        return exist;
    }
    
    
    /**
     * Retrieves all data types in the curation pipeline in a specific state.
     * <p>WARNING: no check on the state!
     *  
     * @param state state of a data type in the curation pipeline (submitted, curation, canceled, pending or published)
     * @return list of simple data types
     */
    public List<SimpleCuraDataType> retrieveWithState(String state)
    {
        List<SimpleCuraDataType> result = new ArrayList<SimpleCuraDataType>();
        DbPoolConnect pool;
        ResultSet rs = null;
        
        // connection to the database pool
        pool = new DbPoolConnect(poolName);
        
        // test without 'newConnection()' before, let's see...
        pool.getConnection();
        
        String sql = "SELECT d.name, d.definition, d.datatype_id, m.state, d.date_creation, m.public_id FROM cura_datatype d, cura_material m WHERE ((d.datatype_id = m.ptr_datatype) AND (m.state = '" + state + "')) ORDER BY d.date_creation";
        rs = pool.request(pool.getStatement(), sql);
        boolean notEmpty;
        try
        {
            notEmpty = rs.first();
            while (notEmpty)
            {
                SimpleCuraDataType temp = new SimpleCuraDataType();
                temp.setId(rs.getString("datatype_id"));
                temp.setName(rs.getString("name"));
                temp.setDefinition(rs.getString("definition"));
                temp.setState(rs.getString("state"));
                temp.setSubmissionDate(rs.getTimestamp("date_creation"));
                temp.setPublicId(rs.getString("public_id"));
                
                // adds this simple data type to the list
                result.add(temp);
                
                // next data type (if it exists)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while searching the data types in the curated pipeline!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        
        // without closing the statements, let's see...
        pool.closeConnection();
        
        return result;
    }
    
    
    /**
     * Copies the content of a <code>CuraDataType</code> object into a <code><DataTypeHybernate/code> object (in order to be able to publish it for example).
     * @param from 
     * @return the fully created <code>CuraDataType</code> object based on the content of <code>CuraDataType</code> object
     */
    private DataTypeHybernate copyDataTypeContent(CuraDataType from)
    {
        DataTypeHybernate result = new DataTypeHybernate();
        
        result.setId(from.getId());
        result.setName(from.getName());
        result.setNameURL(from.getNameURL());
        result.setSynonyms(from.getSynonyms());
        result.setURL(from.getURL());
        result.setURN(from.getURN());
        result.setDeprecatedURIs(from.getDeprecatedURIs());
        result.setDefinition(from.getDefinition());
        result.setRegexp(from.getRegexp());
        result.setResources(from.getResources());
        result.setDocumentationURLs(from.getDocumentationURLs());
        result.setDocumentationIDs(from.getDocumentationIDs());
        result.setDocumentationIDsType(from.getDocumentationIDsType());
        result.setDocHtmlURLs(from.getDocHtmlURLs());
        //result.setDateCreation();
        //result.setDateCreationStr();
        //result.setDateModification()
        //result.setDateModificationStr():
        result.setObsolete(from.isObsolete());
        result.setObsoleteComment(from.getObsoleteComment());
        result.setReplacedBy(from.getReplacedBy());
        
        return result;
    }
}
