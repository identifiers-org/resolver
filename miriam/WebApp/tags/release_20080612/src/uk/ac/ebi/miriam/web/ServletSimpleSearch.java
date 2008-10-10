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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

import uk.ac.ebi.miriam.db.DbPoolConnect;


/**
 * <p>Servlet that handles the basic search.
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
 * @version 20080514
 */
public class ServletSimpleSearch extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 3903960397735739235L;
    private Logger logger = Logger.getLogger(ServletSimpleSearch.class);
    private DbPoolConnect pool;
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletSimpleSearch()
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
        RequestDispatcher view = null;
        ResultSet rs = null;
        String message = null;
        HashMap<String, List<String>> result = new HashMap<String, List<String>>();
        int counter = 0;
        
        // to be able to retrieve UTF-8 elements from HTML forms
        request.setCharacterEncoding("UTF-8");

        // recovery of the parameters from the form
        String words = request.getParameter("searchedTerms");
        
        logger.debug("Basic search for: " + words);
        
        // connection to the database pool
        String poolName = getServletContext().getInitParameter("miriam_db_pool");
        pool = new DbPoolConnect(poolName);
        pool.getConnection();
        
        // search in the 'data type' table
        PreparedStatement stmt1 = pool.getPreparedStatement("SELECT name, definition, datatype_id FROM mir_datatype WHERE ((datatype_id LIKE ?) OR (name LIKE ?) OR (definition LIKE ?))");
        // search in the 'synonym' table
        PreparedStatement stmt2 = pool.getPreparedStatement("SELECT d.name, d.definition, d.datatype_id FROM mir_datatype d, mir_synonym s WHERE ((d.datatype_id = s.ptr_datatype) AND (s.name LIKE ?))");
        // search in the 'resource' table
        PreparedStatement stmt3 = pool.getPreparedStatement("SELECT d.name, d.definition, d.datatype_id FROM mir_datatype d, mir_resource r WHERE ((d.datatype_id = r.ptr_datatype) AND ((r.resource_id LIKE ?) OR (r.info LIKE ?) OR (r.institution LIKE ?) OR (r.location LIKE ?)))");
        
        try
        {
            // from 'data type' table
            stmt1.setString(1, "%" + words + "%");
            stmt1.setString(2, "%" + words + "%");
            stmt1.setString(3, "%" + words + "%");
            logger.debug("SQL query: " + stmt1.toString());
            rs = stmt1.executeQuery();
            
            int nbLines = MiriamUtilities.getRowCount(rs);
            for (int i=1; i<=nbLines; ++i)
            {
                ArrayList<String> temp = new ArrayList<String>();
                temp.add(rs.getString("datatype_id"));
                temp.add(rs.getString("definition"));
                result.put(rs.getString("name"), temp);
                
                rs.next();
            }
            stmt1.close();
            
            // from 'synonym' table
            stmt2.setString(1, "%" + words + "%");
            logger.debug("SQL query: " + stmt2.toString());
            rs = stmt2.executeQuery();
            nbLines = MiriamUtilities.getRowCount(rs);
            for (int i=1; i<=nbLines; ++i)
            {
                ArrayList<String> temp = new ArrayList<String>();
                temp.add(rs.getString("datatype_id"));
                temp.add(rs.getString("definition"));
                result.put(rs.getString("name"), temp);
                
                rs.next();
            }
            stmt2.close();
            
            // from 'resource' table
            stmt3.setString(1, "%" + words + "%");
            stmt3.setString(2, "%" + words + "%");
            stmt3.setString(3, "%" + words + "%");
            stmt3.setString(4, "%" + words + "%");
            logger.debug("SQL query: " + stmt3.toString());
            rs = stmt3.executeQuery();
            nbLines = MiriamUtilities.getRowCount(rs);
            for (int i=1; i<=nbLines; ++i)
            {
                ArrayList<String> temp = new ArrayList<String>();
                temp.add(rs.getString("datatype_id"));
                temp.add(rs.getString("definition"));
                result.put(rs.getString("name"), temp);
                
                rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("An exception occured during the processing or closing of a prepared statement (search in data types)!");
            logger.error("SQL Exception raised: " + e.getMessage());
            message = "Sorry, an error occurred during the processing of your search. As a consequence we cannot guarantee the accuracy of the result.<br />Please contact us <a href=\"mdb?section=contact\" title=\"Contact page\">via this page</a> to solve this issue.";
        }
        finally
        {
            // without closing the statements, let's see...
            pool.closeConnection();
        }
        
        counter = result.size();
        
        if (null != message)
        {
            request.setAttribute("message", "<p>" + message + "</p>");
        }
        request.setAttribute("counter", counter);
        request.setAttribute("words", words);
        request.setAttribute("data", result);
        view = request.getRequestDispatcher("search_result.jsp");
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
