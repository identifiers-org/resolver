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
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;


/**
 * <p>
 * Servlet that handles the deletion of one or more data type(s) in the database.
 * Actually only send an email to the administrator and the curators.
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
 * @version 20080314
 */
public class ServletDataTypeDelete extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
  private static final long serialVersionUID = -3063704726612619124L;
  private Logger logger = Logger.getLogger(ServletDataTypeDelete.class);


  /* (non-Java-doc)
   * @see javax.servlet.http.HttpServlet#HttpServlet()
   */
  public ServletDataTypeDelete()
  {
    super();
  }


  /* (non-Java-doc)
   * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    doPost(request, response);
  }


  /* (non-Java-doc)
   * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    // retrieves the names of all the data types which need to be removed
    String[] data = request.getParameterValues("datatype2remove");

    // retrieves the version of the web application (sid, alpha, main or demo)
    String version = getServletContext().getInitParameter("version");

    // retrieves the user logged who asked for the action
    HttpSession session = request.getSession();
    String user = (String) session.getAttribute("login");

    // retrieves the email addresses of the administrator and the curators
    String emailAdmin = getServletContext().getInitParameter("admin.email");
    String[] emailsCura = getServletContext().getInitParameter("curators.email").split(",");
    
    // TODO:
    // - finish the work (now we just send an email)!

    // we send an email to ask the administrator to do the job for us (yeah, you lazy people)
    StringBuilder emailBody = new StringBuilder();
    emailBody.append("\nThe following data type(s) need(s) to be removed [" + version + " version]:");
    for (int i=0; i<data.length; ++i)
    {
        emailBody.append("\n\tData type identifier (" + i + "): " +  data[i]);
    }
    emailBody.append("\n\nRequested by: " + user);
    GregorianCalendar cal = new GregorianCalendar();
    emailBody.append("\nDate: " + cal.getTime());
    emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMIRIAM Resources\nhttp://www.ebi.ac.uk/miriam/");
    
    MailFacade.send("MIRIAM-" + version + "@ebi.ac.uk", emailAdmin, emailsCura, "[MIRIAM-" + version + "] data-type(s) to remove", emailBody.toString(), "text/plain");
    
    logger.debug("Notification about a deletion: email(s) sent.");

    request.setAttribute("data", data);

    RequestDispatcher view = request.getRequestDispatcher("data_delete.jsp");
    view.forward(request, response);
  }
}
