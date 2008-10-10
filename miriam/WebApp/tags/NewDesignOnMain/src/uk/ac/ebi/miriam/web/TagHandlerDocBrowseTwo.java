/*
 * Author: Camille Laibe
 * Copyright: EMBL-EBI, Computational Neurobiology Group
 * Project: Miriam
 */


package uk.ac.ebi.miriam.web;


import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;


/**
 * <p>
 * Custom tag handler for browsing the documentations (the ones stored with a URI, not a physical address), 
 * part two (all the resources except the primary one)
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>EMBL-EBI, Computational Neurobiology Group</dd>
 * </dl>
 * 
 * @author Camille Laibe
 * @version 20061214
 */
public class TagHandlerDocBrowseTwo extends SimpleTagSupport
{
	private Logger logger = Logger.getLogger(TagHandlerDocBrowse.class);
	private ArrayList urls;   /* list of physical address */
	private ArrayList infos;   /* information */
	private ArrayList institutions;   /* institutions */
	private ArrayList locations;   /* location (country) */
		
	
	/**
	 * Setter of 'urls'
	 * @param ArrayList list of physical address to access to a documentation
	 */
	public void setUrls(ArrayList urls)
	{
		this.urls = urls;
	}
	
	/**
	 * Setter of 'infos'
	 * @param ArrayList list of information
	 */
	public void setInfos(ArrayList infos)
	{
		this.infos = infos;
	}
	
	/**
	 * Setter of 'institutions'
	 * @param ArrayList list of institutions
	 */
	public void setInstitutions(ArrayList institutions)
	{
		this.institutions = institutions;
	}
	
	/**
	 * Setter of 'locations'
	 * @param ArrayList list of locations (countries)
	 */
	public void setLocations(ArrayList locations)
	{
		this.locations = locations;
	}
	
	/**
	 * this method contains all the business part of a tag handler 
	 */
	public void doTag() throws JspException, IOException
	{
		logger.debug("tag handler for data-type documentations (not primary ones)");
		JspContext context = getJspContext();
		
		for (int i=0; i<urls.size(); ++i)
		{
			context.setAttribute("url2", urls.get(i));
			context.setAttribute("info2", infos.get(i));
			context.setAttribute("institution2", institutions.get(i));
			context.setAttribute("location2", locations.get(i));
			
			getJspBody().invoke(null);   // process the body of the tag and print it to the response					
		}
	}
}

