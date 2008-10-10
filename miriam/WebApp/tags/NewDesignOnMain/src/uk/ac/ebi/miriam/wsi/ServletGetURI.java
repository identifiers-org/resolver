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
 * Link to the function: getURI
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
public class ServletGetURI extends BaseAjaxServlet
{
	private static final long serialVersionUID = 539353127772079667L;

	public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String newResult = new String();
		
		// recovery of the parameters
		String name = request.getParameter("getURIParam1");
		String id = request.getParameter("getURIParam2");
		String type = request.getParameter("getURIParam3");
		
		// retrieves the address to access to the Web Services
		String endPoint = getServletContext().getInitParameter("webServicesEndpoint");
		
		// processing the request
	    MiriamLink mir = new MiriamLink(endPoint);
	    String result = mir.getURIAnswer(name, id, type);
	    
	    // checking of the answer
	    if ((result == null) || (result.equalsIgnoreCase("")))
	    {
	    	newResult = "<p>no answer</p>";
	    }
	    else
	    {
	    	newResult = "<p>" + result + "</p>";
	    }
	    
	    return newResult;
	}
}
