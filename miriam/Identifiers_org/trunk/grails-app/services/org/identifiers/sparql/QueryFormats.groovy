package org.identifiers.sparql

/**
 * Created by IntelliJ IDEA.
 * @author Sarala Wimalaratne
 * Date: 17/06/2014
 * Time: 09:38
 */
public enum QueryFormats {
    RDFXML ("application/rdf+xml"),
    JSON ("application/json"),
    NTRIPLES ("text/plain"),
    SPARQL_RESULTS_XML("application/sparql-results+xml"),
    SPARQL_RESULTS_JSON("application/sparql-results+json"),
    X_BINARY_RDF("application/x-binary-rdf-results-table");

    private final String format;

    private QueryFormats(final String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return format;
    }
}
