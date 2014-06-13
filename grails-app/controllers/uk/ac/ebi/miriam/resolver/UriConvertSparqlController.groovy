package uk.ac.ebi.miriam.resolver

import org.identifiers.sparql.RdfSailConnection
import org.identifiers.sparql.Store
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.openrdf.model.impl.ValueFactoryImpl
import org.openrdf.model.vocabulary.OWL
import org.openrdf.query.BooleanQuery
import org.openrdf.query.GraphQuery
import org.openrdf.query.QueryLanguage
import org.openrdf.query.TupleQuery
import org.openrdf.query.TupleQueryResult
import org.openrdf.query.resultio.BooleanQueryResultFormat
import org.openrdf.query.resultio.BooleanQueryResultWriter
import org.openrdf.query.resultio.QueryResultIO
import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriter
import org.openrdf.repository.sail.SailRepository
import org.openrdf.rio.RDFHandler
import org.openrdf.rio.turtle.TurtleWriter

//import org.identifiers.sparql.SparqlUriConversion

class UriConvertSparqlController
{
    def dataSource

    def index()
    {
        // retrieves user query
        String query = params.query

        if (null != query)
        {
            render(status:200, text:processSparqlQuery(query), contentType:"text/plain", encoding:"UTF-8")
        }
        else
        {
            render(status:200, text:"Identifiers.org URI schemes conversion virtual SPARQL endpoint", contentType:"text/plain", encoding:"UTF-8")
        }

        // 'query' parameter
        //render(status:200, text:ResponseRdf.generateResponse(record), contentType:"application/rdf+xml", encoding:"UTF-8")
    }

    /**
     * Processes a SPARQL query and returns the results
     * TODO: this should probably be in SparqlUriConversionService...
     *
     * @param query
     * @return
     */
    def processSparqlQuery(String query)
    {
        Store rep = new Store(dataSource);
  //      TemporaryFolder folder = new TemporaryFolder();
  //      File dataDir = folder.newFolder("data.dir");
        File dataDir = File.createTempFile("data.dir","");
 //       dataDir.delete();
        dataDir.mkdir();
        rep.setDataDir(dataDir);
        rep.setValueFactory(new ValueFactoryImpl());
        SailRepository sr = new SailRepository(rep);
        rep.initialize();

//        String query1 = "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n SELECT ?target WHERE {<http://www.ebi.ac.uk/QuickGO/GTerm?id=GO:0006915> owl:sameAs ?target}";
//        System.out.println(query);
        System.out.println(query);
        TupleQuery pTQ = sr.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query);
        TupleQueryResult eval = pTQ.evaluate();
        /*
        eval.each {
            it.getBinding("target").getValue().toString().endsWith("0006915")
        }
        */
        OutputStream outputStream = new ByteArrayOutputStream()

        //def writer = new StringWriter()
        if (pTQ instanceof TupleQuery)
        {
            SPARQLResultsCSVWriter handler = new SPARQLResultsCSVWriter(outputStream);
            ((TupleQuery) pTQ).evaluate(handler);
        }
        else if (pTQ instanceof GraphQuery)
        {
            RDFHandler createWriter = new TurtleWriter(outputStream);
            ((GraphQuery) pTQ).evaluate(createWriter);
        }
        else if (pTQ instanceof BooleanQuery)
        {
            BooleanQueryResultWriter createWriter = QueryResultIO.createWriter(BooleanQueryResultFormat.TEXT, outputStream);
            boolean evaluate = ((BooleanQuery) pTQ).evaluate();
            createWriter.write(evaluate);
        }

        dataDir.delete()

        return outputStream.toString()
    }
}
