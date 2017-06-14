package uk.ac.ebi.miriam.resolver

import grails.util.Holders

class SearchController {

    static layout = 'main'

    def index() {
        render(view: "search")
    }

    def getAllResources(){
        redirect(url: Holders.getGrailsApplication().config.grails.serverURL + "/rest/collections" );
    }
}
