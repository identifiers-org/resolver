package uk.ac.ebi.miriam.resolver

import uk.ac.ebi.miriam.common.Constants
import uk.ac.ebi.miriam.common.ResourceRecord
import uk.ac.ebi.miriam.exception.InvalidEntityIdentifierException
import uk.ac.ebi.miriam.exception.ResolverErrorException
import uk.ac.ebi.miriam.common.UriRecord
import uk.ac.ebi.miriam.common.Resource
import uk.ac.ebi.miriam.common.Profile
import uk.ac.ebi.miriam.common.DataCollection
import uk.ac.ebi.miriam.exception.NotExistingDataCollectionException

import javax.servlet.http.HttpServletRequest

/**
 * Controller handling the resolving work.
 * Only properly formed requests are directed here, cf. UrlMappings
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2013 BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20130426
 */
class UriRecordController
{
    static layout = 'resolve'   // Sitemesh view layouts template


    // resolves the URL
    def resolve = {
        resolveProcess((String) request.request.requestURL, params.dataCollection, params.entity, params, request)
    }

    def doiResolve ={
        String entityId = params.entity + "/" + params.subEntity;
        resolveProcess((String) request.request.requestURL, params.dataCollection, entityId, params, request)
    }

    private def resolveProcess(String url, String namespace, String entityId, Map params, HttpServletRequest request)
    {
        // TODO: read http://groovy.codehaus.org/JN3035-Exceptions

        // TODO: add tests
        UriRecord record

        try
        {
            // retrieves the record corresponding to the requested URI
            record = Resolver.retrieveUriRecord(url, namespace, entityId)

            // checks if a profile is provided as a parameter, with support with the deprecated parameter 'project'
            if ((null != params.profile) || (null != params.project))
            {
                def profileParam = (null != params.profile) ? params.profile : params.project
                // checks if profile exists
                Profile profile = Profile.findByShortname(profileParam)
                if (null != profile)
                {
                    // checks if profile is public
                    if (profile.open)
                    {
                        String preferredResourceId = profile.getPreferredResource(record.dataCollection.id)
                        if (null != preferredResourceId)
                        {
                            Resource preferredResource = Resource.findById(preferredResourceId)
                            if (params?.redirect == "true")   // direct redirection requested (no top banner to be displayed)
                            {
                                redirect(url: preferredResource.urlPrefix + params.entity + preferredResource.urlSuffix);
                            }
                            else   // top banner displayed + resource loaded in a (i)frame
                            {
                                render(view:"redirect_profile", model:[record:record, preferredResource:preferredResource, profile:profile, entity:params.entity])
                            }
                            
                            return
                        }
                        else   // this profile does not have a preferred resource for this specific data collection
                        {
                            record.addMessage("No preferred resource recorded", "Your request included information about the profile '$profileParam'. Unfortunately this profile does not have any preferred resource for the requested data collection.")
                        }

                        // TODO: direct redirection to preferred resource (with info frame)
                        //render(text:"You will be directly redirected to your preferred resource (once implemented)...\n", contentType:"text/plain", encoding:"UTF-8")
                    }
                    else   // profile is private
                    {
                        // checks is a key has been provided
                        if (null != params.key)
                        {
                            // checks if the key is correct
                            if (profile.key == params.key)
                            {
                                // TODO: direct redirection to preferred resource (with info frame)
                                render(text:"You will be directly redirected to your preferred resource (once implemented)...\n", contentType:"text/plain", encoding:"UTF-8")
                            }
                            else   // the key is wrong
                            {
                                record.addMessage("Incorrect key", "Your request included information about the profile '$profileParam'. Unfortunately the provided key is incorrect.")
                            }
                        }
                        else   // no key provided
                        {
                            record.addMessage("No key provided", "Your request included information about the profile '$profileParam'. Unfortunately this profile is private and no key was provided.")
                        }
                    }
                }
                else   // profile does not exist
                {
                    record.addMessage("Unknown profile", "Your request included information about the profile '$profileParam'. Unfortunately this profile does not exist.")
                }
            }
            else if (null != params.resource)   // checks if a resource is provided as parameter
            {
                // proper resource identifier provided
                if (params.resource ==~ /^MIR:001\d{5}$/)
                {
                    Resource resource = Resource.findById(params.resource)
                    // checks if the resource actually exists
                    if (null != resource)
                    {
                        // checks that the resource belongs to the data collection
                        if (resource.dataCollection.id == record.dataCollection.id)
                        {
                            // checks if the resource is obsolete
                            if (!resource.obsolete)
                            {
                                if (params?.redirect == "true")   // direct redirection requested (no top banner to be displayed
                                {
                                    redirect(url: resource.urlPrefix + params.entity + resource.urlSuffix);
                                }
                                else   // top banner displayed + resource loaded in a (i)frame
                                {
                                    render(view:"redirect_resource", model:[record:record, preferredResource:resource, entity:params.entity])   // TODO: no profile info
                                }
                                return
                            }
                            else
                            {
                                record.addMessage("Obsolete resource", "Your request included information about the resource '$params.resource'. Unfortunately this resource is now obsolete.")
                            }
                        }
                        else   // the resource does not belong to the data collection
                        {
                            record.addMessage("Incorrect resource", "Your request included information about the resource '$params.resource'. Unfortunately this resource does not provide access to the requested entity.")
                        }
                    }
                    else   // the resource does not exist
                    {
                        record.addMessage("Unknown resource", "Your request included information about the resource '$params.resource'. Unfortunately this resource does not exist.")
                    }
                }
                else   // not proper resource identifier
                {
                    record.addMessage("Invalid resource identifier", "Your request included information about the resource '$params.resource'. Unfortunately this is not a valid resource identifier.")
                }
            }
            else   // no profile or resource specified
            {
                // nothing else to do
            }

            // checks if the URI provided is deprecated or not (does not check for the percent encoding of the entity identifier)
            if (isObsoleteURI(record.requestedUriBase, record.officialUri))
            {
                record.addMessage("Obsolete URI", "You queried an obsolete URI! Please use the <a href=\"${record.officialUri}\" title=\"Official URL: ${record.officialUri}\" style=\"font-weight:bold;\">official one</a>.")
            }

 /*           if(request.serverName.contains("info.")){
            // renders the response, based on 'UriRecord', the optional "format" parameter and some content negotiation
                    if (null != params.format)
                {
                    def formatParam = params.format.toLowerCase()
                    if (("rdfxml" == formatParam) || ("rdf" == formatParam))   // 'rdfxml' should be preferred
                    {
                        if (null == params.resource)
                        {
                            renderResponseRdf(record)
                        }
                        else   // the user also added some format parameter: it should not be taken into consideration
                        {
                            record.addMessage("Improper usage of the 'format' parameter", "Identifiers.org does not yet support the usage of the 'format' parameter when requesting a specific resource.")
                            renderResponseHtml(record)
                        }
                    }
                    else if ("html" == formatParam)
                    {
                        renderResponseHtml(record)
                    }
                    else   // anything else... (not supported)
                    {
                        record.addMessage("Invalid 'format' parameter", "The format '$formatParam' is not supported. Please refer to <a href=\"/help\" title=\"Help page\">the help page</a> for the complete list of supported formats.")
                        renderResponseHtml(record)
                    }
                }
                else
                {
                    withFormat {
                        html { renderResponseHtml(record) }   // also handles 'all'
                        rdf { renderResponseRdf(record)}
                        //text { renderResponseText(record) }
                        //xml { renderResponseXml(record) }
                    }
                }*/
            if(request.serverName.contains("info.")){
                withFormat {
                    html { renderResponseHtml(record) }   // also handles 'all'
                    rdf { renderResponseRdf(record)}
                }
            }else{
                withFormat {
                    rdf {
                        forward(controller:"error", action:"unavailableFormat", params:[url:request.request.requestURL])
                    }
                }
            }
        }

        catch (NotExistingDataCollectionException e)
        {
            // 404 with custom message
            forward(controller:"error", action:"unknownDataCollection", params:[url:request.request.requestURL, dataCollection:e.dataCollection])
        }
        catch (InvalidEntityIdentifierException e)
        {
            // 400 with custom message
            forward(controller:"error", action:"invalidIdentifier", params:[url:request.request.requestURL, dataCollection:e.dataCollection, identifier:e.identifier, regexp:e.regexp])
        }
        catch (ResolverErrorException e)
        {
            // 500 with custom error
            forward(controller:"error", action:"internalServerError", params:[url:request.request.requestURL, message:e.getMessage()])
        }
    }


