/*
 * MIRIAM Resources (Web Services)
 * MIRIAM is an online resource created to catalogue biological data types,
 * their URIs and the corresponding physical URLs,
 * whether these are controlled vocabularies or databases.
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2007  Camille Laibe (EMBL - European Bioinformatics Institute, Computational Neurobiology Group)
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


package uk.ac.ebi.miriam.ws;


//for database connection
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;

import javax.sql.DataSource;
// for connection pooling
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
// for logging
import org.apache.log4j.Logger;


/**
 * <p>
 * Initializes DBCP environment by creating a pool for connections
 * 
 * <p>
 * Part of the MIRIAM Web Services package (uk.ac.ebi.miriam.ws)</li>
 * </ul>
 * 
 * <p>
 * Uses:
 * <ul>
 * <li>the pooling services provided by DBCP, from the Apache Jackarta Project</li>
 * <li>the singleton design pattern, in order to have only one pool</li>
 * </ul>
 * 
 * <p>
 * This needs:
 * <ul>
 * <li>the "DatabaseProperties" class (settings for the database connection)</li>
 * </ul>
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2007 Camille Laibe (EMBL - European Bioinformatics Institute, Computational Neurobiology Group)
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
 * @author Camille Laibe
 * @version 20060929
 */
public class DataSourceManager
{
	// singleton
	private static DataSourceManager ourInstance = new DataSourceManager();
	// dataSource (pool)
	private DataSource dataSource;
    // for logging
    private Logger logger = Logger.getLogger(DataSourceManager.class);   // not 'static' in a servlet or j2ee container!
    // parameters for the connection to the database
    private String driverType = new String();
    private String driverName = new String();
    private String serverName = new String();
    private String serverPort = new String();
    private String baseName = new String();
    private String login = new String();
    private String password = new String();
    
    
    // Private constructor suppresses generation of a (public) default constructor,
    // to make sure that the object cannot be instantiated any other way.
    // Work: open the connection to the database
    private DataSourceManager()
    {
    	logger.debug("DataSourceManager Constructor (should be called only one time)");
    	
    	// retrieves the ServletContext
    	HttpServlet srv = (HttpServlet) MessageContext.getCurrentContext().getProperty(HTTPConstants.MC_HTTP_SERVLET);
    	ServletContext context = srv.getServletContext();
    	// retrieves the database connexion parameters in the "web.xml" file
    	driverType = context.getInitParameter("miriam_db_type");
		driverName = context.getInitParameter("miriam_db_driver");
		serverName = context.getInitParameter("miriam_db_server");
		serverPort = context.getInitParameter("miriam_db_port");
		baseName = context.getInitParameter("miriam_db_database");
		login = context.getInitParameter("miriam_db_user");
		password = context.getInitParameter("miriam_db_password");
    	
        // Load the MySQL JDBC driver
        // "mysql-connector-java...jar" should be in the class path
        try
        {
            Class.forName(driverName);
        }
        catch (ClassNotFoundException e)
        {
	    	logger.error("Cannot load the database driver!");
	    	logger.error("ClassNotFound Exception raised: " + e.getMessage());
        }
        
        // Set up the PoolingDataSource.
        // This could be handled auto-magically by
        // an external configuration (.jocl) , perhaps in a near future...
        dataSource = setupDataSource(getUrl(driverType, serverName, serverPort, baseName), login, password);
        logger.debug("Successful set up of the data source (The Pool)");
    }
     
    
    // Sets up the data source
    // @param URL for database connection (without login nor password)
    // @param user name
    // @param password
    // @return DataSource for connection pooling
    private DataSource setupDataSource(String url, String username, String password)
    {
         // First, we'll need a ObjectPool that serves as the
         // actual pool of connections.
         //
         // We'll use a GenericObjectPool instance, although
         // any ObjectPool implementation will suffice.
    	ObjectPool connectionPool = new GenericObjectPool(null);

         // Next, we'll create a ConnectionFactory that the
         // pool will use to create Connections.
         // We'll use the DriverManagerConnectionFactory,
         // using the connect string passed in the command line
         // arguments.
         ConnectionFactory connectionFactory = 
        	 new DriverManagerConnectionFactory(url, username, password);

         // Now we'll create the PoolableConnectionFactory, which wraps
         // the "real" Connections created by the ConnectionFactory with
         // the classes that implement the pooling functionality.
         PoolableConnectionFactory poolableConnectionFactory = 
        	 new PoolableConnectionFactory(connectionFactory,connectionPool,null,null,false,true);

         // Finally, we create the PoolingDriver itself,
         // passing in the object pool we created.
         PoolingDataSource dataSource = new PoolingDataSource(connectionPool);

         return dataSource;
    }
    
    // TEST TEST TEST TEST TEST TEST TEST TEST
/*
    private HttpServletRequest getRequest()
    { 
        MessageContext context = MessageContext.getCurrentContext(); 
        HttpServletRequest req = (HttpServletRequest) context.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST); 
        return req; 
    }
*/
    
    /**
     * Getter for the whole class
     * @return whole class
     */
    public static DataSourceManager getDataSourceInstance()
    {
    	return ourInstance;
    }
    
    
    /**
     * Getter of the datasource
     * @return DataSource object
     */
    public DataSource getDataSource()
    {
    	return dataSource;
    }
    
    
    /**
     * Creates a connection to the pool
     * @return Connection object
     */
    public Connection newConnection()
    {
    	try
		{
			return dataSource.getConnection();
		}
		catch (SQLException e)
		{
	    	logger.error("Cannot create a connection to the pool!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
			return null;
		}
    }
    
    
    /**
     * Closes the connection to the database
     */
    public void closeConnection(Connection connection)
    {
	    try
		{
			connection.close();
		}
		catch (SQLException e)
		{
	    	logger.error("Error during the closing of the connection!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
		}
		logger.debug("Connection with the database: closed.");
    }
    
    
	/*
	 * Creates a URL for the connection to a MYSQL database using JDBC
	 * @return URL (without username and password) for a MySQL database connection (using JDBC)
	 */
	private static String getUrl(String driverType, String serverName, String serverPort, String dataBase)
	{
		String url = new String();
		url = driverType + "://" + serverName +  ":" + serverPort + "/" + dataBase;
			
		return url;
	}
}
