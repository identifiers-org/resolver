package uk.ac.ebi.miriam.resolver

import grails.converters.JSON
import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.web.json.JSONObject

class SearchController {

    static layout = 'main'

    def index() {
        render(view: "search")
    }
}
