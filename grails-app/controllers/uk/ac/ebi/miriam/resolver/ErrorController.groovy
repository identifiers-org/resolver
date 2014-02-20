package uk.ac.ebi.miriam.resolver

import uk.ac.ebi.miriam.common.FeedbackError

/**
 * Controller which handles all errors.
 * 
 * Camille Laibe
 * 20120817
 */
class ErrorController
{
    static layout = 'main'   // Sitemesh view layouts template


    /**
     * Default error page: not much information, but allow the user to report the issue.
     */
    def error = {
        render(view:"error", model:[url:(String) request.request.requestURL, parameters:request.queryString, code:response.status])
    }

    /**
     * Handling of report from users (form displayed when an error occurs).
     * 501 Not Implemented
     */
    def report = {
        System.out.println("NAME: " + params.name)
        System.out.println("EMAIL: " + params.email)
        System.out.println("COMMENT: " + params.comment)
        System.out.println("REQUEST: " + params.request)
        System.out.println("CODE: " + params.code)
        System.out.println("CONF: " + params.configuration)

        render(status:501, text:"Sorry, feature not yet implemented. Please contact us via: biomodels-net-support [AT] lists.sf.net\n!", contentType:"text/plain", encoding:"UTF-8")
    }

    /**
     * The requested URL is about an entity provided by an unknown data collection
     * 404 Not Found
     */
    def unknownDataCollection = {
        FeedbackError error = new FeedbackError()
        error.title = "Unknown data collection"
        error.code = "404 Not Found"
        error.message = "The data collection namespace '$params.dataCollection' is unknown."
        error.request = params.url
        error.status = 404

        generateResponse(error)
    }

    // 500 Internal Server Error
    def internalServerError = {
        FeedbackError error = new FeedbackError()
        error.title = "Internal Server Error"
        error.code = "500 Internal Server Error"
        error.message = params.message
        error.request = params.url
        error.status = 500

        generateResponse(error)
    }

    // The server has not found anything matching the Request-URI.
    // 404 Not Found
    def notFound = {
        FeedbackError error = new FeedbackError()
        error.title = "Entity not found"
        error.code = "404 Not Found"
        error.message = "The system was unable to find suitable records to properly answer your request."
        error.request = params.url
        error.status = 404

        generateResponse(error)
    }

    // The request could not be understood by the server due to malformed syntax
    // 400 Bad Request
    def malformedUrl = {
        FeedbackError error = new FeedbackError()
        error.title = "Bad request"
        error.code = "400 Bad Request"
        error.message = "The requested URL does not seem to be valid."
        error.request = params.url
        error.status = 400

        generateResponse(error)
    }

    // The request could not be understood by the server due to malformed syntax
    // 400 Bad Request
    def invalidIdentifier = {
        FeedbackError error = new FeedbackError()
        error.title = "Invalid entity identifier"
        error.code = "400 Bad Request"
        error.message = "'$params.identifier' does not match the regular expression '$params.regexp' for $params.dataCollection."
        error.request = params.url
        error.status = 400

        generateResponse(error)
    }

    def unavailableFormat = {
        FeedbackError error = new FeedbackError()
        error.title = "Requested format is not supported"
        error.code = "415 Unsupported Media Type"
        error.message = "'$params.url' is not available."
        error.request = params.url
        error.status = 415

        generateResponse(error)
    }


    def reportError = {
        render(text:"Please do something about that!", contentType:"text/plain", encoding:"UTF-8")
    }

    private def generateResponse(FeedbackError error)
    {
        withFormat {
            html { render(status:error.status, view:"custom_error", model:[error:error]) }   // also handles 'all'
            text { render(status:error.status, text:"$error.code\n$error.title: $error.message\nRequest: $error.request\n", contentType:"text/plain", encoding:"UTF-8") }
            //rdf { renderResponseRdf(params.dataCollection, params.entity) }
            //xml { renderResponseXml(params.dataCollection, params.entity) }
        }
    }
}
