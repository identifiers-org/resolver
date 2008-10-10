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


import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import java.sql.*;
import java.util.GregorianCalendar;

import uk.ac.ebi.compneur.util.Base64;
import uk.ac.ebi.miriam.db.DbSimpleConnect;


/**
 * <p>Servlet that handles the password reminder function
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
 * @version 20060928
 */
 public class ServletPassReminder extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
 {
  private static final long serialVersionUID = 1865215447530932510L;
  private Logger logger = Logger.getLogger(ServletPassReminder.class);
  private DbSimpleConnect connection;


  /* (non-Java-doc)
   * @see javax.servlet.http.HttpServlet#HttpServlet()
   */
  public ServletPassReminder()
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
    String pass = new String();
    String email = new String();
    String name = new String();
    String surname = new String();
    String section = "pass_reminder_error.html";

    // to be able to retrieve UTF-8 elements from HTML forms
    request.setCharacterEncoding("UTF-8");

    // recovery of the parameters from the form
    String login = request.getParameter("username");

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
    sql = "SELECT u.password, p.email, p.given_name, p.family_name FROM `users` u, `persons` p WHERE ((u.person_id = p.person_id) AND (u.username = '" + login + "'))";
    rs = connection.request(connection.getStatement(), sql);

        try
        {
          int counter = getRowCount(rs);

          // one and only one result = one user with the same username in the DB
          if (counter == 1)
      {
            pass = rs.getString("password");
            email = rs.getString("email");
            name = rs.getString("given_name");
            surname = rs.getString("family_name");

            // retrieve the readable form of the password
            String realPass = new String(Base64.decode(pass));

          // retrieves the version of the web application (sid, alpha, main or demo)
          String version = getServletContext().getInitParameter("version");

          // retrieves the email address of the administrator
          String emailAdmin = getServletContext().getInitParameter("email");

          // sending the email
          String emailBody = "Dear " + name + " " + surname + ",";
          GregorianCalendar cal = new GregorianCalendar();
          emailBody += "\n\nDate: " + cal.getTime();
          emailBody += "\nYou asked for your password in order to access to MIRIAM Resources.";
          emailBody += "\nHere is the information requested: ";
          emailBody += "\n\tlogin: " + login;
          emailBody += "\n\tpassword: " + realPass;
          emailBody += "\n\nIf you still have problems to connect yourself, you can send an email to the administrator, using the following address: <" + emailAdmin + ">.";
          emailBody += "\n\nThank you for participating in the MIRIAM project!";
          emailBody += "\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMiriam Resources";
          MailFacade.send("Miriam-" + version + "@ebi.ac.uk", email, "[Miriam] Password Reminder", emailBody, "text/plain; charset=UTF-8");

          logger.info("Password reminder: email sent to " + login);
          section = "pass_reminder_success.html";
      }
          // for safety...
          if (counter > 1)
          {
            logger.warn("The user '" + login + "' is not unique in the User database!");
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
    // nothing here.
  }


  /*
   * Count the number of rows in a ResultSet (like 'getColumnCount()' for the columns)
   * @param ResultSet object
   * @return the number of rows in the ResultSet object
   */
  private int getRowCount(ResultSet data)
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
}
