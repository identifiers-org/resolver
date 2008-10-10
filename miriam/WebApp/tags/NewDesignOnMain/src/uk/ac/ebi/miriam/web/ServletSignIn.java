/*
 * Author: Camille Laibe
 * Copyright: EMBL-EBI, Computational Neurobiology Group
 * Project: Miriam
 */


package uk.ac.ebi.miriam.web;


import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import java.sql.*;

import uk.ac.ebi.compneur.util.Base64;
import uk.ac.ebi.miriam.web.MiriamUtilities;
import uk.ac.ebi.miriam.db.DbSimpleConnect;


/**
 * <p>Servlet that handles the authentification of the user
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>EMBL-EBI, Computational Neurobiology Group</dd>
 * </dl>
 * 
 * @author Camille Laibe
 * @version 20070107
 */
 public class ServletSignIn extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
 {
	private static final long serialVersionUID = 1865215447530932510L;
	private Logger logger = Logger.getLogger(ServletSignIn.class);
	private DbSimpleConnect connection;

	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public ServletSignIn()
	{
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doPost(request, response);
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		ResultSet rs = null;
		String sql = new String();
		String realPass = new String();
		String role = new String();
		String section = "signin-error.html";
		
		// to be able to retrieve UTF-8 elements from HTML forms
		request.setCharacterEncoding("UTF-8");
		
		// recovery of the parameters from the form
		String login = request.getParameter("username");
		String pass = request.getParameter("password");
		
		// recovery the database connection params (from the 'web.xml' file)
		String dbDriver = getServletContext().getInitParameter("biomodels_db_driver");
		String dbType = getServletContext().getInitParameter("biomodels_db_type");
		String dbServer = getServletContext().getInitParameter("biomodels_db_server");
		String dbPort = getServletContext().getInitParameter("biomodels_db_port");
		String dbName = getServletContext().getInitParameter("biomodels_db_database");
		String dbUser = getServletContext().getInitParameter("biomodels_db_user");
		String dbPass = getServletContext().getInitParameter("biomodels_db_password");
		
		// database settings
		connection = new DbSimpleConnect();
		connection.setDriver(dbDriver);
		connection.setType(dbType);
		connection.setServer(dbServer);
		connection.setPort(dbPort);
		connection.setName(dbName);
		connection.setUser(dbUser);
		connection.setPass(dbPass);
		// database connection
		connection.getConnection();
		
		// sql query
		sql = "SELECT password, role FROM `users` WHERE `username` = '" + login + "'";
		rs = connection.request(connection.getStatement(), sql);
		
        try
        {
        	int counter = MiriamUtilities.getRowCount(rs);
        	
        	if (counter == 1)
			{
        		realPass = rs.getString(1);
        		role = rs.getString(2);
        		
        		if (realPass.equals(Base64.encode(pass.getBytes())))
        		{
        			// new session, with attributes
        			HttpSession session = request.getSession();
        			session.setAttribute("login", login);
        			session.setAttribute("role", role);
        			section = "signin-success.html";
        			logger.warn("The user '" + login + "' is now logged in!");
        		}
        		else
        		{
        			logger.warn("The user '" + login + "' tried to log in with a wrong password!");
        		}
			}
        	// for safety...
        	else
        	{
        		logger.warn("The user '" + login + "' (not registered in the User database) tried to log in!");
        		if (counter > 1)
        		{
        			logger.warn("The user '" + login + "' is not unique in the User database!");
        		}
        	}
        }
        catch(SQLException e)
        {
        	logger.error("Error during the processing of a query!");
	    	logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
        	connection.closeConnection();
        }
		
        request.setAttribute("section", section);
        
		RequestDispatcher view = request.getRequestDispatcher("static.jsp");
	    view.forward(request, response);
	}   	  	  
	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException
	{
		//Context env = null;
/*		
		logger.debug("--> SignIn: 'init' method...");
		
		try
		{
			// Obtain our environment naming context
			Context initContext = new InitialContext();
			logger.debug("SignIn: context ok...");
			
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			logger.debug("initial context ok...");
			
			// Look up our data source
			pool = (DataSource) envContext.lookup("jdbc/MiriamDB");
			logger.debug("SignIn: pool ok...");
			
			if (pool == null)
			{
				logger.debug("poool is nulll");
				throw new ServletException("'Miriam' is an unknown DataSource");
			}
			else
			{
				logger.debug("pool is not null.");
				// Allocate and use a connection from the pool
				//Connection conn = pool.getConnection();
				//... use this connection to access the database ...
				//conn.close();
			}
		}
		catch (NamingException ne)
		{
			logger.debug("Exception lauched: " + ne.getMessage());
			throw new ServletException(ne.getMessage());
		}
		catch (Exception se)
		{
			logger.debug("Exception standard lauched:" + se.getMessage());
		}
		logger.debug("SignIn: init end.");
*/
	}
}
