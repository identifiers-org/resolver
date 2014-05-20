package uk.ac.ebi.miriam.resolver

import org.identifiers.sparql.RdfSailConnection
import org.identifiers.sparql.Store
import org.junit.rules.TemporaryFolder
import org.openrdf.model.impl.ValueFactoryImpl
import org.openrdf.query.BooleanQuery
import org.openrdf.query.GraphQuery
import org.openrdf.query.QueryLanguage
import org.openrdf.query.TupleQuery
import org.openrdf.query.TupleQueryResult
import org.openrdf.query.resultio.BooleanQueryResultFormat
import org.openrdf.query.resultio.BooleanQueryResultWriter
import org.openrdf.query.resultio.QueryResultIO
import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriter
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
        Store rep = new Store();
        TemporaryFolder folder = new TemporaryFolder();
        File dataDir = folder.newFolder("data.dir");
        rep.setDataDir(dataDir);
        rep.setValueFactory(new ValueFactoryImpl());
        RdfSailConnection sr = new RdfSailConnection(rep);
        rep.initialize();
        TupleQuery pTQ = sr.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query);
        TupleQueryResult eval = pTQ.evaluate();
        /*
        eval.each {
            it.getBinding("target").getValue().toString().endsWith("0006915")
        }
        */

        def writer = new StringWriter()
        if (pTQ instanceof TupleQuery)
        {
            SPARQLResultsCSVWriter handler = new SPARQLResultsCSVWriter(writer);
            ((TupleQuery) pTQ).evaluate(handler);
        }
        else if (pTQ instanceof GraphQuery)
        {
            RDFHandler createWriter = new TurtleWriter(writer);
            ((GraphQuery) pTQ).evaluate(createWriter);
        }
        else if (pTQ instanceof BooleanQuery)
        {
            BooleanQueryResultWriter createWriter = QueryResultIO.createWriter(BooleanQueryResultFormat.TEXT, writer);
            boolean evaluate = ((BooleanQuery) pTQ).evaluate();
            createWriter.write(evaluate);
        }

        dataDir.delete()

        return writer.toString()
    }
}
