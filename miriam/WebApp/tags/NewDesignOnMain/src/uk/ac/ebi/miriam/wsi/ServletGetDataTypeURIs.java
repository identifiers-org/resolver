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
 * Link to the function: getDataTypeURIs
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>EMBL-EBI, Computational Neurobiology Group</dd>
 * </dl>
 * 
 * @author Camille Laibe
 * @version 20060803
 */
public class ServletGetDataTypeURIs extends BaseAjaxServlet
{
	private static final long serialVersionUID = -4205549971171062987L;
	
	
	public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String newResult = new String();
		
		// recovery of the parameters
		String name = request.getParameter("getDataTypeURIsParam1");
		String type = request.getParameter("getDataTypeURIsParam2");
		
		// retrieves the address to access to the Web Services
		String endPoint = getServletContext().getInitParameter("webServicesEndpoint");
		
		// processing the request
	    MiriamLink mir = new MiriamLink(endPoint);
	    String[] result = mir.getDataTypeURIsAnswer(name, type);
	    
	    // checking of the answer
	    if ((result == null) || (result.length == 0))
	    {
	    	newResult = "<p>no entry in the database</p>";
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
