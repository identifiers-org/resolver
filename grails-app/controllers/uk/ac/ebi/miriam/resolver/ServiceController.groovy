package uk.ac.ebi.miriam.resolver

class ServiceController {

    static layout = 'main'

    def index() {
        render(view: "service")
    }

    def info() {
        render (view: "info")
    }

    def restws() {
        render (view: "restws")
    }

    def xmlGen(){

    }
}