    /**
     * Resolves URLs with only indication of a data collection
     */
    def resolvePartialUrl = {
        /* tried to solved the issue of "/registry/" but that does not work
        switch (params.dataCollection) {
            case "help":
                forward(controller:"info", action:"help")
                break
            case "examples":
                forward(controller:"info", action:"examples")
                break
            case "news":
                forward(controller:"info", action:"news")
                break
            case "about":
                forward(controller:"info", action:"about")
                break
            case "registry":
                forward(controller:"redirect", action:"external")
                break
            default:

        }
        */
        resolvePartialUrlProcess((String) request.request.requestURL, params.dataCollection)
    }
    
    private def resolvePartialUrlProcess(String url, String namespace)
    {
        try
        {
            DataCollection data = Resolver.retrievePartialUriRecord(url, namespace)
            Boolean obsolete = true

            if (data.officialUrl().equalsIgnoreCase((String) request.request.requestURL))
            {
                obsolete = false;
            }
            withFormat {
                html { renderPartialResponseHtml(data, (String) request.request.requestURL, obsolete) }   // also handles 'all'
                rdf { forward(controller:"error", action:"unavailableFormat", params:[url:request.request.requestURL]) }
            }
        }
        catch (NotExistingDataCollectionException e)
        {
            // 404 with custom message
            forward(controller:"error", action:"unknownDataCollection", params:[url:request.request.requestURL, dataCollection:e.dataCollection])
        }
        catch (ResolverErrorException e)
        {
            // 500 with custom error
            forward(controller:"error", action:"internalServerError", params:[url:request.request.requestURL, message:e.getMessage()])
        }
    }
    
