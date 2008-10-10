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
import java.util.ArrayList;
//import java.util.ListIterator;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import org.apache.log4j.Logger;


/**
 * <p>
 * Custom tag handler for browsing a ResultSet object (for the summary page of the database).
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
 * @version 20061207
 */
public class TagHandlerDbBrowse extends SimpleTagSupport
{
  private Logger logger = Logger.getLogger(TagHandlerDbBrowse.class);
  private ArrayList data;  //SQLResult


  /**
   * Setter of 'data'
   * @param ResultSet result of a SQL query
   */
  public void setData(ArrayList data)  //SQLResult
  {
    this.data = data;
  }


  /**
   * this method contains all the business part of a tag handler
   */
  public void doTag() throws JspException, IOException
  {
    logger.debug("tag handler for general browsing (begining)");
    JspContext context = getJspContext();

    for (int j=0; j<((ArrayList) data.get(1)).size(); ++j)
    {
      if (j%2 == 0)
      {
        context.setAttribute("class", "par");
      }
      else
      {
        context.setAttribute("class", "odd");
      }

      context.setAttribute("id", (String) ((ArrayList) data.get(0)).get(j));
      context.setAttribute("name", (String) ((ArrayList) data.get(1)).get(j));
      context.setAttribute("uri", (String) ((ArrayList) data.get(2)).get(j));
      context.setAttribute("definition", (String) ((ArrayList) data.get(3)).get(j));

      getJspBody().invoke(null);   // process the body of the tag and print it to the response
    }
  }
}
