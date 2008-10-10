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


import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
// for database pooling
import uk.ac.ebi.miriam.db.DbPoolManage;


/**
 * <p>
 * Engine (controller) of the MIRIAM web application: receives all the requests and transmits them to the dedicated servlets
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
 * @version 20070921
 */
public class WebEngine extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
  private static final long serialVersionUID = -301588370558888237L;
  private Logger logger = Logger.getLogger(WebEngine.class);
  private DbPoolManage pool = null;


  /* (non-Java-doc)
   * @see javax.servlet.http.HttpServlet#HttpServlet()
   */
  public WebEngine()
  {
    super();
  }

  /* (non-Java-doc)
   * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    // default parameters
    String jsp = "static.jsp";
    HttpSession session = request.getSession(false);   // returns pre-existing session or null

    if (request.getParameter("section") != null)
    {
      String section = request.getParameter("section");

      if (section.equalsIgnoreCase("signin"))
      {
        if (MiriamUtilities.isSessionValid(session))
        {
          request.setAttribute("section", "already_logged.html");
        }
        else
        {
          request.setAttribute("section", "signin.html");
        }
      }
      else
      {
        if (section.equalsIgnoreCase("browse"))
        {
          // TODO
          // change this!
          // get parameter for the data-type
          jsp = "/dbBrowse";
        }
        else
        {
          if (section.equalsIgnoreCase("request"))
          {
            // get the parameter for the request needed
            String question = request.getParameter("request");
            //if (question != null)
            //{
              request.setAttribute("request", question);
            //}
            jsp = "dynamic_WS.jsp";
          }
          else
          {
            if (section.equalsIgnoreCase("submit"))
            {
              //request.setAttribute("section", "submission.html");    // old one
              String question = request.getParameter("request");
              request.setAttribute("request", question);
              jsp = "data_submit.jsp";

              /*
              if (MiriamUtilities.isSessionValid(session))
              {
                request.setAttribute("section", "submission.html");
              }
              else
              {
                request.setAttribute("section", "need_login.html");
              }
              */
            }
            else
            {
              if (section.equalsIgnoreCase("news"))
              {
                // TODO
                // change this!
                request.setAttribute("section", "news.html");
              }
              else
              {
                if (section.equalsIgnoreCase("faq"))
                {
                  // TODO
                  // change this!
                  request.setAttribute("section", "faq.html");   // TODO
                }
                else
                {
                  if (section.equalsIgnoreCase("contact"))
                  {
                    request.setAttribute("section", "contact.html");
                  }
                  else
                  {
                    if (section.equalsIgnoreCase("export"))
                    {
                      request.setAttribute("section", "exports.html");
                    }
                    else
                    {
                      if (section.equalsIgnoreCase("ws"))
                      {
                        request.setAttribute("section", "web_services.html");
                      }
                      else
                      {
                        if (section.equalsIgnoreCase("edit"))
                        {
                          jsp = "/dataTypeEdit";
                          /*
                            // only "admi' can edit an already existing data-type
                            if (MiriamUtilities.isUserAdministator(session))
                            {
                              jsp = "/dataTypeEdit";
                            }
                            else
                            {
                              request.setAttribute("section", "need_login.html");
                            }
                          */
                        }
                        else
                        {
                          if (section.equalsIgnoreCase("qualifiers"))
                          {
                            request.setAttribute("section", "qualifiers.html");
                          }
                          else
                          {
                            if (section.equalsIgnoreCase("standard"))
                            {
                              request.setAttribute("section", "standard.html");
                            }
                            else
                            {
                                if (section.equalsIgnoreCase("ws_help"))
                                {
                                    request.setAttribute("section", "web_services_queries.html");
                                }
                                else
                                {
                                    if (section.equalsIgnoreCase("media"))
                                    {
                                        request.setAttribute("section", "media.html");
                                    }
                                    else
                                    {
                                        if (section.equalsIgnoreCase("docs"))
                                        {
                                            request.setAttribute("section", "docs.html");
                                        }
                                        else
                                        {
                                            if (section.equalsIgnoreCase("annotation"))
                                            {
                                                jsp = "/annotation";
                                            }
                                            else   // Default choice
                                            {
                                                request.setAttribute("section", "introduction.html");
                                            }
                                        }
                                    }
                                }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    else
    {
      request.setAttribute("section", "introduction.html");
    }

    RequestDispatcher view = request.getRequestDispatcher(jsp);
    view.forward(request, response);
  }

  /* (non-Java-doc)
   * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    doGet(request, response);
  }

  /**
   * Every Servlet has a begining.
   */
  public void init() throws ServletException
  {
    logger.info("--> WebEngine, init() method <--");

    // recovery the database connection params (from the 'web.xml' file)
    String dbDriver = getServletContext().getInitParameter("miriam_db_driver");
    String dbType = getServletContext().getInitParameter("miriam_db_type");
    String dbServer = getServletContext().getInitParameter("miriam_db_server");
    String dbPort = getServletContext().getInitParameter("miriam_db_port");
    String dbName = getServletContext().getInitParameter("miriam_db_database");
    String dbUser = getServletContext().getInitParameter("miriam_db_user");
    String dbPass = getServletContext().getInitParameter("miriam_db_password");
    String dbPool = getServletContext().getInitParameter("miriam_db_pool");

    pool = new DbPoolManage(dbDriver, dbType, dbServer, dbPort, dbName, dbUser, dbPass, dbPool);
  }


  /**
   * "This is the end... my only friend."
   */
  public void destroy()
  {
    // closing of the pool
    pool.close();
  }
}
