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


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;


/**
 * <p>Handles some database connections for manipulating <code>DataType</code> (some other features are provided by <code>DataTypeHybernate</code>).
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
 * @version 20080711
 */
public class DataTypeDao extends Dao
{
    private Logger logger = Logger.getLogger(DataTypeDao.class);
    
    
    /**
     * Construtor.
     * @param pool database pool
     */
    public DataTypeDao(String pool)
    {
        super(pool);
        
        // setup
        setupEnv();
    }
    
    
    /**
     * Cleans the mess (like releasing the database connection)
     * @see uk.ac.ebi.miriam.db.Dao#clean()
     */
    @Override
    public void clean()
    {
        cleanEnv();
    }
    
    
    /**
     * Tests if a specific data type exists in the database, based on its identifier.
     * 
     * @param id identifier of a data type, for example 'MIR:00000008'.
     * @return <code>boolean</code>: True if the data type exists in the database, False otherwise.
     */
    public boolean exists(String id)
    {
        boolean result = false;   // default value
        
        // setup
        //setupEnv();
        
        PreparedStatement stmt = this.pool.getPreparedStatement("SELECT name FROM mir_datatype WHERE (datatype_id=?)");
        try
        {
            stmt.setString(1, id);
            logger.debug("SQL prepared query: " + stmt.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.first())
            {
                result = true;
            }
            
            stmt.close();
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the existence test of the following data type: " + id);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        
        return result;
    }
    
    
    /**
     * Retrieves the name of a data type based on its identifier.
     * @param id identifier of a data type, for example 'MIR:00000008'.
     * @return name of the data type (<code>null</code> if the data type doesn't exist)
     */
    public String getDataTypeName(String id)
    {
        String result = null;
        
        // setup
        //setupEnv();
        
        PreparedStatement stmt = this.pool.getPreparedStatement("SELECT name FROM mir_datatype WHERE (datatype_id=?)");
        try
        {
            stmt.setString(1, id);
            logger.debug("SQL prepared query: " + stmt.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.first())
            {
                result = rs.getString("name");
            }
            
            stmt.close();
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the existence test of the following data type: " + id);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        
        return result;
    }
    
    
    /**
     * Retrieves a data type (simple version) from the database, based on its identifier.
     * 
     * @param id identifier of a data type (for example: 'MIR:00000008')
     * @return a data type (can be null)
     */
    public SimpleDataType getSimpleDataTypeById(String id)
    {
        SimpleDataType data = null;
        
        PreparedStatement stmt = this.pool.getPreparedStatement("SELECT d.datatype_id, d.name, d.definition, u.uri FROM mir_datatype d, mir_uri u WHERE ((d.datatype_id=?) AND (d.datatype_id=u.ptr_datatype) AND (u.uri_type='URN') AND (u.deprecated=0))");
        try
        {
            stmt.setString(1, id);
            logger.debug("SQL prepared query: " + stmt.toString());
            ResultSet rs = stmt.executeQuery();
            // no check if several results...
            if (rs.first())
            {
                data = new SimpleDataType();
                data.setId(rs.getString("d.datatype_id"));
                data.setName(rs.getString("d.name"));
                data.setDefinition(rs.getString("d.definition"));
                data.setUri(rs.getString("u.uri"));
            }
            
            stmt.close();
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the data type: " + id);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        
        return data;
    }
    
    
    /**
     * Retrieves a list of data type from the database, based on their (primary, not synonym) name.
     * Warning: doesn't retrieve the URI of the data types!
     * @param dataTypeNames list of name of a data types
     * @return list of <code>SimpleDataType</code> (can be empty)
     */
    public List<SimpleDataType> getSimpleDataTypeByNames(List<String> dataTypeNames)
    {
        List<SimpleDataType> data = new ArrayList<SimpleDataType>();
        
        PreparedStatement stmt = this.pool.getPreparedStatement("SELECT datatype_id, name, definition FROM mir_datatype WHERE (name=?)");
        for (String name: dataTypeNames)
        {
            try
            {
                stmt.setString(1, name);
                logger.debug("SQL prepared query: " + stmt.toString());
                ResultSet rs = stmt.executeQuery();
                // no check if several results...
                if (rs.first())
                {
                    SimpleDataType tmp = new SimpleDataType();
                    tmp.setId(rs.getString("datatype_id"));
                    tmp.setName(rs.getString("name"));
                    tmp.setDefinition(rs.getString("definition"));
                    
                    data.add(tmp);
                }
            }
            catch (SQLException e)
            {
                logger.warn("An exception occurred during the retrieval of the data type: " + name);
                logger.warn("SQLException raised: " + e.getMessage());
            }
        }
        
        try
        {
            stmt.close();
        }
        catch (SQLException e)
        {
            logger.warn("An exception occured during the closing of a prepared statement!");
            logger.warn("SQL Exception raised: " + e.getMessage());
        }
        
        return data;
    }
    
    
    /**
     * Updates the last modification date of a data type, based on its identifier.
     * @param dataTypeId identifier of a data type (for example: 'MIR:00000008')
     * @return True if the update is a success, False otherwise
     */
    public boolean updateLastModifDate(String dataTypeId)
    {
        boolean result = false;
        
        // the data type exists
        if (exists(dataTypeId))
        {
            PreparedStatement stmt = this.pool.getPreparedStatement("UPDATE mir_datatype SET date_modif=NOW() WHERE (datatype_id=?)");
            try
            {
                stmt.setString(1, dataTypeId);
                int state = stmt.executeUpdate();
                if (state == 1)
                {
                    result = true;
                }
                
                stmt.close();
            }
            catch (SQLException e)
            {
                logger.warn("An exception occurred during the update of the last modification date of the data type: " + dataTypeId);
                logger.warn("SQLException raised: " + e.getMessage());
            }
            
        }
        
        return result;
    }
    
    
    /**
     * Retrieves the number of (not obsolete) data types currently stored in the database.
     * @return
     */
    public int getNbDataTypes()
    {
        int nb = 0;
        
        String sql = "SELECT COUNT(name) AS number FROM mir_datatype WHERE (obsolete=0)";
        ResultSet rs = pool.request(pool.getStatement(), sql);
        try
        {
            if (rs.first())
            {
                nb = rs.getInt("number");
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the number of (not obsolete) data types stored!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        
        return nb;
    }
    
    
    /**
     * Retrieves the number of (obsolete) data types currently stored in the database.
     * @return
     */
    public int getNbObsoleteDataTypes()
    {
        int nb = 0;
        
        String sql = "SELECT COUNT(name) AS number FROM mir_datatype WHERE (obsolete=1)";
        ResultSet rs = pool.request(pool.getStatement(), sql);
        try
        {
            if (rs.first())
            {
                nb = rs.getInt("number");
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the number of (obsolete) data types stored!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        
        return nb;
    }
    
    
    /**
     * Retrieves the number of all data types (obsolete and not) currently stored in the database.
     * @return
     */
    public int getNbAllDataTypes()
    {
        int nb = 0;
        
        String sql = "SELECT COUNT(name) AS number FROM mir_datatype";
        ResultSet rs = pool.request(pool.getStatement(), sql);
        try
        {
            if (rs.first())
            {
                nb = rs.getInt("number");
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the number of all data types stored!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        
        return nb;
    }
    
    
    /**
     * Retrieves the obsolete information about a given data type (based on its identifier).
     * The result can be null if the data type is not obsolete
     * @param identifier of a data type (for example: "MIR:00000001")
     * @return a <code>HashMap</code> which contains the following keys (and associated values): replacementId, replacementName and replacementComment.
     */
    public HashMap<String, String> getObsoleteInfo(String identifier)
    {
        HashMap<String, String> result = null;
        
        PreparedStatement stmt = this.pool.getPreparedStatement("SELECT obsolete, obsolete_comment, replacement FROM mir_datatype WHERE (datatype_id=?)");
        try
        {
            stmt.setString(1, identifier);
            ResultSet rs = stmt.executeQuery();
            if (rs.first())
            {
                if (rs.getInt("obsolete") == 1)
                {
                    result = new HashMap<String, String>();
                    result.put("replacementComment", rs.getString("obsolete_comment"));
                    result.put("replacementId", rs.getString("replacement"));
                    result.put("replacementName", getDataTypeName(rs.getString("replacement")));
                }
            }
            stmt.close();
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of obsolete information about: " + identifier);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        
        return result;
    }
    
    
    /**
     * Retrieves the newest date of last modification of the database.
     * @return <code>Date</code> of last modification of the database
     */
    public Date getLastModifDate()
    {
        Date lastModif = null;
        
        String query = "SELECT date_modif FROM mir_datatype WHERE 1 ORDER BY date_modif DESC LIMIT 1";
        ResultSet sqlResult = pool.request(pool.getStatement(), query);
        try
        {
            sqlResult.next();
            lastModif = sqlResult.getDate("date_modif");
            sqlResult.close();
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the newest date of last modification!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        
        return lastModif;
    }


    /**
     * Retrieves all the data types stored in the database.
     * @return list of <code>SimpleDataType</code>
     */
    public List<SimpleDataType> getDataTypes()
    {
        List<SimpleDataType> result = new ArrayList<SimpleDataType>();
        
        String query = "SELECT datatype_id, name, definition FROM mir_datatype ORDER BY name";
        ResultSet sqlResult = pool.request(pool.getStatement(), query);
        try
        {
            boolean notEmpty = sqlResult.first();
            while (notEmpty)
            {
                SimpleDataType temp = new SimpleDataType();
                temp.setId(sqlResult.getString("datatype_id"));
                temp.setName(sqlResult.getString("name"));
                temp.setDefinition(sqlResult.getString("definition"));
                
                result.add(temp);
                notEmpty = sqlResult.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the list of data types stored!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        
        return result;
    }
}
