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
 * Servlet used in the client part of MIRIAM WebServices. Call a precise remote method. 
 * <p>
 * Link to the function: getDataTypeURLs
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
public class ServletGetDataTypeURLs extends BaseAjaxServlet
{
	private static final long serialVersionUID = 6308316327596985655L;
	private Logger logger = Logger.getLogger(ServletGetDataTypeURLs.class);
	

	public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		logger.debug("'ServletGetDataTypeURLs' was called...");
		
		String newResult = new String();
		
		// recovery of the parameters
		String name = request.getParameter("getDataTypeURLsParam");
		
		// retrieves the address to access to the Web Services (in the 'web.xml' file)
		String endPoint = getServletContext().getInitParameter("webServicesEndpoint");
		
		// processing the request
	    MiriamLink mir = new MiriamLink(endPoint);
	    String[] result = mir.getDataTypeURLsAnswer(name);
	    
	    // checking of the answer
	    if ((result == null) || (result.length == 0))
	    {
	    	newResult = "<p>no URL has been defined for this data-type</p>";
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
