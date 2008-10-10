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
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;


/**
 * <p>
 * Servlet that handles the deletion of one or more data-type(s) in the database
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
 * @version 20070109
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
    // retrieves the names of all the data-types which need to be removed
    String[] data = request.getParameterValues("datatype2remove");

    // retrieves the version of the web application (sid, alpha, main or demo)
    String version = getServletContext().getInitParameter("version");

    // retrieves the user logged who asked for the action
    HttpSession session = request.getSession();
    String user = (String) session.getAttribute("login");

    // retrieves the email address of the administrator
    String emailAdr = getServletContext().getInitParameter("email");

    // TODO:
    // - finish the work (now we just send an email)!

    // we send an email to ask the administrator to do the job for us (yeah, you lazy people)
    String emailBody = "\nData-type(s) need(s) to be removed [" + version + " version]:";
    for (int i=0; i<data.length; ++i)
      {
      emailBody += "\n\tData type identifier (" + i + "): " +  data[i];
      }
    emailBody += "\nUser: " + user;
    GregorianCalendar cal = new GregorianCalendar();
    emailBody += "\nDate: " + cal.getTime();
    emailBody += "\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMiriam";
    MailFacade.send("Miriam-" + version + "@ebi.ac.uk", emailAdr, "[Miriam-" + version + "] data-type(s) to remove", emailBody, "text/plain");
    logger.debug("email sent.");

    request.setAttribute("data", data);

    RequestDispatcher view = request.getRequestDispatcher("data_delete.jsp");
    view.forward(request, response);
  }
}
