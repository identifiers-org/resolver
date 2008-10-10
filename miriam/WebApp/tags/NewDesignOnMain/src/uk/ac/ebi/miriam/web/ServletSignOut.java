/**
 * 
 * @author Camille Laibe
 * @version 20060612
 * @copyright EMBL-EBI, Computational Neurobiology Group
 * 
 * Servlet that handles the logout of the user
 * 
 */


package uk.ac.ebi.miriam.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Servlet implementation class for Servlet: ServletSignOut
 *
 * @web.servlet
 *   name="ServletSignOut"
 *   display-name="ServletSignOut" 
 *
 * @web.servlet-mapping
 *   url-pattern="/ServletSignOut"
 *  
 */
public class ServletSignOut extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final long serialVersionUID = 7111287432284901060L;

	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public ServletSignOut()
	{
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession session = null;
		session = request.getSession(false);   // returns pre-existing session or null
		if (session != null)
		{
			session.removeAttribute("login");
			session.invalidate();
			request.setAttribute("section", "signout.html");
		}
		
		RequestDispatcher view = request.getRequestDispatcher("static.jsp");
	    view.forward(request, response);
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}   	  	  
	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException
	{
		// TODO Auto-generated method stub
		super.init();
	}   
}