    /**
     * The requested URL is not correct (it does not contain a data collection part and a entity/identifier part).
     */
    def malformedUrl = {
        Boolean invalid = true
        def dataCollectionLocationInUrl = 3   //+1 when running locally

        String namespace = ""
        String entityId = ""

        // performs so more checks to be sure the request is malformed
        def url = (String) request.request.requestURL
        def parts = url.split('/')   // [http:, , identifiers.org, doi, 10.1038, nbt1155]
        // checks if there could be a data collection hidden in the URL
        if (parts.size() > dataCollectionLocationInUrl)   // corresponds to the part holding the data collection namespace
        {
            if (parts[dataCollectionLocationInUrl] != null)
            {
                def criteria = DataCollection.createCriteria()
                def data = criteria.listDistinct {
                    uris {
                        ilike("uriPrefix", "urn:miriam:${parts[dataCollectionLocationInUrl]}")   // case-insensitive 'like' expression
                    }
                }
                if ((null != data) && (data.size() > 0))
                {
                    namespace = parts[dataCollectionLocationInUrl]
                    // finds out the entity identifier (if any)
                    if (parts.size() > dataCollectionLocationInUrl+1)
                    {
                        entityId = ""
                        (dataCollectionLocationInUrl+1..parts.size()-1).each { partIndex ->
                            if (! entityId.empty)
                            {
                                entityId += "/"
                            }
                            entityId += parts[partIndex]
                        }
                    }
                    else   // no entity identifier part
                    {
                        // nothing to do
                    }
                    invalid = false
                }
            }
        }

        // the query was truly invalid
        if (invalid)
        {
            forward(controller:"error", action:"malformedUrl", params:[url:request.request.requestURL])
        }
        else   // the query was correct (could have been some issues with DOIs for example)
        {
            if (!entityId.empty)
            {

                resolveProcess((String) request.request.requestURL, namespace, entityId, params, request)
            }
            else
            {
                resolvePartialUrlProcess(url, namespace)
            }
        }
    }


