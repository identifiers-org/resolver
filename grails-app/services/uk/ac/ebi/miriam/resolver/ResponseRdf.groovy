package uk.ac.ebi.miriam.resolver


import uk.ac.ebi.miriam.common.UriRecord
import uk.ac.ebi.miriam.common.Constants

import groovy.xml.MarkupBuilder

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
 * Copyright (C) 2006-2011 BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20110930
 */
class ResponseRdf
{
    public static final String SIO_NAMESPACE = "http://semanticscience.org/resource/"
    public static final String EDAM_NAMESPACE = "http://identifiers.org/edamontology/"

    /**
     * Generates the RDF response for a resolving query.
     * @param record
     * @return
     */
    public static def String generateResponse(UriRecord record)
    {
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

        //'xmlns:edam':"http://identifiers.org/edamontology/EDAM%3A", 

        def rdf = new MarkupBuilder(writer)
        //mkp.xmlDeclaration(version:'1.0')
        rdf.'rdf:RDF'('xmlns:rdf':"http://www.w3.org/1999/02/22-rdf-syntax-ns#", 'xmlns:rdfs':"http://www.w3.org/2000/01/rdf-schema#", 'xmlns:dcterms':"http://purl.org/dc/terms/", 'xmlns:vcard':"http://www.w3.org/2006/vcard/ns#", 'xmlns:doap':"http://usefulinc.com/ns/doap#", 'xmlns:sio':"http://semanticscience.org/resource/", 'xmlns:edam':"http://purl.bioontology.org/ontology/EDAM/", 'xmlns:mir':"http://identifiers.org/") {
            'rdf:Description'('rdf:about':record.requestedUri) {
                mkp.comment("human readable description")
                'dcterms:title'("Entity $record.entityId provided by the data collection $record.dataCollection.name ($record.dataCollection.id).", 'xml:lang':"en-GB")
                mkp.comment("official URL (the user may have performed the request with a deprecated form)")
                'dcterms:URI'(record.officialUri)
                mkp.comment("identifier (as created and used by the data provider")
                'dcterms:identifier'(record.entityId)
                'sio:SIO_000671'() {
                    'edam:EDAM_0002091'() {
                        'sio:SIO_000300'(record.entityId)
                    }
                }
                mkp.comment("data collection")
                'dcterms:source'('rdf:resource':"$Constants.RESOLVER_URL_ROOT$record.dataCollection.id")
                mkp.comment("physical locations (resources)")
                record.dataCollection.resources.each { res ->
                    res.urls.each { url ->
                        'rdfs:seeAlso'() {
                            'rdf:Description'('rdf:about':url.link) {
                                'dcterms:format'(url.format)
                                'dcterms:publisher'('rdf:resource':res.id)
                            }
                        }
                    }
                }
                mkp.comment("Resolver")
                'dcterms:publisher'('rdf:resource':"http://identifiers.org/")
                mkp.comment("date of the request which generated this document")
                'dcterms:date'(new Date())
            }
            mkp.comment("information about the data collection $record.dataCollection.id")
            'rdf:Description'('rdf:about':"$Constants.RESOLVER_URL_ROOT$record.dataCollection.id") {
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
                'rdf:Description'('rdf:about':res.id) {
                    'dcterms:title'(res.description, 'xml:lang':"en-GB")
                    'vcard:organisation-name'(res.institution)
                    'vcard:country-name'(res.location)
                    'doap:homepage'('rdf:resource':res.homepage)
                    //'dcterms:license'('rdf:resource':URL)
                    mkp.comment("state")
                    'mir:state'(res.state)
                    mkp.comment("reliability")
                    'mir:reliability'(res.reliability)
                }
            }
            
            mkp.comment("\ninformation about Identifiers.org")
            'rdf:Description'('rdf:about':"http://identifiers.org/") {
                'dcterms:title'("Identifiers.org", 'xml:lang':"en-GB")
                'dcterms:alternative'('MIRIAM Resolver', 'xml:lang':"en-GB")
                'dcterms:description'("General cross-referencing and annotation framework.")
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
}
