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
 * function: getDataTypeURNs
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
public class ServletGetDataTypeURNs extends BaseAjaxServlet
{
	private static final long serialVersionUID = 6568255667515195605L;
	
	
	public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String newResult = new String();
		
		// recovery of the parameters
		String name = request.getParameter("getDataTypeURNsParam");
		
		// retrieves the address to access to the Web Services
		String endPoint = getServletContext().getInitParameter("webServicesEndpoint");
		
		// processing the request
	    MiriamLink mir = new MiriamLink(endPoint);
	    String[] result = mir.getDataTypeURNsAnswer(name);
	    
	    // checking of the answer
	    if ((result == null) || (result.length == 0))
	    {
	    	newResult = "<p>no URN has been defined for this data-type</p>";
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
