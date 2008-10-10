/*
 * Author: Camille Laibe
 * Copyright: EMBL-EBI, Computational Neurobiology Group
 * Project: Miriam
 */


package uk.ac.ebi.miriam.wsi;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ajaxtags.servlets.BaseAjaxServlet;


/**
 * <p>
 * "controller" part of the MIRIAM web application, client for the Web Services
 * <p>
 * Link to the function: getNames
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>EMBL-EBI, Computational Neurobiology Group</dd>
 * </dl>
 * 
 * @author Camille Laibe
 * @version 20060802
 */
public class ServletGetNames extends BaseAjaxServlet
{
	private static final long serialVersionUID = -7218941361317962147L;
	
	
	public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String newResult = new String();
		
		// recovery of the parameters
		String uri = request.getParameter("getNamesParam");
		
		// retrieves the address to access to the Web Services
		String endPoint = getServletContext().getInitParameter("webServicesEndpoint");
		
		// processing the request
	    MiriamLink mir = new MiriamLink(endPoint);
	    String[] result = mir.getNamesAnswer(uri);
	    
	    // checking of the answer
	    if ((result == null) || (result.length == 0))
	    {
	    	newResult = "<p>You need to enter a valid <acronym title=\"Uniform Request Identifier\">URI</acronym>!</p>";
	    }
	    // create a beautiful (x)html output
	    else
	    {
	    	newResult = "<ul>";
		    for (int i=0; i<result.length; ++i)
		    {
	    		newResult += "<li>" + result[i] + "</li>";
	    	}
	    	newResult += "</ul>";
	    }
	    
	    return newResult;
	}
}
