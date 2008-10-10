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


import uk.ac.ebi.miriam.db.TagDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <p>Servlet that handles the edition of data types associated with a tag (tag management page).
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
 * @version 20080710
 */
public class ServletTagEditLinks extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -4588065837361774014L;
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletTagEditLinks()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String jsp = null;
        String message = null;
        
        // the user is logged and has curation privileges
        if (MiriamUtilities.hasCuratorRights(request.getSession()))
        {
            // retrieves the information about the tag
            String tagId = request.getParameter("tagId");
            String counterStr = request.getParameter("counter");
            int counter = Integer.parseInt(counterStr);
            List<String> newData = new ArrayList<String>();
            for (int i=1; i<= counter; ++i)
            {
                String tmp = request.getParameter("n_link_" + i);
                if (null != tmp)
                {
                    newData.add(tmp);
                }
            }
            
            // retrieves the name of the database pool
            String poolName = getServletContext().getInitParameter("miriam_db_pool");
            
            // tag management
            TagDao tagDao = new TagDao(poolName);
            
            String tagName = tagDao.getTagName(tagId);
            
            // updates the links
            boolean result = tagDao.updateLinks(tagId, newData);
            
            // cleaning
            tagDao.clean();
            
            // message to the user
            if (result)
            {
                message = "The tag '" + tagName + "' (" + tagId + ") has been updated with success.";
            }
            else
            {
                message = "An error occurred during the update of the tag '" + tagName + "' (" + tagId + ")!";
            }
            
            jsp = "/tagsEdit";
        }
        else   // not logged or not enough privileges
        {
            message = "You need to be authenticated to access this feature!";
            request.setAttribute("section", "signin.html");
            jsp = "static.jsp";
        }
        
        if (null != message)
        {
            request.setAttribute("message", "<p>" + message + "</p>");
        }
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
}
