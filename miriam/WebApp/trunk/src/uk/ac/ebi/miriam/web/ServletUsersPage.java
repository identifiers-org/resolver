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
import uk.ac.ebi.miriam.db.UserManageDetails;

import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
//import org.apache.log4j.Logger;


/**
 * <p>Servlet that handles the management of the users (by the administrator).
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
 * @version 20080515
 */
public class ServletUsersPage extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 4908221107378929063L;
    //private Logger logger = Logger.getLogger(ServletUsersPage.class);
    
    
    /**
     * Default constructor.
     */
    public ServletUsersPage()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        RequestDispatcher view = null;
        String message = null;
        
        // retrieves the user logged (if any)
        HttpSession session = request.getSession();
        String user = (String) session.getAttribute("login");
        String role = (String) session.getAttribute("role");
        
        // the user is logged in
        if (null != user)
        {
            if (role.equals(MiriamUtilities.USER_ADMIN))
            {
                // recovery the database connection params (from the 'web.xml' file)
                String dbDriver = getServletContext().getInitParameter("auth_db_driver");
                String dbType = getServletContext().getInitParameter("auth_db_type");
                String dbServer = getServletContext().getInitParameter("auth_db_server");
                String dbPort = getServletContext().getInitParameter("auth_db_port");
                String dbName = getServletContext().getInitParameter("auth_db_database");
                String dbUser = getServletContext().getInitParameter("auth_db_user");
                String dbPass = getServletContext().getInitParameter("auth_db_password");
                
                UserDao data = new UserDao();
                data.setParameters(dbDriver, dbType, dbServer, dbPort, dbName, dbUser, dbPass);
                List<UserManageDetails> listOfUsers = data.getUsers();
                
                request.setAttribute("data", listOfUsers);
                request.setAttribute("counter", listOfUsers.size());
                view = request.getRequestDispatcher("users.jsp");
            }
            else
            {
                message = "Sorry, you are not authorised to access this page!";
                request.setAttribute("section", "signin.html");
                view = request.getRequestDispatcher("static.jsp");
            }
        }
        else   // user not logged in
        {
            message = "Sorry, you need to login prior to access this page!";
            request.setAttribute("section", "signin.html");
            view = request.getRequestDispatcher("static.jsp");
        }
        
        if (null != message)
        {
            request.setAttribute("message", "<p>" + message + "</p>");
        }
        view.forward(request, response);
    }
    
    
    /**
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException
    {
        // nothing here.
    }
}
