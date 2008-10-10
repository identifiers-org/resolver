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


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.apache.log4j.Logger;


/** 
 * Context for a database connection (connection and statement)
 * 
 * <p>
 * Part of the MIRIAM Web Services package (uk.ac.ebi.miriam.ws)
 * 
 * <p>
 * Uses:
 * <ul>
 * <li>the pooling services provided by DBCP, from the Apache Jackarta Project.
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
 * @version 20060516
 * 
 */
public class DataBaseContext
{
	private Statement statement;
	private Connection connection;
    // for logging
    private Logger logger = Logger.getLogger(DataSourceManager.class);   // not 'static' in a servlet or j2ee container!
    
    
    /**
     * Constructor
     */
    public DataBaseContext()
    {
    	statement = null;
    	connection = null;
    }
    
    
    /**
     * Statement getter
     * @return Statement object
     */
    public Statement getStatement()
    {
    	return statement;
    }
    
    
    /**
     * Connection getter
     * @return Connection object
     */
    public Connection getConnection()
    {
    	return connection;
    }
    
    /**
     * Opens the connection and a statement
     * @param dsm DataSource previously created
     */
    public void open(DataSourceManager dsm)
    {
    	openConnection(dsm.getDataSource());
    	openStatement();
    }
    
    
    /**
     * Closes the connection and the statement
     */
    public void close()
    {
    	closeStatement();
    	closeConnection();
    }
	
	
	/**
	 * Opens a connection to the DataSource
	 * @param dataSource DataSource previously created
	 */
	public void openConnection(DataSource dataSource)
	{
    	try
		{
    		logger.debug("Creating new Connection...");
    		connection = dataSource.getConnection();
    		logger.debug("Connection created.");
		}
		catch (SQLException e)
		{
	    	logger.error("Cannot open a connection to the database!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
		}
	}
	
	
	/**
	 * Opens a connection to the pool of DataSource
	 */
	public void openStatement()
	{
		try
		{
			logger.debug("Creating new Statement...");
			statement = connection.createStatement();
			logger.debug("Statement created.");
		}
		catch (SQLException e)
		{
	    	logger.error("Error during the creation of the statement!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
		}
	}
	
	
	/**
	 * Closes the connection to the DataSource
	 */
	public void closeConnection()
	{
	    try
		{
	    	logger.debug("Closing the connection...");
			connection.close();
			logger.debug("Connection closed.");
		}
		catch (SQLException e)
		{
	    	logger.error("Error during the closing of the connection!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
		}
		logger.debug("Connection with the database: closed.");
	}
	
	
	/**
	 * Closes the Statement
	 */
	public void closeStatement()
	{
		try
		{
			logger.debug("Closing the statement...");
			statement.close();
			logger.debug("Statement closed.");
		}
		catch (SQLException e)
		{
	    	logger.error("Error during the closing of the statement!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
		}
	}
}
