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


package uk.ac.ebi.miriam.web;


import uk.ac.ebi.miriam.web.MiriamUtilities;
import uk.ac.ebi.miriam.db.DbSimpleConnect;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.jasypt.util.password.StrongPasswordEncryptor;

import java.sql.*;


/**
 * <p>Servlet that handles the authentication of the user.
 *
 * <p>
 * Direct connection to the Web Authentication database ('web-auth'), using PreparedStatements.
 * <p>
 * All passwords are stored encrypted with a one-way technique. cf. http://www.jasypt.org/
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
 * @version 20080512
 */
 public class ServletSignIn extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
 {
    private static final long serialVersionUID = 1865215447530932510L;
    private Logger logger = Logger.getLogger(ServletSignIn.class);
    private DbSimpleConnect connection;
    
    
    /*
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletSignIn()
    {
        super();
    }
    
    
    /*
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    
    
    /*
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        ResultSet rs = null;
        String realPass = new String();
        String role = new String();
        RequestDispatcher view = null;
        boolean success = false;

        // to be able to retrieve UTF-8 elements from HTML forms
        request.setCharacterEncoding("UTF-8");

        // recovery of the parameters from the form
        String login = request.getParameter("username");
        String pass = request.getParameter("password");

        // recovery the database connection params (from the 'web.xml' file)
        String dbDriver = getServletContext().getInitParameter("auth_db_driver");
        String dbType = getServletContext().getInitParameter("auth_db_type");
        String dbServer = getServletContext().getInitParameter("auth_db_server");
        String dbPort = getServletContext().getInitParameter("auth_db_port");
        String dbName = getServletContext().getInitParameter("auth_db_database");
        String dbUser = getServletContext().getInitParameter("auth_db_user");
        String dbPass = getServletContext().getInitParameter("auth_db_password");

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
        
        // retrieves the password of a specific user and check if the user is registerd at the same time
        PreparedStatement stmt1 = connection.getPreparedStatement("SELECT u.password FROM auth_user u WHERE (u.login = ?)");
        try
        {
            stmt1.setString(1, login);
            rs = stmt1.executeQuery();
        }
        catch (SQLException e)
        {
            logger.error("Error during a simple (without pooling) SQL query (with prepared statements): check the password of a user!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }

        try
        {
            // the user is registered in the database, we retrieve his password
            int counter = MiriamUtilities.getRowCount(rs);
            if (counter == 1)
            {
                // password stored in the database
                realPass = rs.getString(1);
                
                // tests if the password provided is the same as the one stored in the database
                StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
                if (passwordEncryptor.checkPassword(pass, realPass))
                {
                    // tests is a user has access to the application 'miriam'
                    PreparedStatement stmt2 = connection.getPreparedStatement("SELECT u.login, a.id FROM auth_user u, auth_app a, auth_link l WHERE ((u.login = ?) AND (a.id = ?)  AND (l.ptr_user = ?) AND (l.ptr_app = ?))");
                    try
                    {
                        stmt2.setString(1, login);
                        stmt2.setString(2, "miriam");
                        stmt2.setString(3, login);
                        stmt2.setString(4, "miriam");
                        rs = stmt2.executeQuery();
                    }
                    catch (SQLException e)
                    {
                        logger.error("Error during a simple (without pooling) SQL query (with prepared statements): check if a user has access to an application!");
                        logger.error("SQL Exception raised: " + e.getMessage());
                    }
                    
                    // the user has access to this application
                    int counter2 = MiriamUtilities.getRowCount(rs);
                    if (counter2 == 1)
                    {
                        // retrieves the role of a specific user for a specific application
                        PreparedStatement stmt3 = connection.getPreparedStatement("SELECT l.ptr_role FROM auth_link l WHERE ((l.ptr_user = ?) AND (l.ptr_app = ?))");
                        try
                        {
                            stmt3.setString(1, login);
                            stmt3.setString(2, "miriam");
                            rs = stmt3.executeQuery();
                        }
                        catch (SQLException e)
                        {
                            logger.error("Error during a simple (without pooling) SQL query (with prepared statements): retrieves the role of a user!");
                            logger.error("SQL Exception raised: " + e.getMessage());
                        }
                        
                        // retrieves the role of the user
                        rs.first();
                        role = rs.getString(1);
                        
                        // creation of a new session, with attributes (login and role)
                        HttpSession session = request.getSession();
                        session.setAttribute("login", login);
                        session.setAttribute("role", role);
                        
                        // update the date of last login
                        PreparedStatement stmt4 = connection.getPreparedStatement("UPDATE auth_link SET last_login=NOW() WHERE ((ptr_user=?) AND (ptr_app=?) AND (ptr_role=?))");
                        stmt4.setString(1, login);
                        stmt4.setString(2, "miriam");
                        stmt4.setString(3, role);
                        logger.debug("SQL query: " + stmt4.toString());
                        int resultStatus = stmt4.executeUpdate();
                        stmt4.close();
                        
                        if (resultStatus != 1)   // failure to update the last login date
                        {
                            logger.warn("Unable to update the last login date of '" + login + "' for miriam (" + role + ")");
                        }
                        
                        success = true;
                        logger.info("The user '" + login + "' is now logged in!");
                    }
                    else
                    {
                        if (counter2 == 0)
                        {
                            // the user is in the database but is not registered to access the application 'miriam'
                            logger.warn("The user '" + login + "' is not registered with 'miriam', but tried to access it!");
                        }
                        else
                        {
                            // the user has several roles for one application: this should not be!
                            logger.error("The user '" + login + "' has several roles registered for the following application: 'miriam'!");
                        }
                    }
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
                    logger.warn("The user '" + login + "' is not unique in the user database!");
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Error during the processing of a query!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            try
            {
                stmt1.close();
            }
            catch (SQLException e)
            {
                logger.error("Error during the closing of a prepared statement!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            connection.closeConnection();
        }
        
        // login successful
        if (success)
        {
            view = request.getRequestDispatcher("/user");
        }
        else   // login failure
        {
            //request.setAttribute("section", "signin-error.html");
            request.setAttribute("message", "<p>Invalid username or password! Please, try again.</p>");
            request.setAttribute("section", "signin.html");
            view = request.getRequestDispatcher("static.jsp");
        }
        
        view.forward(request, response);
    }

    /*
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException
    {
        // Context env = null;
        /*
         * logger.debug("--> SignIn: 'init' method..."); try { // Obtain our environment naming context Context
         * initContext = new InitialContext(); logger.debug("SignIn: context ok..."); Context envContext = (Context)
         * initContext.lookup("java:/comp/env"); logger.debug("initial context ok..."); // Look up our data source pool =
         * (DataSource) envContext.lookup("jdbc/MiriamDB"); logger.debug("SignIn: pool ok..."); if (pool == null) {
         * logger.debug("poool is nulll"); throw new ServletException("'Miriam' is an unknown DataSource"); } else {
         * logger.debug("pool is not null."); // Allocate and use a connection from the pool //Connection conn =
         * pool.getConnection(); //... use this connection to access the database ... //conn.close(); } } catch
         * (NamingException ne) { logger.debug("Exception lauched: " + ne.getMessage()); throw new
         * ServletException(ne.getMessage()); } catch (Exception se) { logger.debug("Exception standard lauched:" +
         * se.getMessage()); } logger.debug("SignIn: init end.");
         */
    }
}
