/*
 * Author: Camille Laibe
 * Copyright: EMBL-EBI, Computational Neurobiology Group
 * Project: Miriam
 */


package uk.ac.ebi.miriam.db;


import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.log4j.Logger;


/**
 * <p>
 * Manages the connection to a database (with pooling).
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>EMBL-EBI, Computational Neurobiology Group</dd>
 * </dl>
 * 
 * @author Camille Laibe
 * @version 20060616
 */
public class DbPoolConnect extends DbConnection
{
	private Logger logger = Logger.getLogger(DbPoolConnect.class);
	private String poolName = new String();

	/**
	 * Constructor
	 */
	public DbPoolConnect(String poolName)
	{
		this.poolName = poolName;
	}
	
	/**
	 * Recover a connection from the pool
	 */
	public void newConnection()
	{
		try
		{
			setConnection(DriverManager.getConnection("jdbc:apache:commons:dbcp:" + poolName));
			logger.debug("Successful recovery of a connection from the pool");
		}
		catch (SQLException e)
		{
			logger.error("Cannot open the database connection from the pool!");
        	logger.error("SQL Exception raised: " + e.getMessage());
		}
	}
}
