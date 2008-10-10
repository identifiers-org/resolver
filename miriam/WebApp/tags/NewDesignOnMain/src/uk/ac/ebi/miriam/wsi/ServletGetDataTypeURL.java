/*
 * Author: Camille Laibe
 * Copyright: EMBL-EBI, Computational Neurobiology Group
 * Project: Miriam
 */


package uk.ac.ebi.miriam.wsi;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ajaxtags.servlets.BaseAjaxServlet;
import org.apache.log4j.Logger;


/**
 * <p>
 * "controller" part of the MIRIAM web application, client for the Web Services
 * <p>
 * Link to the function: getDataTypeURL
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>EMBL-EBI, Computational Neurobiology Group</dd>
 * </dl>
 * 
 * @author Camille Laibe
 * @version 20060808
 */
public class ServletGetDataTypeURL extends BaseAjaxServlet
{
	private static final long serialVersionUID = -149277998186284003L;
	private Logger logger = Logger.getLogger(ServletGetDataTypeURL.class);
	
	public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		logger.debug("'ServletGetDataTypeURL' was called...");
		
		String newResult = new String();
		
		// recovery of the parameters
		String name = request.getParameter("getDataTypeURLParam");
		
		// retrieves the address to access to the Web Services
		String endPoint = getServletContext().getInitParameter("webServicesEndpoint");
		
		// processing the request
	    MiriamLink mir = new MiriamLink(endPoint);
	    String result = mir.getDataTypeURLAnswer(name);
	    
	    // checking of the answer
	    if ((result == null) || (result.equalsIgnoreCase("")))
	    {
	    	newResult = "<p>no URL has been defined for this data-type</p>";
	    }
	    else
	    {
	    	newResult = "<p>" + result + "</p>";
	    }
	    
	    return newResult;
	}
}
