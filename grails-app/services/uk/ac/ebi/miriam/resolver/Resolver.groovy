package uk.ac.ebi.miriam.resolver

import grails.util.Holders
import uk.ac.ebi.miriam.common.Format
import uk.ac.ebi.miriam.common.Resource
import uk.ac.ebi.miriam.common.UriRecord
import uk.ac.ebi.miriam.common.UrlRecord
import uk.ac.ebi.miriam.common.ResourceRecord

import uk.ac.ebi.miriam.exception.InvalidEntityIdentifierException
import uk.ac.ebi.miriam.exception.ResolverErrorException

import uk.ac.ebi.miriam.common.Reliability
import uk.ac.ebi.miriam.common.Constants
import uk.ac.ebi.miriam.common.DataCollection
import uk.ac.ebi.miriam.exception.NotExistingDataCollectionException
import uk.ac.ebi.miriam.common.DataCollectionRecord

import javax.xml.ws.Holder


/**
 * Where all the resolving work is done.
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
 * @author Mihai Glonț <mihai.glont@ebi.ac.uk>
 * @version 20160412
 */
class Resolver
{

    /*
     * Simple comparator for prioritising a collection's resources.
     *
     * Arranges resources in descending order based on their uptime. In case of a tie,
     * we fall back on the natural order of the resources' id.
     */
    private static final Comparator RESOURCE_COMPARATOR = { Resource a, Resource b ->
        int u1 = a.reliability!=null ? a.reliability.uptimePercent() : 0;
        int u2 = b.reliability!=null ? b.reliability.uptimePercent() : 0;
        if(u1 == u2) {
            return a.id <=> b.id
        }
        u2 <=> u1
    } as Comparator

    /**
     * Retrieves the UriRecord from the requested URL in order to build the answer.
     * @param dataCollection
     * @param entity
     * @return
     * @throws uk.ac.ebi.miriam.exception.NotExistingDataCollectionException
     * @throws InvalidEntityIdentifierException
     * @throws ResolverErrorException
     */
    public static def UriRecord retrieveUriRecord(String url, String dataCollection, String entity) throws NotExistingDataCollectionException, InvalidEntityIdentifierException, ResolverErrorException
    {
        UriRecord record = null;

        // searches for data collections with the requested namespace
        def criteria = DataCollection.createCriteria()
        def matchingDataCollections = criteria.listDistinct {
            uris {
                ilike("uriPrefix", "urn:miriam:$dataCollection")   // case-insensitive 'like' expression
            }
        }

        // exactly one matching data collection
        if (matchingDataCollections.size() == 1)
        {
            def dataRecord = matchingDataCollections.get(0)

            // checks that the identifier part is valid regarding the data collection
            if (entity ==~ dataRecord.regexp)
            {
                // gathers all information necessary for the response
                record = new UriRecord()
                record.requestedUri = url
                if (url.indexOf("?") > 0)
                {
                    record.requestedUriBase = url.substring(0, url.indexOf("?"))
                }
                else
                {
                    record.requestedUriBase = url
                }

                record.requestedEntityURI = record.requestedUri.replace("info.","")

                record.namespace = dataRecord.officialUrn().substring(11)
                //record.officialUri = Constants.RESOLVER_URL_ROOT + "/" + dataRecord.officialUrn().substring(11) + "/" + URLEncoder.encode(entity)
                record.officialUri = Holders.getGrailsApplication().config.grails.serverURL + "/" + dataRecord.officialUrn().substring(11) + "/" + entity   // no encoding of the entity identifier part
                record.infoUri = Holders.getGrailsApplication().config.getProperty('subdomain') + "/" + dataRecord.officialUrn().substring(11) + "/" + entity
                record.entityId = entity
                DataCollectionRecord tempDataCollection = new DataCollectionRecord()
                tempDataCollection.id = dataRecord.id
                tempDataCollection.name = dataRecord.name
                def tempSynonyms = []
                dataRecord.synonyms.each { syn ->
                    tempSynonyms.add(syn.name)
                }
                tempDataCollection.synonyms = tempSynonyms
                //tempData.tags = dataRecord.tags
                def tempResources = []
                dataRecord.resources.each {
                    // we only consider non obsolete resources
                    if (! it.obsolete)
                    {
                        ResourceRecord tempRes = new ResourceRecord()
                        tempRes.id = it.id
                        tempRes.description = it.info
                        tempRes.homepage = it.urlRoot
                        tempRes.institution = it.institution
                        tempRes.location = it.location
                        tempRes.deprecated = it.obsolete
                        tempRes.deniesFrame = it.deniesFrame
                        tempRes.primary = it.primary

                        if (null != it.reliability)   // so far, no health record is available for the first day(s) after creation of a new data collection
                        {
                            tempRes.state = Reliability.getHumanState(it.reliability.state)
                            tempRes.reliability = it.reliability.uptimePercent()
                        }
                        else   // no health info available
                        {
                            tempRes.state = "Unknown"
                            tempRes.reliability = 0
                        }
                        //TODO: tempRes.license =   // link towards a license or some terms of use
                        def tempUrls = []
                        tempUrls << new UrlRecord(it.urlPrefix + entity + it.urlSuffix, "HTML")
                        it.format.each {
                            tempUrls << new UrlRecord(it.urlPrefix + entity + it.urlSuffix, it.mimetype.displaytext)
                        }
                        tempRes.urls = tempUrls
                        tempResources << tempRes
                    }
                }
                tempDataCollection.resources = tempResources
                record.dataCollection = tempDataCollection
            }
            else   // invalid entity identifier
            {
                throw new InvalidEntityIdentifierException("The identifier '$entity' is invalid for the data collection '$dataCollection'!", dataCollection, entity, dataRecord.regexp)
            }
        }
        else if (matchingDataCollections.size() > 1)   // more than one matching data collections
        {
            throw new ResolverErrorException("There are more than one data collection matching the namespace '$dataCollection'!", dataCollection)
        }
        else   // if (matchingDataCollections.size() == 0)   // no matching data collection
        {
            throw new NotExistingDataCollectionException("The data collection '$dataCollection' does not exist!", dataCollection)
        }

        return record
    }


