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


import uk.ac.ebi.miriam.db.DataTypeHybernate;
import uk.ac.ebi.miriam.db.DbPoolConnect;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;


/**
 * <p>Servlet which handles the modification of an existed data-type (part 1: displays the information).
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
 * @version 20070518
 */
public class ServletDataTypeEdit extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
  private static final long serialVersionUID = -1926642849664446407L;
  private Logger logger = Logger.getLogger(ServletDataTypeEdit.class);
  private DbPoolConnect pool;

  /* (non-Java-doc)
   * @see javax.servlet.http.HttpServlet#HttpServlet()
   */
  public ServletDataTypeEdit()
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
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    DataTypeHybernate data = new DataTypeHybernate();
    RequestDispatcher view = null;
    ResultSet rs = null;
    String sql = new String();
    boolean exist = false;

    // retrieves the parameter (name of a data-type)
    String param = request.getParameter("data");

    // retrieve the name of the database pool
    String poolName = getServletContext().getInitParameter("miriam_db_pool");

    // tests if there is an existing data-type with this name
    if (param != null)
    {
      // connexion pool management
      pool = new DbPoolConnect(poolName);

      // test without 'newConnection()' before, let's see...
      pool.getConnection();

      sql = "SELECT name FROM mir_datatype WHERE (datatype_id='" + param + "')";
      rs = pool.request(pool.getStatement(), sql);
      try
      {
        if (rs.first())
        {
          exist = true;
        }
      }
      catch (SQLException e)
      {
        logger.warn("An exception occured during the test to know if a particular data-type exists!");
        logger.warn("SQL Exception raised: " + e.getMessage());
      }

      // without closing the statement, let's see...
      pool.closeConnection();
    }

    if (exist)
    {
      logger.debug("Edition mode of the information about a data-type needed...");

      // retrieve all the information about the data-type
      data.retrieveData(poolName, param);

      // modify the MIRIAM IDs of the documentation (to remove the data-type part and the '#')
      data.transDocIds();

      request.setAttribute("data", data);

      view = request.getRequestDispatcher("data_edit.jsp");
    }
    else
    {
      request.setAttribute("section", "introduction.html");
      view = request.getRequestDispatcher("static.jsp");
    }

    view.forward(request, response);
  }
}
