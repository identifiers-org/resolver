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
import javax.servlet.http.HttpSession;


/**
 * <p>Servlet which handles the modification of the tags associated with a data type.
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
 * @version 20080609
 */
public class ServletTagEdit extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -5760501195995064502L;
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletTagEdit()
    {
        super();
    }
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        RequestDispatcher view = null;
        SimpleDataType data = null;
        List<Tag> tags = new ArrayList<Tag>();
        List<Tag> allTags = new ArrayList<Tag>();
        String message = null;
        
        // retrieves the id of the data type to be updated
        String dataId = request.getParameter("data");
        
        // current session
        HttpSession session = request.getSession();
        
        // user with curator's rights
        if (MiriamUtilities.hasCuratorRights(session))
        {
            // retrieves the name of the database pool
            String poolName = getServletContext().getInitParameter("miriam_db_pool");
            
            // checks if the data type exists
            DataTypeDao dao = new DataTypeDao(poolName);
            boolean exist = dao.exists(dataId);
            dao.clean();
            
            // the data type exists
            if (exist)
            {
                // retrieves the data type (simple version)
                DataTypeDao dataDao = new DataTypeDao(poolName);
                data = dataDao.getSimpleDataTypeById(dataId);
                dataDao.clean();
                
                TagDao tagDao = new TagDao(poolName);
                // retrieves the list of tags associated with the data type
                tags = tagDao.retrieveTags(dataId);
                // retrieves the list of all existing tags (this list will be used for adding new tag)
                allTags = tagDao.retrieveAllTags();
                // remove the tags already associated with the data type
                allTags.removeAll(tags);
                tagDao.clean();
                
                request.setAttribute("data", data);
                request.setAttribute("tags", tags);
                request.setAttribute("allTags", allTags);
                view = request.getRequestDispatcher("tag_edit.jsp");
            }
            else   // the data type doesn't exist (or no parameter in the URL)
            {
                message = "The requested data type doesn't exist!";
                request.setAttribute("section", "not_existing.html");
                view = request.getRequestDispatcher("static.jsp");
            }
        }
        else   // user no authorised to access the edition of tags
        {
            message = "Sorry, you are not authorised to access this feature. Alternatively, you can use the following form.";
            request.setAttribute("info", "Edit tags for: " + dataId);
            view = request.getRequestDispatcher("support.jsp");
        }
        
        if (null != message)
        {
            request.setAttribute("message", "<p>" + message + "</p>");
        }
        view.forward(request, response);
    }
}
