package uk.ac.ebi.miriam.resolver

class ServiceController {

    static layout = 'main'

    def index() {
        render(view: "service")
    }
}
