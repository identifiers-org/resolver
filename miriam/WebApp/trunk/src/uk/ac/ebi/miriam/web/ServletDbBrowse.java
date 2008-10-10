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


import uk.ac.ebi.miriam.db.DataTypeDao;
import uk.ac.ebi.miriam.db.DbPoolConnect;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;


/**
 * <p>Servlet which Handles the queries to the database for browsing the data types.
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
 * @version 20080711
 */
public class ServletDbBrowse extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 8149522239197419697L;
    private Logger logger = Logger.getLogger(ServletDbBrowse.class);
    private DbPoolConnect pool;
    
    
    /** 
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletDbBrowse()
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
    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        ResultSet rs = null;
        ResultSet rs2 = null;
        ArrayList<List<String>> result = new ArrayList<List<String>>();
        String sql = new String();
        String sql2 = new String();
        String id = new String();
        RequestDispatcher view = null;
        String message = null;
        int offset = 0;   // starting point of the display of the data types (default value: first data type in the alphabetical order)
        int number = 20;   // number of data types per page (default value: 20)
        int max = 0;   // number of data types stored
        String option = "";   // option of the display (current, obsolete or all)
        boolean hasNext = false;   // next page display necessary or not (last page)
        boolean hasPrevious = false;   // previous page display necessary or not (first page)
        int previousOffset = 0;
        int nextOffset = 0;
        
        // retrieves the parameter corresponding to the name of a data type
        String param = request.getParameter("data");
        
        // a specific data type has been requested (not the display of the list of stored data types)
        if (param != null)
        {
            logger.debug("Complete information about a data type needed...");
            view = request.getRequestDispatcher("/dataTypeInfo");
        }
        else   // dislays the list of stored data types
        {
            // connection to the database pool
            String poolName = getServletContext().getInitParameter("miriam_db_pool");
            pool = new DbPoolConnect(poolName);
            
            // retrieves the parameter indicating the scope of the request: 'all', 'obsolete' or 'current' (default if no param)
            String display = request.getParameter("display");
            // retrieves the parameter indicating the offset of the display
            String offsetStr = request.getParameter("offset");
            if (null != offsetStr)
            {
                offset = Integer.parseInt(offsetStr);
            }
            // retrieves the parameter indicating the number of data types per page to display
            String numberStr = request.getParameter("nb");
            if (null != numberStr)
            {
                number = Integer.parseInt(numberStr);
            }
            
            // get the number of data types currently stored in the data base
            DataTypeDao dataDao = new DataTypeDao(poolName);
            if ((null != display) && (display.equalsIgnoreCase("obsolete")))
            {
                max = dataDao.getNbObsoleteDataTypes();
            }
            else if ((null != display) && (display.equalsIgnoreCase("all")))
            {
                max = dataDao.getNbAllDataTypes();
            }
            else  // current data types only
            {
                max = dataDao.getNbDataTypes();
            }
            dataDao.clean();
            
            // checking for the value of the number of data types per page (sets a minimum and avoid negative values)
            if (number < 10)
            {
                number = 20;
            }
            if (number > max)
            {
                number = max;
            }
            // checking for the value of the offset (sets a min and max)
            if (offset < 0)
            {
                offset = 0;
            }
            if (offset >= max)
            {
                offset = 0;
            }
            
            // display = all, obsolete or current (== no value)
            if (null == display)
            {
                display = "current";   // default value
            }
            else
            {
                option = "&amp;display=" + display;
            }
            
            logger.debug("browsing the data types (" + offset + ", " + number + ", " + display + ")...");
            
            if (display.equalsIgnoreCase("all"))
            {
                sql = "SELECT name, definition, datatype_id FROM mir_datatype ORDER BY name LIMIT " + offset + "," + number;
                message = "ALL";
            }
            else
            {
                if (display.equalsIgnoreCase("obsolete"))
                {
                    sql = "SELECT name, definition, datatype_id FROM mir_datatype WHERE (obsolete=1) ORDER BY name LIMIT " + offset + "," + number;
                    message = "OBSOLETE";
                }
                else   // default case
                {
                    sql = "SELECT name, definition, datatype_id FROM mir_datatype WHERE (obsolete=0) ORDER BY name LIMIT " + offset + "," + number;
                }
            }
            
            // test without 'newConnection()' before, let's see...
            pool.getConnection();
            
            rs = pool.request(pool.getStatement(), sql);
            int nbLines = DbPoolConnect.getRowCount(rs);
            if (nbLines >= 1)
            {
                ArrayList<String> internalId = new ArrayList<String>();
                ArrayList<String> name = new ArrayList<String>();
                ArrayList<String> uri = new ArrayList<String>();
                ArrayList<String> def = new ArrayList<String>();
                // ArrayList nameURL = new ArrayList();
                for (int i = 1; i <= nbLines; ++i)
                {
                    try
                    {
                        internalId.add(rs.getString("datatype_id"));
                        name.add(rs.getString("name"));
                        // nameURL.add(MiriamUtilities.nameTrans(rs.getString("name")));
                        
                        String temp = new String();
                        temp = rs.getString(2);
                        def.add(temp); /**/
                        /*
                         * // the definition is quite long if (temp.length() > 15) { // we just display the 15 first
                         * characters of the definition at this step temp = temp.substring(0, 15); temp += "[...]";
                         * def.add(temp); } else { def.add(temp); }
                         */
                        id = rs.getString(3);
                        
                        // search the official URI (URL by default, URN if no URL)
                        String uri_temp = new String();
                        sql2 = "SELECT uri, uri_type FROM mir_uri WHERE ((ptr_datatype='" + id + "') AND (deprecated='0'))";
                        rs2 = pool.request(pool.getStatement(), sql2);
                        int nbLines2 = DbPoolConnect.getRowCount(rs2);
                        if (nbLines2 == 1)
                        {
                            uri_temp = rs2.getString(1);
                        }
                        else
                        {
                            // TODO
                            // - better algorithm with detection of two URN or two URL in the db
                            if (nbLines2 == 2)
                            {
                                uri_temp = rs2.getString(1);   // default value
                                for (int j = 1; j <= nbLines2; ++j)
                                {
                                    if (rs2.getString(2) == "URL")
                                    {
                                        uri_temp = rs2.getString(1);
                                    }
                                    rs2.next();
                                }
                            }
                            else   // error
                            {
                                logger.warn("The data type (" + id + ") has no (or more than 2) URI!");
                            }
                        }
                        uri.add(uri_temp);
                        
                        rs.next();
                    }
                    catch (SQLException e)
                    {
                        logger.warn("An exception occured during the processing of the result of a SQL query!");
                        logger.warn("SQL Exception raised: " + e.getMessage());
                    }
                    result.add(internalId);
                    result.add(name);
                    result.add(uri);
                    result.add(def);
                    //result.add(nameURL);
                }
            }
            
            // without closing the statement, let's see...
            pool.closeConnection();
            
            // generates the value of the offset for the previous and next link
            if ((offset + number) < max)
            {
                hasNext = true;
                nextOffset = number + offset;
            }
            else
            {
                // no next link
                hasNext = false;
            }
            if (offset > 0)
            {
                hasPrevious = true;
                if (offset - number >= 0)
                {
                    previousOffset = offset - number;
                }
                else
                {
                    previousOffset = 0;
                }
            }
            else
            {
                // no previous link
                hasPrevious = false;
            }
            
            
            logger.debug("end of browsing.");
            
            request.setAttribute("preferences", message);
            request.setAttribute("data", result);
            request.setAttribute("option", option);
            request.setAttribute("hasPrevious", hasPrevious);
            request.setAttribute("hasNext", hasNext);
            request.setAttribute("previousOffset", previousOffset);
            request.setAttribute("nextOffset", nextOffset);
            request.setAttribute("number", number);
            request.setAttribute("max", max);
            view = request.getRequestDispatcher("dynamic_browse.jsp");
        }
        
        view.forward(request, response);
    }
}
