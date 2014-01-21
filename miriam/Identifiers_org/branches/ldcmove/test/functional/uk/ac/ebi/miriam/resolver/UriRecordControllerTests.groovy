package uk.ac.ebi.miriam.resolver

import grails.util.Holders
import uk.ac.ebi.miriam.common.Constants

/**
 * Functional testings.
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
 * @version 20110919
 */
class UriRecordControllerTests extends functionaltestplugin.FunctionalTestCase
{

    // tests default response (should be HTML)
    void testResolveDefault()
    {
        redirectEnabled = false   // for properly handling the 303
        //get('http://localhost:8080/resolver/uniprot/P12345')   // Accept: */*
        //get('/uniprot/P12345')
        get(Holders.getGrailsApplication().config.getProperty('subdomain') + '/uniprot/P12345')   // Accept: */*
//        assertStatus 303

        assertContentType "text/html"   // the application returns the new media type
        assertContentContains "html"
    }
    
    // tests HTML response
    void testResolveHtmlObsolete()
    {
        redirectEnabled = false   // for properly handling the 303
        get(Holders.getGrailsApplication().config.getProperty('subdomain') + '/uniprot/P12345') {
            headers['Accept'] = 'text/html'
        }
//        assertStatus 303
        assertContentType "text/html"   // the application returns the new media type
        assertContentContains "html"
    }

    // tests HTML response
    void testResolveHtml()
    {
        redirectEnabled = false   // for properly handling the 303
        get(Holders.getGrailsApplication().config.getProperty('subdomain') + '/uniprot/P12345') {
            headers['Accept'] = 'application/xhtml+xml'
        }
//        assertStatus 303
        assertContentType "text/html"   // yes, we did not ask for exactly that...
        assertContentContains "html"
    }

    // tests XML response (not supported yet, so default HTML response provided)
    void testResolveXml()
    {
        redirectEnabled = false   // for properly handling the 303
        get(Holders.getGrailsApplication().config.getProperty('subdomain') + '/uniprot/P12345') {
            headers['Accept'] = 'text/xml'
        }
//        assertStatus 303
        assertContentType "text/html"   // the application returns the new media type
        assertContentContains "html"
    }
    
    // tests RDF response
    void testResolveRdf()
    {
        redirectEnabled = false   // for properly handling the 303
        get(Holders.getGrailsApplication().config.getProperty('subdomain') + '/uniprot/P12345') {
            headers['Accept'] = 'application/rdf+xml'
        }
//        assertStatus 303
        assertContentType "application/rdf+xml"
        assertContentContains "rdf"
    }

    // tests plain response
    void testResolvePlain()
    {
        redirectEnabled = false   // for properly handling the 303
        get(Holders.getGrailsApplication().config.getProperty('subdomain') + '/uniprot/P12345') {
            headers['Accept'] = 'text/plain'
        }
//        assertStatus 303
        assertContentType "text/html"
        assertContentContains "html"
    }

    // test not existing data collection
    void testNotExistingDataCollection()
    {
        get(Holders.getGrailsApplication().config.getProperty('subdomain') + '/wrong/P12345')
        assertStatus 404
        assertContentType "text/html"
        assertContentContains "Unknown data collection"
    }

    // test invalid entity identifier
    void testInvalidEntityIdentifier()
    {
        get(Holders.getGrailsApplication().config.getProperty('subdomain') + '/uniprot/123456')
        assertStatus 400
        assertContentType "text/html"
        assertContentContains "Invalid entity identifier"
    }

    void testDoiPercentEncoded()
    {
        redirectEnabled = false   // for properly handling the 303
        get(Holders.getGrailsApplication().config.getProperty('subdomain') + '/doi/10.1038%2Fnbt1156')
//        assertStatus 303
        assertContentType "text/html"   // the application returns the new media type
        assertContentContains "Digital Object Identifier"
    }
    
    void testDoiNotPercentEncoded()
    {
        redirectEnabled = false   // for properly handling the 303
        get(Holders.getGrailsApplication().config.getProperty('subdomain') + '/doi/10.1038/nbt1156')
//        assertStatus 303
        assertContentType "text/html"   // the application returns the new media type
        assertContentContains "Digital Object Identifier"
    }

    void testOntologyPercentEncoded()
    {
        redirectEnabled = false   // for properly handling the 303
        get(Holders.getGrailsApplication().config.getProperty('subdomain') + '/obo.go/GO%3A0006915')
//        assertStatus 303
        assertContentType "text/html"   // the application returns the new media type
        assertContentContains "Gene Ontology"
    }

    void testOntologyNotPercentEncoded()
    {
        redirectEnabled = false   // for properly handling the 303
        get(Holders.getGrailsApplication().config.getProperty('subdomain') + '/obo.go/GO:0006915')
//        assertStatus 303
        assertContentType "text/html"   // the application returns the new media type
        assertContentContains "Gene Ontology"
    }

    void testFormatHtmlInUrl()
    {
        redirectEnabled = false   // for properly handling the 303
        get(Holders.getGrailsApplication().config.getProperty('subdomain') + '/obo.go/GO:0006915?format=html')
//        assertStatus 303
        assertContentType "text/html"
        assertContentContains "html"
    }

    void testFormatRdfInUrl()
    {
        redirectEnabled = false   // for properly handling the 303
        get(Holders.getGrailsApplication().config.getProperty('subdomain') + '/obo.go/GO:0006915.rdf')
//        assertStatus 303
        assertContentType "application/rdf+xml"
        assertContentContains "rdf"
    }

    void testFormatNonExistingRdfInUrl()
    {
        redirectEnabled = false   // for properly handling the 303
        get(Holders.getGrailsApplication().config.grails.serverURL + '/go/GO:0006915.rdf')
//        assertStatus 303
        assertContentType "application/rdf+xml"
        assertContentContains "rdf"
    }

    /* the following does not work: because of the 'iframe'???
    void testDataCollectionUrl()
    {
        redirectEnabled = false   // for properly handling the 303
        get(URL_ROOT + '/biomodels.db/')
        assertStatus 303
    }
    */

    void testObsoleteEntityUrl()
    {
        redirectEnabled = false   // for properly handling the 303
        get(Holders.getGrailsApplication().config.getProperty('subdomain') + '/teddy/TEDDY_0000050')
//        assertStatus 303
        assertContentType "text/html"   // the application returns the new media type
        assertContentContains "Obsolete URI"
    }

    /* the following does not work: because of the 'iframe'???
    void testObsoleteDataCollectionUrl()
    {
        redirectEnabled = false   // for properly handling the 303
        get(URL_ROOT + '/phosphosite/')
        assertStatus 303
        assertContentType "text/html"   // the application returns the new media type
        assertContentContains "The requested URI is deprecated"
    }
    */

    /* Filter test - redirecting to info.servername - tested on local host
    * */
    void testInfoFilterWithEntity(){
        redirectEnabled = false
        get(Holders.getGrailsApplication().config.grails.serverURL+"/pubmed/16333295")
        assertRedirectUrl(Holders.getGrailsApplication().config.getProperty('subdomain')+"/pubmed/16333295")
    }

    void testInfoFilterWithoutEntity(){
        redirectEnabled = false
        get(Holders.getGrailsApplication().config.getProperty('subdomain')+"/pubmed")
        assertRedirectUrl(Holders.getGrailsApplication().config.grails.serverURL+"/pubmed")
    }
}
