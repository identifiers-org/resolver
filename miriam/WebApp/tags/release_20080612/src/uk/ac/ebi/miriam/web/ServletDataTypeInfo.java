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


/**
 * TODO
 *
 * - add the documentation links
 * -
 *
 */


package uk.ac.ebi.miriam.web;


import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

import uk.ac.ebi.miriam.db.DataTypeHybernate;
import uk.ac.ebi.miriam.db.DbPoolConnect;


/**
 * <p>Servlet that handles the queries to the database for accessing the complete information about a data type
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
 * @author Camille Laibe
 * @version 20080513
 */
public class ServletDataTypeInfo extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -6045828167769196502L;
    private Logger logger = Logger.getLogger(ServletDataTypeInfo.class);
    private DbPoolConnect pool;
    
    
    /** 
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletDataTypeInfo()
    {
        super();
    }
    
    
    /** 
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        ResultSet rs = null;
        String sql = new String();
        DataTypeHybernate data = new DataTypeHybernate();
        RequestDispatcher view = null;
        boolean exist = false;
        
        // retrieves the parameter (identifier of a data type)
        String id = request.getParameter("data");
        
        // retrieves the name of the database pool
        String poolName = getServletContext().getInitParameter("miriam_db_pool");
        
        // tests if there is an existing data type with this name
        if (! MiriamUtilities.isEmpty(id))
        {
            // connection pool management
            pool = new DbPoolConnect(poolName);
            
            // test without 'newConnection()' before, let's see...
            pool.getConnection();
            
            sql = "SELECT name FROM mir_datatype WHERE (datatype_id='" + id + "')";
            rs = pool.request(pool.getStatement(), sql);
            try
            {
                if (rs.first())
                {
                    exist = true;
                }
            }
            catch (SQLException e)
            {
                logger.warn("An exception occured during the test to know if a particular data type exists!");
                logger.warn("SQL Exception raised: " + e.getMessage());
            }
            
            // without closing the statement, let's see...
            pool.closeConnection();
        }
        
        if (exist)
        {
            // retrieves all the information about the data type
            data.retrieveData(poolName, id);
            
            // some HTML modifications
            /*
             * if (data.getDeprecatedURIs().isEmpty()) { ArrayList temp = new ArrayList(); temp.add("<i>No element</i>");
             * data.setDeprecatedURIs(temp); }
             */
            
            request.setAttribute("data", data);
            view = request.getRequestDispatcher("data_browse.jsp");
        }
        else
        {
            request.setAttribute("section", "not_existing.html");
            view = request.getRequestDispatcher("static.jsp");
        }
        
        view.forward(request, response);
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }
}
