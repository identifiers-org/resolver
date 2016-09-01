package uk.ac.ebi.miriam.resolver

import grails.converters.JSON
import groovy.json.JsonSlurper

class RestwsController {

    def index() {
        def resources

        if (params.term) {
            URL path = new URL("http://localhost:8090/miriamws/rest/resourceuris/"+params.term)
            resources = new JsonSlurper().parse(path)
        }

        render(resources as JSON)
    }
}
