package uk.ac.ebi.miriam.resolver

import org.codehaus.groovy.grails.web.json.JSONWriter
import org.identifiers.sparql.QueryFormats
import org.identifiers.sparql.RdfSailConnection
import org.identifiers.sparql.Store
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.openrdf.model.impl.ValueFactoryImpl
import org.openrdf.model.vocabulary.OWL
import org.openrdf.query.BooleanQuery
import org.openrdf.query.GraphQuery
import org.openrdf.query.Query
import org.openrdf.query.QueryLanguage
import org.openrdf.query.TupleQuery
import org.openrdf.query.TupleQueryResult
import org.openrdf.query.resultio.BooleanQueryResultFormat
import org.openrdf.query.resultio.BooleanQueryResultWriter
import org.openrdf.query.resultio.QueryResultIO
import org.openrdf.query.resultio.binary.BinaryQueryResultWriter
import org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriter
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLParser
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter
import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriter
import org.openrdf.repository.sail.SailRepository
import org.openrdf.rio.RDFHandler
import org.openrdf.rio.rdfxml.RDFXMLWriter
import org.openrdf.rio.turtle.TurtleWriter

//import org.identifiers.sparql.SparqlUriConversion

class UriConvertSparqlController
{
    def dataSource

    def index()
    {
        // retrieves user query
        String query = params.query

        if(query!=null){
            processSparqlQuery(query);
        }
        else
        {
            render(status:200, text:"Identifiers.org URI schemes conversion virtual SPARQL endpoint", contentType:"text/plain", encoding:"UTF-8")
        }
    }

    /**
     * Processes a SPARQL query and returns the results
     * TODO: this should probably be in SparqlUriConversionService...
     *
     * @param query
     * @return
     */
    def processSparqlQuery(String query) {
        Store rep = new Store(dataSource);
        File dataDir = File.createTempFile("data.dir","");
        dataDir.mkdir();
        rep.setDataDir(dataDir);
        rep.setValueFactory(new ValueFactoryImpl());
        SailRepository sr = new SailRepository(rep);
        rep.initialize();

        Query prepareQuery = sr.getConnection().prepareQuery(QueryLanguage.SPARQL, query);

/*        withFormat {
            html{render(status:200, text:htmlOutput(pTQ), contentType:"application/sparql-result+text", encoding:"UTF-8")}
            rdf{render(status:200, text:rdfOutput(pTQ), contentType:"application/sparql-result+xml", encoding:"UTF-8")}
            json{render(status:200, text:jsonOutput(pTQ), contentType:"application/sparql-result+json", encoding:"UTF-8")}
        }*/

        String accessType = request.getHeader("Accept");
        OutputStream output = new ByteArrayOutputStream();

        if (prepareQuery instanceof TupleQuery) {
            if(isNullOrEmpty(accessType)){
                accessType = QueryFormats.SPARQL_RESULTS_XML.toString()
            }
            executeTupleQuery((TupleQuery) prepareQuery, accessType, output)
        } else if (prepareQuery instanceof GraphQuery) {
            if(isNullOrEmpty(accessType)){
                accessType = QueryFormats.RDFXML.toString()
            }
            executeConstructQuery((GraphQuery) prepareQuery, accessType, output);
        }  else if (prepareQuery instanceof BooleanQuery) {
            if(isNullOrEmpty(accessType)){
                accessType = QueryFormats.RDFXML.toString()
            }
            executeBooleanQuery((BooleanQuery) prepareQuery, accessType, output);
        }

        dataDir.delete()

    }

    private void executeTupleQuery(TupleQuery prepareQuery, String accessType, OutputStream output){
        if (accessType.equals(QueryFormats.SPARQL_RESULTS_JSON.toString())) {
            prepareQuery.evaluate(new SPARQLResultsJSONWriter(output));
        } else if (accessType.equals(QueryFormats.SPARQL_RESULTS_XML.toString())) {
            prepareQuery.evaluate(new SPARQLResultsXMLWriter(output));
        } else if (accessType.equals(QueryFormats.X_BINARY_RDF.toString())){
            prepareQuery.evaluate(new BinaryQueryResultWriter(output) )
        }
        else {
            prepareQuery.evaluate(new SPARQLResultsXMLWriter(output));
        }
        render(text:output, contentType: accessType, encoding:"UTF-8");
    }
    private void executeConstructQuery(GraphQuery prepareQuery, String accessType, OutputStream output){
        if (accessType.equals(QueryFormats.NTRIPLES.toString())) {
            prepareQuery.evaluate(new TurtleWriter(output));
        }else  if (accessType.equals(QueryFormats.RDFXML.toString())) {
            prepareQuery.evaluate(new RDFXMLWriter(output));
        }else{
            prepareQuery.evaluate(new RDFXMLWriter(output));
        }
        render(text:output, contentType: accessType, encoding:"UTF-8");
    }

    private void executeBooleanQuery(BooleanQuery prepareQuery, String accessType, OutputStream output) {
        output.write(String.valueOf(prepareQuery.evaluate()).getBytes());
        render(text:output, contentType: accessType, encoding:"UTF-8");
    }

    public static boolean isNullOrEmpty(Object o) {
   		if (o == null) {
   			return true;
   		}
   		return "".equals(o);
   	}

}
