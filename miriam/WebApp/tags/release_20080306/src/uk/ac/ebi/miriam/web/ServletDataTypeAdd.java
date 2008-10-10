/*
 * MIRIAM Resources (Web Application)
 * MIRIAM is an online resource created to catalogue biological data types,
 * their URIs and the corresponding physical URLs,
 * whether these are controlled vocabularies or databases.
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2008  Camille Laibe (EMBL - European Bioinformatics Institute, Computational Neurobiology Group)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package uk.ac.ebi.miriam.web;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
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
 * - remove the "http://www.pubmed.gov/#" and other "http://www.doi.org/" which are hard coded ==> retrieve that from the DB!
 * - add the setting of the 'obsolete' param for a resource in the edit interface
 */


/**
 * <p>
 * Servlet that handles the add of a new data-type in the database
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2008 Camille Laibe (EMBL - European Bioinformatics Institute, Computational Neurobiology Group)
 * <br />
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <br />
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * </dd>
 * </dl>
 * </p>
 *
 * @author Camille Laibe <camille.laibe@ebi.ac.uk>
 * @version 20080306
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
        boolean isSpam = true;   // worse case

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
        String spam = request.getParameter("pourriel");

        // retrieves the synonym(s), if necessary
        int synonymsCount = Integer.parseInt(strSynonymsCount);
        ArrayList<String> synonyms = new ArrayList<String>();
        for (int i=1; i<=synonymsCount; ++i)
        {
            if ((request.getParameter("synonym"+i) != null) && (! MiriamUtilities.isEmpty(request.getParameter("synonym"+i))))
            {
                synonyms.add(request.getParameter("synonym"+i));
            }
        }

        // retrieves the obsolete/depreciated URI(s), if necessary
        int deprecatedCount = Integer.parseInt(strDeprecatedCount);
        ArrayList<String> deprecated = new ArrayList<String>();
        for (int i=1; i<=deprecatedCount; ++i)
        {
            if ((request.getParameter("deprecated"+i) != null) && (! MiriamUtilities.isEmpty(request.getParameter("deprecated"+i))))
            {
                deprecated.add(request.getParameter("deprecated"+i));
            }
        }

        // retrieves the supplementary resource(s) information, if necessary
        int resourcesCount = Integer.parseInt(strResourcesCount);
        ArrayList<Resource> resources = new ArrayList<Resource>();
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
        
        // retrieves the documentation(s) information, if necessary (can be a URL, a PMID, a DOI, ...)
        int docsCount = Integer.parseInt(strDocsCount);
        Map<String, List<String>> docUris = new HashMap<String, List<String>>();
        ArrayList<String> docsUri = new ArrayList<String>();
        ArrayList<String> docsDoi = new ArrayList<String>();
        ArrayList<String> docsUrl = new ArrayList<String>();
        for (int i=1; i<=docsCount; ++i)
        {
            if ((request.getParameter("docUri"+i) != null) && (request.getParameter("docType"+i) != null))
            {
                if (request.getParameter("docType"+i).equalsIgnoreCase("PMID"))   // PMID
                {
                    String tmp = "http://www.pubmed.gov/" + "#" + request.getParameter("docUri"+i);
                    docsUri.add(tmp);
                }
                else
                {
                    if (request.getParameter("docType"+i).equalsIgnoreCase("DOI"))   // DOI
                    {
                        String tmp = "http://www.doi.org/" + "#" + request.getParameter("docUri"+i);
                        docsDoi.add(tmp);
                    }
                    else   // default: physical location
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

        // fills the data-type
        // do not set the identifier (normal: not created yet)
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
        
        // stores the MIRIAM URIs and their type in the object DataType(Hybernate)
        docUris.put("PMID", docsUri);
        docUris.put("DOI", docsDoi);
        for (String uri: docsUri)
        {
            data.addDocumentationIDType("PMID");
            data.addDocumentationID(uri);
        }
        for (String uri: docsDoi)
        {
            data.addDocumentationIDType("DOI");
            data.addDocumentationID(uri);
        }

        // retrieves the version of the web application (sid, alpha, main or demo)
        String version = getServletContext().getInitParameter("version");

        // retrieves the email address of the administrator
        String emailAdr = getServletContext().getInitParameter("email");

        // retrieves the user logged who asked for the action
        HttpSession session = request.getSession();
        String user = (String) session.getAttribute("login");

        // retrieves the name of the pool
        String poolName = getServletContext().getInitParameter("miriam_db_pool");

        // is this submission a spam?
        if (spam.equalsIgnoreCase(""))
        {
            isSpam = false;
        }
        else
        {
            isSpam = true;
        }


        // minimum valid information needed to create a new data-type
        if (data.isValid())
        {
            // the data-type already exists
            if (data.isExisting(poolName, true))
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

                // spam?
                if (isSpam)
                {
                    emailBody += "\n\nWARNING: there are strong suspicions that this submission is a spam!\n\n";
                }

                // result of the queries
                if ((user != null) && (queryResult == 1))
                {
                    emailBody += "\nEverything is OK: the new data-type is now stored in the database.\n";
                }
                if ((user != null) && (queryResult != 1))
                {
                    emailBody += "\nWARNING: a problem occurred during the update of the database!\n";
                }

                // adds all the information about the new data-type in the email
                emailBody += data.toString();

                // the user is not anonymous
                if (user != null)
                {
                    emailBody += "\nUser: " + user;
                }
                else   // anonymous user
                {
                    emailBody += "\n\nTHE USER WAS NOT AUTHENTICATED: THIS DATA-TYPE NEEDS TO BE ADDED MANUALLY!\n";
                    emailBody += "User information: " + userInfo;
                    emailBody += "\nIP address: " + request.getRemoteAddr();
                    emailBody += "\nHost: " + request.getRemoteHost();   // TEST
                    emailBody += "\nPort" + request.getRemotePort();   // TEST
                    emailBody += "\nRemote User" + request.getRemoteUser();   // TEST
                }
                GregorianCalendar cal = new GregorianCalendar();
                emailBody += "\nDate: " + cal.getTime();
                emailBody += "\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMIRIAM";
                // sends the email, changing the "object" field regarding the fact that the user was authenticated or not
                if (user != null)
                {
                    if (isSpam)
                    {
                        MailFacade.send("MIRIAM-" + version + "@ebi.ac.uk", emailAdr, "[MIRIAM-" + version + "] new SPAM submited?", emailBody, "text/plain; charset=UTF-8");
                    }
                    else
                    {
                        MailFacade.send("MIRIAM-" + version + "@ebi.ac.uk", emailAdr, "[MIRIAM-" + version + "] new data-type submited", emailBody, "text/plain; charset=UTF-8");
                    }
                }
                else
                {
                    if (isSpam)
                    {
                        MailFacade.send("MIRIAM-" + version + "@ebi.ac.uk", emailAdr, "[MIRIAM-" + version + "] new SPAM pending?", emailBody, "text/plain; charset=UTF-8");
                    }
                    else
                    {
                        MailFacade.send("MIRIAM-" + version + "@ebi.ac.uk", emailAdr, "[MIRIAM-" + version + "] new data-type pending", emailBody, "text/plain; charset=UTF-8");
                    }
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
                    logger.info("IP address: " + request.getRemoteAddr());
                    logger.info("Host: " + request.getRemoteHost());   // TEST
                    logger.info("Port" + request.getRemotePort());   // TEST
                    logger.info("Remote User" + request.getRemoteUser());   // TEST
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
            logger.info("One or more element(s) required for submitting a new data-type is/are missing or wrong!");
            request.setAttribute("section", "add_error.html");
            jsp = "static.jsp";
        }

        // sends information of the result of the process to the user
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
}
