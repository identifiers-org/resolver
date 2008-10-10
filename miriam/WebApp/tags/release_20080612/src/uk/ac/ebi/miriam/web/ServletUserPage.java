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
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

import uk.ac.ebi.miriam.db.UserDao;


/**
 * <p>Servlet that handles the personal page of a logged user.
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
public class ServletUserPage extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -1641170917109370773L;
    private Logger logger = Logger.getLogger(ServletUserPage.class);
    
    
    /**
     * Default constructor.
     */
    public ServletUserPage()
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
        
        // retrieves the user logged (if any)
        HttpSession session = request.getSession();
        String user = null;
        //String role = null;
        user = (String) session.getAttribute("login");
        //role = (String) session.getAttribute("role");
        
        // the user is logged in
        if (null != user)
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
            
            if (data.retrieve(user))   // successful retrieval of the personnal data of the user
            {
                request.setAttribute("data", data);
                view = request.getRequestDispatcher("user.jsp");
            }
            else   // the personnal data of the user can't be retrieved from the database
            {
                logger.warn("Can't retrieve personnal information about '" + user + "'");
                request.setAttribute("message", "<p>We are unable to retrieve your user information!<br /> Please contact us in order to solve this issue. We apologise for any inconvenience caused.</p>");
                request.setAttribute("section", "signin.html");
                view = request.getRequestDispatcher("static.jsp");
            }
        }
        else   // the user is not logged in -> forward to the login page
        {
            request.setAttribute("section", "signin.html");
            view = request.getRequestDispatcher("static.jsp");
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
