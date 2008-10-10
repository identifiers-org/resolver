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


import uk.ac.ebi.miriam.db.CuraDataType;
import uk.ac.ebi.miriam.db.CuraDataTypeDao;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;


/**
 * <p>Servlet that handles the display of a single data type in the curation pipeline.
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
 * @version 20080610
 */
public class ServletCurationData extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 7591896355199780934L;
    private Logger logger = Logger.getLogger(ServletCurationData.class);
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletCurationData()
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
        String jsp;
        String message = null;
        
        logger.debug("request data type in curation pipeline");
        
        // the user is logged and has curation privileges
        if (MiriamUtilities.hasCuratorRights(request.getSession()))
        {
            // retrieves the parameter (identifier of a data type)
            String id = request.getParameter("data");
            
            if (! MiriamUtilities.isEmpty(id))
            {
                // retrieves the requested data type
                String poolName = getServletContext().getInitParameter("miriam_db_pool");
                CuraDataTypeDao curaDao = new CuraDataTypeDao(poolName);
                CuraDataType result = curaDao.retrieve(id);
                
                // the data type exist
                if (null != result)
                {
                    // modifies the MIRIAM IDs of the documentation (to remove the data type part and the separator)
                    for (int i=0; i<result.getDocumentationIDs().size(); ++i)
                    {
                        String tmp = MiriamUtilities.getElementPart(result.getDocumentationID(i));
                        // decode the string
                        tmp = URLDecoder.decode(tmp, "UTF-8");
                        result.setDocumentationID(i, tmp);
                    }
                    
                    request.setAttribute("data", result);
                    jsp = "data_curation.jsp";
                }
                else
                {
                    message = "The requested data type doesn't exist in the curation pipeline!";
                    request.setAttribute("section", "not_existing.html");
                    jsp = "static.jsp";
                }
            }
            else
            {
                request.setAttribute("section", "not_existing.html");
                jsp = "static.jsp";
            }
        }
        else   // not logged or not enough privileges
        {
            message = "You need to be authenticated to access this feature!";
            request.setAttribute("section", "signin.html");
            jsp = "static.jsp";
        }
        
        // sends information of the result of the process to the user
        if (null != message)
        {
            request.setAttribute("message", "<p>" + message + "</p>");
        }
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
}