    public static def DataCollection retrieveCollectionUriRecord(String url, String dataCollection) throws NotExistingDataCollectionException, ResolverErrorException
    {
        DataCollection data = null

        // searches for data collections with the requested namespace
        def criteria = DataCollection.createCriteria()
        def matchingDataCollections = criteria.listDistinct {
            uris {
                ilike("uriPrefix", "urn:miriam:$dataCollection")   // case-insensitive 'like' expression
            }
        }

        // exactly one matching data collection
        if (matchingDataCollections.size() == 1)
        {
            data = matchingDataCollections.get(0)

        }
        else if (matchingDataCollections.size() > 1)   // more than one matching data collections
        {
            throw new ResolverErrorException("There are more than one data collection matching the namespace '$dataCollection'!", dataCollection)
        }
        else   // if (matchingDataCollections.size() == 0)   // no matching data collection
        {
            throw new NotExistingDataCollectionException("The data collection '$dataCollection' does not exist!", dataCollection)
        }

        return data
    }


    /**
     * Retrieves the most reliable resource of the data collection whose identifier is given as a parameter, favouring EBI resources.
     *
     * In case several resources have the same uptime, one is randomly selected.
     * @param id identifier of a data collection
     * @return
     */
    public static String getMostReliableResourceId(String id)
    {
        String mostReliableResourceId = null

        boolean collectionExists = DataCollection.exists(id)
        if (collectionExists) {
            def orderedResources = new TreeSet<Resource>(RESOURCE_COMPARATOR)
            def endorsedResources = new TreeSet<Resource>(RESOURCE_COMPARATOR)

            def data = DataCollection.load(id) // proxy suffices here
            //TODO data.resources should return all resources, not just one.
            def resources = Resource.findAllByDataCollection data //data.resources
            // searches the best reliability
            resources.each { Resource r ->
                if (!r.obsolete) {
                    orderedResources << r
                    if (isEndorsedResource(r)) {
                        endorsedResources.add(r)
                    }
                }
            }

            if (endorsedResources) {
                orderedResources = endorsedResources
            }

            if (!orderedResources)   // no (best) resource found: there is an issue here!
            {
                return mostReliableResourceId
            }
            Resource best = orderedResources.first()
            mostReliableResourceId = best.id
        }

        mostReliableResourceId
    }

    /**
     * Returns the direct resource for the supplied data collection.
     *
     * The precedence order is
     *      primary resource > endorsed resource > highest uptime
     *
     * If the resource is obsolete or currently down, then it's ignored.
     */
    public static String getDirectResourceId(String id){

        Set<Resource> runningResources = getRunningResources(id);

        if(runningResources){
            Resource best = runningResources.first()
            return best.id
        }

        return null
    }

    private static boolean isEndorsedResource(Resource r) {
        r?.institution?.contains("European Bioinformatics Institute") &&
                isResourceUp(r) && !r.obsolete
    }

    private static boolean isResourceUp(Resource r) {
        final Integer state = r?.reliability?.state //int can't handle null
        state > 0 && state <= 3
    }

    public static Format getDirectResourceIdWithRDF(String id){

        Set<Resource> runningResources= getRunningResources(id);

        if (runningResources) {
            for (Resource resource : runningResources) {
                for (Format format : resource.format) {
                    if (format.mimetype.displaytext.equals("RDF")) {
                        return format;
                    }
                }
            }
        }

        return null
    }

    private static Set<Resource> getRunningResources(String id){

        boolean collectionExists = DataCollection.exists(id)
        if (collectionExists){
            def data = DataCollection.load id
            //TODO data.resources should return all resources, not just one.
            def resources = Resource.findAllByDataCollection data

            Set<Resource> runningResources = new TreeSet<Resource>(RESOURCE_COMPARATOR)
            Set<Resource> endorsedResources = new TreeSet<Resource>(RESOURCE_COMPARATOR)

            for (Resource r: resources) {
                //filter non-obsolete, running resources
                if (!r.obsolete) {
                    if (isResourceUp(r)) {
                        if (r.primary) {
                            runningResources.clear();
                            return runningResources << r
                        }
                        runningResources.add(r)
                        if (isEndorsedResource(r)) {
                            endorsedResources << r
                        }
                    }
                }
            }
            if (endorsedResources) {
                runningResources = endorsedResources
            }

            // if there are no running resources, we fall back to the original list
            if(!runningResources) {
                runningResources.addAll(resources)
            }
            return runningResources
        }
        return null
    }
}
