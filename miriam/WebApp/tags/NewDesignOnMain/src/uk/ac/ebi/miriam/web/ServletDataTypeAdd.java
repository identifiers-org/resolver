/*
 * Author: Camille Laibe
 * Copyright: EMBL-EBI, Computational Neurobiology Group
 * Project: Miriam
 */


package uk.ac.ebi.miriam.web;


import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;

import uk.ac.ebi.miriam.db.DataTypeHybernate;
import uk.ac.ebi.miriam.db.Resource;

/*
 * TODO:
 * 
 * - add the setting of the 'obsolete' param for a resource in the edit interface 
 */


/**
 * <p>
 * Servlet that handles the add of a new data-type in the database
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>EMBL-EBI, Computational Neurobiology Group</dd>
 * </dl>
 * 
 * @author Camille Laibe
 * @version 20070123
 */
public class ServletDataTypeAdd extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final long serialVersionUID = -3573751137425794361L;
	private Logger logger = Logger.getLogger(ServletDataTypeAdd.class);

	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public ServletDataTypeAdd()
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
		DataTypeHybernate data = new DataTypeHybernate();
		int queryResult = 0;
		String emailBody = "";
		String jsp = "data_edit_done.jsp";
		
		// to be able to retrieve UTF-8 elements from HTML forms
		request.setCharacterEncoding("UTF-8");
		
		// retrieves all the parameters of the data-type
		String name = request.getParameter("name");
		String strSynonymsCount = request.getParameter("synonymsCounter");
		String definition = request.getParameter("def");
		String pattern = request.getParameter("pattern");
		String url = request.getParameter("url");
		String urn = request.getParameter("urn");
		String strDeprecatedCount = request.getParameter("deprecatedCounter");
		String strResourcesCount = request.getParameter("resourcesCounter");
		String strDocsCount = request.getParameter("docCounter");
		String userInfo = request.getParameter("user");
		
		// retrieves the synonym(s), if necessary 
		int synonymsCount = Integer.parseInt(strSynonymsCount);
		ArrayList synonyms = new ArrayList();
		for (int i=1; i<=synonymsCount; ++i)
		{
			if ((request.getParameter("synonym"+i) != null) && (! MiriamUtilities.isEmpty(request.getParameter("synonym"+i))))
			{
				synonyms.add(request.getParameter("synonym"+i));
			}
		}
		
		// retrieves the deprecated URI(s), if necessary
		int deprecatedCount = Integer.parseInt(strDeprecatedCount);
		ArrayList deprecated = new ArrayList();
		for (int i=1; i<=deprecatedCount; ++i)
		{
			if ((request.getParameter("deprecated"+i) != null) && (! MiriamUtilities.isEmpty(request.getParameter("deprecated"+i))))
			{
				deprecated.add(request.getParameter("deprecated"+i));
			}
		}
		
		// retrieves the supplementary resource(s) information, if necessary
		int resourcesCount = Integer.parseInt(strResourcesCount);
		ArrayList resources = new ArrayList();
		//ArrayList resourcesDeSuffix = new ArrayList();
		//ArrayList resourcesDr = new ArrayList();
		for (int i=1; i<=resourcesCount; ++i)
		{
			if ((request.getParameter("dataEntryPrefix"+i) != null) && (request.getParameter("dataResource"+i) != null) && (! MiriamUtilities.isEmpty(request.getParameter("dataEntryPrefix"+i))) && (! MiriamUtilities.isEmpty(request.getParameter("dataResource"+i))))   // the 'dataEntrySuffix' is optional
			{
				Resource temp = new Resource();
				temp.setUrl_prefix(request.getParameter("dataEntryPrefix"+i));
				temp.setUrl_suffix(request.getParameter("dataEntrySuffix"+i));
				temp.setUrl_root(request.getParameter("dataResource"+i));
				temp.setInfo(request.getParameter("information"+i));
				temp.setInstitution(request.getParameter("institution"+i));
				temp.setLocation(request.getParameter("country"+i));
				
				// TODO: the field "obsolete" cannot be set during the "submission" step, but need to be modified in the "edit" step
				
				resources.add(temp);
			}
		}
		
		// retrieves the documentation(s) information, if necessary
		int docsCount = Integer.parseInt(strDocsCount);
		ArrayList docsUri = new ArrayList();
		ArrayList docsUrl = new ArrayList();
		for (int i=1; i<=docsCount; ++i)
		{
			if ((request.getParameter("docUri"+i) != null) && (request.getParameter("docType"+i) != null))
			{
				if (request.getParameter("docType"+i).equalsIgnoreCase("MIRIAM"))
				{
					String tmp = "http://www.pubmed.gov/#" + request.getParameter("docUri"+i);
					docsUri.add(tmp);
				}
				else
				{
					docsUrl.add(request.getParameter("docUri"+i));
				}
			}
		}
		
		// some checks
		if ((MiriamUtilities.isEmpty(definition)) || (definition.equalsIgnoreCase("Enter definition here...")))
		{
			definition = null;
		}
		if ((MiriamUtilities.isEmpty(pattern)) || (pattern.equalsIgnoreCase("Enter Identifier pattern here...")))
		{
			pattern = null;
		}
		
		// fill the data-type
		data.setName(name);
		data.setSynonyms(synonyms);
		data.setURL(url);
		data.setURN(urn);
		data.setDeprecatedURIs(deprecated);
		data.setDefinition(definition);
		data.setRegexp(pattern);
		if (resources.isEmpty())
		{
			data.setResources(null);   // perhaps useless...
		}
		else
		{
			data.setResources(resources);
		}
		data.setDocumentationURLs(docsUrl);
		data.setDocumentationIDs(docsUri);
		
		// retrieves the version of the web application (sid, alpha, main or demo)
		String version = getServletContext().getInitParameter("version");
		
		// retrieves the email address of the administrator
		String emailAdr = getServletContext().getInitParameter("email");
		
		// retrieves the user logged who asked for the action
		HttpSession session = request.getSession();
		String user = (String) session.getAttribute("login");
		
		// retrieve the name of the pool
		String poolName = getServletContext().getInitParameter("miriam_db_pool");

		// minimum valid information needed to create a new data-type 
		if (data.isValid())
		{
			// the data-type already exists
			if (data.isExisting(poolName))
			{
				logger.info("The new data-type '" + data.getName() + "' already exists in the database!");
				logger.info("Therefore, the adding process was canceled. Sorry about that.");
				
				request.setAttribute("section", "add_already_existing.html");
				jsp = "static.jsp";
			}
			else   // the data-type doesn't exit
			{
				// the user is not anonymous: update of the database
				if (user != null)
				{
					queryResult = data.storeObject(poolName);
				}
				
				// sends the email to the administrator (even if the user was logged)
				emailBody = "\nA new data-type has just been submitted [" + version + " version]:";
				
				// result of the queries
				if ((user != null) && (queryResult == 1))
				{
					emailBody += "\nEverything is OK: the new data-type is now stored in the database.\n";
				}
				if ((user != null) && (queryResult != 1))
				{
					emailBody += "\nWARNING: a problem occured during the update of the database!\n";
				}
				
				// add all the information about the new data-type in the email
				emailBody += data.toString();
				
				// the user is not anonymous
				if (user != null)
				{
					emailBody += "\nUser: " + user;
				}
				else   // anonymous user
				{
					emailBody += "\n\nTHE USER WAS NOT AUTHENTICATED: THIS DATA-TYPE NEED TO BE ADDED MANUALLY!\n";
					emailBody += "User information: " + userInfo;
				}
				GregorianCalendar cal = new GregorianCalendar();
				emailBody += "\nDate: " + cal.getTime();
				emailBody += "\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMiriam";
				// sends the email, changing the "object" field regarding the fact that the user was authenticated or not 
				if (user != null)
				{
					MailFacade.send("Miriam-" + version + "@ebi.ac.uk", emailAdr, "[Miriam-" + version + "] new data-type submited", emailBody, "text/plain; charset=UTF-8");
				}
				else
				{
					MailFacade.send("Miriam-" + version + "@ebi.ac.uk", emailAdr, "[Miriam-" + version + "] new data-type pending", emailBody, "text/plain; charset=UTF-8");
				}
				
				// log the event, with all the information (if the user was authenticated)
				if (user != null)
				{
					if (queryResult == 1)   // success
					{
						logger.info("New data-type added to the database, by " + user + ": " + data.getName() + " (" + data.getSynonyms().toString() + ")");
						// general information
						logger.info(data.toString());
					}
					else   // failure
					{
						logger.info("A problem occured while trying to add a new data-type to the database, by " + user + ": " + data.getName() + " (" + data.getSynonyms().toString() + ")");
						// general information
						logger.info(data.toString());
					}
				}
				else   // the user was not authenticated
				{
					logger.info("New data-type submitted (but still pending): " + data.getName() + " (" + data.getSynonyms().toString() + ")");
					logger.info("User information: " + userInfo);
					// general information
					logger.info(data.toString());
				}
				
				// sends all the information to the JSP
				request.setAttribute("data", data);
				request.setAttribute("actionType", "add");
			}
		}
		else
		{
			logger.info("One or more element(s) required for submitting a new data-type is missing or wrong!");
			request.setAttribute("section", "add_error.html");
			jsp = "static.jsp";
		}
		
		// send information of the result of the process to the user
		RequestDispatcher view = request.getRequestDispatcher(jsp);
		view.forward(request, response);
	}
}
