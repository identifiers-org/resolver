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


package uk.ac.ebi.miriam.wsi;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ajaxtags.servlets.BaseAjaxServlet;


/**
 * <p>
 * "controller" part of the MIRIAM web application, client for the Web Services
 * <p>
 * Link to the function: isDeprecated
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
 * @version 20060802
 */
public class ServletIsDeprecated extends BaseAjaxServlet
{
  private static final long serialVersionUID = -129585994328807153L;


  public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
    String newResult = new String();

    // recovery of the parameters
    String uri = request.getParameter("isDeprecatedParam");

    // retrieves the address to access to the Web Services
    String endPoint = getServletContext().getInitParameter("webServicesEndpoint");

    // processing the request
      MiriamLink mir = new MiriamLink(endPoint);
      String result = mir.isDeprecatedAnswer(uri);

      // checking of the answer
      if ((result == null) || (result.equalsIgnoreCase("")) || ((!result.equalsIgnoreCase("true")) && (!result.equalsIgnoreCase("false"))))
      {
        newResult = "<p>You need to enter a valid <acronym title=\"Uniform Request Identifier\">URI</acronym>!</p>";
      }
      else
      {
        if (result.equalsIgnoreCase("true"))
        {
          newResult = "<p>Warning: this URI is deprecated!</p>";
        }
        if (result.equalsIgnoreCase("false"))
        {
          newResult = "<p>This URI is not deprecated.</p>";
        }
      }

      return newResult;
  }
}
