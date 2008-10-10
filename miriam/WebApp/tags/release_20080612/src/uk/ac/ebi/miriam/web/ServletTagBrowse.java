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


import uk.ac.ebi.miriam.db.SimpleDataType;
import uk.ac.ebi.miriam.db.Tag;
import uk.ac.ebi.miriam.db.TagDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import org.apache.log4j.Logger;


/**
 * <p>Servlet which Handles the display of the tags (list of all the tags, list of all the data types associated with a given tag).
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
 * @version 20080528
 */
public class ServletTagBrowse extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 4354970895676023682L;
    //private Logger logger = Logger.getLogger(ServletTagBrowse.class);
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletTagBrowse()
    {
        super();
    }
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        RequestDispatcher view = null;
        String message = null;
        
        // retrieves the name of the database pool
        String poolName = getServletContext().getInitParameter("miriam_db_pool");
        
        // retrieves the parameter (identifier of a tag), if any
        String id = request.getParameter("id");
        
        // tag management
        TagDao tagDao = new TagDao(poolName);
        
        // a specific tag has been requested
        if (! MiriamUtilities.isEmpty(id))
        {
            if (tagDao.exists(id))
            {
                List<SimpleDataType> data = null; 
                Tag tag = null;
                tag = tagDao.getTagFromId(id);
                data = tagDao.getDataTypesFromTagId(tag.getId());
                List<Tag> tags = new ArrayList<Tag>();
                tags.add(tag);
                
                request.setAttribute("tagCount", tags.size());   // should be equals to 1 
                request.setAttribute("dataCount", data.size());
                request.setAttribute("data", data);
                request.setAttribute("tags", tags);
                
                view = request.getRequestDispatcher("metadata_links.jsp");
            }
            else   // the tag doesn't exit: displays the whole list of tags with a message
            {
                List<Tag> data = null;
                data = tagDao.retrieveTags();
                
                request.setAttribute("data", data);
                message = "The requested tag doesn't exit!";
                request.setAttribute("message", "<p>" + message + "</p>");
                view = request.getRequestDispatcher("metadata.jsp");
            }
        }
        else   // no parameter: displays the whole list of tags
        {
            List<Tag> data = null;
            data = tagDao.retrieveTags();
            
            request.setAttribute("data", data);
            view = request.getRequestDispatcher("metadata.jsp");
        }
        
        // cleaning
        tagDao.clean();
        
        view.forward(request, response);
    }
}
