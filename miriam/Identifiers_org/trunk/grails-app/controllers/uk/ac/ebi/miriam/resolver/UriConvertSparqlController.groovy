package uk.ac.ebi.miriam.resolver

//import org.identifiers.sparql.SparqlUriConversion

class UriConvertSparqlController
{
    def index()
    {
        // retrieves user query
        String query = params.query

        if (null != query)
        {
            render(status:200, text:SparqlUriConversion.processSparqlQuery(query), contentType:"text/plain", encoding:"UTF-8")
        }
        else
        {
            render(status:200, text:"Identifiers.org URI schemes conversion virtual SPARQL endpoint", contentType:"text/plain", encoding:"UTF-8")
        }

        // 'query' parameter
        //render(status:200, text:ResponseRdf.generateResponse(record), contentType:"application/rdf+xml", encoding:"UTF-8")
    }
}
