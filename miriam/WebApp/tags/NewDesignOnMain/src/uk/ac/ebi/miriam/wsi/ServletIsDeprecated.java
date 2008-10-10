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
 * Link to the function: isDeprecated
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
