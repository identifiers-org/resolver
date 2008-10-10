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
 * Link to the function: getDataEntries
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
public class ServletGetDataEntries extends BaseAjaxServlet
{
	private static final long serialVersionUID = -366000878529345434L;
	
	
	/**
	 * 
	 * @return String containing the answer
	 */
	public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String newResult = new String();
		
		// recovery of the parameters
		String name = request.getParameter("getDataEntriesParam1");
		String id = request.getParameter("getDataEntriesParam2");
		
		// retrieves the address to access to the Web Services
		String endPoint = getServletContext().getInitParameter("webServicesEndpoint");
		
		// processing the request
	    MiriamLink mir = new MiriamLink(endPoint);
	    String[] result = mir.getDataEntriesAnswer(name, id);
	    
	    // checking of the answer
	    if ((result == null) || (result.length == 0))
	    {
	    	newResult = "<p>no answer</p>";
	    }
	    // create a beautiful (x)html output
	    else
	    {
	    	newResult = "<ul>";
		    for (int i=0; i<result.length; ++i)
		    {
	    		newResult += "<li><a href=\"" + result[i] + "\" title=\"" + result[i] + "\">" + result[i] + "</li>";
	    	}
	    	newResult += "</ul>";
	    }
	    
	    return newResult;
	}
}
