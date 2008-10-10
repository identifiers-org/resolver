/*
 * Author: Camille Laibe
 * Copyright: EMBL-EBI, Computational Neurobiology Group
 * Project: Miriam
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
 * <dd>EMBL-EBI, Computational Neurobiology Group</dd>
 * </dl>
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
