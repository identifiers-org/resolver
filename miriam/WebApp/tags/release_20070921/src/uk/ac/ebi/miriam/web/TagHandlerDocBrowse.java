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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import org.apache.log4j.Logger;

import uk.ac.ebi.miriam.db.DbPoolConnect;
import uk.ac.ebi.miriam.web.MiriamUtilities;


/**
 * <p>
 * Custom tag handler for browsing the documentations (the ones stored with a URI, not a physical address)
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
 * @version 20061220
 */
public class TagHandlerDocBrowse extends SimpleTagSupport
{
  private Logger logger = Logger.getLogger(TagHandlerDocBrowse.class);
  private ArrayList data;   /* list of URIs */
  private String pool;   /* name of the database connection pool */


  /**
   * Setter of 'data'
   * @param ArrayList list of all the MIRIAM URIs of the documentations
   */
  public void setData(ArrayList data)
  {
    this.data = data;
  }

  /**
   * Setter of 'pool'
   * @param String name of the database connection pool
   */
  public void setPool(String pool)
  {
    this.pool = pool;
  }

  /**
   * this method contains all the business part of a tag handler
   */
  public void doTag() throws JspException, IOException
  {
    logger.debug("tag handler for data-type documentations (from URIs)");
    JspContext context = getJspContext();
    DbPoolConnect dbPool;
    ResultSet sqlResult;
    String prefixPrimary = new String();
    String suffixPrimary = new String();
    String infoPrimary = new String();
    String institutionPrimary = new String();
    String locationPrimary = new String();
    ArrayList prefixList = new ArrayList();
    ArrayList suffixList = new ArrayList();
    ArrayList infoList = new ArrayList();
    ArrayList institutionList = new ArrayList();
    ArrayList locationList = new ArrayList();
    ArrayList urlList = new ArrayList();

    // connexion pool management
    dbPool = new DbPoolConnect(pool);

    // test without 'newConnection()' before, let's see...
    dbPool.getConnection();

    // searching for all the resoures available for documentations (and their properties)
    String query = "SELECT resource_id, url_element_prefix, url_element_suffix, info, institution, location FROM mir_resource WHERE (ptr_datatype = 'MIR:00000015')";
    sqlResult = dbPool.request(dbPool.getStatement(), query);
    int nbRows = MiriamUtilities.getRowCount(sqlResult);

    try
    {
      for (int j=1; j<=nbRows; ++j)
      {
        // this is the main resource for documentations (MIR:00100028 <=> Medline at the EBI)
        if ((sqlResult.getString("resource_id")).compareToIgnoreCase("MIR:00100028") == 0)
        {
          prefixPrimary = MiriamUtilities.urlConvert(sqlResult.getString("url_element_prefix"));
          suffixPrimary = MiriamUtilities.urlConvert(sqlResult.getString("url_element_suffix"));
          infoPrimary = sqlResult.getString("info");
          institutionPrimary = sqlResult.getString("institution");
          locationPrimary = sqlResult.getString("location");
        }
        else
        {
          prefixList.add(MiriamUtilities.urlConvert(sqlResult.getString("url_element_prefix")));
          suffixList.add(MiriamUtilities.urlConvert(sqlResult.getString("url_element_suffix")));
          infoList.add(sqlResult.getString("info"));
          institutionList.add(sqlResult.getString("institution"));
          locationList.add(sqlResult.getString("location"));
        }

        sqlResult.next();
      }
    }
    catch (SQLException e)
      {
        logger.error("Error during the transformation URIs to URLs (for display)!");
        logger.error("SQL Exception raised: " + e.getMessage());
      }

    // without closing the statement, let's see...
    dbPool.closeConnection();

    // for all the pieces of documentation...
    for (int i=0; i<data.size(); ++i)
    {
      // empty the list
      urlList.clear();
      // get the ID of the element from the URI (removing the URI of the data-type)
      String uri = MiriamUtilities.getElementPart((String) data.get(i));

      // for all the secondary resources available which stored documentation information, we build the physical address...
      for (int j=0; j<prefixList.size(); ++j)
      {
        String temp = prefixList.get(j) + uri + suffixList.get(j);
        urlList.add(temp);
      }

      String url = prefixPrimary + uri + suffixPrimary;

      context.setAttribute("id", String.valueOf(i+1));

      context.setAttribute("url", url);
      context.setAttribute("info", infoPrimary);
      context.setAttribute("institution", institutionPrimary);
      context.setAttribute("location", locationPrimary);

      context.setAttribute("urlList", urlList);
      context.setAttribute("infoList", infoList);
      context.setAttribute("institutionList", institutionList);
      context.setAttribute("locationList", locationList);

      getJspBody().invoke(null);   // process the body of the tag and print it to the response
    }
  }
}
