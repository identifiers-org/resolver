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


/**
 * <p>
 * Servlet that handles the logout of the user.
 *
 * @web.servlet
 *   name="ServletSignOut"
 *   display-name="ServletSignOut"
 *
 * @web.servlet-mapping
 *   url-pattern="/ServletSignOut"
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
 * @version 20060612
 */
public class ServletSignOut extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
  private static final long serialVersionUID = 7111287432284901060L;

  /* (non-Java-doc)
   * @see javax.servlet.http.HttpServlet#HttpServlet()
   */
  public ServletSignOut()
  {
    super();
  }

  /* (non-Java-doc)
   * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    HttpSession session = null;
    session = request.getSession(false);   // returns pre-existing session or null
    if (session != null)
    {
      session.removeAttribute("login");
      session.invalidate();
      request.setAttribute("section", "signout.html");
    }

    RequestDispatcher view = request.getRequestDispatcher("static.jsp");
      view.forward(request, response);
  }

  /* (non-Java-doc)
   * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    doGet(request, response);
  }

  /* (non-Javadoc)
   * @see javax.servlet.GenericServlet#init()
   */
  public void init() throws ServletException
  {
    // TODO Auto-generated method stub
    super.init();
  }
}
