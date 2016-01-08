import com.sun.xml.internal.bind.v2.TODO
import org.codehaus.groovy.grails.web.mapping.RegexUrlMapping
import uk.ac.ebi.miriam.common.Constants
import grails.util.Holders

import java.util.regex.Pattern

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

        "/documentation" {
            controller = "info"
	        action = "documentation"
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
        "/registry" {
            controller = "redirect"
            action = "external"
        }

        // virtual SPARQL endpoint for URI schemes conversion
        "/sparql" {
            controller = "uriConvertSparql"
            action = "index"
        }

        // access to a data collection
        "/$dataCollection" {
            controller = "uriRecord"
	        action = "resolveCollectionUrl"
        }

        // any proper MIRIAM URL
        "/$dataCollection/$entity(.$format)?" {
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