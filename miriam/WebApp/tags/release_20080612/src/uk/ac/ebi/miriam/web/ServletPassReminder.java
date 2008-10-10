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


import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.jasypt.util.password.StrongPasswordEncryptor;
import java.sql.*;
import java.util.GregorianCalendar;

import uk.ac.ebi.miriam.db.DbSimpleConnect;


/**
 * <p>Servlet that handles the password reset function (generates a new random one and send it to the user).
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
public class ServletPassReminder extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 1865215447530932510L;
    private Logger logger = Logger.getLogger(ServletPassReminder.class);
    private DbSimpleConnect connection;
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletPassReminder()
    {
        super();
    }
    
    
    /** 
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    
    
    /** 
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        ResultSet rs = null;
        String email = new String();
        String name = new String();
        String surname = new String();
        String message = null;
        
        // to be able to retrieve UTF-8 elements from HTML forms
        request.setCharacterEncoding("UTF-8");
        
        // recovery of the parameters from the form
        String login = request.getParameter("username");
        
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
        
        // checks if the user exists in the our records
        PreparedStatement stmt0 = connection.getPreparedStatement("SELECT firstname, lastname, email FROM auth_user WHERE (login=?)");
        try
        {
            stmt0.setString(1, login);
            rs = stmt0.executeQuery();
            
            int counter = MiriamUtilities.getRowCount(rs);
            
            // one and only one result = one user with the same username in the DB
            if (counter == 1)
            {
                // information about the user retrieved
                email = rs.getString("email");
                name = rs.getString("firstname");
                surname = rs.getString("lastname");
                
                // creates a new (random) password
                String newPass = (MiriamUtilities.randomPassGen(12));
                // encrypts the password
                StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
                String encryptedPassword = passwordEncryptor.encryptPassword(newPass);
                
                // updates the database
                PreparedStatement stmt = connection.getPreparedStatement("UPDATE auth_user SET password=? WHERE (login=?)");
                stmt.setString(1, encryptedPassword);
                stmt.setString(2, login);
                logger.debug("SQL query (password reset): " + stmt.toString());
                int resultStatus = stmt.executeUpdate();
                stmt.close();
                
                if (resultStatus != 1)   // failure to update the last login date
                {
                    message = "Sorry, we are unable to reset your password!<br /> Please contact us <a href=\"mdb?section=contact\" title=\"Contact page\">via this page</a> to solve this issue. Thank you.";
                    logger.warn("Unable to reset the password of '" + login + "'!");
                }
                else
                {
                    // retrieves the version of the web application (sid, alpha, main or demo)
                    String version = getServletContext().getInitParameter("version");
                    
                    // retrieves the email address of the administrator
                    String emailAdmin = getServletContext().getInitParameter("admin.email");
                    
                    // sending the email
                    StringBuilder emailBody = new StringBuilder();
                    emailBody.append("Dear " + name + " " + surname + ",");
                    GregorianCalendar cal = new GregorianCalendar();
                    emailBody.append("\n\nDate: " + cal.getTime());
                    emailBody.append("\nYou asked for the reinitialisation of your password in order to access MIRIAM Resources.");
                    emailBody.append("\nHere are your new login details:");
                    emailBody.append("\nlogin: " + login);
                    emailBody.append("\npassword: " + newPass);
                    emailBody.append("\n\nWe strongly suggest you to change the password for a more convenient one.");
                    emailBody.append("\nIf you still have problems to login, you can send an email to the administrator, using the following address: <" + emailAdmin + ">.");
                    emailBody.append("\nThank you for participating in the MIRIAM project!");
                    emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMIRIAM Resources\nhttp://www.ebi.ac.uk/miriam/");
                    MailFacade.send("MIRIAM-" + version + "@ebi.ac.uk", email, "[MIRIAM Resources] Password Reset", emailBody.toString(), "text/plain; charset=UTF-8");
                    
                    logger.info("Password reset for the user '" + login + "', email sent to " + email);
                    message = "Your password has been successfully sent to the email address registered in our users database.<br />Should you experience any problem in receiving the new password, please <a href=\"mdb?section=contact\" title=\"Contact page\">contact us</a>.";
                }
            }
            else
            {
                // for safety...
                if (counter > 1)
                {
                    message = "We are sorry, but an error occurred while reseting your password. Please contact us <a href=\"mdb?section=contact\" title=\"Contact page\">via this page</a> in order to solve this issue. Thank you";
                    logger.warn("The user '" + login + "' is not unique in the Users database!");
                }
                else
                {
                    message = "We are very sorry, but we can't find you in our list of authorised users.<br /> If you want to participate, you can contact us <a href=\"mdb?section=contact\" title=\"Contact page\">via this page</a>. Thank you for your interest in this project.";
                    logger.info("An anonymous user wanted to reset the password for the non existing login '" + login + "'!");
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
        
        request.setAttribute("message", "<p>" + message + "</p>");
        request.setAttribute("section", "signin.html");
        RequestDispatcher view = request.getRequestDispatcher("static.jsp");
        view.forward(request, response);
    }
    
    
    /** 
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException
    {
        // nothing here.
    }
}
