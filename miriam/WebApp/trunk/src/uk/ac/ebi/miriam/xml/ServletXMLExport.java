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


package uk.ac.ebi.miriam.xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

import uk.ac.ebi.miriam.web.MiriamUtilities;


/**
 * <p>
 * Servlet for the export of the whole MIRIAM DataBase in an XML file (interface part, not business part).
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
 * @version 20061215
 */
public class ServletXMLExport extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
  private static final long serialVersionUID = 9134086517606902726L;
  private Logger logger = Logger.getLogger(Miriam2XML.class);
  private final String realName = "Resources.xml";


  /*
   * Constructor
   */
  public ServletXMLExport()
  {
    super();
  }

  /*
   * Answering to the requests (via html forms)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    String path = new String();
    String fileName = new String();
    String poolName = new String();
    String shortName = new String();
    ServletOutputStream stream = null;
    BufferedInputStream buf = null;

    //String path = request.getContextPath();
    path = getServletContext().getInitParameter("exportDir");

    // recovery of the parameters
    shortName = request.getParameter("getNameParam");

    // the name given is not proper (full of spaces)
    if (MiriamUtilities.isEmpty(shortName))
    {
      shortName = "Resources";
    }

    // add the extension '.xml' if it doesn't already exist
    if (shortName.indexOf(".xml") == -1)
    {
      shortName += ".xml";
    }

    // add the path
    fileName = path + realName;

    // retrieves the name of the pool
    poolName = getServletContext().getInitParameter("miriam_db_pool");

    // processing the request
    Miriam2XML dump = new Miriam2XML(poolName, fileName);
    if (dump.export())
    {
      // everything's ok
    }
    else
    {
      logger.error("Unable to export the MIRIAM database!");
    }

    // sending the file generated
    try
    {
      stream = response.getOutputStream();
      File xml = new File(fileName);
      // set response headers
      response.setContentType("text/xml");
      response.addHeader("Content-Disposition", "attachment; filename=" + shortName);
      response.setContentLength((int) xml.length());
      FileInputStream input = new FileInputStream(xml);
      buf = new BufferedInputStream(input);
      int readBytes = 0;
      // read from the file and write to the servletOutputStream
      while ((readBytes = buf.read()) != -1)
      {
        stream.write(readBytes);
      }
    }
    catch (IOException e)
    {
      throw new ServletException(e.getMessage());
    }
    finally
    {
      // close the input/output streams
      if (stream != null)
      {
        stream.close();
      }
      if (buf != null)
      {
        buf.close();
      }
    }

  }

}
