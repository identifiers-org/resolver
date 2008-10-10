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


import uk.ac.ebi.miriam.db.UserDao;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;


/**
 * <p>Servlet that handles the update of a user information.
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
 * @version 20080513
 */
public class ServletUpdateUserInfo extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -62371977743718760L;
    private Logger logger = Logger.getLogger(ServletUpdateUserInfo.class);
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletUpdateUserInfo()
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
        
        // to be able to retrieve UTF-8 elements from HTML forms
        request.setCharacterEncoding("UTF-8");
        
        // recovery of the parameters from the form
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String organisation = request.getParameter("organisation");
        
        // retrieves the database connection parameters (from the 'web.xml' file)
        String dbDriver = getServletContext().getInitParameter("auth_db_driver");
        String dbType = getServletContext().getInitParameter("auth_db_type");
        String dbServer = getServletContext().getInitParameter("auth_db_server");
        String dbPort = getServletContext().getInitParameter("auth_db_port");
        String dbName = getServletContext().getInitParameter("auth_db_database");
        String dbUser = getServletContext().getInitParameter("auth_db_user");
        String dbPass = getServletContext().getInitParameter("auth_db_password");
        
        // retrieves the login of the current user
        HttpSession session = request.getSession(false); // returns pre-existing session or null
        String login = (String) session.getAttribute("login");
        
        UserDao data = new UserDao();
        data.setParameters(dbDriver, dbType, dbServer, dbPort, dbName, dbUser, dbPass);
        
        // all the parameters ùust not be empty
        if ((!MiriamUtilities.isEmpty(firstName)) && (!MiriamUtilities.isEmpty(lastName)) && (!MiriamUtilities.isEmpty(email)) && (!MiriamUtilities.isEmpty(organisation)))
        {
            // email valid (very simple check)
            if (email.contains("@"))
            {
                data.setLogin(login);
                data.setFirstName(firstName);
                data.setLastName(lastName);
                data.setEmail(email);
                data.setOrganisation(organisation);
                
                if (data.updateUserInfo())
                {
                    message = "Your user information have been updated successfully!";
                    logger.debug("User information updated for '" + login + "'");
                }
                else
                {
                    message = "Sorry, we are unable to update your user informations!<br /> Please contact us <a href=\"mdb?section=contact\" title=\"Contact page\">via this page</a> to solve this issue. Thank you.";
                    logger.warn("Unable to update the user information of '" + login + "'!");
                }
            }
            else
            {
                message = "The email address must be valid!";
            }
        }
        else
        {
            message = "All the fields must be populated!";
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
