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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.GregorianCalendar;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jasypt.util.password.StrongPasswordEncryptor;

import uk.ac.ebi.miriam.db.DbSimpleConnect;


/**
 * <p>Servlet that handles the password change function (update the password and send a reminder to the user).
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
public class ServletPassChange extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -2508702980340674515L;
    private Logger logger = Logger.getLogger(ServletPassChange.class);
    private DbSimpleConnect connection;
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletPassChange()
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
        String message = null;
        ResultSet rs = null;
        
        // to be able to retrieve UTF-8 elements from HTML forms
        request.setCharacterEncoding("UTF-8");
        
        // recovery of the parameters from the form
        String oldPass = request.getParameter("oldPass");
        String newPass1 = request.getParameter("newPass1");
        String newPass2 = request.getParameter("newPass2");
        
        // checks if the two new passwords are equals
        if (newPass1.equals(newPass2))
        {
            // retrieves the database connection parameters (from the 'web.xml' file)
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
            
            // retrieves the login of the current user
            HttpSession session = request.getSession(false); // returns pre-existing session or null
            String login = (String) session.getAttribute("login");
            
            // checks if the old password is correct
            PreparedStatement stmt0 = connection.getPreparedStatement("SELECT firstname, lastname, password, email FROM auth_user WHERE (login=?)");
            try
            {
                stmt0.setString(1, login);
                rs = stmt0.executeQuery();
                
                int counter = MiriamUtilities.getRowCount(rs);
                if (counter == 1)
                {
                    String oldPassDb = rs.getString("password");
                    String firstName = rs.getString("firstname");
                    String lastName = rs.getString("lastname");
                    String email = rs.getString("email");
                    
                    // checks if the old password given is the same than the one stored in the database
                    StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
                    if (passwordEncryptor.checkPassword(oldPass, oldPassDb))   // passwords are equal
                    {
                        // encrypt the new password
                        String encryptedPassword = passwordEncryptor.encryptPassword(newPass1);
                        
                        // updates the database
                        PreparedStatement stmt1 = connection.getPreparedStatement("UPDATE auth_user SET password=? WHERE (login=?)");
                        stmt1.setString(1, encryptedPassword);
                        stmt1.setString(2, login);
                        logger.debug("SQL query (password update): " + stmt1.toString());
                        int resultStatus = stmt1.executeUpdate();
                        stmt1.close();
                        
                        if (resultStatus != 1)   // failure to update the password
                        {
                            message = "Sorry, we are unable to update your password!<br /> Please contact us <a href=\"mdb?section=contact\" title=\"Contact page\">via this page</a> to solve this issue. Thank you.";
                        }
                        else
                        {
                            // retrieves the version of the web application (sid, alpha, main or demo)
                            String version = getServletContext().getInitParameter("version");
                            
                            // retrieves the email address of the administrator
                            String emailAdmin = getServletContext().getInitParameter("admin.email");
                            
                            // sending the email
                            StringBuilder emailBody = new StringBuilder();
                            emailBody.append("Dear " + firstName + " " + lastName + ",");
                            GregorianCalendar cal = new GregorianCalendar();
                            emailBody.append("\n\nDate: " + cal.getTime());
                            emailBody.append("\nThis is a confirmation that your password to access MIRIAM Resources has been successfully updated.");
                            emailBody.append("\nIf you have problems to login, you can send an email to the administrator, using the following address: <" + emailAdmin + ">.");
                            emailBody.append("\nThank you for participating in the MIRIAM project!");
                            emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMIRIAM Resources\nhttp://www.ebi.ac.uk/miriam/");
                            MailFacade.send("MIRIAM-" + version + "@ebi.ac.uk", email, "[MIRIAM Resources] Password updated", emailBody.toString(), "text/plain; charset=UTF-8");
                            
                            logger.info("Password updated for the user '" + login + "', email sent to " + email);
                            
                            message = "Your password has now been successfully updated!";
                        }
                    }
                    else   // passwords are different
                    {
                        message = "As long as you don't accurately provide your current password, we can't update it!";
                    }
                }
                else   // safety...
                {
                    message = "Sorry, we are unable to update your password!<br /> Please contact us <a href=\"mdb?section=contact\" title=\"Contact page\">via this page</a> to solve this issue. Thank you.";
                    if (counter > 1)
                    {
                        logger.warn("The user '" + login + "' is not unique in the Users database!");
                    }
                }
            }
            catch(SQLException e)
            {
                message = "Sorry, we are unable to update your password!<br /> Please contact us <a href=\"mdb?section=contact\" title=\"Contact page\">via this page</a> to solve this issue. Thank you.";
                logger.error("Error during the processing of a query!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            finally
            {
                connection.closeConnection();
            }
        }
        else   // the new passwords are different
        {
            message = "The new password and its confirmation need to be equal!";
        }
        
        request.setAttribute("message", "<p>" + message + "</p>");
        RequestDispatcher view = request.getRequestDispatcher("/user");
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