    /**
     * Renders the HTML response.
     * Initially was using using "303 See Other", but due to issues with IE8.
     * Now uses "300 Multiple Choices" or "203 Non-Authoritative Information" if only one resource recorded.
     * @param record
     * @return
     */
    private def renderResponseHtml(UriRecord record)
    {
        // there is only one recorded resource: we display it
        if (record.dataCollection.resources.size() == 1)
        {
            // the resource denies the loading of its content from a frame:
            if (record.dataCollection.resources[0].deniesFrame)
            {
                // display in a table (like if there were more than one resource)
                render(status:300, view:"resolve", model:[record:record])
            }
            else   // Identifiers.org banner + external content loaded in a frame
            {
                render(status:203, view:"resolveOne", model:[record:record])
            }
        }
        else   // more than one resource: we display all of them in a table
        {
            // separates the primary/official resource (if any) from the other(s), for easier display
            ResourceRecord primaryResource = null
            List<ResourceRecord> allResources = []
            record.dataCollection.resources.each {
                if (it.primary)
                {
                    primaryResource = it
                }
                else
                {
                    allResources << it
                }
            }

            render(status:300, view:"resolve", model:[record:record, primaryResource:primaryResource, allResources:allResources])
        }
    }


    /**
     * Renders the plain text response.
     * @param record
     * @return
     *
    private def renderResponseText(UriRecord record)
    {
        render(text:"URI resolved: entity '$entity' provided by '$dataCollection'\n", contentType:"text/plain", encoding:"UTF-8")
    }
    */

    /**
     * Renders the RDF response.
     * Was previously using using: 303 See Other, but various browsers (at least Chrome) do not handle that well (we should also have provided a target Location).
     * Now uses "300 Multiple Choices" or "203 Non-Authoritative Information" if only one resource recorded, same for the HTML response.
     * @param record
     * @return
     */
    private def renderResponseRdf(UriRecord record)
    {
        render(status:200, text:ResponseRdf.generateResponse(record), contentType:"application/rdf+xml", encoding:"UTF-8")

        /* previous version (issues with Chrome)
        // only one resource, 203 Non-Authoritative Information
        if (record.dataCollection.resources.size() == 1)
        {
            render(status:200, text:ResponseRdf.generateResponse(record), contentType:"application/rdf+xml", encoding:"UTF-8")
        }
        else   // 300 Multiple Choices
        {
            render(status:200, text:ResponseRdf.generateResponse(record), contentType:"application/rdf+xml", encoding:"UTF-8")
        }
        */
    }

    /**
     * Renders the XML response.
     * @param record
     * @return
     *
    private def renderResponseXml(UriRecord record)
    {
        render(text:"<xml><miriam>Here should be the XML version of the response</miriam></xml>\n", contentType:"application/xml", encoding:"UTF-8")
    }
    */


    /**
     * Renders the partial (only request for a namespace) HTML response.
     * Initially was using "303 See Other", but due to issues with IE8.
     * Same issues with "302 Found", "307 Temporary Redirect".
     * Currently using: "203 Non-Authoritative Information" (The server successfully processed the request, but is returning information that may be from another source).
     * @param record
     * @param url
     * @param obsolete
     * @return
     */
    private def renderPartialResponseHtml(DataCollection record, String url, Boolean obsolete)
    {
        render(status:203, view:"resolvePartial", model:[record:record, requestedUri:url, obsolete:obsolete])
    }

    /**
     * Checks whether a URI is obsolete or not.
     * Does not check the entity identifier part.
     * @param providedUri
     * @param Official
     * @return
     */
    private def Boolean isObsoleteURI(String providedUri, String officialUri)
    {
        def isObsoleteURI = true

        providedUri = providedUri.replace('info.',""); //get rid of "info."
        def providedUriParts = providedUri.split('/')   // [http:, , identifiers.org, doi, 10.1038, nbt1155]
        def officialUriParts = officialUri.split('/')
        
        // URI of a data collection
        if ((officialUriParts.length == 4) && (providedUriParts.length == 4))
        {
            if (providedUri == officialUri)
            {
                isObsoleteURI = false
            }
        }
        else if ((officialUriParts.length > 4) && (providedUriParts.length > 4))   // URI of a data point
        {
            if ((officialUriParts[0] == providedUriParts[0]) && (officialUriParts[2] == providedUriParts[2]) && (officialUriParts[3] == providedUriParts[3]))
            {
                isObsoleteURI = false
            }
        }
        
        return isObsoleteURI
    }
}