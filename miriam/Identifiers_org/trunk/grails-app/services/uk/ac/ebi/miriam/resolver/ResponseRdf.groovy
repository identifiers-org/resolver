package uk.ac.ebi.miriam.resolver

import grails.util.Holders
import uk.ac.ebi.miriam.common.DataCollection
import uk.ac.ebi.miriam.common.UriRecord
import uk.ac.ebi.miriam.common.Constants

import groovy.xml.MarkupBuilder
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.*
import com.hp.hpl.jena.rdf.model.Property
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype


/**
 * Handles anything linked with RDF.
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
 * @version 20140519
 */
class ResponseRdf
{
    public static final String SIO_NAMESPACE = "http://semanticscience.org/resource/"
    public static final String EDAM_NAMESPACE = "http://identifiers.org/edamontology/"

    /**
     * Generates the RDF response for a full canonical URI query.
     * @param record
     * @return
     */
    public static def String generateResponse(UriRecord record)
    {
        String resolver_url_root = Holders.getGrailsApplication().config.grails.serverURL;
        /* Jena test
        Model model = ModelFactory.createDefaultModel()
        Resource resUri = model.createResource(record.requestedUri)
        resUri.addProperty(DC.title, model.createTypedLiteral("Entity $record.entityId provided by the data collection $record.dataCollection.name ($record.dataCollection.id).", XSDDatatype.XSDstring))
        
        // TODO: add DC.URI
        //resUri.addProperty(DC.URI, model.createTypedLiteral("$record.officialUri", XSDDatatype.XSDanyURI)) does not work
        //resUri.addProperty(DC.URI, model.createLiteral(record.officialUri))   does not work
        //resUri.addProperty(DC.URI, "$record.officialUri")   does not work

        resUri.addProperty(DC.identifier, model.createTypedLiteral(record.entityId, XSDDatatype.XSDstring))
        
        // has identifier (SIO)
        Property hasIdProp = model.createProperty(SIO_NAMESPACE, "SIO_000671")
        resUri.addProperty(hasIdProp, "Simple test SIO")

        // accession (EDAM)
        Property accProp = model.createProperty(EDAM_NAMESPACE, "EDAM:0002091")
        resUri.addProperty(accProp, "Simple test EDAM")

        //model.setNsPrefix("sio" , SIO_NAMESPACE)
        
        //Property sioProp = ResourceFactory.createProperty(SIO_NAMESPACE, "sio")
        //Property edamProp = ResourceFactory.createProperty(EDAM_NAMESPACE, "edam")
        //resUri.addProperty(sioProp, model.createLiteral("SIO_000671"))

        //.addProperty(edamProp, model.createLiteral("EDAM:0002091")).addProperty(sioProp, model.createLiteral(record.entityId))
        model.write(System.out)
        //model.write(System.out, "RDF/XML-ABBREV");
        //model.write(System.out, "RDF/XML-ABBREV");
        */

        // Groovy way
        def writer = new StringWriter()
        writer.append("""<?xml version="1.0" encoding="utf-8"?>\n""")

        def rdf = new MarkupBuilder(writer)
        //mkp.xmlDeclaration(version:'1.0')
        rdf.'rdf:RDF'('xmlns:rdf':"http://www.w3.org/1999/02/22-rdf-syntax-ns#", 'xmlns:rdfs':"http://www.w3.org/2000/01/rdf-schema#", 'xmlns:dcterms':"http://purl.org/dc/terms/", 'xmlns:vcard':"http://www.w3.org/2006/vcard/ns#", 'xmlns:doap':"http://usefulinc.com/ns/doap#", 'xmlns:sio':"http://semanticscience.org/resource/", 'xmlns:edam':"http://identifiers.org/edam/", 'xmlns:dcat':"http://www.w3.org/ns/dcat#", 'xmlns:idot':"http://identifiers.org/idot/") {
            'rdf:Description'('rdf:about':record.requestedUri) {
                'dcterms:title'("Entity $record.entityId provided by the data collection $record.dataCollection.name ($record.dataCollection.id).", 'xml:lang':"en-GB")
                mkp.comment("human readable description")
                'dcterms:URI'('rdf:resource':record.officialUri)
                mkp.comment("official URI (the user may have performed the request with a deprecated form)")
                'dcterms:identifier'(record.entityId)
                mkp.comment("identifier (as created and used by the data provider)")
                'sio:SIO_000671'() {   // has identifier
                    'edam:data_2091'() {   // Accession
                        'sio:SIO_000300'(record.entityId)   // has value
                        'rdf:type'('rdf:resource':"http://idtype.identifiers.org/$record.namespace")
                    }
                }
                'rdf:type'('rdf:about':"$resolver_url_root/$record.namespace")
                mkp.comment("data collection")
                'dcterms:source'('rdf:resource':"$resolver_url_root/miriam.collection/$record.dataCollection.id")
                mkp.comment("physical locations (resources)")
                record.dataCollection.resources.each { res ->
                    res.urls.each { url ->
                        'rdfs:seeAlso'() {
                            'rdf:Description'('rdf:about':url.link) {
                                'dcterms:format'(url.format)
                                'dcterms:publisher'('rdf:resource':"$resolver_url_root/miriam.resource/$res.id")
                            }
                        }
                    }
                }
                mkp.comment("Resolver: Identifiers.org")
                'dcterms:publisher'('rdf:resource':"http://identifiers.org/")
                def formatXmlDate = ISODateTimeFormat.dateTime()
                'dcterms:date'(formatXmlDate.print(new DateTime()))
                mkp.comment("date of the request which generated this document")
            }
            mkp.comment("information about the data collection $record.dataCollection.id")
            'rdf:Description'('rdf:about':"$resolver_url_root/miriam.collection/$record.dataCollection.id") {
                'dcterms:identifier'(record.dataCollection.id)
                'dcterms:title'(record.dataCollection.name, 'xml:lang':"en-GB")
                record.dataCollection.synonyms.each { syn ->
                    'dcterms:alternative'(syn)
                }
                record.dataCollection.tags.each { tag
                    'dcterms:subject'(tag)
                }
            }
            record.dataCollection.resources.each { res ->
                mkp.comment("information about resource $res.id")
                'rdf:Description'('rdf:about':"$resolver_url_root/miriam.resource/$res.id") {
                    'dcterms:title'(res.description, 'xml:lang':"en-GB")
                    'vcard:organisation-name'(res.institution)
                    'vcard:country-name'(res.location)
                    'doap:homepage'('rdf:resource':res.homepage)
                    //'dcterms:license'('rdf:resource':URL)
                    'idot:state'(res.stateStr())
                    mkp.comment("state")
                    'idot:reliability'(res.reliability)
                    mkp.comment("reliability (percent)")
                    if (res.primary)
                    {
                        'idot:primary'("true")
                    }
                }
            }
            
            mkp.comment("\ninformation about Identifiers.org")
            'rdf:Description'('rdf:about':"http://identifiers.org/") {
                'dcterms:title'("Identifiers.org", 'xml:lang':"en-GB")
                'dcterms:alternative'('MIRIAM Resolver', 'xml:lang':"en-GB")
                'dcterms:description'("General cross-referencing and annotation framework, providing unique and perennial URIs for life science data.")
                'rdfs:seeAlso'() {
                    'rdf:Description'('rdf:about':"http://www.ebi.ac.uk/miriam/") {
                        'dcterms:format'("text/html")
                        'dcterms:title'('MIRIAM Registry', 'xml:lang':"en-GB")
                    }
                }
                //'dcterms:modified'()
            }
        }

        return writer.toString()
    }

