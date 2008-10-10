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


//import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.List;
//import org.apache.log4j.Logger;


/**
 * <p>Generic abstract class for database connection (Data Access Object).
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
 * @version 20080526
 */
public abstract class Dao
{
    //private Logger logger = Logger.getLogger(Dao.class);
    protected DbPoolConnect pool;
    
    
    /**
     * Constructor.
     * @param pool data base pool
     */
    public Dao(String pool)
    {
        this.pool = new DbPoolConnect(pool);
    }
    
    
    /**
     * Sets up the database connection environment.
     */
    protected void setupEnv()
    {
        // test without 'newConnection()' before, let's see...
        this.pool.getConnection();
    }
    
    
    /**
     * Cleans the database connection environment.
     */
    protected void cleanEnv()
    {
        // without closing the statement, let's see...
        pool.closeConnection();
    }
    
    
    /**
     * Closes the connection and cleans the mess.
     */
    public abstract void clean();
    
    
    /**
     * Executes a simple SQL query.
     * @param sql SQL query
     * @return ResultSet
     */
    public ResultSet simpleQuery(String sql)
    {
        ResultSet result;
        result = pool.request(pool.getStatement(), sql);
        
        return result;
    }
    
    
    /*
     * Executes a prepared statement.
     * @param SQL prepared statement
     * @return ResultSet
    public ResultSet preparedQuery(String sql, List<Object> params)
    {
        ResultSet result = null;
        
        PreparedStatement stmt = pool.getPreparedStatement(sql);
        try
        {
            // what to do with the different parameters (different types)?
            //stmt.setString(1, ?);
            //stmt.setInt(5, ?);
            result = stmt.executeQuery();
            
            // what to do with the result before closing the prepared statement?
            
            stmt.close();
        }
        catch (SQLException e)
        {
            // do something here!
        }
        
        return result;
    }
    */
}
