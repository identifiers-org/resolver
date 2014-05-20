package org.identifiers.sparql

import uk.ac.ebi.miriam.common.URIextended

import groovy.sql.Sql
import javax.sql.DataSource;
import org.openrdf.model.impl.ValueFactoryImpl
import org.openrdf.query.TupleQuery
import org.openrdf.query.TupleQueryResult
import org.junit.rules.TemporaryFolder;
import org.openrdf.query.QueryLanguage
import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriter
import org.openrdf.query.GraphQuery
import org.openrdf.rio.RDFHandler
import org.openrdf.rio.turtle.TurtleWriter
import org.openrdf.query.BooleanQuery
import org.openrdf.query.resultio.BooleanQueryResultWriter
import org.openrdf.query.resultio.BooleanQueryResultFormat
import org.openrdf.query.resultio.QueryResultIO


/**
 * Based on: commit ab57ce4828b0b5639dd854e9fb9c8e39ece4d8a6 2014-05-20 05:10:40
 */
class SparqlUriConversionService
{
    def dataSource

    /**
     * Processes a SPARQL query and returns the results
     * @param query
     * @return
     */
    def processSparqlQuery(String query)
    {
        Store rep = new Store();
        TemporaryFolder folder = new TemporaryFolder();
        File dataDir = folder.newFolder("data.dir");

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
