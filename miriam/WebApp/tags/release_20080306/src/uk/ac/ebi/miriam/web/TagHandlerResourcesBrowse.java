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


import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.log4j.Logger;

import uk.ac.ebi.miriam.db.Resource;


/**
 * <p>
 * Custom tag handler for browsing a ResultSet object
 * (for the summary page of the database)
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
 * @version 20070107
 */
public class TagHandlerResourcesBrowse extends SimpleTagSupport
{
  private Logger logger = Logger.getLogger(TagHandlerResourcesBrowse.class);
  private ArrayList data;   /* list of the resources */


  /**
   * Setter of 'data'
   * @param ArrayList of the resources
   */
  public void setData(ArrayList data)
  {
    this.data = data;
  }

  /**
   * this method contains all the business part of a tag handler
   */
  public void doTag() throws JspException, IOException
  {
    logger.debug("tag handler for data-type resources (physical locations) browsing");
    JspContext context = getJspContext();
    int size;

    size = data.size();

    for (int j=0; j<size; ++j)
    {
      if (j == (size - 1))
      {
        context.setAttribute("end", "true");
      }
      else
      {
        context.setAttribute("end", "false");
      }
      context.setAttribute("id", String.valueOf(j+1));
      context.setAttribute("resourceId", ((Resource) data.get(j)).getId());
      context.setAttribute("prefix", MiriamUtilities.urlConvert((String) ((Resource) data.get(j)).getUrl_prefix()));
      context.setAttribute("suffix", MiriamUtilities.urlConvert((String) ((Resource) data.get(j)).getUrl_suffix()));
      context.setAttribute("base", MiriamUtilities.urlConvert((String) ((Resource) data.get(j)).getUrl_root()));
      context.setAttribute("info", ((Resource) data.get(j)).getInfo());
      context.setAttribute("institution", ((Resource) data.get(j)).getInstitution());
      context.setAttribute("location", ((Resource) data.get(j)).getLocation());
      if (((Resource) data.get(j)).isObsolete())
      {
        context.setAttribute("obsolete", "1");
      }
      else
      {
        context.setAttribute("obsolete", "0");
      }

      getJspBody().invoke(null);   // process the body of the tag and print it to the response
    }
  }
}
