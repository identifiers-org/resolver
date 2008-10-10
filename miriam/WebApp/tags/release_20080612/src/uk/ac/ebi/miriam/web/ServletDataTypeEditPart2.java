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


import uk.ac.ebi.miriam.db.DataTypeHybernate;
import uk.ac.ebi.miriam.db.Resource;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;


/**
 * <p>Servlet which handles the modification of an existed data type (part 2: update the database).
 * 
 * <p>
 * TODO:
 *   - remove the URLs hard coded ("urn:miriam:pubmed" or "urn:miriam:doi") and replace them with a database access
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
 * @version 20080610
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
        StringBuilder emailBody = new StringBuilder();
        String jsp = "data_edit_done.jsp";
        
        logger.debug("The edit form for an update has been filled...");
        
        // to be able to retrieve UTF-8 elements from HTML forms
        request.setCharacterEncoding("UTF-8");
        
        // retrieves all the parameters of the data type
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
        String userInfo = request.getParameter("user");
        
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
        for (int i=1; i<=resourcesCount; ++i)
        {
            if ((request.getParameter("dataEntryPrefix"+i) != null) && (request.getParameter("dataResource"+i) != null) && (! MiriamUtilities.isEmpty(request.getParameter("dataEntryPrefix"+i))) && (! MiriamUtilities.isEmpty(request.getParameter("dataResource"+i))))   // the 'dataEntrySuffix' is optional
            {
                Resource temp = new Resource();
                temp.setUrl_prefix(request.getParameter("dataEntryPrefix"+i));
                temp.setUrl_suffix(request.getParameter("dataEntrySuffix"+i));
                temp.setUrl_root(request.getParameter("dataResource"+i));
                temp.setExample(request.getParameter("dataExample"+i));
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
                    String tmp = "urn:miriam:pubmed" + ":" + URLEncoder.encode(request.getParameter("docUri"+i), "UTF-8");
                    docsUri.add(tmp);
                }
                else
                {
                    if (request.getParameter("docType"+i).equalsIgnoreCase("DOI"))   // DOI
                    {
                        String tmp = "urn:miriam:doi" + ":" + URLEncoder.encode(request.getParameter("docUri"+i), "UTF-8");
                        docsDoi.add(tmp);
                    }
                    else   // default: physical location
                    {
                        docsUrl.add(request.getParameter("docUri"+i));
                    }
                }
            }
        }
        
        /*
        int docsCount = Integer.parseInt(strDocsCount);
        ArrayList<String> docsUri = new ArrayList<String>();
        ArrayList<String> docsUrl = new ArrayList<String>();
        for (int i=1; i<=docsCount; ++i)
        {
            if ((request.getParameter("doc"+i) != null) && (request.getParameter("docType"+i) != null))   // old documentations
            {
                //logger.debug("-------- OLD DOC: " + request.getParameter("doc"+i));   // TEST
                if (request.getParameter("docType"+i).equalsIgnoreCase("URL"))
                {
                    docsUrl.add(request.getParameter("doc"+i));
                }
                else
                {
                    docsUri.add(request.getParameter("doc"+i));
                }
            }
            else   // new documentation(s) added
            {
                //logger.debug("-------- NEW DOC: " + request.getParameter("docUri"+i));   // TEST
                if ((request.getParameter("docUri"+i) != null) && (request.getParameter("docType"+i) != null))
                {
                    if (request.getParameter("docType"+i).equalsIgnoreCase("URL"))
                    {
                        docsUrl.add(request.getParameter("docUri"+i));
                    }
                    else
                    {
                        docsUri.add("http://www.pubmed.gov/#" + request.getParameter("docUri"+i));
                    }
                }
            }
        }
        */
        
        // some checks
        if ((MiriamUtilities.isEmpty(definition)) || (definition.equalsIgnoreCase("Enter definition here...")))
        {
            definition = null;
        }
        if ((MiriamUtilities.isEmpty(pattern)) || (pattern.equalsIgnoreCase("Enter Identifier pattern here...")))
        {
            pattern = null;
        }
        
        // fills the data type (with the updated information)
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
        
        // retrieves the version of the web application (sid/local, alpha, main or demo)
        String version = getServletContext().getInitParameter("version");
        
        // retrieves the email addresses of the administrator and the curators
        String emailAdr = getServletContext().getInitParameter("admin.email");
        String[] emailsCura = getServletContext().getInitParameter("curators.email").split(",");
        
        // retrieves the user logged who asked for the action
        HttpSession session = request.getSession();
        String user = (String) session.getAttribute("login");
        
        // retrieves the name of the pool
        String poolName = getServletContext().getInitParameter("miriam_db_pool");
        
        
        // minimum valid information needed to create a new data type
        if (data.isValid())
        {
            // the data type already exists
            if (data.isExisting(poolName, false))
            {
                // check if (at least) one resource is official (not obsolete)
                if (data.hasOfficialResource())
                {
                    // the user is not anonymous: update of the database
                    if (user != null)
                    {
                        // retrieves the old data type
                        DataTypeHybernate oldOne = new DataTypeHybernate();
                        oldOne.retrieveData(poolName, data.getId());
                        
                        // logs the old information about the data type (just in case)
                        logger.info("The data type '" + data.getId() + "' will be updated soon, by " + user + ": " + oldOne.getName() + " (synonyms: " + oldOne.getSynonyms().toString() + "; ID: " + oldOne.getId() + ")");
                        logger.info("Here are the information previously stored in the database:\n");
                        logger.info(data.toString());
                        
                        // sends an email to the administrator and curators with the OLD information about the data type
                        emailBody.append("\nThe data type '" + data.getId() + "' will be updated [" + version + " version], here are the information previously stored:\n");
                        emailBody.append(oldOne.toString());
                        emailBody.append("\nUser: " + user);
                        emailBody.append("\nIP address: " + request.getRemoteAddr());
                        //emailBody.append("\nHost: " + request.getRemoteHost());   // TEST
                        //emailBody.append("\nPort" + request.getRemotePort());   // TEST
                        //emailBody.append("\nRemote User" + request.getRemoteUser());   // TEST
                        GregorianCalendar cal = new GregorianCalendar();
                        emailBody.append("\nDate: " + cal.getTime());
                        emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMIRIAM Resources\nhttp://www.ebi.ac.uk/miriam/");
                        MailFacade.send("MIRIAM-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[MIRIAM-" + version + "] backup: '" + data.getName()  + "'", emailBody.toString(), "text/plain; charset=UTF-8");
                        
                        // update the database
                        queryResult = data.updateObject(oldOne, poolName);
                        
                        // update the DataType object (to retrieve the "deprecated" resources)
                        String tmpId = data.getId();
                        data.destroy();
                        data.retrieveData(poolName, tmpId);
                        
                        // sends the email to the administrator with the NEW information about the data type
                        emailBody = new StringBuilder();
                        emailBody.append("\nThe data type '" + data.getId() + "' has just been updated [" + version + " version]:");
                        
                        // result of the queries
                        if (queryResult == 1)
                        {
                            emailBody.append("\nEverything is OK: the data type was updated with success.\n");
                        }
                        if (queryResult != 1)
                        {
                            emailBody.append("\nWARNING: a problem occurred during the update of the data type!\n");
                        }
                        
                        // add all the information about the new data type in the email
                        emailBody.append(data.toString());
                        
                        // quick diff
                        DataTypeHybernate newData = new DataTypeHybernate();
                        newData.retrieveData(poolName, data.getId());
                        emailBody.append("\n--- QuickDiff ---\n\n" + newData.diff(oldOne));
                        
                        emailBody.append("\nUser: " + user);
                        emailBody.append("\nIP address: " + request.getRemoteAddr());
                        //emailBody.append("\nLocal IP address: " + request.getLocalAddr());   // TEST
                        //emailBody.append("\nHost: " + request.getRemoteHost());   // TEST
                        //emailBody.append("\nPort: " + request.getRemotePort());   // TEST
                        //emailBody.append("\nRemote User: " + request.getRemoteUser());   // TEST
                        emailBody.append("\nDate: " + cal.getTime());
                        emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMIRIAM Resources\nhttp://www.ebi.ac.uk/miriam/");
                        
                        // sends the email
                        MailFacade.send("MIRIAM-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[MIRIAM-" + version + "] updated: '" + data.getName() + "'", emailBody.toString(), "text/plain; charset=UTF-8");
                        
                        // log the event, with all the information
                        if (queryResult == 1)   // success
                        {
                            logger.info("Data type '" + data.getId() + "' updated in the database, by " + user + ": " + data.getName() + " (synonyms: " + data.getSynonyms().toString() + "; ID: " + data.getId() + ")");
                        }
                        else   // failure
                        {
                            logger.info("A problem occured while trying to update the data type '" + data.getId() + "' in the database, by " + user + ": " + data.getName() + " (" + data.getSynonyms().toString() + ")");
                        }
                        logger.info(data.toString());
                        logger.info("IP address: " + request.getRemoteAddr());
                        logger.info("Host: " + request.getRemoteHost());   // TEST
                        logger.info("Port: " + request.getRemotePort());   // TEST
                        logger.info("Remote User: " + request.getRemoteUser());   // TEST
                        
                        /*
                         out.println("Request headers:");
                         Enumeration headerNames = request.getHeaderNames();
                         while (headerNames.hasMoreElements()) {
                           String name = (String) headerNames.nextElement();
                           String value = request.getHeader(name);
                           out.println("  " + name + " : " + value);
                         }
                         */
                        
                        // sends all the information to the JSP
                        request.setAttribute("data", data);
                        request.setAttribute("actionType", "edit");
                    }
                    else   // the user is anonymous
                    {
                        // retrieves the old data type
                        DataTypeHybernate oldOne = new DataTypeHybernate();
                        oldOne.retrieveData(poolName, data.getId());
                        
                        // logs the old information about the data type (just in case)
                        logger.info("The data type '" + data.getId() + "' needs to be updated (and still pending): " + oldOne.getName() + " (synonyms: " + oldOne.getSynonyms().toString() + "; ID: " + oldOne.getId() + ")");
                        logger.info("Here are the information previously stored in the database:\n");
                        logger.info(oldOne.toString());
                        
                        // sends an email to the administrator with the OLD information about the data type
                        emailBody.append("\nThe data type '" + data.getId() + "' needs to be updated [" + version + " version], here are the information previously stored in the database:");
                        emailBody.append(oldOne.toString());
                        emailBody.append("\n\nTHE USER WAS NOT AUTHENTICATED: therefore, no change have been done so far (and no modification will be done automatically).");
                        emailBody.append("\nUser information: " + userInfo);
                        emailBody.append("\nIP address: " + request.getRemoteAddr());
                        //emailBody.append("\nHost: " + request.getRemoteHost());   // TEST
                        //emailBody.append("\nPort: " + request.getRemotePort());   // TEST
                        //emailBody.append("\nRemote User: " + request.getRemoteUser());   // TEST
                        GregorianCalendar cal = new GregorianCalendar();
                        emailBody.append("\nDate: " + cal.getTime());
                        emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMIRIAM Resources\nhttp://www.ebi.ac.uk/miriam/");
                        MailFacade.send("MIRIAM-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[MIRIAM-" + version + "] backup pending: '" + data.getName() + "'", emailBody.toString(), "text/plain; charset=UTF-8");
                        
                        // send an email to the administrator and curators with the NEW information about the data type
                        emailBody = new StringBuilder();
                        emailBody.append("\nThe data type '" + data.getId() + "' needs to be updated [" + version + " version], here are the new information (the deleted resources are not shown here):");
                        emailBody.append(data.toString());
                        
                        // quick diff
                        emailBody.append("\n--- QuickDiff ---\n\n" + data.diff(oldOne));
                        
                        emailBody.append("\n\nTHE USER WAS NOT AUTHENTICATED: therefore, no change have been done so far (and no modification will be done automatically).");
                        emailBody.append("\nUser information: " + userInfo);
                        emailBody.append("\nIP address: " + request.getRemoteAddr());
                        emailBody.append("\nHost: " + request.getRemoteHost());   // TEST
                        emailBody.append("\nPort: " + request.getRemotePort());   // TEST
                        emailBody.append("\nRemote User: " + request.getRemoteUser());   // TEST
                        emailBody.append("\nDate: " + cal.getTime());
                        emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMIRIAM Resources\nhttp://www.ebi.ac.uk/miriam/");
                        MailFacade.send("MIRIAM-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[MIRIAM-" + version + "] update pending: '" + data.getName() + "'", emailBody.toString(), "text/plain; charset=UTF-8");
                        
                        // logs the event, with all the information
                        logger.info("The Data type '" + data.getId() + "' needs to be updated in the database (still pending): " + data.getName() + " (synonyms: " + data.getSynonyms().toString() + "; ID: " + data.getId() + ")");
                        logger.info("User information: " + userInfo);
                        logger.info("IP address: " + request.getRemoteAddr());
                        logger.info("Host: " + request.getRemoteHost());   // TEST
                        logger.info("Port" + request.getRemotePort());   // TEST
                        logger.info("Remote User" + request.getRemoteUser());   // TEST
                        logger.info("Here are the updated information which need to be modified in the database:");
                        logger.info(data.toString());
                    }
                    
                    // sends all the information to the JSP
                    request.setAttribute("data", data);
                    request.setAttribute("actionType", "edit");
                }
                else   // the data type has no official resource
                {
                    logger.info("The data type '" + data.getName() + "' (id: " + data.getId() + ") has no official resource!");
                    logger.info(data.toString());
                    logger.info("==> Therefore, the updating process was canceled. Sorry about that.");
                    
                    request.setAttribute("section", "edit_error.html");
                    jsp = "static.jsp";
                }
            }
            else   // the data type doesn't exit: so we can't update it
            {
                logger.info("The data type '" + data.getName() + "' (id: " + data.getId() + ") doesn't exist in the database!");
                logger.info(data.toString());
                logger.info("==> Therefore, the updating process was canceled. Sorry about that.");
                
                request.setAttribute("section", "edit_error.html");
                jsp = "static.jsp";
            }
        }
        else
        {
            logger.info("One or more element(s) required for updating the data type '" + data.getName() + "' (id: " + data.getId() + ") are missing or wrong!");
            logger.info(data.toString());
            logger.info("==> Therefore, the updating process was canceled. Sorry about that.");
            
            request.setAttribute("section", "edit_error.html");
            jsp = "static.jsp";
        }
        
        // sends information of the result of the process to the user
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
    
}