    /**
     * Generates the RDF response for a partial canonical URI query (collection info only).
     * @param record
     * @param requested URL
     * @param whether the requested URL is deprecated or not
     * @return
     */
    public static def String generateCollectionResponse(DataCollection collection, String url, Boolean obsolete)
    {
        String resolver_url_root = Holders.getGrailsApplication().config.grails.serverURL;

        // Groovy way
        def writer = new StringWriter()
        writer.append("""<?xml version="1.0" encoding="utf-8"?>\n""")

        def rdf = new MarkupBuilder(writer)

        rdf.'rdf:RDF'('xmlns:rdf':"http://www.w3.org/1999/02/22-rdf-syntax-ns#", 'xmlns:rdfs':"http://www.w3.org/2000/01/rdf-schema#", 'xmlns:dcterms':"http://purl.org/dc/terms/", 'xmlns:vcard':"http://www.w3.org/2006/vcard/ns#", 'xmlns:doap':"http://usefulinc.com/ns/doap#", 'xmlns:sio':"http://semanticscience.org/resource/", 'xmlns:edam':"http://identifiers.org/edam/", 'xmlns:dcat':"", 'xmlns:idot':"http://identifiers.org/idot/") {
            'dcat:CatalogRecord'('rdf:about': url) {
                'dcat:identifier'(collection.id)
                'idot:namespace'(collection.namespace())
                'dcterms:title'(collection.name)
                'dcat:description'(collection.definition, 'xml:lang':"en-GB")
                collection.synonyms.each { syn ->
                    'dcterms:alternative'(syn)
                }
                'idot:idRegexPattern'(collection.regexp)
                'void:uriSpace'('rdf:resource': url + "/")   // final '/' is mandatory here
                'void:exampleResource'(collection.resources.iterator().next().exampleId)
                'dcat:issued'(collection.dateCreation)
                'dcat:modified'(collection.dateModification)
                /* TODO
                collection.tags.each { tag ->
                    'dcat:keyword'(tag)
                }
                */
                /* physical locations (resources) */
                'dcat:distribution' {
                    collection.resources.each { res ->
                        mkp.comment("information about resource $res.id")
                        'dcat:Distribution'('rdf:about': resolver_url_root + "/miriam.resource/" + res.id + "#application/xhtml+xml") {
                            'dcterms:publisher'('rdf:about': resolver_url_root + "/miriam.resource/" + res.id) {
                                'dcterms:title'(res.info, 'xml:lang': "en-GB")
                                'vcard:organisation-name'(res.institution)
                                if (null != res.location)
                                {
                                    'vcard:country-name'(res.location)
                                }
                                'dcat:landingPage'(res.urlRoot)
                                mkp.comment("state")
                                'idot:state'(res.reliability.state)
                                mkp.comment("reliability")
                                'idot:reliability'(res.reliability.uptimePercent())
                                if (res.primary)
                                {
                                    'idot:primary'("true")
                                }
                                //'dcterms:license'('rdf:resource':URL)
                            }
                            'dcat:mediaType'("application/xhtml+xml")
                            'dcat:accessURL'(res.urlPrefix + '$id' + res.urlSuffix)
                        }
                    }
                }
            }
        }

        return writer.toString()
    }
}
