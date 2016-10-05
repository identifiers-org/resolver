package uk.ac.ebi.miriam.resolver

class SearchController {

    static layout = 'main'

    def index() {
        render(view: "search")
    }
}
