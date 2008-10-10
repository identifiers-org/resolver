/*
 * Author: Camille Laibe
 * Copyright: EMBL-EBI, Computational Neurobiology Group
 * Project: Miriam
 */


package uk.ac.ebi.miriam.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

import uk.ac.ebi.miriam.db.DataTypeHybernate;
import uk.ac.ebi.miriam.db.Resource;


/**
 * <p>Servlet which handles the modification of an existed data-type (part 2: update the database).
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
public class ServletDataTypeEditPart2 extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final long serialVersionUID = 8119310952378238094L;
	private Logger logger = Logger.getLogger(ServletDataTypeEditPart2.class);
	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public ServletDataTypeEditPart2()
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
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		DataTypeHybernate data = new DataTypeHybernate();
		int queryResult = 0;
		String emailBody = "";
		String jsp = "data_edit_done.jsp";
		
		logger.info("The edit form for an update has been filled...");
		
		// to be able to retrieve UTF-8 elements from HTML forms
		request.setCharacterEncoding("UTF-8");
		
		// retrieves all the parameters of the data-type
		String internalId = request.getParameter("id");   // Integer.parseInt(request.getParameter("internalId"));
		String name = request.getParameter("name");
		String strSynonymsCount = request.getParameter("synonymsCounter");
		String definition = request.getParameter("def");
		String pattern = request.getParameter("pattern");
		String url = request.getParameter("url");
		String urn = request.getParameter("urn");
		String strDeprecatedCount = request.getParameter("deprecatedCounter");
		String strResourcesCount = request.getParameter("resourcesCounter");
		String strDocsCount = request.getParameter("docCounter");
		
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
				// simple check: the new resources don't have this parameter 
				if (request.getParameter("obsolete"+i) != null)
				{
					if ((request.getParameter("obsolete"+i)).compareToIgnoreCase("1") == 0)
					{
						temp.setObsolete(true);
					}
					else
					{
						temp.setObsolete(false);
					}
				}
				// simple check: the new resources don't have this parameter
				if (request.getParameter("resourceId"+i) != null)
				{
					if ((request.getParameter("resourceId"+i)).compareToIgnoreCase("null") == 0)
					{
						//temp.setId(null);
						temp.setId("null");
					}
					else
					{
						temp.setId(request.getParameter("resourceId"+i));
					}
				}
				
				// TODO: the field "obsolete" cannot be set during the "submission" step, but need to be modified in the "edit" step (that means HERE!)
				//       for the moment, the only way to make a resource 'obsolete' is to delete it, and there is not way to make it official again (except messing the database by hand)... 
				
				resources.add(temp);
			}
		}
		
		// retrieves the documentation(s) information, if necessary
		int docsCount = Integer.parseInt(strDocsCount);
		ArrayList docsUri = new ArrayList();
		ArrayList docsUrl = new ArrayList();
		for (int i=1; i<=docsCount; ++i)
		{
			// TODO: everything should be 'NEW' now, need to be tested!
			//logger.debug("++++++++ Type of doc: " + request.getParameter("docType"+i));   // REMOVE
			if ((request.getParameter("doc"+i) != null) && (request.getParameter("docType"+i) != null))   // old documentations
			{
				//logger.debug("-------- OLD DOC: " + request.getParameter("doc"+i));   // TEST
				if (request.getParameter("docType"+i).equalsIgnoreCase("MIRIAM"))
				{
					docsUri.add(request.getParameter("doc"+i));
				}
				else
				{
					docsUrl.add(request.getParameter("doc"+i));
				}
			}
			else   // new documentation(s) added
			{
				//logger.debug("-------- NEW DOC: " + request.getParameter("docUri"+i));   // TEST
				if ((request.getParameter("docUri"+i) != null) && (request.getParameter("docType"+i) != null))
				{
					if (request.getParameter("docType"+i).equalsIgnoreCase("MIRIAM"))
					{
						docsUri.add("http://www.pubmed.gov/#" + request.getParameter("docUri"+i));
					}
					else
					{
						docsUrl.add(request.getParameter("docUri"+i));
					}
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
		
		// fill the data-type (with the updated information)
		data.setName(name);
		data.setSynonyms(synonyms);
		data.setURL(url);
		data.setURN(urn);
		data.setDeprecatedURIs(deprecated);
		data.setDefinition(definition);
		data.setRegexp(pattern);
		data.setId(internalId);
		data.setResources(resources);
/*
		if ((resourcesDePrefix.isEmpty()) || (resourcesDr.isEmpty()))
		{
			data.setLocations(null);
		}
		else
		{
			data.setLocations(resourcesDePrefix, resourcesDeSuffix, resourcesDr);
		}
*/
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
				// check if (at least) one resource is official (not obsolete)
				if (data.hasOfficialResource())
				{
					// the user is not anonymous: update of the database
					if (user != null)
					{
						// retrieve the old data-type
						DataTypeHybernate oldOne = new DataTypeHybernate();
						oldOne.retrieveData(poolName, data.getId());
	
						// logs the old information about the data-type (just in case)
						logger.info("The data-type '" + data.getId() + "' will be updated soon, by " + user + ": " + oldOne.getName() + " (synonyms: " + oldOne.getSynonyms().toString() + "; ID: " + oldOne.getId() + ")");
						logger.info("Here are the information previously stored in the database:");
						logger.info(data.toString());
						
						// sends an email to the administrator with the OLD information about the data-type
						emailBody = "\nThe data-type '" + data.getId() + "' will be updated [" + version + " version], here are the information previously stored:";
						emailBody += oldOne.toString();
						emailBody += "\nUser: " + user;
						GregorianCalendar cal = new GregorianCalendar();
						emailBody += "\nDate: " + cal.getTime();
						emailBody += "\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMiriam";
						MailFacade.send("Miriam-" + version + "@ebi.ac.uk", emailAdr, "[Miriam-" + version + "] data-type '" + data.getId()  + "' before update", emailBody, "text/plain; charset=UTF-8");
						
						// update the database
						queryResult = data.updateObject(oldOne, poolName);
						
						// update the DataType object (to retrieve the "deprecated" resources)
						String tmpId = data.getId();
						data.destroy();
						data.retrieveData(poolName, tmpId);
						
						// sends the email to the administrator with the NEW information about the data-type
						emailBody = "\nThe data-type '" + data.getId() + "' has just been updated [" + version + " version]:";
						
						// result of the queries
						if (queryResult == 1)
						{
							emailBody += "\nEverything is OK: the data-type was updated with success.\n";
						}
						if (queryResult != 1)
						{
							emailBody += "\nWARNING: a problem occured during the update of the data-type!\n";
						}
						
						// add all the information about the new data-type in the email
						emailBody += data.toString();
						emailBody += "\nUser: " + user;
						emailBody += "\nDate: " + cal.getTime();
						emailBody += "\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMiriam";
						
						// send the email
						MailFacade.send("Miriam-" + version + "@ebi.ac.uk", emailAdr, "[Miriam-" + version + "] data-type '" + data.getId() + "' updated", emailBody, "text/plain; charset=UTF-8");
						
						// log the event, with all the information
						if (queryResult == 1)   // success
						{
							logger.info("Data-type '" + data.getId() + "' updated in the database, by " + user + ": " + data.getName() + " (synonyms: " + data.getSynonyms().toString() + "; ID: " + data.getId() + ")");
						}
						else   // failure
						{
							logger.info("A problem occured while trying to update the data-type '" + data.getId() + "' in the database, by " + user + ": " + data.getName() + " (" + data.getSynonyms().toString() + ")");
						}
						logger.info(data.toString());
						
						// sends all the information to the JSP
						request.setAttribute("data", data);
						request.setAttribute("actionType", "edit");
					}
					else   // the user is anonymous
					{
						// retrieve the old data-type
						DataTypeHybernate oldOne = new DataTypeHybernate();
						oldOne.retrieveData(poolName, data.getId());
						
						// logs the old information about the data-type (just in case)
						logger.info("The data-type '" + data.getId() + "' need to be updated (but still pending): " + oldOne.getName() + " (synonyms: " + oldOne.getSynonyms().toString() + "; ID: " + oldOne.getId() + ")");
						logger.info("Here are the information previously stored in the database:");
						logger.info(oldOne.toString());
						
						// sends an email to the administrator with the OLD information about the data-type
						emailBody = "\nThe data-type '" + data.getId() + "' need to be updated [" + version + " version], here are the information previously stored:";
						emailBody += oldOne.toString();
						emailBody += "\n\nTHE USER WAS NOT AUTHENTICATED: therefore, no changes have been done so far (and no modification will be done automatically).";
						GregorianCalendar cal = new GregorianCalendar();
						emailBody += "\nDate: " + cal.getTime();
						emailBody += "\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMiriam"; 
						MailFacade.send("Miriam-" + version + "@ebi.ac.uk", emailAdr, "[Miriam-" + version + "] data-type '" + data.getId() + "' before update", emailBody, "text/plain; charset=UTF-8");
						
						// send an email to the administrator with the NEW information about the data-type
						emailBody = "\nThe data-type '" + data.getId() + "' need to be updated [" + version + " version], here are the new information (the deleted resources are not shown here):";
						emailBody += data.toString();
						emailBody += "\n\nTHE USER WAS NOT AUTHENTICATED: therefore, no changes have been done so far (and no modification will be done automatically).";
						emailBody += "\nDate: " + cal.getTime();
						emailBody += "\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMiriam";
						MailFacade.send("Miriam-" + version + "@ebi.ac.uk", emailAdr, "[Miriam-" + version + "] data-type '" + data.getId() + "' update pending", emailBody, "text/plain; charset=UTF-8");
						
						// log the event, with all the information
						logger.info("The Data-type '" + data.getId() + "' need to be updated in the database (still pending): " + data.getName() + " (synonyms: " + data.getSynonyms().toString() + "; ID: " + data.getId() + ")");
						logger.info("Here are the updated information which need to be modify in the database:");
						logger.info(data.toString());
					}
					
					// sends all the information to the JSP
					request.setAttribute("data", data);
					request.setAttribute("actionType", "edit");
				}
				else   // the data-type has no official resource
				{
					logger.info("The data-type '" + data.getName() + "' (id: " + data.getId() + ") has no official resource!");
					logger.info(data.toString());
					logger.info("==> Therefore, the updating process was canceled. Sorry about that.");
					
					request.setAttribute("section", "edit_error.html");
					jsp = "static.jsp";
				}
			}
			else   // the data-type doesn't exit: so we can't update it
			{
				logger.info("The data-type '" + data.getName() + "' (id: " + data.getId() + ") doesn't exist in the database!");
				logger.info(data.toString());
				logger.info("==> Therefore, the updating process was canceled. Sorry about that.");
				
				request.setAttribute("section", "edit_error.html");
				jsp = "static.jsp";
			}
		}
		else
		{
			logger.info("One or more element(s) required for updating the data-type '" + data.getName() + "' (id: " + data.getId() + ") are missing or wrong!");
			logger.info(data.toString());
			logger.info("==> Therefore, the updating process was canceled. Sorry about that.");
			
			request.setAttribute("section", "edit_error.html");
			jsp = "static.jsp";
		}
		
		// send information of the result of the process to the user
		RequestDispatcher view = request.getRequestDispatcher(jsp);
		view.forward(request, response);
	}
	
}
