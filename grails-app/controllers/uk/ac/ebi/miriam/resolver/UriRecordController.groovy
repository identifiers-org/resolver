package uk.ac.ebi.miriam.resolver

import uk.ac.ebi.miriam.common.ResourceRecord
import uk.ac.ebi.miriam.exception.InvalidEntityIdentifierException
import uk.ac.ebi.miriam.exception.ResolverErrorException
import uk.ac.ebi.miriam.common.UriRecord
import uk.ac.ebi.miriam.common.Resource
import uk.ac.ebi.miriam.common.Profile
import uk.ac.ebi.miriam.common.DataCollection
import uk.ac.ebi.miriam.exception.NotExistingDataCollectionException

import javax.servlet.http.HttpServletRequest
import java.util.regex.Pattern

/**
 * Controller handling the resolving work.
 * Only properly formed requests are directed here, cf. UrlMappings
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2014 BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @modified Sarala Wimalaratne
 * @version 20140518
 */
class UriRecordController
{
    static layout = 'resolve'   // Sitemesh view layouts template


    // resolves the URL
    def resolve = {
        resolveProcess((String) request.request.requestURL, params.dataCollection, params.entity, params, request)
    }

    def doiResolve ={
        String entityId = request.forwardURI.substring(5)
        String dataCollection = "doi"
        resolveProcess((String) request.request.requestURL, dataCollection, entityId, params, request)
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

            //Redirect obsolete to official uri
            if (isObsoleteURI(record.requestedUriBase, record.officialUri))
            {
                if(request.queryString==null)
                {
                    redirect(url:record.officialUri)
                }
                else
                {
                    redirect(url:record.officialUri + '?' + request.queryString )
                }
                return;
            }

            // checks if a resource identifier is provided as parameter
            if (null != params.resource)
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
                            // direct redirection requested
                            redirect(url: resource.urlPrefix + params.entity + resource.urlSuffix);
                        }
                        else
                        {
                            // the resource does not belong to the data collection
                            record.addMessage("Incorrect resource", "Your request included information about the resource '$params.resource'. Unfortunately this resource does not provide access to the requested entity.")
                        }
                    }
                    else
                    {
                        // the resource does not exist
                        record.addMessage("Unknown resource", "Your request included information about the resource '$params.resource'. Unfortunately this resource does not exist.")
                    }
                }
                else
                {
                    // not proper resource identifier
                    record.addMessage("Invalid resource identifier", "Your request included information about the resource '$params.resource'. Unfortunately this is not a valid resource identifier.")
                }
            }
            else if (!request.serverName.contains("info."))   // canonical URIs
            {
                // if only one resource redirect
/*                if(record.dataCollection.resources.size() == 1)
                {
                    Resource preferredResource = Resource.findById(record.dataCollection.resources.iterator().next().id);
                    redirect(url: preferredResource.urlPrefix + params.entity + preferredResource.urlSuffix);
                    return;
                }*/

                // if profile is not provided, the assigns the 'direct' profile
                if(params.profile == null)
                {
                    params.profile = "direct"
                }
                def profileParam = params.profile;

                // checks if requested profile exists
                Profile profile = Profile.findByShortname(profileParam)
                if (null != profile)
                {
                    // checks if profile is public
                    if (profile.open)
                    {
                        String preferredResourceId = profile.getPreferredResource(record.dataCollection.id)
                        Resource preferredResource = Resource.findById(preferredResourceId)
                        if (params.banner != null && params.banner == "true")
                        {
                             // top banner displayed + one chosen resource loaded in a (i)frame
                            render(view:"redirect_profile", model:[record:record, preferredResource:preferredResource, profile:profile, entity:params.entity])
                        }
                        else
                        {
                            // direct redirection (no top banner is displayed)
                            redirect(url: preferredResource.urlPrefix + params.entity + preferredResource.urlSuffix);
                        }
                    }
                    else   // profile is private
                    {
                        // checks if a key has been provided
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
            if (request.serverName.contains("info."))   // info subdomain URIs
            {
                withFormat {
                    html { renderResponseHtml(record) }   // also handles 'all'
                    rdf { renderResponseRdf(record)}
                }
            }
            else   // other URIs: error
            {
                withFormat {
                    html{ }
                    rdf { forward(controller:"error", action:"unavailableFormat", params:[url:request.request.requestURL]) }

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
     * Resolves data collection URLs.
     */
    def resolveCollectionUrl = {

        if(params.dataCollection.contains(":")){
            resolveResourcePrefixUrl((String) request.request.requestURL, params.dataCollection);
            return
        }

        resolveCollectionUrlProcess((String) request.request.requestURL, params.dataCollection)
    }
    
    private def resolveCollectionUrlProcess(String url, String namespace)
    {
        try
        {
            DataCollection data = Resolver.retrieveCollectionUriRecord(url, namespace)
            Boolean obsolete = true

            if (data.officialUrl().equalsIgnoreCase((String) request.request.requestURL))
            {
                obsolete = false;
            }
            withFormat {
                html {
                    //redirects without a traling slash
                    if (!url.endsWith("/")) {
                        url = url + "/"
                        redirect(url: url)
                        return;
                    }else
                        renderPartialResponseHtml(data, (String) request.request.requestURL, obsolete) }   // also handles 'all'   //TODO: directly displays content from Identifiers.org
                rdf { renderCollectionResponseRdf(data, (String) request.request.requestURL, obsolete) }
                //rdf { forward(controller:"error", action:"unavailableFormat", params:[url:request.request.requestURL]) }
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

    private def resolveResourcePrefixUrl(String url, String prefixedResource) {

        String prefix = prefixedResource.substring(0,prefixedResource.indexOf(":"))
        String entity = prefixedResource.substring(prefixedResource.indexOf(":")+1)

        def resource = Resource.findAllByResource_prefix(prefix)

        if(resource) {
            if (Pattern.matches(resource.dataCollection.regexp[0], entity)) {
                String redirectURL = "${resource.urlPrefix[0]}${entity}${resource.urlSuffix[0]}"
                redirect(url: redirectURL)
            } else
                forward(controller: "error", action: "invalidIdentifier", params: [url: request.request.requestURL, dataCollection: resource.dataCollection, identifier: entity, regexp: resource.dataCollection.regexp])
        }else{
            forward(controller:"error", action:"unknownDataResource", params:[url:request.request.requestURL, resourcePrefix:prefix])
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
                resolveCollectionUrlProcess(url, namespace)
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

            render(status:200, view:"resolve", model:[record:record, primaryResource:primaryResource, allResources:allResources])
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
     * Renders the response to a collection URI in HTML.
     * Initially was using "303 See Other", but issues with IE8. Same issues with "302 Found", "307 Temporary Redirect".
     * Currently using: "203 Non-Authoritative Information" (The server successfully processed the request, but is returning information that may be from another source).
     * @param record
     * @param url
     * @param obsolete
     * @return
     */
    private def renderPartialResponseHtml(DataCollection record, String url, Boolean obsolete)
    {
        render(status:203, view:"resolveCollection", model:[record:record, requestedUri:url, obsolete:obsolete])
    }

    /**
     * Renders the partial (only request for a namespace) HTML response.
     *
     * @param record
     * @param url
     * @param obsolete
     * @return
     */
    private def renderCollectionResponseRdf(DataCollection record, String url, Boolean obsolete)
    {
        render(status:200, text:ResponseRdf.generateCollectionResponse(record, url, obsolete), contentType:"application/rdf+xml", encoding:"UTF-8")
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
