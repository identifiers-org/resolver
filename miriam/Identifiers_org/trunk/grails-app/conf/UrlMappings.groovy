import uk.ac.ebi.miriam.common.Constants

/**
 * Mapping of all URLs used.
 *
 * Camille Laibe
 * 20110930
 */
class UrlMappings
{

	static mappings = {

        /* TODO: test!
        "/error" {
            controller = "error"
            action = "error"
        }
        "/report" {
            controller = "error"
            action = "report"
        }
        */
        
        // root URL
        "/" {
	        controller = "info"
	        action = "intro"
        }

        /* could be move to UriRecordController (resolvePartialUrl), but does not solve the issue with "/registry/" */
        "/help" {
            controller = "info"
	        action = "help"
        }

        "/examples" {
            controller = "info"
	        action = "examples"
        }
        
        "/news" {
            controller = "info"
	        action = "news"
        }

        "/about" {
            controller = "info"
	        action = "about"
        }

        "/media/$file" {
            controller = "media"
            action = "download"
        }

        // allows "/registry" and "/registry/"
        "/registry**" {
            controller = "redirect"
            action = "external"
        }
        
        // access to a data collection
        "/$dataCollection" {
            controller = "uriRecord"
	        action = "resolvePartialUrl"
        }

        // any proper MIRIAM URL
        "/$dataCollection/$entity" {
	        controller = "uriRecord"
	        action = "resolve"
        }

        // doi MIRIAM URL
        "/doi/$entity**" {
	        controller = "uriRecord"
	        action = "doiResolve"
        }

        // any other URLs
        "/**" {
            controller = "uriRecord"
	        action = "malformedUrl"
        }

        // 500 Internal Server Error
        /*
		"500" {
            controller = "error"
            action = "internalServerError"
        }
        */

        // 400 Bad Request
        // The request could not be understood by the server due to malformed syntax.
        /*
        "400" {
            controller = "error"
            action = "badRequest"
        }
         */

        // 404 Not Found
        // The server has not found anything matching the Request-URI.
        /*
        
        DOES NOT SEEM TO WORK WITH 2.0.0.M1

        "404" {
             controller = "error"
             action = "reportError"
        }

        "404"(controller:"error", action:"reportError")
        */
	}
}
