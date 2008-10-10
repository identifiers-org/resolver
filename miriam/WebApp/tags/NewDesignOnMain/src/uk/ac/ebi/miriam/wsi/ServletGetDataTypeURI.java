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
 * Link to the function: getDataTypeURI
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
 public class ServletGetDataTypeURI extends BaseAjaxServlet
 {
	private static final long serialVersionUID = -1743285908049800126L;

	public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String newResult = new String();
		
		// recovery of the parameters
		String name = request.getParameter("getDataTypeURIParam1");
		String type = request.getParameter("getDataTypeURIParam2");
		
		// retrieves the address to access to the Web Services
		String endPoint = getServletContext().getInitParameter("webServicesEndpoint");
		
		// processing the request
	    MiriamLink mir = new MiriamLink(endPoint);
	    String result = mir.getDataTypeURIAnswer(name, type);
	    
	    // checking of the answer
	    if ((result == null) || (result.equalsIgnoreCase("")))
	    {
	    	newResult = "<p>no entry in the database for this data-type</p>";
	    }
	    else
	    {
	    	newResult = "<p>" + result + "</p>";
	    }
	    
	    return newResult;
	}
}