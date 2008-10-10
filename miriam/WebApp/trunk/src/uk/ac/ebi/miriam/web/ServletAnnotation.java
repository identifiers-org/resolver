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


import uk.ac.ebi.miriam.db.Annotation;
import uk.ac.ebi.miriam.db.DataTypeDao;
import uk.ac.ebi.miriam.db.DbPoolConnect;
import uk.ac.ebi.miriam.db.Tag;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;


/**
 * <p>Servlet which Handles the queries to the database for browsing the annotation part of a data type.
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
 * @version 20080704
 */
public class ServletAnnotation extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 728527662440668395L;
    private Logger logger = Logger.getLogger(ServletAnnotation.class);
    private DbPoolConnect pool;
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletAnnotation()
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
        boolean exist = false;
        ArrayList<Annotation> data = new ArrayList<Annotation>();
        AnnoDisplay result = new AnnoDisplay();
        RequestDispatcher view = null;
        String name = null;
        
        // retrieves the parameter (identifier of a data type)
        String id = request.getParameter("data");
        result.setId(id);
        
        // retrieves the name of the database pool
        String poolName = getServletContext().getInitParameter("miriam_db_pool");
        DataTypeDao dao = new DataTypeDao(poolName);
        
        // tests if there is an existing data type with this name
        if (id != null)
        {
            exist = dao.exists(id);
            name = dao.getDataTypeName(id);
        }
        
        if (exist)
        {
            // connection pool management
            pool = new DbPoolConnect(poolName);
            
            // test without 'newConnection()' before, let's see...
            pool.getConnection();
            
            sql = "SELECT anno.id, anno.format, anno.name, anno.information FROM mir_annotation anno, mir_anno_link link WHERE ((link.ptr_datatype = '" + id + "') AND (link.ptr_annotation = anno.id)) ORDER BY anno.format, anno.name";
            rs = pool.request(pool.getStatement(), sql);
            Annotation anno = null;
            
            boolean notEmpty;
            try
            {
                String format = new String();
                notEmpty = rs.next();
                
                while (notEmpty)
                {
                    
                    Tag temp = new Tag();
                    format = rs.getString("anno.format");
                    temp.setId(rs.getString("anno.id"));
                    temp.setName(rs.getString("anno.name"));
                    temp.setInfo(rs.getString("anno.information"));
                    
                    // an annotation with the same format already exists
                    if ((anno != null) && (anno.getFormat().equals(format)))
                    {
                        // adds the new tag
                        anno.addTag(temp);
                    }
                    else
                    {
                        // no annotation created so far
                        if (anno == null)
                        {
                            // creation of an Annotation
                            anno = new Annotation(format);
                            
                            // adds the new tag
                            anno.addTag(temp);
                        }
                        else   // an annotation has already been created 
                        {
                            // adds the previous annotation to list
                            data.add(anno);
                            
                            // creation of a new Annotation (new format)
                            anno = new Annotation(format);
                            
                            // adds the new tag
                            anno.addTag(temp);
                        }
                    }
                    // next resource (if it exists)
                    notEmpty = rs.next();
                }
            }
            catch (SQLException e)
            {
                logger.error("Error while searching the annotation!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            
            // adds the last annotation to the list
            if (anno != null)
            {
                data.add(anno);
            }
            
            // add the list of annotation to the resulting object
            result.setAnnotation(data);
            
            // checks if the data type is obsolete (to add a warning to the user in this case)
            HashMap<String, String> obsoleteInfo = dao.getObsoleteInfo(id);
            if (null != obsoleteInfo)
            {
                request.setAttribute("obsolete", true);
                request.setAttribute("replacementName", obsoleteInfo.get("replacementName"));
                request.setAttribute("replacementId", obsoleteInfo.get("replacementId"));
                request.setAttribute("replacementComment", obsoleteInfo.get("replacementComment"));
            }
            
            request.setAttribute("name", name);
            request.setAttribute("data", result);
            view = request.getRequestDispatcher("annotation.jsp");
        }
        else   // the data type doesn't exist
        {
            String message = "Sorry, the requested data type doesn't exist in the database...";
            request.setAttribute("message", "<p>" + message + "</p>");
            view = request.getRequestDispatcher("/dbBrowse");
        }
        
        // cleaning
        pool.closeConnection();
        dao.clean();
        
        view.forward(request, response);
    }
    
}
