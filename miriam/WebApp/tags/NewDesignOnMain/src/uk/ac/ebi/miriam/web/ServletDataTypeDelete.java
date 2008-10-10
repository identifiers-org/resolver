/*
 * Author: Camille Laibe
 * Copyright: EMBL-EBI, Computational Neurobiology Group
 * Project: Miriam
 */


package uk.ac.ebi.miriam.web;


import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;


/**
 * <p>
 * Servlet that handles the deletion of one or more data-type(s) in the database
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>EMBL-EBI, Computational Neurobiology Group</dd>
 * </dl>
 * 
 * @author Camille Laibe
 * @version 20070109
 */
public class ServletDataTypeDelete extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final long serialVersionUID = -3063704726612619124L;
	private Logger logger = Logger.getLogger(ServletDataTypeDelete.class);
	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public ServletDataTypeDelete()
	{
		super();
	}
	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doPost(request, response);
	}
	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// retrieves the names of all the data-types which need to be removed
		String[] data = request.getParameterValues("datatype2remove");
		
		// retrieves the version of the web application (sid, alpha, main or demo) 
		String version = getServletContext().getInitParameter("version");
		
		// retrieves the user logged who asked for the action
		HttpSession session = request.getSession();
		String user = (String) session.getAttribute("login");
		
		// retrieves the email address of the administrator
		String emailAdr = getServletContext().getInitParameter("email");
		
		// TODO:
		// - finish the work (now we just send an email)!
		
		// we send an email to ask the administrator to do the job for us (yeah, you lazy people)
		String emailBody = "\nData-type(s) need(s) to be removed [" + version + " version]:";
		for (int i=0; i<data.length; ++i)
	    {
			emailBody += "\n\tData type identifier (" + i + "): " +  data[i];
	    }
		emailBody += "\nUser: " + user;
		GregorianCalendar cal = new GregorianCalendar();
		emailBody += "\nDate: " + cal.getTime();
		emailBody += "\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMiriam";
		MailFacade.send("Miriam-" + version + "@ebi.ac.uk", emailAdr, "[Miriam-" + version + "] data-type(s) to remove", emailBody, "text/plain");
		logger.debug("email sent.");
		
		request.setAttribute("data", data);
		
		RequestDispatcher view = request.getRequestDispatcher("data_delete.jsp");
		view.forward(request, response);
	}
}
