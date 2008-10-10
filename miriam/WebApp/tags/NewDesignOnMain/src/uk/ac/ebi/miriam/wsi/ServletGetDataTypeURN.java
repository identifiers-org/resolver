/*
 * Author: Camille Laibe
 * Copyright: EMBL-EBI, Computational Neurobiology Group
 * Project: Miriam
 */


package uk.ac.ebi.miriam.wsi;


import javax.servlet.http.*;
import org.ajaxtags.servlets.BaseAjaxServlet;


/**
 * <p>
 * "controller" part of the MIRIAM web application, client for the Web Services
 * <p>
 * Link to the function: getDataTypeURN
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
public class ServletGetDataTypeURN extends BaseAjaxServlet
{
	private static final long serialVersionUID = -2600640603395677070L;
	
	public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String newResult = new String();
		
		// recovery of the parameters
		String name = request.getParameter("getDataTypeURNParam");
		
		// retrieves the address to access to the Web Services
		String endPoint = getServletContext().getInitParameter("webServicesEndpoint");
		
		// processing the request
	    MiriamLink mir = new MiriamLink(endPoint);
	    String result = mir.getDataTypeURNAnswer(name);
	    
	    // checking of the answer
	    if ((result == null) || (result.equalsIgnoreCase("")))
	    {
	    	newResult = "<p>no URN has been defined for this data-type</p>";
	    }
	    else
	    {
	    	newResult = "<p>" + result + "</p>";
	    }
	    
	    return newResult;
	}
}
