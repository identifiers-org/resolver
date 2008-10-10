/*
 * MIRIAM Resources (Web Application)
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


package uk.ac.ebi.miriam.web;


import uk.ac.ebi.compneur.util.Base64;
import uk.ac.ebi.miriam.web.MiriamUtilities;
import uk.ac.ebi.miriam.db.DbSimpleConnect;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import java.sql.*;


/**
 * <p>Servlet that handles the authentification of the user.
 *
 * <p>
 * Direct connection to the 'users' database (BioModels Database, in fact), using PreparedStatements.
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
 * @version 20070516
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
        PreparedStatement stmt = connection.getPreparedStatement("SELECT password, role FROM users WHERE username = ?");
        try
        {
            stmt.setString(1, login);
            rs = stmt.executeQuery();
        }
        catch (SQLException e)
        {
            logger.debug("Error during a simple (without pooling) SQL query (with prepared statements)!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }

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
              logger.info("The user '" + login + "' is now logged in!");
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
            try
            {
                stmt.close();
            }
            catch (SQLException e)
            {
                logger.error("Error during the closing of a prepared statement!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
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
