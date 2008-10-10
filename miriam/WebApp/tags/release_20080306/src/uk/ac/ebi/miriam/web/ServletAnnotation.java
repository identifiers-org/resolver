/*
 * MIRIAM Resources (Web Application)
 * MIRIAM is an online resource created to catalogue biological data types,
 * their URIs and the corresponding physical URLs,
 * whether these are controlled vocabularies or databases.
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2007  Camille Laibe (EMBL - European Bioinformatics Institute, Computational Neurobiology Group)
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
import uk.ac.ebi.miriam.db.DbPoolConnect;
import uk.ac.ebi.miriam.db.Tag;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;


/**
 * <p>Servlet which Handles the queries to the database for browsing the annotation part of a data-type.
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2007 Camille Laibe (EMBL - European Bioinformatics Institute, Computational Neurobiology Group)
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
 * @version 20070923
 */
public class ServletAnnotation extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 728527662440668395L;
    private Logger logger = Logger.getLogger(ServletAnnotation.class);
    private DbPoolConnect pool;
    
    
    /* (non-Java-doc)
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletAnnotation()
    {
      super();
    }
    
    
    /* (non-Java-doc)
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        ResultSet rs = null;
        String sql = new String();
        boolean exist = false;
        ArrayList data = new ArrayList();
        AnnoDisplay result = new AnnoDisplay();
        RequestDispatcher view = null;
        
        // retrieves the parameter (identifier of a data type)
        String id = request.getParameter("data");
        result.setId(id);
        
        // retrieve the name of the database pool
        String poolName = getServletContext().getInitParameter("miriam_db_pool");

        // tests if there is an existing data-type with this name
        if (id != null)
        {
          // connexion pool management
          pool = new DbPoolConnect(poolName);

          // test without 'newConnection()' before, let's see...
          pool.getConnection();

          sql = "SELECT name FROM mir_datatype WHERE (datatype_id='" + id + "')";
          rs = pool.request(pool.getStatement(), sql);
          try
          {
            if (rs.first())
            {
                result.setName(rs.getString("name"));
                exist = true;
            }
          }
          catch (SQLException e)
          {
            logger.warn("An exception occured during the test to know if a particular data-type exists!");
            logger.warn("SQL Exception raised: " + e.getMessage());
          }
        }
        
        if (exist)
        {
            sql = "SELECT anno.format, anno.tag, anno.information, anno.id FROM mir_datatype data, mir_annotation anno WHERE ((data.datatype_id = '" + id + "') AND (data.datatype_id = anno.ptr_datatype)) ORDER BY anno.format";
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
                    temp.setName(rs.getString("anno.tag"));
                    temp.setInfo(rs.getString("anno.information"));
                    
                    /*System.out.println("FORMAT: " + format);*/
                    
                    // an annotation with the same format already exists
                    if ((anno != null) && (anno.getFormat().equals(format)))
                    {
                        /*System.out.println("new tag to be added: " + temp.getName());*/
                        // adds the new tag
                        anno.addTag(temp);
                        /*System.out.println("new tag added: " + temp.getName());*/
                    }
                    else
                    {
                        // no annotation created so far
                        if (anno == null)
                        {
                            /*System.out.println("creation new annotation");*/
                            // creation of an Annotation
                            anno = new Annotation(format);
                            /*System.out.println("creation new annotation done");*/
                            
                            // adds the new tag
                            /*System.out.println("new tag to be added (annotation created from scratch): " + temp.getName());*/
                            anno.addTag(temp);
                            /*System.out.println("new tag added (annotation created from scratch): " + temp.getName());*/
                        }
                        else   // an annotation has already been created 
                        {
                            /*System.out.println("add previous annotation");*/
                            // adds the previous annotation to list
                            data.add(anno);
                            
                            // creation of a new Annotation (new format)
                            /*System.out.println("create new new annotation done");*/
                            anno = new Annotation(format);
                            /*System.out.println("new new annotation creation done");*/
                            
                            // adds the new tag
                            /*System.out.println("new tag to be added (newly created annotation): " + temp.getName());*/
                            anno.addTag(temp);
                            /*System.out.println("new tag added (newly created annotation): " + temp.getName());*/
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
                /*System.out.println("add last annotation to the list");*/
                data.add(anno);
                /*System.out.println("last annotation done");*/
            }
            
            /* DEBUG
            if ((data != null) && (! data.isEmpty()))
            {
                for (int i=0; i<data.size(); ++i)
                {
                    System.out.println(((Annotation) data.get(i)).toString());
                }
            }
            */
            
            // add the list of annotation to the resulting object
            result.setAnnotation(data);
            
            request.setAttribute("data", result);
            view = request.getRequestDispatcher("annotation.jsp");
        }
        else   // the data type doesn't exist
        {
            request.setAttribute("section", "introduction.html");
            view = request.getRequestDispatcher("static.jsp");
        }
        
        /*System.out.println("end of process");*/
        
        // without closing the statement, let's see...
        pool.closeConnection();
        
        view.forward(request, response);
    }
    
}
