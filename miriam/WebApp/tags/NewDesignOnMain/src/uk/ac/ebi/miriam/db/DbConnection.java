/*
 * Author: Camille Laibe
 * Copyright: EMBL-EBI, Computational Neurobiology Group
 * Project: Miriam
 */


package uk.ac.ebi.miriam.db;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import org.apache.log4j.Logger;


/**
 * <p>
 * Manages the connection to a database (template). Only one connection, but several statements allowed  
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>EMBL-EBI, Computational Neurobiology Group</dd>
 * </dl>
 * 
 * @author Camille Laibe
 * @version 20060918
 */
abstract class DbConnection
{
	private Logger logger = Logger.getLogger(DbConnection.class);
	private Connection connection = null;
	private LinkedList statements = new LinkedList();
	
	
	/**
	 * Creates a new connection or recovery from a pool
	 * @return Connection object
	 */
	public abstract void newConnection();
	
	
	/**
	 * Setter: update the connection
	 */
	protected void setConnection(Connection conn)
	{
		this.connection = conn;
	}
	
	
	/**
	 * Getter: access to the Connection object
	 */
	public Connection getConnection()
	{
		// if no call of the 'newConnection()' before, we do it now
		if (connection == null)
		{
			newConnection();
		}
		
		return connection;
	}
	
	
	/**
	 * Closes the connection to the database
	 * @param Connection
	 */
	public void closeConnection()
	{
		// we close all the open statements if needed
		if (! statements.isEmpty())
		{
			closeStatements();
		}
		
		// now we close the connection
		try
		{
			connection.close();
		}
		catch (SQLException e)
		{
			logger.warn("An exception occured during the closing of the connection!");
			logger.warn("SQL Exception raised: " + e.getMessage());
		}
	}
	
	
	/**
	 * Creates a (new) statement
	 * @return Statement created
	 */
	public Statement getStatement()
	{
		Statement stmt = null;
		
		if (connection != null)
		{
			try
			{
				stmt = connection.createStatement();
				logger.debug("successful creation of a new statement");
			}
			catch (SQLException e)
			{
				logger.error("An exception occured during the creation of a statement");
				logger.error("SQL Exception raised: " + e.getMessage());
			}
		}
		if (stmt != null)
		{
			statements.add(stmt);
		}
		
		return (Statement) statements.getLast();
	}
	
	
	/**
	 * Closes the lastest open statement
	 */
	public void closeStatement()
	{
		Statement temp;
		
		temp = (Statement) statements.removeLast();
		try
		{
			temp.close();
		}
		catch (SQLException e)
		{
			logger.warn("An exception occured during the closing of a statement");
			logger.warn("SQL Exception raised: " + e.getMessage());
		}
	}
	
	
	/**
	 * Closes all the opened statements
	 */
	public void closeStatements()
	{
		Statement temp;
		
		while (! statements.isEmpty())
		{
			temp = (Statement) statements.removeLast();
			try
			{
				temp.close();
			}
			catch (SQLException e)
			{
				logger.warn("An exception occured during the closing of a statement");
				logger.warn("SQL Exception raised: " + e.getMessage());
			}
		}
	}
	
	
	/**
	 * Closes all the useless open things (connection, statements, the door behind you, ...)
	 */
	public void closeAll()
	{
		closeStatements();
		closeConnection();
	}
	
	
	/**
	 * Performes an SQL request using the Statement in parameter
	 * @param Statement object already created
	 * @param String SQL query
	 * @return ResultSet rsult of the query
	 */
	public ResultSet request(Statement stmt, String query)
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
	
	
	/**
	 * Performes an SQL update request using the Statement in parameter
	 * @param Statement object already created
	 * @param String SQL query
	 * @return ResultSet rsult of the query
	 */
	public int requestUpdate(Statement stmt, String query)
	{
		try
		{
			logger.debug("SQL update query: " + query);
			return stmt.executeUpdate(query);
		}
		catch (SQLException e)
		{
	    	logger.error("Error during the execution of an update query!");
	    	logger.error("Query: " + query);
	    	logger.error("SQL Exception raised: " + e.getMessage());
	    	return 0;
		}
	}
	
	
	/**
	 * Counts the number of rows in a ResultSet (like 'getColumnCount()', via 'getMetaData()', for the columns)
	 * @param ResultSet object already created
	 * @return int number of rows in the ResultSet object
	 */
	public static int getRowCount(ResultSet data)
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
	
	
	/**
	 * Sets the connection's auto-commit mode to the given state.
	 * @param bool boolean (true or false) representing the wanted state for the AutoCommit feature
	 */
	public void setAutoCommit(boolean bool)
	{
		try
		{
			this.connection.setAutoCommit(bool);
		}
		catch (SQLException e)
		{
			logger.error("Error during the setting at '" + bool + "' of the commit feature!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
		}
	}
	
	
	/**
	 * Makes all changes made since the previous commit/rollback permanent and releases any database locks currently held by the connection. 
     * This method should be used only when auto-commit mode has been disabled (with the 'setAutoCommit' method).
	 */
	public void commit()
	{
		try
		{
			this.connection.commit();
		}
		catch (SQLException e)
		{
			logger.error("Error during the the commit of the connection!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
		}
	}
}
